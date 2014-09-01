package de.csgis.geobricks;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public interface CustomConfigurator {
	void config(HttpServletRequest request, HttpServletResponse response,
			JSONObject staticConfig, String confDir);
}
