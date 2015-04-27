package de.csgis.geobricks.servlet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.csgis.geobricks.servlet.client.ClasspathResourceServletTest;
import de.csgis.geobricks.servlet.client.ConfigServletTest;
import de.csgis.geobricks.servlet.client.ExternalResourceServletTest;
import de.csgis.geobricks.servlet.client.IndexHTMLContentProcessorTest;
import de.csgis.geobricks.servlet.client.IndexHTMLRedirectFilterTest;
import de.csgis.geobricks.servlet.client.MainModuleContentProcessorTest;

@RunWith(Suite.class)
@SuiteClasses({ ClasspathResourceServletTest.class, ConfigServletTest.class,
		ExternalResourceServletTest.class, IndexHTMLRedirectFilterTest.class,
		IndexHTMLContentProcessorTest.class,
		MainModuleContentProcessorTest.class,
		HTTPCodeServletExceptionTest.class, OutputFilterTest.class })
public class AllServletTests {
}
