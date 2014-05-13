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
import de.csgis.geobricks.login.LoginPlugin;
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
						"ui : ["
								+ "{ eventName : 'ui-accordion:create', div : 'layers-accordion', parentDiv : 'layout-side'},"
								+ "{ eventName : 'ui-toolbar:create', div : 'mytoolbar', parentDiv : 'layout-center'},"
								+ "{ eventName : 'ui-html', div : 'title', parentDiv : 'layout-header', html: 'Basic viewer'},"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnNavigate', css : 'toolbar-button', image : 'images/navigate.png', sendEventName : 'map-control-navigate' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnMeasureDistance', css : 'toolbar-button', image : 'images/measure-distance.png', sendEventName : 'map-control-measure-distance' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnMeasureArea', css : 'toolbar-button', image : 'images/measure-area.png', sendEventName : 'map-control-measure-area' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnPreviousExtent', css : 'toolbar-button', image : 'images/zoom-previous.png', sendEventName : 'extent-history:back', enableEventName : 'extent-history:back-enabled', disableEventName : 'extent-history:back-disabled' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnFullExtent', css : 'toolbar-button', image : 'images/full-extent.png', sendEventName : 'initial-zoom' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnNextExtent', css : 'toolbar-button', image : 'images/zoom-next.png', sendEventName : 'extent-history:forward', enableEventName : 'extent-history:forward-enabled', disableEventName : 'extent-history:forward-disabled' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnCoordinateSearch', css : 'toolbar-button', image : 'images/coordinate-search.png', sendEventName : 'ui-show', sendEventMessage : 'myform' },"

								+ "{ eventName : 'ui-dialog:create', parentDiv : 'layout-center', div : 'myform', css : 'coordinate-dialog', title : 'Coordinate Search', closeButton : true, visible : false },"
								+ "{ eventName : 'ui-choice-field:create', parentDiv : 'myform', div : 'myform-crsselection', css : 'coordinate-crs', label : 'CRS: ', values: [ 'EPSG:4326', 'EPSG:25830' ] },"
								+ "{ eventName : 'ui-numeric-field:create', parentDiv : 'myform', div : 'myform-lat', css : 'coordinate-input', label : 'Lat: '},"
								+ "{ eventName : 'ui-numeric-field:create', parentDiv : 'myform', div : 'myform-lon', css : 'coordinate-input', label : 'Lon: '},"
								+ "{ eventName : 'ui-button:create', parentDiv : 'myform', div : 'btnGoToCoordinate', css : 'coordinate-button', text : 'Go!'},"
								+ "{ eventName : 'ui-form-collector:extend', button: 'btnGoToCoordinate', divs : ['myform-crsselection', 'myform-lat', 'myform-lon'], names : ['crs', 'lat', 'lon'], sendEventName : 'zoomTo' },"

								+ "{ eventName : 'ui-login:create', div : 'login', parentDiv : 'layout-header', css : 'login' }"
								+ "]"));

		String mapConfig = "center: { lat : 49, lon : 11.2, zoomLevel : 7},"
				+ "olmap : { div : 'layout-center' },"
				+ "'pan-zoom-bar': { mapOffsetX : 10, mapOffsetY : 35 },"
				+ "'load-layers' : [" //
				+ "{ eventName : 'add-layer-group', name : 'Kartendaten', id : 'kartendaten', visibility : true },"
				+ "{ eventName : 'add-layer-group', name : 'Schutzgebiete', id : 'schutzgebiete', visibility : true },"
				// Base layers
				+ "{ eventName : 'add-layer', id : 'osm', isBaseLayer : true, url : 'http://ows.terrestris.de/osm/service', label : 'OpenStreetMap', layerName : 'OSM-WMS', groupId : 'main' },"
				+ "{ eventName : 'add-layer', id : 'gmaps_roadmap', isBaseLayer : true, type : 'Google', label : 'Google Maps (Karte)', layerName : 'Google Streets', groupId : 'main' }, "
				+ "{ eventName : 'add-layer', id : 'gmaps_hybrid', isBaseLayer : true, type : 'Google', label : 'Google Maps (Satellit)', layerName : 'Google Hybrid', groupId : 'main', googleType : 'hybrid' }, "
				// Kartendaten
				+ "{ eventName : 'add-layer', id : 'flurkarte', url : 'http://80.237.188.118/proxy/goto_vfs_map.cgi', layerName : 'flkgrenzenbayern', groupId : 'kartendaten', label : 'Flurkartenschnitt 1:5.000', visible : false }, "
				// Schutzgebiete
				+ "{ eventName : 'add-layer', id : 'naturparke', url : 'http://80.237.188.118/proxy/goto_vfs_map.cgi', layerName : 'naturparke', groupId : 'schutzgebiete', label : 'Naturparke', visible : true },"
				+ "{ eventName : 'base-layer', id : 'osm'}]";
		plugins.doPut(OLMapPlugin.ID, new BasicNameValuePair("configuration",
				mapConfig));
		plugins.doPut(LayoutPlugin.ID);
		plugins.doPut(LayerListPlugin.ID);
		plugins.doPut(AddressSearchPlugin.ID);
		plugins.doPut(BaseLayerPlugin.ID);
		plugins.doPut(FeatureInfoPlugin.ID);
		plugins.doPut(
				LoginPlugin.ID,
				new BasicNameValuePair(
						"configuration",
						"servlet : { loginUrl : 'http://80.237.188.118/vfs/php/login_ext.php',"
								+ "logoutUrl : 'http://80.237.188.118/vfs/php/logout_ext.php',"
								+ "userParamName : 'username',"
								+ "passParamName : 'password',"
								+ "cookieName : 'PHPSESSID',"
								+ "cookieSessionParam : 'sid' } "));

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}
}
