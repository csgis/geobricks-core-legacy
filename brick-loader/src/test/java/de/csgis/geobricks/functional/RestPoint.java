package de.csgis.geobricks.functional;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

public class RestPoint {

	private ServerManager serverManager;
	private String path;

	public RestPoint(ServerManager serverManager, String path) {
		this.serverManager = serverManager;
		this.path = path;
	}

	public void doPut(String resource) throws ClientProtocolException,
			IOException {
		serverManager.doPut(path + "/" + resource);
	}

	public HttpResponse doGet(String resource) throws MalformedURLException,
			IOException {
		return serverManager.doGet(path + "/" + resource);
	}

}
