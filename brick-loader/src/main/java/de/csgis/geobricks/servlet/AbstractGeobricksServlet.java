package de.csgis.geobricks.servlet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.csgis.geobricks.guice.GuiceServletConfig;

/**
 * {@link HttpServlet} with members injected using Guice.
 * 
 * @author vicgonco
 */
public class AbstractGeobricksServlet extends HttpServlet {
	@Inject
	private Config config;

	@Override
	public void init() throws ServletException {
		super.init();
		GuiceServletConfig.injector().injectMembers(this);
	}

	protected Config getConfig() {
		return config;
	}
}
