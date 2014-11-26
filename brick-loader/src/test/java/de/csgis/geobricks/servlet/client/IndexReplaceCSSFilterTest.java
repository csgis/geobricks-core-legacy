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

public class IndexReplaceCSSFilterTest {
	private static final String INDEX = "/webapp/index.html";

	private IndexReplaceCSSFilter filter;

	@Before
	public void setup() {
		filter = new IndexReplaceCSSFilter();
	}

	@Test
	public void replaceCSS() throws Exception {
		String[] styles = new String[] { "a.css", "b.css" };
		PluginDescriptor descriptor = new PluginDescriptor();
		Collections.addAll(descriptor.getStyles(), styles);

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content,
				new PluginDescriptor[] { descriptor }, mock(File.class));

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
		String processed = filter.process(content,
				new PluginDescriptor[] { new PluginDescriptor() },
				mock(File.class));

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("<link"));
	}

	@Test
	public void testCSSFromStylesDir() throws Exception {
		File[] files = new File[] { new File(IndexReplaceCSSFilter.STYLES_DIR
				+ File.separator + "testing.css") };

		File configDir = mock(File.class);
		when(configDir.listFiles(any(FilenameFilter.class))).thenReturn(files);
		when(configDir.getPath()).thenReturn(IndexReplaceCSSFilter.STYLES_DIR);

		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content,
				new PluginDescriptor[] { new PluginDescriptor() }, configDir);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		checkCSS(processed, IndexReplaceCSSFilter.STYLES_DIR + "/testing.css");
	}

	private void checkCSS(String content, String css) {
		String regex = "<link\\s+rel\\s*=\\s*\"stylesheet\"\\s+href\\s*=\\s*\""
				+ css + "\"\\s*/>";
		Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher(content).find());
	}
}
