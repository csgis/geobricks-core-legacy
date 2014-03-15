package de.csgis.geobricks.manual;

import java.util.Scanner;

import de.csgis.geobricks.functional.RestPoint;
import de.csgis.geobricks.functional.ServerManager;

public class HelloWorldDemo {

	public static void main(String[] args) throws Exception {
		ServerManager serverManager = new ServerManager();
		serverManager.start("geobricks");
		RestPoint apps = new RestPoint(serverManager, "rest/apps");
		apps.doPut("stadtplan");
		RestPoint plugins = new RestPoint(serverManager,
				"rest/apps/stadtplan/plugins");
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter to add hello.js plugin");
		// sc.nextLine();
		plugins.doPut("olmap");
		System.out.println("plugin added");
		System.out.println("Enter to remove the plugin");
		sc.nextLine();

		plugins.doDelete("olmap");
		System.out.println("plugin removed");
		System.out.println("Enter to stop server");
		sc.nextLine();
		sc.close();
		serverManager.stop();
	}

}
