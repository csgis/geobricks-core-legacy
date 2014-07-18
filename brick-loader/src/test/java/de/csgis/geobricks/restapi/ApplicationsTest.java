package de.csgis.geobricks.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;

import de.csgis.geobricks.guice.RuntimeModule;

public class ApplicationsTest {
	private static final String APP_ID = "viewer";

	@Inject
	private Applications apps;

	@Before
	public void cleanDatabase() throws Exception {
		Guice.createInjector(new RuntimeModule()).injectMembers(this);
		try {
			apps.delete(APP_ID);
		} catch (ApplicationNotFoundException e) {
			// ignore
		}
	}

	@Test
	public void putAndGetApplication() throws Exception {
		apps.put(APP_ID);
		assertEquals(APP_ID, apps.get(APP_ID).getId());
	}

	@Test
	public void getNonExistingApplication() throws Exception {
		try {
			apps.get("nonexisting");
			fail();
		} catch (ApplicationNotFoundException e) {
		}
	}

	@Test
	public void putExistingApplication() throws Exception {
		apps.put(APP_ID);
		assertEquals(APP_ID, apps.get(APP_ID).getId());
		apps.put(APP_ID);
		assertEquals(APP_ID, apps.get(APP_ID).getId());
	}

	@Test
	public void deleteApplication() throws Exception {
		apps.put(APP_ID);
		apps.get(APP_ID);
		apps.delete(APP_ID);
		try {
			apps.get(APP_ID);
			fail();
		} catch (ApplicationNotFoundException e) {
		}
	}

	@Test
	public void deleteNonExistingApplication() throws Exception {
		try {
			apps.delete("nonexisting");
			fail();
		} catch (ApplicationNotFoundException e) {
		}
	}
}
