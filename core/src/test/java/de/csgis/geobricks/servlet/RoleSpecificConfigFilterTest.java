package de.csgis.geobricks.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.Geobricks;
import net.sf.json.JSONObject;

public class RoleSpecificConfigFilterTest {
	private File configDir, roleDir;
	private RoleSpecificConfigFilter filter;

	@Before
	public void setup() throws IOException {
		configDir = File.createTempFile("geobricks", "");
		configDir.delete();
		configDir.mkdir();

		roleDir = new File(configDir, RoleSpecificConfigFilter.ROLE_DIR);
		roleDir.mkdir();

		filter = new RoleSpecificConfigFilter(configDir.getAbsolutePath());
	}

	@After
	public void teardown() throws IOException {
		FileUtils.deleteDirectory(configDir);
	}

	@Test
	public void noRoleOnRequest() throws Exception {
		JSONObject config = JSONObject.fromObject("{ plugin : { a : true }}");

		HttpServletRequest request = mockRequest(null);
		JSONObject modified = filter.modifyConfig(config, request,
				mock(HttpServletResponse.class));
		assertEquals(config, modified);
	}

	@Test
	public void roleWithoutSpecificConf() throws Exception {
		JSONObject config = JSONObject.fromObject("{ plugin : { a : true }}");

		HttpServletRequest request = mockRequest("role1");
		JSONObject modified = filter.modifyConfig(config, request,
				mock(HttpServletResponse.class));
		assertEquals(config, modified);
	}

	@Test
	public void addsPlugin() throws Exception {
		JSONObject config = JSONObject.fromObject("{}");
		String role = "role1";

		File tmp = new File(roleDir, role + ".json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ plugin : {'a' : true }}", writer);
		writer.close();

		HttpServletRequest request = mockRequest(role);
		JSONObject modified = filter.modifyConfig(config, request,
				mock(HttpServletResponse.class));
		assertTrue(modified.has("plugin"));
		assertTrue(modified.getJSONObject("plugin").getBoolean("a"));

		tmp.delete();
	}

	@Test
	public void modifiesPluginConfig() throws Exception {
		JSONObject config = JSONObject.fromObject("{ 'a' : false }");
		String role = "role1";

		File tmp = new File(roleDir, role + ".json");
		FileWriter writer = new FileWriter(tmp);
		IOUtils.write("{ plugin : {'a' : true, 'b' : false }}", writer);
		writer.close();

		HttpServletRequest request = mockRequest(role);
		JSONObject modified = filter.modifyConfig(config, request,
				mock(HttpServletResponse.class));
		assertTrue(modified.has("plugin"));
		assertTrue(modified.getJSONObject("plugin").getBoolean("a"));
		assertFalse(modified.getJSONObject("plugin").getBoolean("b"));

		tmp.delete();
	}

	private HttpServletRequest mockRequest(String role) {
		HttpServletRequest request = mock(HttpServletRequest.class);

		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(Geobricks.SESSION_ATTR_ROLE))
				.thenReturn(role);

		return request;
	}
}
