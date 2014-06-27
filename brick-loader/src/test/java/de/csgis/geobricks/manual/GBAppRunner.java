package de.csgis.geobricks.manual;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Scanner;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.tests.RestPoint;
import de.csgis.geobricks.tests.ServerManager;

public class GBAppRunner {

	public static void main(String[] args) throws Exception {

		String configurationFile = null;
		if (args.length != 1) {
			System.out.println("Usage: GBAppRunner <app-configuration-file>");
			System.exit(-1);
		} else {
			configurationFile = args[0];
		}

		ServerManager serverManager = new ServerManager();
		serverManager.start("geobricks");

		String jsonInput = IOUtils.toString(new FileInputStream(
				configurationFile));
		JSONObject input = (JSONObject) JSONSerializer.toJSON(jsonInput
				.toString());
		RestPoint appsRestPoint = new RestPoint(serverManager, Geobricks.root
				.rest().apps().path());
		String appName = input.getString("app");
		appsRestPoint.doPut(appName);
		RestPoint pluginsRestPoint = new RestPoint(serverManager,
				Geobricks.root.rest().app(appName).plugins().path());
		JSONObject plugins = input.getJSONObject("plugins");
		Iterator<?> pluginIdIterator = plugins.keys();
		while (pluginIdIterator.hasNext()) {
			String id = (String) pluginIdIterator.next();
			JSONObject pluginConfiguration = plugins.getJSONObject(id);
			pluginsRestPoint.doPut(id, pluginConfiguration.toString());
		}

		Scanner sc = new Scanner(System.in);
		System.out.println("ENTER to stop the server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}
}
