package de.csgis.geobricks.guice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ PluginListenerTest.class, GuiceServletConfigTest.class })
public class AllGuiceTests {
}
