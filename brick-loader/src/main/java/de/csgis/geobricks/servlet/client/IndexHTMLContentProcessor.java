package de.csgis.geobricks.servlet.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
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
import de.csgis.geobricks.servlet.CharResponseWrapper;

@Singleton
public class IndexHTMLContentProcessor implements Filter {
	private static final Logger logger = Logger
			.getLogger(IndexHTMLContentProcessor.class);

	public static final String STYLES_DIR = "styles";

	private PluginDescriptor[] descriptors;

	private File stylesDir;

	private File appProperties;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext context = filterConfig.getServletContext();
		descriptors = (PluginDescriptor[]) context
				.getAttribute(Geobricks.ATTR_PLUGINS_DESC);

		String path = context.getRealPath("/" + STYLES_DIR);
		if (path != null) {
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory() && dir.canRead()) {
				stylesDir = dir;
			}
		}

		String confDir = context.getAttribute(Geobricks.ATTR_CONF_DIR)
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
			minified = Boolean.parseBoolean(properties.getProperty("minified"));
		} catch (IOException e) {
			logger.error(
					"Error reading app.properties file: "
							+ appProperties.getAbsolutePath()
							+ ". Assuming minified_js=false", e);
		}
		response.getWriter().print(
				process(wrapper.toString(), descriptors, stylesDir, minified));
	}

	public String process(String content, PluginDescriptor[] descriptors,
			File stylesDir, boolean minified) {
		if (minified) {
			String css = "<link rel=\"stylesheet\" href=\"optimized/portal-style.css\"/>\n";
			content = content.replace("$styleSheets", css
					+ getCSSFromDir(stylesDir));
			return content.replace("$mainModule", "optimized/portal");
		} else {
			StringBuilder str = new StringBuilder();
			for (PluginDescriptor descriptor : descriptors) {
				for (String style : descriptor.getStyles()) {
					str.append("<link rel=\"stylesheet\" href=\"" + style
							+ "\"/>\n");
				}
			}
			content = content.replace("$styleSheets", str.toString()
					+ getCSSFromDir(stylesDir));
			return content.replace("$mainModule", "modules/main");
		}
	}

	private String getCSSFromDir(File stylesDir) {
		String ret = "";
		if (stylesDir != null) {
			File[] cssFiles = stylesDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".css");
				}
			});

			if (cssFiles != null) {
				for (File css : cssFiles) {
					String path = css.getPath();
					String stylesPath = stylesDir.getPath();
					if (path.startsWith(stylesPath)) {
						path = path.substring(stylesPath.length() + 1);
					}
					ret += "<link rel=\"stylesheet\" href=\"" + STYLES_DIR
							+ "/" + path + "\"/>\n";
				}
			}
		}

		return ret;
	}

	@Override
	public void destroy() {
	}
}
