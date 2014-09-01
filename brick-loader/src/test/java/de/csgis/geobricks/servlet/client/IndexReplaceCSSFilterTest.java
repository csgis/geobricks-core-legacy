package de.csgis.geobricks.servlet.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import de.csgis.geobricks.PluginDescriptor;

public class IndexReplaceCSSFilterTest {
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

		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/index.html"));
		String processed = filter.process(content,
				new PluginDescriptor[] { descriptor });

		assertTrue(content.contains("$styleSheets"));
		assertFalse(processed.contains("$styleSheets"));
		for (String css : styles) {
			checkCSS(processed, css);
		}
	}

	@Test
	public void noCSS() throws Exception {
		String content = IOUtils.toString(getClass().getResourceAsStream(
				"/de/csgis/geobricks/webapp/index.html"));
		String processed = filter.process(content,
				new PluginDescriptor[] { new PluginDescriptor() });

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
