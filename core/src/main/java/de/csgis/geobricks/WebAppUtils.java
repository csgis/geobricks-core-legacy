package de.csgis.geobricks;

import javax.servlet.ServletContext;

public class WebAppUtils {

	public static String getApplicationId(ServletContext context) {
		String id = context.getInitParameter("geobricks-app-id");
		if (id == null) {
			id = context.getContextPath();
			if (id.startsWith("/")) {
				id = id.substring(1);
			}
		}
		return id;
	}

}
