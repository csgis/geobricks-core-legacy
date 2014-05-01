package de.csgis.geobricks.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "plugin_configs")
public class Plugin {
	@EmbeddedId
	private PrimaryKey primaryKey;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("appId")
	@JoinColumn(name = "app_id", nullable = false)
	private Application application;

	@Column(name = "config")
	private String configuration;

	public Plugin() {
	}

	public Plugin(String pluginId, Application application) {
		this.primaryKey = new PrimaryKey(pluginId, application.getId());
		this.application = application;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getId() {
		return primaryKey.pluginId;
	}

	@Embeddable
	public static class PrimaryKey implements Serializable {
		private static final long serialVersionUID = 1L;

		@Column(name = "app_id", nullable = false, updatable = false)
		private String appId;

		@Column(name = "plugin_id", nullable = false, updatable = false)
		private String pluginId;

		public PrimaryKey() {
		}

		public PrimaryKey(String id, String applicationId) {
			this.appId = applicationId;
			this.pluginId = id;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PrimaryKey)) {
				return false;
			}

			PrimaryKey pk = (PrimaryKey) obj;
			return pk.appId.equals(appId) && pk.pluginId.equals(pluginId);
		}

		@Override
		public int hashCode() {
			return appId.hashCode() + pluginId.hashCode();
		}
	}
}
