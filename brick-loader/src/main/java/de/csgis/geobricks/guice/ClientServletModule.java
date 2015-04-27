package de.csgis.geobricks.guice;

import java.io.File;

import com.google.inject.servlet.ServletModule;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.Path;
import de.csgis.geobricks.servlet.OutputFilter;
import de.csgis.geobricks.servlet.client.ClasspathResourceServlet;
import de.csgis.geobricks.servlet.client.ConfigServlet;
import de.csgis.geobricks.servlet.client.ExternalResourceServlet;
import de.csgis.geobricks.servlet.client.IndexHTMLContentProcessor;
import de.csgis.geobricks.servlet.client.IndexHTMLRedirectFilter;
import de.csgis.geobricks.servlet.client.MainModuleContentProcessor;

public class ClientServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		// Output filter
		filterRegex(Path.root.all().path()).through(OutputFilter.class);

		// Redirect client app requests to index.html
		filterRegex(Path.root.path()).through(IndexHTMLRedirectFilter.class);

		// main.js module
		filterRegex(Path.root.module("main.js").path()).through(
				MainModuleContentProcessor.class);
		serveRegex(Path.root.module("main.js").path()).with(
				new ClasspathResourceServlet("modules"));

		// config.js
		serveRegex(Path.root.file("config.js").path())
				.with(ConfigServlet.class);

		// Static content
		serveRegex(Path.root.modules().all().path()).with(
				new ClasspathResourceServlet("modules"));
		serveRegex(Path.root.styles().all().path()).with(
				new ClasspathResourceServlet("styles"));
		serveRegex(Path.root.jslib().all().path()).with(
				new ClasspathResourceServlet("jslib"));
		serveRegex(Path.root.images().all().path()).with(
				new ClasspathResourceServlet("images"));
		serveRegex(Path.root.theme().all().path()).with(
				new ClasspathResourceServlet("theme"));
		Object confDir = getServletContext().getAttribute(
				Geobricks.ATTR_CONF_DIR);
		if (confDir != null) {
			serveRegex(Path.root._static().all().path()).with(
					new ExternalResourceServlet(new File(confDir.toString())));
		}

		// Application index.html
		String indexPath = Path.root.file("index.html").path();
		filterRegex(indexPath, Path.root.path() + "/").through(
				IndexHTMLContentProcessor.class);
		serveRegex(indexPath, Path.root.path() + "/").with(
				new ClasspathResourceServlet("", "index.html"));
	}
}