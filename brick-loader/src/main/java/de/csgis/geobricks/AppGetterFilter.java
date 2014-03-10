package de.csgis.geobricks;

import javax.inject.Singleton;

/**
 * Puts the name of the application as an attribute in the request
 * 
 * @author fergonco
 */
@Singleton
public class AppGetterFilter extends AbstractGetterFilter {
	@Override
	protected String getRoot() {
		return Geobricks.APPS_ROOT;
	}

	@Override
	protected String getAttributeName() {
		return Geobricks.APP_ID_HTTP_ATTRIBUTE;
	}
}