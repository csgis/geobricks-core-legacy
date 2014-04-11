package de.csgis.geobricks.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Plugin {
	@Id
	private String id;

	private String configuration;

	public Plugin() {
	}

	public Plugin(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
}
