package de.csgis.geobricks.manual;

import java.util.Scanner;

import org.apache.http.message.BasicNameValuePair;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.login.LoginPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.timeSelector.TimeSelectorPlugin;
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
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnTransparency', css : 'toolbar-button', image : 'images/transparency.png', sendEventName : 'toggle-opacity', sendEventMessage : { layers : [ 'kartiergebiete' ] } },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnPreviousExtent', css : 'toolbar-button', image : 'images/zoom-previous.png', sendEventName : 'extent-history:back', enableEventName : 'extent-history:back-enabled', disableEventName : 'extent-history:back-disabled' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnFullExtent', css : 'toolbar-button', image : 'images/full-extent.png', sendEventName : 'initial-zoom' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnNextExtent', css : 'toolbar-button', image : 'images/zoom-next.png', sendEventName : 'extent-history:forward', enableEventName : 'extent-history:forward-enabled', disableEventName : 'extent-history:forward-disabled' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnZoomRegion', css : 'toolbar-button', image : 'images/zoom-region.png', sendEventName : 'map-control-zoom-region' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnCoordinateSearch', css : 'toolbar-button', image : 'images/coordinate-search.png', sendEventName : 'ui-show', sendEventMessage : 'myform' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnTimeSelector', css : 'toolbar-button', image : 'images/time.png', sendEventName : 'ui-show', sendEventMessage : 'time-selector' },"
								+ "{ eventName : 'ui-button:create', parentDiv : 'mytoolbar', div : 'btnHelp', css : 'toolbar-button', image : 'http://vfsviewer.vfs-muenchen.de/toolicons/help.png', sendEventName : 'ui-open-url', sendEventMessage : {url : 'http://vfsviewer.vfs-muenchen.de/docs/vfshilfe.pdf', target : '_blank' } },"

								+ "{ eventName : 'ui-dialog:create', parentDiv : 'layout-center', div : 'myform', css : 'coordinate-dialog', title : 'Coordinate Search', closeButton : true, visible : false },"
								+ "{ eventName : 'ui-choice-field:create', parentDiv : 'myform', div : 'myform-crsselection', css : 'coordinate-crs', label : 'CRS: ', values: [ 'EPSG:4326', 'EPSG:25830' ] },"
								+ "{ eventName : 'ui-numeric-field:create', parentDiv : 'myform', div : 'myform-lat', css : 'coordinate-input', label : 'Lat: '},"
								+ "{ eventName : 'ui-numeric-field:create', parentDiv : 'myform', div : 'myform-lon', css : 'coordinate-input', label : 'Lon: '},"
								+ "{ eventName : 'ui-button:create', parentDiv : 'myform', div : 'btnGoToCoordinate', css : 'coordinate-button', text : 'Go!'},"
								+ "{ eventName : 'ui-form-collector:extend', button: 'btnGoToCoordinate', divs : ['myform-crsselection', 'myform-lat', 'myform-lon'], names : ['crs', 'lat', 'lon'], sendEventName : 'zoomTo' },"

								+ "{ eventName : 'ui-login:create', div : 'login', parentDiv : 'layout-header', css : 'login' }"
								+ "]"));

		String mapConfig = "center: { lat : 49, lon : 11.2, zoomLevel : 7, useLinks : true },"
				+ "'toggle-opacity' : { opacity : 0.5 },"
				+ "olmap : { div : 'layout-center' },"
				+ "'pan-zoom-bar': { mapOffsetX : 10, mapOffsetY : 35 },"
				+ "'load-layers' : [" //
				+ "{ eventName : 'add-layer-group', name : 'Kartendaten', id : 'kartendaten', visibility : true },"
				+ "{ eventName : 'add-layer-group', name : 'Schutzgebiete', id : 'schutzgebiete', visibility : true },"
				// Base layers
				+ "{ eventName : 'add-layer', id : 'osm', isBaseLayer : true, url : 'http://ows.terrestris.de/osm/service', label : 'OpenStreetMap', layerName : 'OSM-WMS', groupId : 'main' },"
				+ "{ eventName : 'add-layer', id : 'gmaps_roadmap', isBaseLayer : true, type : 'Google', label : 'Google Maps (Karte)', layerName : 'Google Streets', groupId : 'main' }, "
				+ "{ eventName : 'add-layer', id : 'gmaps_hybrid', isBaseLayer : true, type : 'Google', label : 'Google Maps (Satellit)', layerName : 'Google Hybrid', groupId : 'main', googleType : 'hybrid' }, "
				// Schutzgebiete
				+ "{ eventName : 'add-layer', id : 'naturparke', type : 'wms', url : 'http://80.237.188.118/proxy/goto_vfs_map.cgi', layerName : 'naturparke', groupId : 'schutzgebiete', label : 'Naturparke', visible : true,legendUrl : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_map.cgi?SCALE=3466752.130795755&TRANSPARENT=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic&EXCEPTIONS=application%2Fvnd.ogc.se_xml&FORMAT=image/png&LAYER=naturparke' },"
				// Kartendaten
				+ "{ eventName : 'add-layer', id : 'flurkarte', type : 'wms', url : 'http://80.237.188.118/proxy/goto_vfs_map.cgi', layerName : 'flkgrenzenbayern', groupId : 'kartendaten', label : 'Flurkartenschnitt 1:5.000', visible : false, legendUrl : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_maplegend.cgi?TRANSPARENT=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic&EXCEPTIONS=application%2Fvnd.ogc.se_xml&FORMAT=image/png&LAYER=flkgrenzenbayern' }, "
				+ "{ eventName : 'add-layer', id : 'kartiergebiete', type : 'wms', url : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_maplegend.cgi', layerName : 'waldbesitzer_jahr', groupId : 'kartendaten', label : 'Kartiergebiete', visible : true, legendUrl : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_maplegend.cgi?TRANSPARENT=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic&EXCEPTIONS=application%2Fvnd.ogc.se_xml&FORMAT=image/png&LAYER=waldbesitzer_jahr&SCALE=3466752.130795755', timestamps : [ '1991-01-01T00:00:00.000Z', '1992-01-01T00:00:00.000Z', '1993-01-01T00:00:00.000Z', '1994-01-01T00:00:00.000Z', '1999-01-01T00:00:00.000Z', '2003-01-01T00:00:00.000Z', '2005-01-01T00:00:00.000Z' ], timeAttribute : 'timequery', timeExpression : 'jahr %operator% %time%', timeExpressionDefault : 'true' }, "
				+ "{ eventName : 'base-layer', id : 'osm'}]";
		plugins.doPut(OLMapPlugin.ID, new BasicNameValuePair("configuration",
				mapConfig));
		plugins.doPut(LayoutPlugin.ID);
		plugins.doPut(LayerListPlugin.ID);
		plugins.doPut(AddressSearchPlugin.ID);
		plugins.doPut(BaseLayerPlugin.ID);
		plugins.doPut(TimeSelectorPlugin.ID);
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
