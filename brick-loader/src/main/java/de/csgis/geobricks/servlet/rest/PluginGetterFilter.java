package de.csgis.geobricks.servlet.rest;

import javax.inject.Singleton;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.Path;

/**
 * Puts the name of the plugin as an attribute in the request
 * 
 * @author vicgonco
 */
@Singleton
public class PluginGetterFilter extends AbstractGetterFilter {
	@Override
	protected String getPreviousSegment() {
		return Path.PLUGINS;
	}

	@Override
	protected String getAttributeName() {
		return Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE;
	}
}
