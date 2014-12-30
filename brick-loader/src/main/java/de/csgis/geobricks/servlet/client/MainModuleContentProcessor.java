package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@Singleton
public class MainModuleContentProcessor implements Filter {

	private PluginDescriptor[] descriptors;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		descriptors = (PluginDescriptor[]) filterConfig.getServletContext()
				.getAttribute(Geobricks.ATTR_PLUGINS_DESC);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, wrapper);
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
