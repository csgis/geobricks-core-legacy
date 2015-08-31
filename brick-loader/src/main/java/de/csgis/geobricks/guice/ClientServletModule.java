package de.csgis.geobricks.guice;

import java.io.File;
import java.io.IOException;

import com.google.inject.servlet.ServletModule;

import de.csgis.geobricks.Path;
import de.csgis.geobricks.PluginDescriptorReader;
import de.csgis.geobricks.servlet.ClasspathResourceServlet;
import de.csgis.geobricks.servlet.Config;
import de.csgis.geobricks.servlet.ConfigServlet;
import de.csgis.geobricks.servlet.ExternalResourceServlet;
import de.csgis.geobricks.servlet.IndexHTMLContentProcessor;
import de.csgis.geobricks.servlet.IndexHTMLRedirectFilter;
import de.csgis.geobricks.servlet.MainModuleContentProcessor;
import de.csgis.geobricks.servlet.OutputFilter;

public class ClientServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		// Bind object instances
		PluginDescriptorReader reader = new PluginDescriptorReader();
		Config config;
		try {
			config = new Config(getServletContext(), reader);
		} catch (IOException e) {
			throw new RuntimeException("Cannot initialize Config object.", e);
		}

		bind(PluginDescriptorReader.class).toInstance(reader);
		bind(Config.class).toInstance(config);

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
		serveRegex(Path.root._static().all().path()).with(
				new ExternalResourceServlet(new File(config.getConfigDir(),
						"_static")));

		// Application index.html
		String indexPath = Path.root.file("index.html").path();
		filterRegex(indexPath, Path.root.path() + "/").through(
				IndexHTMLContentProcessor.class);
		serveRegex(indexPath, Path.root.path() + "/").with(
				new ClasspathResourceServlet("", "index.html"));
	}
}