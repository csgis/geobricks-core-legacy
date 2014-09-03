package de.csgis.geobricks.config;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ApplicationListenerTest.class, PluginListenerTest.class,
		JarPluginScannerTest.class })
public class AllConfigTests {
}
