package org.fiteagle.adapters.epc.dm;

import java.io.IOException;
import java.util.logging.Logger;

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
public class EpcFilter implements Filter {
	private static final Logger LOGGER = Logger.getLogger(EpcFilter.class
			.toString());

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Init not implemented");
	}

	@Override
	public void doFilter(final ServletRequest servletRequest,
			final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {

		final HttpServletRequest req = (HttpServletRequest) servletRequest;
		if (req.getMethod().equals("GET")) {
			servletRequest.getRequestDispatcher("/ontology").forward(
					servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
		LOGGER.info("Destroy not implemented");
	}
}
