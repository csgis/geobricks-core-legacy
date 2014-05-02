package de.csgis.geobricks.manual;

import java.util.Scanner;

import org.apache.http.message.BasicNameValuePair;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.featureInfo.FeatureInfoPlugin;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.ui.UIPluginDescriptor;

public class BasicViewerDemo {
	private static final String APP = "viewer";

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
				new BasicNameValuePair(
						"configuration",
						"ui : { post: ["
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'mybutton', css : 'toolbar-button', image : 'images/close.png', sendEventName : 'map-control-measure-area' }"
								+ "], "
								+ "pre : ["
								+ "{ eventName : 'ui-toolbar:create', div : 'mytoolbar', parentDiv : 'layout-center'},"
								+ "{ eventName : 'ui-accordion:create', div : 'layers-accordion', parentDiv : 'layout-side'},"
								+ "{ eventName : 'ui-html', div : 'title', parentDiv : 'layout-header', html: 'Basic viewer'}"
								+ "]}"));

		plugins.doPut(OLMapPlugin.ID);
		plugins.doPut(LayoutPlugin.ID);
		plugins.doPut(LayerListPlugin.ID);
		plugins.doPut(AddressSearchPlugin.ID);
		plugins.doPut(BaseLayerPlugin.ID);
		plugins.doPut(FeatureInfoPlugin.ID);

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}
}
