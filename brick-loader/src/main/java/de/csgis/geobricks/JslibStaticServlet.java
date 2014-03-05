package de.csgis.geobricks;

import javax.inject.Singleton;

@Singleton
public class JslibStaticServlet extends StaticServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected String getFolder() {
		return "jslib";
	}

}
