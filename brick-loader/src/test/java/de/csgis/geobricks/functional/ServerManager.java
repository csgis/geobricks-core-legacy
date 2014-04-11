package de.csgis.geobricks.functional;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.webapp.WebAppContext;

public class ServerManager {

	private String appName;
	private int port = 9090;
	private Server server;

	public void start(String appName) throws Exception {
		this.appName = appName;
		server = new Server(port);
		server.setStopAtShutdown(true);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/" + appName);
		webAppContext.setResourceBase("src/main/webapp");
		webAppContext.setOverrideDescriptor("src/test/resources/test-web.xml");
		server.setHandler(webAppContext);

		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public HttpResponse doPut(String path, NameValuePair... params)
			throws IOException, ClientProtocolException {
		RequestBuilder request = RequestBuilder.create("put")
				.setUri(getPath(path)).addParameters(params);
		return execute(request.build());
	}

	public HttpResponse doDelete(String path) throws IOException,
			ClientProtocolException {
		return execute(new HttpDelete(getPath(path)));
	}

	public HttpResponse doGet(String path) throws MalformedURLException,
			IOException {
		return execute(new HttpGet(getPath(path)));
	}

	private String getPath(String path) throws IOException {
		return URIUtil.encodePath("http://localhost:" + port + "/" + appName
				+ path);
	}

	private HttpResponse execute(HttpUriRequest put) throws IOException,
			ClientProtocolException {
		HttpClient client = HttpClients.createDefault();
		HttpResponse response = client.execute(put);
		return response;
	}
}
