package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.servlet.HTTPCodeServletException;

/**
 * Serves static content from the resources space, starting to look in its own
 * package and in the folder passed to the constructor.
 * 
 * @author fergonco
 */
public class StaticServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String folder;
	private String resource;
	private ContentProcessor processor;
	private RequestPreprocessor preprocessor;

	public StaticServlet(String folder) {
		this(folder, null, null, null);
	}

	public StaticServlet(String folder, ContentProcessor processor) {
		this(folder, null, processor, null);
	}

	public StaticServlet(String folder, String resource,
			ContentProcessor processor, RequestPreprocessor preprocessor) {
		this.folder = folder != null ? folder : "";
		this.resource = resource;
		this.processor = processor;
		this.preprocessor = preprocessor;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (preprocessor != null) {
			preprocessor.preprocess(req);
		}

		String requestURI = req.getRequestURI();

		String resource;
		if (this.resource != null) {
			resource = this.resource;
		} else {
			resource = requestURI.substring(requestURI.indexOf(folder)
					+ folder.length() + 1);
		}

		InputStream resourceStream = Geobricks.root.file(folder).file(resource)
				.getResourceAsStream();

		if (resourceStream == null) {
			throw new HTTPCodeServletException("Cannot find resource:"
					+ resource, HttpServletResponse.SC_NOT_FOUND);
		}

		try {
			if (processor == null) {
				IOUtils.copy(resourceStream, resp.getOutputStream());
			} else {
				String content = IOUtils.toString(resourceStream);
				resp.getOutputStream().print(processor.process(content));
			}
		} finally {
			resourceStream.close();
		}
	}

	public static interface ContentProcessor {
		String process(String content);
	}

	public static interface RequestPreprocessor {
		void preprocess(HttpServletRequest request) throws ServletException,
				IOException;
	}
}
