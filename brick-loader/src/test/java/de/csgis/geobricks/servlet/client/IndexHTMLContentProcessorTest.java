package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;
import de.csgis.geobricks.servlet.Config;

public class IndexHTMLContentProcessorTest {
	private static final String INDEX = "/webapp/index.html";

	private IndexHTMLContentProcessor filter;

	@Before
	public void setup() {
		filter = new IndexHTMLContentProcessor();
	}

	@Test
	public void replaceCSS() throws Exception {
		String[] styles = new String[] { "a.css", "b.css" };
		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getStyles(), styles);

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));

		Config config = mockConfig(null, new PluginDescriptor[] { descriptor },
				"", false);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		for (String css : styles) {
			checkCSS(processed, css);
		}
	}

	@Test
	public void noCSS() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig(null,
				new PluginDescriptor[] { new PluginDescriptor() }, "", false);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("<link rel=\"stylesheet\""));
	}

	@Test
	public void minifiedJS() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig(null,
				new PluginDescriptor[] { new PluginDescriptor() }, "", true);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$mainModule"));
		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$mainModule"));
		assertFalse(processed.contains("$styleSheets"));
		assertTrue(processed.contains("\"main\" : \"optimized/portal\""));
		checkCSS(processed, "optimized/portal-style.css");
	}

	@Test
	public void notMinifiedJS() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig(null,
				new PluginDescriptor[] { new PluginDescriptor() }, "", false);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$mainModule"));
		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$mainModule"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("\"main\" : \"optimized/portal\""));
		assertFalse(processed.contains("optimized/portal-style.css"));
	}

	@Test
	public void CSSFromStylesDir() throws Exception {
		File confDir = File.createTempFile("geobricks-test", "");
		confDir.delete();
		confDir.mkdir();

		File css = new File(confDir, "_static" + File.separator + "css");
		css.mkdirs();

		File file = new File(css, "testing.css");
		FileWriter writer = new FileWriter(file);
		IOUtils.write("", writer);
		writer.close();

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig(null,
				new PluginDescriptor[] { new PluginDescriptor() },
				confDir.getAbsolutePath(), false);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		checkCSS(processed, IndexHTMLContentProcessor.STYLES_DIR
				+ "/testing.css");
	}

	public void minifiedIncludesCSSFromStylesDir() throws Exception {
		File confDir = File.createTempFile("geobricks-test", "");
		confDir.delete();
		confDir.mkdir();

		File css = new File(confDir, "_static" + File.separator + "css");
		css.mkdirs();

		File file = new File(css, "testing.css");
		FileWriter writer = new FileWriter(file);
		IOUtils.write("", writer);
		writer.close();

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig(null,
				new PluginDescriptor[] { new PluginDescriptor() },
				confDir.getAbsolutePath(), true);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		checkCSS(processed, "optimized/portal-style.css");
		checkCSS(processed, IndexHTMLContentProcessor.STYLES_DIR
				+ "/testing.css");
	}

	@Test
	public void replacesTitleIfSpecified() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig("My Page",
				new PluginDescriptor[] { new PluginDescriptor() }, "", true);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$title"));
		assertFalse(processed.contains("$title"));
		assertTrue(processed.contains("<title>My Page</title>"));
	}

	@Test
	public void replacesEmptyTitleIfNotSpecified() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		Config config = mockConfig(null,
				new PluginDescriptor[] { new PluginDescriptor() }, "", true);
		String processed = filter.process(content, config);

		assertTrue(content.contains("$title"));
		assertFalse(processed.contains("$title"));
		assertTrue(processed.contains("<title></title>"));
	}

	private void checkCSS(String content, String css) {
		String regex = "<link\\s+rel\\s*=\\s*\"stylesheet\"\\s+href\\s*=\\s*\""
				+ css + "\"\\s*/>";
		Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher(content).find());
	}

	private Config mockConfig(String title, PluginDescriptor[] descriptors,
			String confDir, boolean minified) {
		Properties properties = new Properties();
		if (title != null) {
			properties.setProperty("title", title);
		}
		properties.setProperty("minified", Boolean.toString(minified));

		Config config = mock(Config.class);
		if (confDir != null) {
			when(config.getConfigDir()).thenReturn(confDir);
		}
		when(config.getAppProperties()).thenReturn(properties);
		when(config.getPluginDescriptors()).thenReturn(descriptors);

		return config;
	}
}
