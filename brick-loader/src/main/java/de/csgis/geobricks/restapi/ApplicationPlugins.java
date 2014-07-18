package de.csgis.geobricks.restapi;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.model.Application;
import de.csgis.geobricks.model.ApplicationPluginUsage;

public class ApplicationPlugins {

	@Inject
	private EntityManager entityManager;

	@Inject
	private PluginRegistry registry;

	@Inject
	private Applications applications;

	private Application app;

	void setApplication(String appId) throws ApplicationNotFoundException {
		app = applications.get(appId);
	}

	public ApplicationPluginUsage get(String pluginId)
			throws PluginNotFoundException {
		ApplicationPluginUsage plugin = app.getPlugin(pluginId);
		if (plugin == null) {
			throw new PluginNotFoundException(app.getId(), pluginId);
		} else {
			return plugin;
		}
	}

	public void delete(String pluginId) throws PluginNotFoundException {
		ApplicationPluginUsage plugin = get(pluginId);

		app.getPlugins().remove(plugin);

		entityManager.getTransaction().begin();
		entityManager.merge(app);
		entityManager.remove(plugin);
		entityManager.getTransaction().commit();
	}

	public void put(String pluginId) throws PluginNotAvailableException,
			InvalidPluginConfiguration {
		put(pluginId, null);
	}

	public void put(String pluginId, String configurationString)
			throws PluginNotAvailableException, InvalidPluginConfiguration {

		PluginDescriptor pluginDescriptor = registry.getPlugin(pluginId);
		if (pluginDescriptor == null) {
			throw new PluginNotAvailableException("Plugin does not exist: "
					+ pluginId);
		}

		ApplicationPluginUsage plugin = new ApplicationPluginUsage(pluginId,
				app);
		if (configurationString != null) {
			try {
				JSONSerializer.toJSON(configurationString);
			} catch (JSONException e) {
				throw new InvalidPluginConfiguration(pluginId, e);
			}
			plugin.setConfiguration(configurationString);
		} else {
			plugin.setConfiguration(pluginDescriptor.getDefaultConfiguration());
		}

		app.putPlugin(plugin);

		entityManager.getTransaction().begin();
		entityManager.merge(plugin);
		entityManager.merge(app);
		entityManager.getTransaction().commit();

	}

}
