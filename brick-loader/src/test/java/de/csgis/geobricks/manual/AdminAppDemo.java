package de.csgis.geobricks.manual;

import java.util.Scanner;

import org.apache.http.message.BasicNameValuePair;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.admin.AdminPluginDescriptor;
import de.csgis.geobricks.divstack.DivStackPluginDescriptor;
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

		String post = "[{ eventName : 'ui-selectable-list:create', div: 'apps_list', parentDiv : 'layout-center', url :'/geobricks/rest/apps'},"
				+ "{ eventName : 'ui-selectable-list:create', div : 'plugins_list', parentDiv : 'layout-center'},"
				+ "{ eventName : 'ui-text-area', div : 'plugin_configuration', parentDiv : 'layout-center'}"
				+ "]";
		String pre = "[{ eventName : 'ui-html', " + "div : 'title', "
				+ "parentDiv : 'layout-header', "
				+ "html: 'Geobricks admin application'}]";
		plugins.doPut(UIPluginDescriptor.ID, new BasicNameValuePair(
				"configuration", "ui : { post: " + post + ", " + "pre : " + pre
						+ "}"));
		plugins.doPut(LayoutPlugin.ID);
		plugins.doPut(AdminPluginDescriptor.ID);
		plugins.doPut(DivStackPluginDescriptor.ID);

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}
}
