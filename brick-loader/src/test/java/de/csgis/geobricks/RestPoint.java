package de.csgis.geobricks;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

public class RestPoint {

	private ServerManager serverManager;
	private String path;
	private String appName;

	public RestPoint(ServerManager serverManager, String appName, String path) {
		this.serverManager = serverManager;
		this.appName = appName;
		this.path = path;
	}

	public HttpResponse doPut() throws ClientProtocolException, IOException {
		return serverManager.doPut(appName, path, "");
	}

	public HttpResponse doPut(String resource) throws ClientProtocolException,
			IOException {
		return serverManager.doPut(appName, path + "/" + resource, "");
	}

	public HttpResponse doPut(String resource, String content)
			throws ClientProtocolException, IOException {
		return serverManager.doPut(appName, path + "/" + resource, content);
	}

	public HttpResponse doGet() throws ClientProtocolException, IOException {
		return serverManager.doGet(appName, path);
	}

	public HttpResponse doGet(String resource) throws MalformedURLException,
			IOException {
		return serverManager.doGet(appName, path + "/" + resource);
	}

	public HttpResponse doDelete() throws ClientProtocolException, IOException {
		return serverManager.doDelete(appName, path);
	}

	public HttpResponse doDelete(String resource)
			throws ClientProtocolException, IOException {
		return serverManager.doDelete(appName, path + "/" + resource);
	}
}
