package de.csgis.geobricks.servlet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClasspathResourceServletTest.class, ConfigServletTest.class,
		ExternalResourceServletTest.class, IndexHTMLRedirectFilterTest.class,
		IndexHTMLContentProcessorTest.class,
		MainModuleContentProcessorTest.class,
		HTTPCodeServletExceptionTest.class, OutputFilterTest.class })
public class AllServletTests {
}
