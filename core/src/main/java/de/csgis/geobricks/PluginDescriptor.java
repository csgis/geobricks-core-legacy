package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

public class PluginDescriptor {
	private String id;
	private JSONObject defaultConfiguration;
	private Set<String> modules = new HashSet<String>();
	private List<String> styles = new ArrayList<String>();
	private Map<String, String> dependencies = new HashMap<String, String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getModules() {
		return modules;
	}

	public List<String> getStyles() {
		return styles;
	}

	public Map<String, String> getDependencies() {
		return dependencies;
	}

	public JSONObject getDefaultConfiguration() {
		return defaultConfiguration;
	}

	public void setDefaultConfiguration(JSONObject defaultConfiguration) {
		this.defaultConfiguration = defaultConfiguration;
	}
}
