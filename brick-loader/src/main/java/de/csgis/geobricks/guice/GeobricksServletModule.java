package de.csgis.geobricks.guice;

import javax.inject.Inject;

import com.google.inject.servlet.ServletModule;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.servlet.FilterDescriptor;
import de.csgis.geobricks.servlet.ServletDescriptor;
import de.csgis.geobricks.servlet.client.ConfigServlet;
import de.csgis.geobricks.servlet.client.GetApplicationInstanceFilter;
import de.csgis.geobricks.servlet.client.IndexHTMLRedirectFilter;
import de.csgis.geobricks.servlet.client.IndexReplaceCSSFilter;
import de.csgis.geobricks.servlet.client.MainModuleContentProcessor;
import de.csgis.geobricks.servlet.client.StaticBinaryServlet;
import de.csgis.geobricks.servlet.client.StaticTextServlet;
import de.csgis.geobricks.servlet.rest.AppGetterFilter;
import de.csgis.geobricks.servlet.rest.ApplicationListServlet;
import de.csgis.geobricks.servlet.rest.ApplicationsServlet;
import de.csgis.geobricks.servlet.rest.AvailablePluginsListServlet;
import de.csgis.geobricks.servlet.rest.OutputFilter;
import de.csgis.geobricks.servlet.rest.PluginGetterFilter;
import de.csgis.geobricks.servlet.rest.PluginListServlet;
import de.csgis.geobricks.servlet.rest.PluginsServlet;

public class GeobricksServletModule extends ServletModule {
	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	protected void configureServlets() {
		// Redirect client app requests to index.html
		filterRegex(Geobricks.root.apps().any().path()).through(
				IndexHTMLRedirectFilter.class);

		// Common application id getter for REST API and client requests
		filterRegex(Geobricks.root.apps().all().path(),
				Geobricks.root.rest().apps().all().path()).through(
				AppGetterFilter.class);

		// Common output filter for REST APPI and client requests
		filterRegex(Geobricks.root.rest().all().path()).through(
				OutputFilter.class);

		/*
		 * Admin REST API. Note that order matters. If more than one regex
		 * matches the same path, the first declared servlet will take care.
		 */
		// Plugins
		serveRegex(Geobricks.root.rest().apps().any().plugins().path()).with(
				PluginListServlet.class);
		serveRegex(Geobricks.root.rest().apps().any().plugins().any().path())
				.with(PluginsServlet.class);

		// Available plugin list
		serve(Geobricks.root.rest().plugins().path()).with(
				AvailablePluginsListServlet.class);

		// Apps
		serveRegex(Geobricks.root.rest().apps().path()).with(
				ApplicationListServlet.class);
		serveRegex(Geobricks.root.rest().apps().any().path()).with(
				ApplicationsServlet.class);

		// Plugin getter filter
		filterRegex(Geobricks.root.rest().apps().any().plugins().any().path())
				.through(PluginGetterFilter.class);

		/*
		 * Client Requests
		 */
		// main.js module
		filterRegex(Geobricks.root.apps().any().module("main.js").path())
				.through(MainModuleContentProcessor.class);
		serveRegex(Geobricks.root.apps().any().module("main.js").path()).with(
				new StaticTextServlet("modules"));

		// Config.js
		serveRegex(Geobricks.root.apps().any().file("config.js").path()).with(
				ConfigServlet.class);

		// Static content
		serveRegex(Geobricks.root.apps().any().modules().all().path()).with(
				new StaticBinaryServlet("modules"));
		serveRegex(Geobricks.root.apps().any().jslib().all().path()).with(
				new StaticBinaryServlet("jslib"));
		serveRegex(Geobricks.root.apps().any().images().all().path()).with(
				new StaticBinaryServlet("images"));

		// Application index.html
		String indexPath = Geobricks.root.apps().any().file("index.html")
				.path();
		filterRegex(indexPath, Geobricks.root.apps().any().path() + "/")
				.through(IndexReplaceCSSFilter.class);
		serveRegex(indexPath, Geobricks.root.apps().any().path() + "/").with(
				new StaticTextServlet("", "index.html"));

		// Application instance getter
		filterRegex(Geobricks.root.apps().all().path()).through(
				GetApplicationInstanceFilter.class);

		/*
		 * PLUGINS
		 */
		// Filters from plugins
		for (FilterDescriptor filter : pluginRegistry.getFilters()) {
			filterRegex(filter.getRegex(), filter.getRegexes()).through(
					filter.getServletClass());
		}

		// Servlets from plugins
		for (ServletDescriptor servlet : pluginRegistry.getServlets()) {
			serveRegex(servlet.getRegex(), servlet.getRegexes()).with(
					servlet.getServletClass());
		}
	}
}
