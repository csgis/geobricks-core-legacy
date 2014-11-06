package de.csgis.geobricks.servlet.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@Singleton
public class IndexHTMLContentProcessor implements Filter {
	private static final Logger logger = Logger
			.getLogger(IndexHTMLContentProcessor.class);

	private PluginDescriptor[] descriptors;
	private File appProperties;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext context = filterConfig.getServletContext();
		descriptors = (PluginDescriptor[]) context
				.getAttribute(Geobricks.ATTR_PLUGINS_DESC);
		String confDir = context.getAttribute(Geobricks.ATTR_PLUGINS_DESC)
				.toString();
		appProperties = new File(confDir, "app.properties");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, wrapper);

		boolean minified = false;
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(appProperties));
			minified = Boolean.parseBoolean(properties
					.getProperty("minified_js"));
		} catch (IOException e) {
			logger.error(
					"Error reading app.properties file: "
							+ appProperties.getAbsolutePath()
							+ ". Assuming minified_js=false", e);
		}
		response.getWriter().print(
				process(wrapper.toString(), descriptors, minified));
	}

	public String process(String content, PluginDescriptor[] descriptors,
			boolean minified) {
		StringBuilder str = new StringBuilder();
		for (PluginDescriptor descriptor : descriptors) {
			for (String style : descriptor.getStyles()) {
				str.append("<link rel=\"stylesheet\" href=\"" + style
						+ "\"/>\n");
			}
		}

		content = content.replace("$styleSheets", str.toString());
		if (minified) {
			return content.replace("$mainModule", "modules/main");
		} else {
			return content.replace("$mainModule", "optimized/portal.js");
		}
	}

	@Override
	public void destroy() {
	}
}
