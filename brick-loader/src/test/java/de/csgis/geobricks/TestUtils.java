package de.csgis.geobricks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import org.apache.http.HttpResponse;

public class TestUtils {
	public static JSONArray parseJsonArray(HttpResponse response)
			throws IllegalStateException, IOException {
		return JSONArray.fromObject(parseJson(response));
	}

	public static JSONObject parseJsonObject(HttpResponse response)
			throws IllegalStateException, IOException {
		return JSONObject.fromObject(parseJson(response));
	}

	private static JSONTokener parseJson(HttpResponse response)
			throws IllegalStateException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
			builder.append(line).append("\n");
		}
		return new JSONTokener(builder.toString());
	}
}
