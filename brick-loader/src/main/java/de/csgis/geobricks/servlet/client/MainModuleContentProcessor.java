package de.csgis.geobricks.servlet.client;

import java.io.IOException;

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
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.CharResponseWrapper;
import de.csgis.geobricks.servlet.Config;

@Singleton
public class MainModuleContentProcessor implements Filter {

	private Config config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = (Config) filterConfig.getServletContext().getAttribute(
				Geobricks.ATTR_CONFIG);
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

	public String process(String content, PluginDescriptor[] descriptors) {
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
	}
}
