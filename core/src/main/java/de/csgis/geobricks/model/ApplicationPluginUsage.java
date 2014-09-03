package de.csgis.geobricks.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ApplicationPluginUsage {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@ManyToOne
	private Application application;

	private String pluginId;

	@Column(columnDefinition = "text")
	private String configuration;

	public ApplicationPluginUsage() {
	}

	public ApplicationPluginUsage(String pluginId, Application application) {
		this.pluginId = pluginId;
		this.application = application;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getPluginId() {
		return pluginId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
