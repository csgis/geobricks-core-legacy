package de.csgis.geobricks.manual;

import java.util.Scanner;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.featureInfo.FeatureInfoPlugin;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.title.TitlePlugin;

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
		plugins.doPut(OLMapPlugin.NAME);
		plugins.doPut(TitlePlugin.NAME);
		plugins.doPut(LayoutPlugin.NAME);
		plugins.doPut(LayerListPlugin.NAME);
		plugins.doPut(AddressSearchPlugin.NAME);
		plugins.doPut(BaseLayerPlugin.NAME);
		plugins.doPut(FeatureInfoPlugin.NAME);
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
