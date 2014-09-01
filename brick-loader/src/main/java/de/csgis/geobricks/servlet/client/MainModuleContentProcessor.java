package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@Singleton
public class MainModuleContentProcessor implements Filter {

	private PluginDescriptor[] descriptors;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		descriptors = (PluginDescriptor[]) filterConfig.getServletContext()
				.getAttribute(Geobricks.DESCRIPTORS_ATTRIBUTE);
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
		StringBuilder str = new StringBuilder();
		str.append("\"jquery\" : \"../jslib/jquery-1.11.0.min\",");

		for (PluginDescriptor descriptor : descriptors) {
			Map<String, String> dependencies = descriptor.getDependencies();
			for (Object key : dependencies.keySet()) {
				String name = key.toString();

				str.append('"').append(name).append('"');
				str.append(':');
				// Paths are relative to modules so we go up one level
				str.append("\"../").append(dependencies.get(name)).append('"');
				str.append(",\n\t\t");
			}
		}

		// remove last comma
		if (str.indexOf(",") != -1) {
			str.setLength(str.lastIndexOf(","));
		}

		return content.replace("$nonRequireJSDependencies", str.toString());
	}

	@Override
	public void destroy() {
	}
}
