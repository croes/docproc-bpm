package be.gcroes.thesis.docproc.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.routing.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestAuthFilter extends Filter{
    
    private static Logger logger = LoggerFactory.getLogger(RestAuthFilter.class);
    
    public int beforeHandle(Request request, Response response){
        HttpServletRequest httpRequest = ServletUtils.getRequest(request);
        HttpSession session = httpRequest.getSession(false);
        if(session != null){
            String user = (String) session.getAttribute("user");
            String password = (String) session.getAttribute("password");
            if(user != null && password != null){
                logger.info("Adding ChallengeResponse for user {} with pass {}", user, password);
                ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
                request.setChallengeResponse(cr);
            }
        }
        return Filter.CONTINUE;
    }

}
