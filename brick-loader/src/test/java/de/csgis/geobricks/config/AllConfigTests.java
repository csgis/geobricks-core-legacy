package de.csgis.geobricks.config;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConfiguredApplicationTest.class, PluginListenerTest.class,
		JarPluginScannerTest.class })
public class AllConfigTests {
}
