package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
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
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.ApplicationPluginUsage;

@Singleton
public class IndexReplaceCSSFilter implements Filter {
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

		Application app = (Application) request
				.getAttribute(Geobricks.APP_INSTANCE_HTTP_ATTRIBUTE);
		Set<ApplicationPluginUsage> plugins = app.getPlugins();
		StringBuilder str = new StringBuilder();
		for (ApplicationPluginUsage plugin : plugins) {
			PluginDescriptor descriptor = pluginRegistry.getPlugin(plugin
					.getPluginId());
			String[] styleSheets = descriptor.getStyleSheets();
			if (styleSheets != null) {
				for (String styleSheet : styleSheets) {
					str.append("<link rel=\"stylesheet\" href=\"" + styleSheet
							+ "\"/>\n");
				}
			}
		}

		String indexHtmlContent = wrapper.toString().replace("$styleSheets",
				str.toString());

		response.getOutputStream().print(indexHtmlContent);
	}

	@Override
	public void destroy() {
	}
}
