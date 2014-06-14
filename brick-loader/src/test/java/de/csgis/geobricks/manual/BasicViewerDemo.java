package de.csgis.geobricks.manual;

import java.util.Scanner;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.addressSearch.AddressSearchPlugin;
import de.csgis.geobricks.baseLayer.BaseLayerPlugin;
import de.csgis.geobricks.coordinateSearch.CoordinateSearchPlugin;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;
import de.csgis.geobricks.layerList.LayerListPlugin;
import de.csgis.geobricks.layout.LayoutPlugin;
import de.csgis.geobricks.olmap.OLMapPlugin;
import de.csgis.geobricks.overviewMap.OverviewMapPlugin;
import de.csgis.geobricks.printing.PrintingPlugin;
import de.csgis.geobricks.queryInfo.QueryInfoPlugin;
import de.csgis.geobricks.timeSelector.TimeSelectorPlugin;
import de.csgis.geobricks.toolbar.ToolbarPlugin;
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
				"{ui : ["
						+ "{ eventName : 'ui-accordion:create', div : 'layers-accordion', parentDiv : 'layout-side', css: 'layers-accordion'},"
						+ "{ eventName : 'ui-html', div : 'title', parentDiv : 'layout-header', html: 'Basic viewer', css: 'title' },"
						+ "{ eventName : 'ui-autocomplete:create', div : 'gemeinde_autocomplete', parentDiv : 'layout-header', sendEventName : 'search-gemeinde', options : ['gemeinde', 'another_gemeinde']}"
						+ "]}");

		String mapConfig = "{center: { lat : 49, lon : 11.2, zoomLevel : 7, useLinks : true },"
				+ "'navigate' : { enabled : true },"
				+ "'toggle-opacity' : { opacity : 0.5, enabled : true },"
				+ "'measure-area' : { enabled : true },"
				+ "'measure-distance' : { enabled : true },"
				+ "'zoom-region' : { enabled : true },"
				+ "'zoom' : { enabled : true },"
				+ "'extent-history' : { enabled : true },"
				+ "'query-info' : { enabled : true, filter : { property : 'queryable', match : true }},"
				+ "olmap : { div : 'layout-center', proxy : '/http_proxy/proxy?url=' },"
				+ "'pan-zoom-bar': { mapOffsetX : 10, mapOffsetY : 35 },"
				+ "'load-layers' : [" //
				+ "{ eventName : 'add-layer-group', name : 'Kartendaten', id : 'kartendaten', visibility : true },"
				+ "{ eventName : 'add-layer-group', name : 'Schutzgebiete', id : 'schutzgebiete', visibility : true },"
				// Base layers
				+ "{ eventName : 'add-layer', id : 'osm', isBaseLayer : true, type: 'wms', url : 'http://ows.terrestris.de/osm/service', label : 'OpenStreetMap', layerName : 'OSM-WMS', groupId : 'main' },"
				+ "{ eventName : 'add-layer', id : 'gmaps_roadmap', isBaseLayer : true, type : 'Google', label : 'Google Maps (Karte)', layerName : 'Google Streets', groupId : 'main' }, "
				+ "{ eventName : 'add-layer', id : 'gmaps_hybrid', isBaseLayer : true, type : 'Google', label : 'Google Maps (Satellit)', layerName : 'Google Hybrid', groupId : 'main', googleType : 'hybrid' }, "
				// Schutzgebiete
				+ "{ eventName : 'add-layer', id : 'naturparke', type : 'wms', url : 'http://80.237.188.118/proxy/goto_vfs_map.cgi', layerName : 'naturparke', groupId : 'schutzgebiete', label : 'Naturparke', visible : true,legendUrl : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_map.cgi?SCALE=3466752.130795755&TRANSPARENT=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic&EXCEPTIONS=application%2Fvnd.ogc.se_xml&FORMAT=image/png&LAYER=naturparke' },"
				// Kartendaten
				+ "{ eventName : 'add-layer', id : 'flurkarte', type : 'wms', url : 'http://80.237.188.118/proxy/goto_vfs_map.cgi', layerName : 'flkgrenzenbayern', groupId : 'kartendaten', label : 'Flurkartenschnitt 1:5.000', visible : false, legendUrl : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_maplegend.cgi?TRANSPARENT=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic&EXCEPTIONS=application%2Fvnd.ogc.se_xml&FORMAT=image/png&LAYER=flkgrenzenbayern' }, "
				+ "{ eventName : 'add-layer', id : 'gemeinde', type : 'wms', url : 'http://80.237.188.121/geoserver/common/wms', layerName : 'gemeinde', groupId : 'kartendaten', label : 'Gemeinde', visible : true,legendUrl : 'https://gb-data/geoserver/wms?REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH=20&HEIGHT=20&LAYER=common:gemeinde&transparent=true', options : { singleTile : true } }, "
				+ "{ eventName : 'add-layer', id : 'landkreis', type : 'wms', url : 'http://80.237.188.121/geoserver/common/wms', layerName : 'landkreis', groupId : 'kartendaten', label : 'Landkreis', visible : true,legendUrl : 'https://gb-data/geoserver/wms?REQUEST=GetLegendGraphic&VERSION=1.0.0&FORMAT=image/png&WIDTH=20&HEIGHT=20&LAYER=common:landkreis&transparent=true', options : { singleTile : true } }, "
				+ "{ eventName : 'add-layer', id : 'kartiergebiete', type : 'wms', url : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_maplegend.cgi', layerName : 'waldbesitzer_jahr', groupId : 'kartendaten', label : 'Kartiergebiete', visible : true, legendUrl : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_maplegend.cgi?TRANSPARENT=true&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetLegendGraphic&EXCEPTIONS=application%2Fvnd.ogc.se_xml&FORMAT=image/png&LAYER=waldbesitzer_jahr&SCALE=3466752.130795755', timestamps : [ '1991-01-01T00:00:00.000Z', '1992-01-01T00:00:00.000Z', '1993-01-01T00:00:00.000Z', '1994-01-01T00:00:00.000Z', '1999-01-01T00:00:00.000Z', '2003-01-01T00:00:00.000Z', '2005-01-01T00:00:00.000Z' ], timeAttribute : 'timequery', timeExpression : 'jahr %operator% %time%', timeExpressionDefault : 'true' }, "
				+ "{ eventName : 'add-layer', id : 'kartiergebiete-query', type : 'wms', url : 'http://vfsviewer.vfs-muenchen.de/proxy/goto_vfs_map.cgi', layerName : 'waldbesitzer_frei', groupId : 'hidden', label : 'Kartiergebiete', visible : false, options : { queryable : true } }, "
				+ "{ eventName : 'base-layer', id : 'osm'}]}";
		plugins.doPut(OLMapPlugin.ID, mapConfig);
		plugins.doPut(LayoutPlugin.ID);
		plugins.doPut(LayerListPlugin.ID);
		plugins.doPut(AddressSearchPlugin.ID);
		plugins.doPut(BaseLayerPlugin.ID);
		plugins.doPut(TimeSelectorPlugin.ID);
		plugins.doPut(QueryInfoPlugin.ID);
		plugins.doPut(ToolbarPlugin.ID);
		plugins.doPut(CoordinateSearchPlugin.ID);
		plugins.doPut(OverviewMapPlugin.ID,
				"{'overview-map' : { width : 250, height : 250, "
						+ "extent : [1000000, 6000000, 1600000, 6600000],"
						+ "minZoomLevel : 5," + "maximized : true } }");
		plugins.doPut(PrintingPlugin.ID);
		// plugins.doPut(LoginPlugin.ID);

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to remove time-selector");
		sc.nextLine();
		plugins.doDelete(TimeSelectorPlugin.ID);
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}
}
