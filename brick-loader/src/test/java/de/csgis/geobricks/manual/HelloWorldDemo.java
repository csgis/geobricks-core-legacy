package de.csgis.geobricks.manual;

import java.util.Scanner;

import de.csgis.geobricks.Geobricks;
import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;

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
		plugins.doPut("olmap");
		plugins.doPut("title");
		plugins.doPut("layout");
		plugins.doPut("layer-list");
		plugins.doPut("address-search");
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
