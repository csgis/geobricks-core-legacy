package de.csgis.geobricks.servlet.client;

import javax.inject.Inject;

import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.servlet.client.StaticServlet.ContentProcessor;

public class IndexContentProcessor implements ContentProcessor {
	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	public String process(String content) {
		String[] styleSheets = pluginRegistry.getStyleSheets();
		StringBuilder str = new StringBuilder();
		for (String styleSheet : styleSheets) {
			str.append("<link rel=\"stylesheet\" href=\"" + styleSheet
					+ "\"/>\n");
		}

		return content.replace("$styleSheets", str.toString());
	}
}
