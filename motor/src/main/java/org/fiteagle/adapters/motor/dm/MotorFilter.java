package org.fiteagle.adapters.motor.dm;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by dne on 24.06.15.
 */
@WebFilter("/hallo")
public class MotorFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
	    final FilterChain filterChain) throws IOException, ServletException {

	final HttpServletRequest req = (HttpServletRequest) servletRequest;
	if (req.getMethod().equals("GET")) {
	    servletRequest.getRequestDispatcher("/ontology").forward(servletRequest, servletResponse);
	}
    }

    @Override
    public void destroy() {

    }
}
