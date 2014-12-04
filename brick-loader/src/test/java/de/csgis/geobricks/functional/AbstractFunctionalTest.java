package de.csgis.geobricks.functional;

import de.csgis.geobricks.RestPoint;
import de.csgis.geobricks.ServerManager;

public class AbstractFunctionalTest {
	protected static ServerManager serverManager = new ServerManager();

	protected static final String APP_ID = "viewer";
	protected static final String REST_APP_ID = "rest";

	protected static RestPoint app;

	static {
		try {
			serverManager.installApp(APP_ID, "src/test/webapp");
			app = new RestPoint(serverManager, APP_ID, "");

			serverManager.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
