package de.csgis.geobricks;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.csgis.geobricks.functional.AllFunctionalTests;
import de.csgis.geobricks.servlet.AllServletTests;

@RunWith(Suite.class)
@SuiteClasses({ WebAppUtilsTest.class, AllFunctionalTests.class,
		AllServletTests.class })
public class AllTests {

}
