package de.csgis.geobricks.manual;

import java.util.Scanner;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.admin.AdminPluginDescriptor;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.ui.UIPluginDescriptor;

public class AdminAppDemo {
	private static final String APP = "admin";

	public static void main(String[] args) throws Exception {
		ServerManager serverManager = new ServerManager();
		serverManager.start("geobricks");
		RestPoint apps = new RestPoint(serverManager, Geobricks.root.rest()
				.apps().path());
		apps.doPut(APP);
		RestPoint plugins = new RestPoint(serverManager, Geobricks.root.rest()
				.app(APP).plugins().path());

		plugins.doPut(
				UIPluginDescriptor.ID,
				"{ui : ["//
						+ "{ eventName : 'ui-container:create', div : 'apps_panel', parentDiv : 'layout-center'},"
						+ "{ eventName : 'ui-selectable-list:create', div: 'apps_list', parentDiv : 'apps_panel', url :'/geobricks/rest/apps'},"

						+ "{ eventName : 'ui-container:create', div : 'plugins_panel', parentDiv : 'layout-center'},"
						+ "{ eventName : 'ui-selectable-list:create', div : 'plugins_list', parentDiv : 'plugins_panel'},"

						+ "{ eventName : 'ui-container:create', div : 'configuration_panel', parentDiv : 'layout-center'},"
						+ "{ eventName : 'ui-text-area-field:create', div : 'plugin_configuration', parentDiv : 'configuration_panel', label : 'Configuration: ', cols : '100', rows : '5'},"
						+ "{ eventName : 'ui-button:create', div : 'btnAccept', parentDiv : 'configuration_panel', label : 'Accept'},"
						+ "{ eventName : 'ui-form-collector:extend', button: 'btnAccept', divs : ['plugin_configuration']},"
						+ "{ eventName : 'ui-button:create', div : 'btnCancel', parentDiv : 'configuration_panel', label : 'Cancel', sendEventName : 'ui-show', sendEventMessage : 'plugins_panel'},"

						+ "{ eventName : 'ui-html', div : 'title', parentDiv : 'layout-header', html: 'Geobricks admin application'},"

						+ "{ eventName : 'ui-divstack:create', divs : ['apps_panel', 'plugins_panel', 'configuration_panel' ] },"
						+ "]}");
		plugins.doPut(LayoutPlugin.ID);
		plugins.doPut(AdminPluginDescriptor.ID);

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}
}
