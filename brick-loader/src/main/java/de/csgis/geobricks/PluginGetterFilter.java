package de.csgis.geobricks;

import javax.inject.Singleton;

/**
 * Puts the name of the plugin as an attribute in the request
 * 
 * @author vicgonco
 */
@Singleton
public class PluginGetterFilter extends AbstractGetterFilter {
	@Override
	protected String getRoot() {
		return Geobricks.PLUGINS_ROOT;
	}

	@Override
	protected String getAttributeName() {
		return Geobricks.PLUGIN_NAME_HTTP_ATTRIBUTE;
	}
}
