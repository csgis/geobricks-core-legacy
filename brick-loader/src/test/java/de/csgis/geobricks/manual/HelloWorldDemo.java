package de.csgis.geobricks.manual;

import java.util.Scanner;

import org.apache.http.message.BasicNameValuePair;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.admin.AdminPluginDescriptor;
import de.csgis.geobricks.divstack.DivStackPluginDescriptor;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.title.TitlePlugin;
import de.csgis.geobricks.ui.UIPluginDescriptor;

public class HelloWorldDemo {

	public static void main(String[] args) throws Exception {
		ServerManager serverManager = new ServerManager();
		serverManager.start("geobricks");
		RestPoint apps = new RestPoint(serverManager, Geobricks.root.rest()
				.apps().path());
		apps.doPut("stadtplan");
		RestPoint plugins = new RestPoint(serverManager, Geobricks.root.rest()
				.app("stadtplan").plugins().path());
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to add hello.js plugin");
		// sc.nextLine();
		// plugins.doPut(OLMapPlugin.NAME);
		plugins.doPut(
				TitlePlugin.NAME,
				new BasicNameValuePair(
						"configuration",
						"\"title\":{\"div\":\"layout-header\", \"text\":\"Geobricks admin application\"}"));
		plugins.doPut(LayoutPlugin.NAME);
		plugins.doPut(new AdminPluginDescriptor().getName());
		plugins.doPut(new UIPluginDescriptor().getName());
		plugins.doPut(new DivStackPluginDescriptor().getName());
		// plugins.doPut(LayerListPlugin.NAME);
		// plugins.doPut(AddressSearchPlugin.NAME);
		// plugins.doPut(BaseLayerPlugin.NAME);
		System.out.println("plugin added");
		System.out.println("Enter to remove the plugin");
		sc.nextLine();

		plugins.doDelete("address-search");
		System.out.println("plugin removed");
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}

}
