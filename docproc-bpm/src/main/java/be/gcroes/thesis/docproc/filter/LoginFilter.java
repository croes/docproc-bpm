package be.gcroes.thesis.docproc.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.realm.GenericPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter implements Filter{
    
    private static Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        GenericPrincipal principal = (GenericPrincipal) httpRequest.getUserPrincipal();
        if(principal != null){
            String user = principal.getName();
            String password = principal.getPassword();
            if (user != null && session.getAttribute("user") == null) {
                logger.info("NEW SESSION DETECTED, SAVING USER {} WITH PASS {}", user, password);
                session.setAttribute("user", user);
                session.setAttribute("password", password);
                // First-time login. You can do your intercepting thing here.
                //auth with REST API as well  
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
        
    }
}
