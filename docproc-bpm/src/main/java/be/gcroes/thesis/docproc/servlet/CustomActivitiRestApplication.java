/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.gcroes.thesis.docproc.servlet;

import java.util.List;

import org.activiti.rest.common.api.ActivitiUtil;
import org.activiti.rest.common.api.DefaultResource;
import org.activiti.rest.common.filter.JsonpFilter;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.rest.service.application.ActivitiRestServicesApplication;
import org.activiti.rest.service.application.RestServicesInit;
import org.codehaus.jackson.map.SerializationConfig;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.engine.Engine;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.SecretVerifier;
import org.restlet.security.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gcroes.thesis.docproc.filter.RestAuthFilter;


public class CustomActivitiRestApplication extends ActivitiRestServicesApplication {
    
  private static Logger logger = LoggerFactory.getLogger(CustomActivitiRestApplication.class);

  protected RestResponseFactory restResponseFactory;
  
  public CustomActivitiRestApplication() {
    super();
  }
  
  /**
   * Creates a root Restlet that will receive all incoming calls.
   */
  @Override
  public synchronized Restlet createInboundRoot() {
    initializeAuthentication();
    
    Router router = new Router(getContext());
    router.attachDefault(DefaultResource.class);
    RestServicesInit.attachResources(router);
        
    RestAuthFilter restAuthFilter = new RestAuthFilter();
    JsonpFilter jsonpFilter = new JsonpFilter(getContext());
    
    restAuthFilter.setNext(authenticator);
    authenticator.setNext(jsonpFilter);
    jsonpFilter.setNext(router);
    

    // Get hold of JSONConverter and enable ISO-date format by default
    List<ConverterHelper> registeredConverters = Engine.getInstance().getRegisteredConverters();
    for(ConverterHelper helper : registeredConverters) {
      if(helper instanceof JacksonConverter) {
        JacksonConverter jacksonConverter = (JacksonConverter) helper;
        jacksonConverter.getObjectMapper().configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
      }
    }
    return restAuthFilter;
  }
  
  
  public void setRestResponseFactory(RestResponseFactory restResponseFactory) {
    this.restResponseFactory = restResponseFactory;
  }
  
  public RestResponseFactory getRestResponseFactory() {
    if(restResponseFactory == null) {
      restResponseFactory = new RestResponseFactory();
    }
    return restResponseFactory;
  }
  
  public void initializeAuthentication() {
      Verifier verifier = new SecretVerifier() {

        @Override
        public boolean verify(String username, char[] password) throws IllegalArgumentException {
          boolean verified = ActivitiUtil.getIdentityService().checkPassword(username, new String(password));
          return verified;
        }
      };
      
      // Set authenticator as a NON-optional filter. If certain request require no authentication, a custom RestAuthenticator
      // should be used to free the request from authentication.
      authenticator = new ChallengeAuthenticator(null, true, ChallengeScheme.HTTP_BASIC,
            "LDAP Realm") {
        
        @Override
        protected boolean authenticate(Request request, Response response) {
          
          // Check if authentication is required if a custom RestAuthenticator is set
          if(restAuthenticator != null && !restAuthenticator.requestRequiresAuthentication(request)) {
            return true;
          }
          
          if (request.getChallengeResponse() == null) {
            return false;
          } else {
            boolean authenticated = super.authenticate(request, response);
            if(authenticated && restAuthenticator != null) {
              // Additional check to see if authenticated user is authorised. By default, when no RestAuthenticator
              // is set, a valid user can perform any request.
              authenticated = restAuthenticator.isRequestAuthorized(request);
            }
            return authenticated;
          }
        }
      };
      authenticator.setVerifier(verifier);
    }
}