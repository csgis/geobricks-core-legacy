package de.csgis.geobricks.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Application {
	@Id
	private String id;

	private Set<Plugin> plugins = new HashSet<Plugin>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "app_plugins", 
		joinColumns = { @JoinColumn(name = "app_id") }, 
		inverseJoinColumns = { @JoinColumn(name = "plugin_id") })
	public Set<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(Set<Plugin> plugins) {
		this.plugins = plugins;
	}
}
