package de.csgis.geobricks.functional;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;

public class RestPoint {

	private ServerManager serverManager;
	private String path;

	public RestPoint(ServerManager serverManager, String path) {
		this.serverManager = serverManager;
		this.path = path;
	}

	public HttpResponse doPut() throws ClientProtocolException, IOException {
		return serverManager.doPut(path);
	}

	public HttpResponse doPut(String resource, NameValuePair... params)
			throws ClientProtocolException, IOException {
		return serverManager.doPut(path + "/" + resource, params);
	}

	public HttpResponse doGet() throws ClientProtocolException, IOException {
		return serverManager.doGet(path);
	}

	public HttpResponse doGet(String resource) throws MalformedURLException,
			IOException {
		return serverManager.doGet(path + "/" + resource);
	}

	public HttpResponse doDelete() throws ClientProtocolException, IOException {
		return serverManager.doDelete(path);
	}

	public HttpResponse doDelete(String resource)
			throws ClientProtocolException, IOException {
		return serverManager.doDelete(path + "/" + resource);
	}
}
