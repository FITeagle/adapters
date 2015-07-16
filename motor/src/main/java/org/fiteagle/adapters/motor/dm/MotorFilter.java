package org.fiteagle.adapters.motor.dm;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dne on 24.06.15.
 */
@WebFilter("/hallo")
public class MotorFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)servletRequest;
        if (  req.getMethod().equals("GET") )  {
            servletRequest.getRequestDispatcher("/ontology").forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
