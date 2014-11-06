package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		String processed = filter.process(content,
				new PluginDescriptor[] { descriptor }, false);

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
				new PluginDescriptor[] { new PluginDescriptor() }, false);

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("<link"));
	}

	@Test
	public void minifiedJS() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content,
				new PluginDescriptor[] { new PluginDescriptor() }, true);

		assertTrue(content.contains("$mainModule"));
		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$mainModule"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("\"main\" : \"optimized/portal\""));
		checkCSS(processed, "optimized/portal-style.css");
	}

	@Test
	public void notMinifiedJS() throws Exception {
		String content = IOUtils
				.toString(getClass().getResourceAsStream(INDEX));
		String processed = filter.process(content,
				new PluginDescriptor[] { new PluginDescriptor() }, false);

		assertTrue(content.contains("$mainModule"));
		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$mainModule"));
		assertFalse(processed.contains("$styleSheets"));
		assertFalse(processed.contains("\"main\" : \"optimized/portal\""));
		assertFalse(processed.contains("optimized/portal-style.css"));
	}

	private void checkCSS(String content, String css) {
		String regex = "<link\\s+rel\\s*=\\s*\"stylesheet\"\\s+href\\s*=\\s*\""
				+ css + "\"\\s*/>";
		Pattern pattern = Pattern.compile(regex);
		assertTrue(pattern.matcher(content).find());
	}
}
