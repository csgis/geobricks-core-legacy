package de.csgis.geobricks.guice;

import com.google.inject.servlet.ServletModule;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.servlet.GetApplicationInstanceFilter;
import de.csgis.geobricks.servlet.client.ConfigServlet;
import de.csgis.geobricks.servlet.client.GetApplicationIdFilter;
import de.csgis.geobricks.servlet.client.IndexHTMLRedirectFilter;
import de.csgis.geobricks.servlet.client.IndexReplaceCSSFilter;
import de.csgis.geobricks.servlet.client.MainModuleContentProcessor;
import de.csgis.geobricks.servlet.client.StaticBinaryServlet;
import de.csgis.geobricks.servlet.client.StaticTextServlet;

public class ClientServletModule extends ServletModule {
	@Override
	protected void configureServlets() {
		// Redirect client app requests to index.html
		filterRegex(Geobricks.root.path()).through(
				IndexHTMLRedirectFilter.class);

		// Common application id getter for client requests
		filterRegex(Geobricks.root.all().path()).through(
				GetApplicationIdFilter.class);

		/*
		 * Client Requests
		 */
		// main.js module
		filterRegex(Geobricks.root.module("main.js").path()).through(
				MainModuleContentProcessor.class);
		serveRegex(Geobricks.root.module("main.js").path()).with(
				new StaticTextServlet("modules"));

		// Config.js
		serveRegex(Geobricks.root.file("config.js").path()).with(
				ConfigServlet.class);

		// Static content
		serveRegex(Geobricks.root.modules().all().path()).with(
				new StaticBinaryServlet("modules"));
		serveRegex(Geobricks.root.jslib().all().path()).with(
				new StaticBinaryServlet("jslib"));
		serveRegex(Geobricks.root.images().all().path()).with(
				new StaticBinaryServlet("images"));

		// Application index.html
		String indexPath = Geobricks.root.file("index.html").path();
		filterRegex(indexPath, Geobricks.root.path() + "/").through(
				IndexReplaceCSSFilter.class);
		serveRegex(indexPath, Geobricks.root.path() + "/").with(
				new StaticTextServlet("", "index.html"));

		// Application instance getter
		filterRegex(Geobricks.root.all().path()).through(
				GetApplicationInstanceFilter.class);
	}
}