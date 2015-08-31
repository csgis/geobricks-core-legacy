package de.csgis.geobricks.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.CharResponseWrapper;
import de.csgis.geobricks.servlet.Config;

/**
 * Filter to process the main.js document (replace placeholders, add extra
 * parts,...), depending on the request.
 * 
 * @author vicgonco
 */
@Singleton
public class MainModuleContentProcessor implements Filter {

	@Inject
	private Config config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		CharResponseWrapper wrapper = new CharResponseWrapper(resp);
		chain.doFilter(request, wrapper);

		PluginDescriptor[] descriptors = config.getPluginDescriptors(req, resp);
		response.getWriter().write(process(wrapper.toString(), descriptors));
	}

	/**
	 * Processes the main.js content.
	 * 
	 * @param content
	 *            The content of the main.js document.
	 * @param descriptors
	 *            The plugin descriptors for the application.
	 * @return The content of the main.js document already processed.
	 */
	String process(String content, PluginDescriptor[] descriptors) {
		JSONObject paths = new JSONObject();
		JSONObject shim = new JSONObject();

		for (PluginDescriptor descriptor : descriptors) {
			paths.putAll(descriptor.getRequirePaths());
			shim.putAll(descriptor.getRequireShim());
		}

		content = content.replace("$paths", "paths : " + paths.toString());
		content = content.replace("$shim", "shim : " + shim.toString());
		return content;
	}

	@Override
	public void destroy() {
		// do nothing
	}
}
