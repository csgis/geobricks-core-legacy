package de.csgis.geobricks.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name = "apps")
public class Application {
	private String id;

	private Set<Plugin> plugins = new HashSet<Plugin>();

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "application")
	@PrivateOwned
	public Set<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(Set<Plugin> plugins) {
		this.plugins = plugins;
	}

	public void setPlugin(Plugin plugin) {
		Plugin toRemove = null;
		for (Plugin p : plugins) {
			if (p.getId().equals(plugin.getId())) {
				toRemove = p;
			}
		}

		if (toRemove != null) {
			plugins.remove(toRemove);
		}

		plugins.add(plugin);
	}

	public Plugin getPlugin(String id) {
		for (Plugin plugin : plugins) {
			if (plugin.getId().equals(id)) {
				return plugin;
			}
		}

		return null;
	}
}
