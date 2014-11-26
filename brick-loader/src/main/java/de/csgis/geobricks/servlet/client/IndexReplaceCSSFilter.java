package de.csgis.geobricks.servlet.client;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginDescriptor;

@Singleton
public class IndexReplaceCSSFilter implements Filter {
	public static final String STYLES_DIR = "styles";

	private PluginDescriptor[] descriptors;

	private File stylesDir;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext context = filterConfig.getServletContext();

		String path = context.getRealPath("/" + STYLES_DIR);
		if (path != null) {
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory() && dir.canRead()) {
				stylesDir = dir;
			}
		}

		descriptors = (PluginDescriptor[]) context
				.getAttribute(Geobricks.ATTR_PLUGINS_DESC);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CharResponseWrapper wrapper = new CharResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter(request, wrapper);
		response.getWriter().print(
				process(wrapper.toString(), descriptors, stylesDir));
	}

	public String process(String content, PluginDescriptor[] descriptors,
			File stylesDir) {
		StringBuilder str = new StringBuilder();
		for (PluginDescriptor descriptor : descriptors) {
			for (String style : descriptor.getStyles()) {
				str.append("<link rel=\"stylesheet\" href=\"" + style
						+ "\"/>\n");
			}
		}

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
					str.append("<link rel=\"stylesheet\" href=\"" + STYLES_DIR
							+ "/" + path + "\"/>\n");
				}
			}
		}

		return content.replace("$styleSheets", str.toString());
	}

	@Override
	public void destroy() {
	}
}
