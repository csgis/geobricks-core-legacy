package de.csgis.geobricks.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.CharResponseWrapper;
import de.csgis.geobricks.servlet.Config;

/**
 * Filter to process the index.html document (replace placeholders, add extra
 * parts,...), depending on the request.
 * 
 * @author vicgonco
 */
@Singleton
public class IndexHTMLContentProcessor implements Filter {
	public static final String STYLES_DIR = "_static" + File.separator + "css";

	@Inject
	private Config config;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		CharResponseWrapper wrapper = new CharResponseWrapper(resp);
		chain.doFilter(request, wrapper);

		response.getWriter().print(
				process(wrapper.toString(), config, req, resp));
	}

	/**
	 * Processes the index.html content.
	 * 
	 * @param content
	 *            The index.html content.
	 * @param config
	 *            The configuration object.
	 * @param request
	 *            The HTTP request for the index.html document.
	 * @param response
	 *            The HTTP response.
	 * @return The index.html content already processed.
	 * @throws IOException
	 *             if any I/O error occurs while processing the index.html
	 *             content.
	 */
	String process(String content, Config config, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Properties properties = config.getAppProperties();
		boolean minified = Boolean.parseBoolean(properties
				.getProperty("minified"));
		String title = properties.getProperty("title");
		PluginDescriptor[] descriptors = config.getPluginDescriptors(request,
				response);
		File stylesDir = new File(config.getConfigDir(), STYLES_DIR);

		String replaced;
		if (minified) {
			String css = "<link rel=\"stylesheet\" href=\"optimized/portal-style.css\"/>\n";
			content = content.replace("$styleSheets", css
					+ getCSSTagsFromDir(stylesDir));
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
					+ getCSSTagsFromDir(stylesDir));
			replaced = content.replace("$mainModule", "modules/main");
		}

		if (title != null) {
			replaced = replaced.replace("$title", title);
		} else {
			replaced = replaced.replace("$title", "");
		}
		return replaced;
	}

	/**
	 * Obtains the <code>&lt;link&gt;</code> tags, corresponding to all CSS
	 * files within the given directory.
	 * 
	 * @param stylesDir
	 *            The directory containing the CSS files.
	 * @return A string containing all <code>&lt;link&gt;</code> tags for all
	 *         CSS files.
	 */
	private String getCSSTagsFromDir(File stylesDir) {
		if (!stylesDir.exists() || !stylesDir.isDirectory()
				|| !stylesDir.canRead()) {
			return "";
		}

		File[] cssFiles = stylesDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".css");
			}
		});

		if (cssFiles == null) {
			return "";
		}

		String ret = "";
		for (File css : cssFiles) {
			String path = css.getPath();
			String stylesPath = stylesDir.getPath();
			if (path.startsWith(stylesPath)) {
				path = path.substring(stylesPath.length() + 1);
			}
			ret += "<link rel=\"stylesheet\" href=\"" + STYLES_DIR + "/" + path
					+ "\"/>\n";
		}

		return ret;
	}

	@Override
	public void destroy() {
		// do nothing
	}
}
