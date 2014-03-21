package de.csgis.geobricks.servlet.client;

import javax.inject.Inject;

import de.csgis.geobricks.NonRequireDependency;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.servlet.client.StaticServlet.ContentProcessor;

public class MainModuleContentProcessor implements ContentProcessor {

	@Inject
	private PluginRegistry pluginRegistry;

	@Override
	public String process(String content) {
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

		// remove last comma
		str.setLength(str.lastIndexOf(","));
		return content.replace("$nonRequireJSDependencies", str.toString());
	}
}
