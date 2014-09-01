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

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@Singleton
public class IndexReplaceCSSFilter implements Filter {

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
		response.getWriter().print(process(wrapper.toString(), descriptors));
	}

	public String process(String content, PluginDescriptor[] descriptors) {
		StringBuilder str = new StringBuilder();
		for (PluginDescriptor descriptor : descriptors) {
			for (String style : descriptor.getStyles()) {
				str.append("<link rel=\"stylesheet\" href=\"" + style
						+ "\"/>\n");
			}
		}

		return content.replace("$styleSheets", str.toString());
	}

	@Override
	public void destroy() {
	}
}
