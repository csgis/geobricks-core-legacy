package de.csgis.geobricks.servlet.client;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

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
import de.csgis.geobricks.servlet.CharResponseWrapper;
import de.csgis.geobricks.servlet.Config;

@Singleton
public class IndexHTMLContentProcessor implements Filter {
	public static final String STYLES_DIR = "_static" + File.separator + "css";

	private Config config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		config = (Config) filterConfig.getServletContext().getAttribute(
				Geobricks.ATTR_CONFIG);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		response.getWriter().print(process(wrapper.toString(), config));
	}

	public String process(String content, Config config) {
		Properties properties = config.getAppProperties();
		boolean minified = Boolean.parseBoolean(properties
				.getProperty("minified"));
		String title = properties.getProperty("title");
		PluginDescriptor[] descriptors = config.getPluginDescriptors();
		File stylesDir = new File(config.getConfigDir(), STYLES_DIR);

		String replaced;
		if (minified) {
			String css = "<link rel=\"stylesheet\" href=\"optimized/portal-style.css\"/>\n";
			content = content.replace("$styleSheets", css
					+ getCSSFromDir(stylesDir));
			replaced = content.replace("$mainModule", "optimized/portal");
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
			replaced = content.replace("$mainModule", "modules/main");
		}

		if (title != null) {
			replaced = replaced.replace("$title", title);
		} else {
			replaced = replaced.replace("$title", "");
		}
		return replaced;
	}

	private String getCSSFromDir(File stylesDir) {
		String ret = "";

		if (stylesDir.exists() && stylesDir.isDirectory()
				&& stylesDir.canRead()) {
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
