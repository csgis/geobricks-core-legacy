package de.csgis.geobricks.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.junit.Test;

public class JarPluginScannerTest {
	@Test
	public void entries() {
		ServletContext context = mock(ServletContext.class);
		when(context.getResourcePaths(anyString())).thenReturn(
				Collections.singleton("/resources.jar"));
		when(context.getResourceAsStream(anyString())).thenReturn(
				getClass().getResourceAsStream("/resources.jar"));

		JarPluginScanner scanner = new JarPluginScanner();
		scanner.init(context);
		scanner.next();

		Set<String> entries = scanner.entries();
		assertEquals(1, entries.size());
		assertEquals(PluginListener.modulesDir + File.separator + "mock.css",
				entries.iterator().next());
	}

	@Test
	public void pluginConf() {
		ServletContext context = mock(ServletContext.class);
		when(context.getResourcePaths(anyString())).thenReturn(
				Collections.singleton("/resources.jar"));
		when(context.getResourceAsStream(anyString())).thenReturn(
				getClass().getResourceAsStream("/resources.jar"));

		JarPluginScanner scanner = new JarPluginScanner();
		scanner.init(context);
		scanner.next();

		JSONObject conf = scanner.pluginConf();
		assertEquals("mock", conf.getString("id"));
	}

	@Test
	public void exception() throws Exception {
		InputStream stream = mock(InputStream.class);
		when(stream.read()).thenThrow(new IOException());
		when(stream.read(any(byte[].class))).thenThrow(new IOException());
		when(stream.read(any(byte[].class), anyInt(), anyInt())).thenThrow(
				new IOException());

		ServletContext context = mock(ServletContext.class);
		when(context.getResourcePaths(anyString())).thenReturn(
				Collections.singleton("/resources.jar"));
		when(context.getResourceAsStream(anyString())).thenReturn(stream);

		JarPluginScanner scanner = new JarPluginScanner();
		scanner.init(context);
		assertFalse(scanner.next());
		assertEquals(0, scanner.entries().size());
		assertNull(scanner.pluginConf());
	}

	@Test
	public void entriesForSeveralJars() {
		Set<String> resources = new HashSet<String>();
		resources.add("r1");
		resources.add("r2");
		resources.add("r3");

		ServletContext context = mock(ServletContext.class);
		when(context.getResourcePaths(anyString())).thenReturn(resources);
		when(context.getResourceAsStream(anyString()))
				.thenReturn(getClass().getResourceAsStream("/resources.jar"))
				.thenReturn(getClass().getResourceAsStream("/resources2.jar"));

		JarPluginScanner scanner = new JarPluginScanner();
		scanner.init(context);
		assertTrue(scanner.next());
		assertNotNull(scanner.pluginConf());
		assertEquals(1, scanner.entries().size());
		assertTrue(scanner.next());
		assertNotNull(scanner.pluginConf());
		assertEquals(1, scanner.entries().size());
		assertFalse(scanner.next());
	}

	@Test
	public void ignoreNonPluginJars() {
		Set<String> resources = new HashSet<String>();
		resources.add("r1");
		resources.add("r2");

		ServletContext context = mock(ServletContext.class);
		when(context.getResourcePaths(anyString())).thenReturn(resources);
		when(context.getResourceAsStream(anyString())).thenReturn(
				getClass().getResourceAsStream("/empty.jar")).thenReturn(
				getClass().getResourceAsStream("/resources.jar"));

		JarPluginScanner scanner = new JarPluginScanner();
		scanner.init(context);
		assertTrue(scanner.next());
		assertNotNull(scanner.pluginConf());
		assertEquals(1, scanner.entries().size());
		assertFalse(scanner.next());
	}
}
