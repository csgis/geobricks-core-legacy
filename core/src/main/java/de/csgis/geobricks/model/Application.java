package de.csgis.geobricks.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
public class Application {
	private String id;

	private Set<ApplicationPluginUsage> plugins = new HashSet<ApplicationPluginUsage>();

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "application")
	@PrivateOwned
	public Set<ApplicationPluginUsage> getPlugins() {
		return plugins;
	}

	public void setPlugins(Set<ApplicationPluginUsage> plugins) {
		this.plugins = plugins;
	}

	public void putPlugin(ApplicationPluginUsage plugin) {
		ApplicationPluginUsage toRemove = null;
		for (ApplicationPluginUsage p : plugins) {
			if (p.getPluginId().equals(plugin.getPluginId())) {
				toRemove = p;
			}
		}

		if (toRemove != null) {
			plugins.remove(toRemove);
		}

		plugins.add(plugin);
	}

	public ApplicationPluginUsage getPlugin(String id) {
		for (ApplicationPluginUsage plugin : plugins) {
			if (plugin.getPluginId().equals(id)) {
				return plugin;
			}
		}

		return null;
	}
}
