package org.fiteagle.abstractAdapter.dm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Model;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jena.atlas.logging.Log;


public class RESTFilter implements Filter {
	  
	private final Logger LOGGER = Logger.getLogger(this.getClass().toString());
	private String token = "Test123";
	
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String uri = req.getRequestURI();
        String path = uri.substring(req.getContextPath().length());
    
        if (req.getMethod().equals("POST") && req.getHeader("Token").equals(token))  {
        	request.getRequestDispatcher("/config").forward(request, response);
        }else{
        	res.sendError(401);
    		LOGGER.log(Level.SEVERE, "Someone tried to push an Config-File with incorrect token");
        }
        	
    }
        @Override
        public void init (FilterConfig arg0)throws ServletException {
            // TODO Auto-generated method stub

        }

}
