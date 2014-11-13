package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

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
		StringBuilder paths = new StringBuilder();
		StringBuilder shim = new StringBuilder();

		for (PluginDescriptor descriptor : descriptors) {
			Map<String, String> requirePaths = descriptor.getRequirePaths();
			for (Object key : requirePaths.keySet()) {
				String name = key.toString();

				paths.append('"').append(name).append('"');
				paths.append(':');
				paths.append("\"").append(requirePaths.get(name)).append('"');
				paths.append(",\n\t\t");
			}

			Map<String, String[]> requireShim = descriptor.getRequireShim();
			for (Object key : requireShim.keySet()) {
				String name = key.toString();
				JSONArray array = new JSONArray(Arrays.asList(requireShim
						.get(name)));
				shim.append("\"" + name + "\": " + array.toString() + ",\n\t\t");
			}
		}

		// remove last comma
		if (paths.indexOf(",") != -1) {
			paths.setLength(paths.lastIndexOf(","));
		}
		if (shim.indexOf(",") != -1) {
			shim.setLength(shim.lastIndexOf(","));
		}

		content = content.replace("$paths", "paths : {" + paths.toString()
				+ "}");
		content = content.replace("$shim", "shim : {" + shim.toString() + "}");
		return content;
	}

	@Override
	public void destroy() {
	}
}
