package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;

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
		String processed = filter.process(content, null,
				new PluginDescriptor[] { descriptor }, mock(File.class), false);

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
		String processed = filter.process(content, null,
				new PluginDescriptor[] { new PluginDescriptor() },
				mock(File.class), false);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("<link rel=\"stylesheet\""));
	}

	@Test
	public void minifiedJS() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content, null,
				new PluginDescriptor[] { new PluginDescriptor() },
				mock(File.class), true);

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
		String processed = filter.process(content, null,
				new PluginDescriptor[] { new PluginDescriptor() },
				mock(File.class), false);

		assertTrue(content.contains("$mainModule"));
		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$mainModule"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("\"main\" : \"optimized/portal\""));
		assertFalse(processed.contains("optimized/portal-style.css"));
	}

	@Test
	public void CSSFromStylesDir() throws Exception {
		File[] files = new File[] { new File(
				IndexHTMLContentProcessor.STYLES_DIR + File.separator
						+ "testing.css") };

		File configDir = mock(File.class);
		when(configDir.listFiles(any(FilenameFilter.class))).thenReturn(files);
		when(configDir.getPath()).thenReturn(
				IndexHTMLContentProcessor.STYLES_DIR);

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content, null,
				new PluginDescriptor[] { new PluginDescriptor() }, configDir,
				false);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		checkCSS(processed, IndexHTMLContentProcessor.STYLES_DIR
				+ "/testing.css");
	}

	public void minifiedIncludesCSSFromStylesDir() throws Exception {
		File[] files = new File[] { new File(
				IndexHTMLContentProcessor.STYLES_DIR + File.separator
						+ "testing.css") };

		File configDir = mock(File.class);
		when(configDir.listFiles(any(FilenameFilter.class))).thenReturn(files);
		when(configDir.getPath()).thenReturn(
				IndexHTMLContentProcessor.STYLES_DIR);

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content, null,
				new PluginDescriptor[] { new PluginDescriptor() }, configDir,
				true);

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
		String processed = filter.process(content, "My Page",
				new PluginDescriptor[] { new PluginDescriptor() },
				mock(File.class), true);

		assertTrue(content.contains("$title"));
		assertFalse(processed.contains("$title"));
		assertTrue(processed.contains("<title>My Page</title>"));
	}

	@Test
	public void replacesEmptyTitleIfNotSpecified() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content, null,
				new PluginDescriptor[] { new PluginDescriptor() },
				mock(File.class), true);

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
}
