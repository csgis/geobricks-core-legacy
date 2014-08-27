package de.csgis.geobricks.functional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import de.csgis.geobricks.NonRequireDependency;
import de.csgis.geobricks.PluginDescriptor;

public class MockPlugin implements PluginDescriptor {
	public static final String ID = "mock";
	public static final String CSS = "mock.css";

	@Override
	public NonRequireDependency[] getNonRequireDependencies() {
		return new NonRequireDependency[0];
	}

	@Override
	public String getDefaultConfiguration() {
		return null;
	}

	@Override
	public String[] getStyleSheets() {
		return new String[] { CSS };
	}

	@Override
	public String getName() {
		return ID;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String[] getModules() {
		return null;
	}

	@Override
	public void config(HttpServletRequest request,
			HttpServletResponse response, JSONObject staticConfig,
			String confDir) {
	}
}
