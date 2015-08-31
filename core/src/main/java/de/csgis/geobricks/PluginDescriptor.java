package de.csgis.geobricks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

/**
 * Bean class for describing a plugin.
 * 
 * @author vicgonco
 */
public class PluginDescriptor {
	private String id;
	private JSONObject defaultConfiguration;
	private Set<String> modules = new HashSet<String>();
	private List<String> styles = new ArrayList<String>();
	private JSONObject requirePaths = new JSONObject();
	private JSONObject requireShim = new JSONObject();
	private Set<PluginDescriptor> dependencies = new HashSet<PluginDescriptor>();

	public PluginDescriptor() {
	}

	public PluginDescriptor(String id) {
		this.id = id;
	}

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

	public JSONObject getRequirePaths() {
		return requirePaths;
	}

	public void setRequirePaths(JSONObject requirePaths) {
		this.requirePaths = requirePaths;
	}

	public JSONObject getRequireShim() {
		return requireShim;
	}

	public void setRequireShim(JSONObject requireShim) {
		this.requireShim = requireShim;
	}

	public JSONObject getDefaultConfiguration() {
		return defaultConfiguration;
	}

	public void setDefaultConfiguration(JSONObject defaultConfiguration) {
		this.defaultConfiguration = defaultConfiguration;
	}

	public Set<PluginDescriptor> getDependencies() {
		return dependencies;
	}
}
