package de.csgis.geobricks.functional;

import de.csgis.geobricks.GeobricksRuntimeModule;

public class GeobricksTestModule extends GeobricksRuntimeModule {

	@Override
	protected String getPersistenceUnitName() {
		return "test";
	}
}
