package de.csgis.geobricks.guice;

import java.util.Map;

import javax.servlet.Filter;

import com.google.inject.servlet.ServletModule;

import de.csgis.geobricks.CustomConfigurator;
import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.Path;
import de.csgis.geobricks.servlet.OutputFilter;
import de.csgis.geobricks.servlet.client.ConfigServlet;
import de.csgis.geobricks.servlet.client.IndexHTMLRedirectFilter;
import de.csgis.geobricks.servlet.client.IndexHTMLContentProcessor;
import de.csgis.geobricks.servlet.client.MainModuleContentProcessor;
import de.csgis.geobricks.servlet.client.StaticBinaryServlet;
import de.csgis.geobricks.servlet.client.StaticTextServlet;

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
				new StaticTextServlet("modules"));

		// config.js
		serveRegex(Path.root.file("config.js").path())
				.with(ConfigServlet.class);

		// Static content
		serveRegex(Path.root.modules().all().path()).with(
				new StaticBinaryServlet("modules"));
		serveRegex(Path.root.styles().all().path()).with(
				new StaticBinaryServlet("styles"));
		serveRegex(Path.root.jslib().all().path()).with(
				new StaticBinaryServlet("jslib"));
		serveRegex(Path.root.images().all().path()).with(
				new StaticBinaryServlet("images"));

		// Application index.html
		String indexPath = Path.root.file("index.html").path();
		filterRegex(indexPath, Path.root.path() + "/").through(
				IndexHTMLContentProcessor.class);
		serveRegex(indexPath, Path.root.path() + "/").with(
				new StaticTextServlet("", "index.html"));

		CustomConfigurator[] configurators = (CustomConfigurator[]) getServletContext()
				.getAttribute(Geobricks.ATTR_CONFIGURATORS);
		for (CustomConfigurator configurator : configurators) {
			Map<String, Class<? extends Filter>> filters = configurator
					.getFilters();
			if (filters != null) {
				for (String regex : filters.keySet()) {
					filterRegex(regex).through(filters.get(regex));
				}
			}
		}
	}
}