package de.csgis.geobricks.servlet.rest;

import javax.inject.Singleton;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.Path;

/**
 * Puts the name of the application as an attribute in the request
 * 
 * @author fergonco
 */
@Singleton
public class AppGetterFilter extends AbstractGetterFilter {
	@Override
	protected String getPreviousSegment() {
		return Path.APPS;
	}

	@Override
	protected String getAttributeName() {
		return Geobricks.APP_ID_HTTP_ATTRIBUTE;
	}
}