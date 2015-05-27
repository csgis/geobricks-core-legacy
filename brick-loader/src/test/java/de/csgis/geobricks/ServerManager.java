package de.csgis.geobricks;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerManager {

	private static final int PORT = 9090;
	private Server server;
	private HandlerList handlerList = new HandlerList();

	public void installApp(String appName, String resourceBase)
			throws Exception {
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/" + appName);
		webAppContext.setResourceBase(resourceBase);
		handlerList.addHandler(webAppContext);
	}

	public void start() throws Exception {
		server = new Server(PORT);
		server.setStopAtShutdown(true);
		server.setHandler(handlerList);
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public HttpResponse doPut(String appName, String path, String content)
			throws IOException, ClientProtocolException {
		HttpEntity entity = new StringEntity(content);
		RequestBuilder request = RequestBuilder.create("put")
				.setUri(getPath(appName, path)).setEntity(entity);
		return execute(request.build());
	}

	public HttpResponse doDelete(String appName, String path)
			throws IOException, ClientProtocolException {
		return execute(new HttpDelete(getPath(appName, path)));
	}

	public HttpResponse doGet(String appName, String path)
			throws MalformedURLException, IOException {
		return execute(new HttpGet(getPath(appName, path)));
	}

	private String getPath(String appName, String path) throws IOException {
		return URIUtil.encodePath("http://localhost:" + PORT + "/" + appName
				+ path);
	}

	private HttpResponse execute(HttpUriRequest put) throws IOException,
			ClientProtocolException {
		HttpClient client = HttpClients.createDefault();
		HttpResponse response = client.execute(put);
		return response;
	}

	public int getPort() {
		return PORT;
	}
}
