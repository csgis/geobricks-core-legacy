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

import net.sf.json.JSONObject;
import de.csgis.geobricks.ConfiguredApplication;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;

@Singleton
public class IndexReplaceCSSFilter implements Filter {
	@Inject
	private PluginRegistry pluginRegistry;

	private JSONObject pluginsConfiguration;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		pluginsConfiguration = (JSONObject) filterConfig.getServletContext()
				.getAttribute(ConfiguredApplication.ATTR_PLUGINS_CONF);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		response.getWriter().print(
				process(wrapper.toString(), pluginsConfiguration));
	}

	public String process(String content, JSONObject pluginsConfiguration) {
		StringBuilder str = new StringBuilder();
		for (Object plugin : pluginsConfiguration.keySet()) {
			PluginDescriptor descriptor = pluginRegistry.getPlugin(plugin
					.toString());
			String[] styleSheets = descriptor.getStyleSheets();
			if (styleSheets != null) {
				for (String styleSheet : styleSheets) {
					str.append("<link rel=\"stylesheet\" href=\"" + styleSheet
							+ "\"/>\n");
				}
			}
		}

		return content.replace("$styleSheets", str.toString());
	}

	@Override
	public void destroy() {
	}
}
