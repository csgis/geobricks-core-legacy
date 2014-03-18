package de.csgis.geobricks.functional;

import de.csgis.geobricks.guice.RuntimeModule;

public class GeobricksTestModule extends RuntimeModule {

	@Override
	protected String getPersistenceUnitName() {
		return "test";
	}
}
