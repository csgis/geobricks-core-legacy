package de.csgis.geobricks.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.Geobricks;

/**
 * Abstract filter to handle modifications on config.js. Subclasses only need to
 * implement the
 * {@link #modifyConfig(HttpServletRequest, HttpServletResponse, JSONObject)}
 * method and modify the given config.js response in JSON.
 * 
 * @author vicgonco
 * 
 */
public abstract class AbstractConfigFilter implements Filter {
	protected String confDir;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		confDir = filterConfig.getServletContext()
				.getAttribute(Geobricks.ATTR_CONF_DIR).toString();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		CharResponseWrapper wrapper = new CharResponseWrapper(resp);
		chain.doFilter(request, wrapper);

		String str = wrapper.toString();
		// Remove initial 'var require = '
		str = str.substring(str.indexOf('{'));

		JSONObject config = JSONObject.fromObject(str);
		modifyConfig(req, resp, config.getJSONObject("config"));

		resp.setContentType("application/javascript");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write("var require = " + config.toString());
	}

	@Override
	public void destroy() {
		// do nothing
	}

	/**
	 * Modifies the given config.js response object.
	 * 
	 * @param req
	 *            The servlet request, in case headers/cookies/etc need to be
	 *            handled.
	 * @param resp
	 *            The servlet response, in case headers/cookies/etc need to be
	 *            handled.
	 * @param config
	 *            the JSON object representing the config.js response.
	 * @throws IOException
	 *             if any I/O error occurs while modifying the config JSON
	 *             object.
	 */
	protected abstract void modifyConfig(HttpServletRequest req,
			HttpServletResponse resp, JSONObject config) throws IOException;
}