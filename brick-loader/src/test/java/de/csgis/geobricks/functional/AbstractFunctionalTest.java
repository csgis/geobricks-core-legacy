package de.csgis.geobricks.functional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;

public abstract class AbstractFunctionalTest {
	private HttpClient client;

	public AbstractFunctionalTest() {
		this.client = HttpClients.createDefault();
	}

	public HttpResponse doGet(String path) throws IOException {
		return client.execute(new HttpGet(getBase() + path));
	}

	public int doGetStatus(String path) throws IOException {
		return doGet(path).getStatusLine().getStatusCode();
	}

	public HttpResponse doPut(String path) throws IOException {
		return client.execute(new HttpPut(getBase() + path));
	}

	public int doPutStatus(String path) throws IOException {
		return doPut(path).getStatusLine().getStatusCode();
	}

	public HttpResponse doDelete(String path) throws IOException {
		return client.execute(new HttpDelete(getBase() + path));
	}

	public int doDeleteStatus(String path) throws IOException {
		return doDelete(path).getStatusLine().getStatusCode();
	}

	public JSONArray parseJsonArray(HttpResponse response)
			throws IllegalStateException, IOException {
		return JSONArray.fromObject(parseJson(response));
	}

	public JSONObject parseJsonObject(HttpResponse response)
			throws IllegalStateException, IOException {
		return JSONObject.fromObject(parseJson(response));
	}

	private JSONTokener parseJson(HttpResponse response)
			throws IllegalStateException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
			builder.append(line).append("\n");
		}
		return new JSONTokener(builder.toString());
	}

	protected abstract String getBase();
}
