package de.csgis.geobricks.functional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.csgis.geobricks.guice.RuntimeModule;

public class TestModule extends RuntimeModule {

	@Override
	protected String getPersistenceUnitName() {
		return "test";
	}

	@Override
	protected Map<String, String> getConnectionProperties() throws IOException {
		return new HashMap<String, String>();
	}
}
