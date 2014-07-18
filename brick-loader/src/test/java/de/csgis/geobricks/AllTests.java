package de.csgis.geobricks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.csgis.geobricks.restapi.ApplicationPluginsTest;
import de.csgis.geobricks.restapi.ApplicationsTest;
import de.csgis.geobricks.servlet.client.GetApplicationIdFilterTest;

@RunWith(Suite.class)
@SuiteClasses({ ApplicationsTest.class, ApplicationPluginsTest.class,
		GetApplicationIdFilterTest.class, WebAppUtilsTest.class })
public class AllTests {
}
