package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.PluginRegistry;
import de.csgis.geobricks.guice.RuntimeModule;

public class IndexReplaceCSSFilterTest {
	private Injector injector;
	private IndexReplaceCSSFilter filter;
	private PluginRegistry pluginRegistry;

	@Before
	public void setup() {
		injector = Guice.createInjector(new RuntimeModule());
		filter = injector.getInstance(IndexReplaceCSSFilter.class);
		pluginRegistry = injector.getInstance(PluginRegistry.class);
	}

	@Test
	public void replaceCSS() throws Exception {
		String id = "myplugin";
		String[] styleSheets = new String[] { "a.css", "b.css" };

		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getStyleSheets()).thenReturn(styleSheets);
		when(plugin.getId()).thenReturn(id);

		pluginRegistry.putPlugin(plugin);

		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/index.html"));
		String processed = filter.process(content,
				JSONObject.fromObject("{'" + id + "' : {}}"));

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		for (String css : styleSheets) {
			checkCSS(processed, css);
		}
	}

	@Test
	public void pluginWithoutCSS() throws Exception {
		String id = "myplugin";

		PluginDescriptor plugin = mock(PluginDescriptor.class);
		when(plugin.getStyleSheets()).thenReturn(null);
		when(plugin.getId()).thenReturn(id);

		pluginRegistry.putPlugin(plugin);

		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/index.html"));
		String processed = filter.process(content,
				JSONObject.fromObject("{'" + id + "' : {}}"));

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("<link"));
	}

	private void checkCSS(String content, String css) {
		String regex = "<link\\s+rel\\s*=\\s*\"stylesheet\"\\s+href\\s*=\\s*\""
				+ css + "\"\\s*/>";
		Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher(content).find());
	}
}
