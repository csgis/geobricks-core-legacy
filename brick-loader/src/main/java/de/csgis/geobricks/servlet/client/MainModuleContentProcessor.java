package de.csgis.geobricks.servlet.client;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.NonRequireDependency;
import de.csgis.geobricks.PluginRegistry;

@Singleton
public class MainModuleContentProcessor implements Filter {

	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		NonRequireDependency[] dependencies = pluginRegistry
				.getNonRequireDependencies();
		StringBuilder str = new StringBuilder();
		for (NonRequireDependency dep : dependencies) {
			str.append('"').append(dep.getName()).append('"');
			str.append(':');
			// Paths are relative to modules so we go up one level
			str.append("\"../").append(dep.getPath()).append('"');
			str.append(",\n\t\t");
		}

		// remove last comma
		str.setLength(str.lastIndexOf(","));
		String returnCode = wrapper.toString().replace(
				"$nonRequireJSDependencies", str.toString());

		response.getWriter().write(returnCode);
	}

	@Override
	public void destroy() {
	}
}
