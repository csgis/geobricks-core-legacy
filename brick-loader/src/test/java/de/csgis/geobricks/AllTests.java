package de.csgis.geobricks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.csgis.geobricks.functional.AllFunctionalTests;
import de.csgis.geobricks.guice.AllGuiceTests;
import de.csgis.geobricks.servlet.AllServletTests;

@RunWith(Suite.class)
@SuiteClasses({ AllFunctionalTests.class, AllGuiceTests.class,
		AllServletTests.class, WebAppUtilsTest.class,
		ConfiguredApplicationTest.class, PluginRegistryTest.class,
		WebAppUtilsTest.class })
public class AllTests {
}
