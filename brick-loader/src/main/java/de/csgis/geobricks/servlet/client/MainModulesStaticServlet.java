package de.csgis.geobricks.servlet.client;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.NonRequireDependency;
import de.csgis.geobricks.PluginRegistry;

@Singleton
public class MainModulesStaticServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream resourceStream = Geobricks.root.module("main.js")
				.getResourceAsStream();
		String main;
		try {
			main = IOUtils.toString(resourceStream);
		} finally {
			resourceStream.close();
		}
		NonRequireDependency[] dependencies = pluginRegistry
				.getNonRequireDependencies();
		StringBuilder str = new StringBuilder();
		for (NonRequireDependency dep : dependencies) {
			str.append('"').append(dep.getName()).append('"');
			str.append(':');
			// Paths are relative to modules so we go up one level
			str.append("\"../").append(dep.getPath()).append('"');
			str.append(",\n\t\t");
		}
		// remove last coma
		str.setLength(str.lastIndexOf(","));
		main = main.replace("$nonRequireJSDependencies", str.toString());
		resp.getOutputStream().print(main);
	}

}
