package de.dbo.logging;

import static de.dbo.logging.LoggerInitialization.LOGGER_CONFIG_PROPERTY;
import static de.dbo.logging.LoggerInitialization.initializeLogger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

import org.apache.log4j.LogManager;

public class LoggerInitializationTest {
	private static final Logger log = LoggerFactory.getLogger(LoggerInitializationTest.class);
	
	@Test
	public void testEmptyFile() throws Exception {
		System.setProperty(LOGGER_CONFIG_PROPERTY, "src/test/resources/empty.properties");
		initializeLogger();
		assertFalse(isLogggerInitialized());
		System.err.println("Loggger was not initilaized from empty file");
	}
	
	@Test
	public void testFile() throws Exception {
		System.setProperty(LOGGER_CONFIG_PROPERTY, "src/test/resources/data/dbo2.log4j.properties");
		initializeLogger();
		assertTrue("Logger was not initialized", isLogggerInitialized());
		log.info("Loggger initialized from  default resource");
	}
	
	@Test
	public void testDefaultResource() throws Exception {
		System.getProperties().remove(LOGGER_CONFIG_PROPERTY);
		initializeLogger();
		assertTrue("Logger was not initialized", isLogggerInitialized());
		log.info("Loggger initialized from  default resource");
	}
	
	@Test
	public void testCustomResource() throws Exception {
		assertTrue("Initialization failed", initializeLogger("data/dbo2.log4j.properties"));
		assertTrue("Logger was not initialized", isLogggerInitialized());
		log.info("Loggger initialized from  custom resource");
	}
	
	private boolean isLogggerInitialized() {
		return  LogManager.getRootLogger().getAllAppenders().hasMoreElements();
	}
	

}
