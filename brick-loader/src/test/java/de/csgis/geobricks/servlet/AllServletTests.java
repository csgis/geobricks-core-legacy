package de.csgis.geobricks.servlet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.csgis.geobricks.servlet.client.CharResponseWrapperTest;
import de.csgis.geobricks.servlet.client.ConfigServletTest;
import de.csgis.geobricks.servlet.client.IndexHTMLRedirectFilterTest;
import de.csgis.geobricks.servlet.client.IndexHTMLContentProcessorTest;
import de.csgis.geobricks.servlet.client.MainModuleContentProcessorTest;
import de.csgis.geobricks.servlet.client.StaticServletTest;

@RunWith(Suite.class)
@SuiteClasses({ CharResponseWrapperTest.class, ConfigServletTest.class,
		IndexHTMLRedirectFilterTest.class, IndexHTMLContentProcessorTest.class,
		MainModuleContentProcessorTest.class, StaticServletTest.class,
		OutputFilterTest.class })
public class AllServletTests {

}
