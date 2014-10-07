package de.dbo.logging;

import static de.dbo.logging.LoggerInitialization.LOGGER_CONFIG_PATH_PROPERTY;
import static de.dbo.logging.LoggerInitialization.LOGGER_CONFIG_RESOURCE_PROPERTY;
import static de.dbo.logging.LoggerInitialization.initializeLogger;
import static de.dbo.logging.LoggerInitialization.isAvailable;
import static de.dbo.logging.LoggerInitialization.outAndErrToLog;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.LogManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerInitializationTest {
	private static final Logger log = LoggerFactory.getLogger(LoggerInitializationTest.class);

    /**
     * No parameters at all.
     * Logger should be initialized from default configuration-resource
     *
     * @throws Exception
     */
    @Test
    public void testDefaultInitialization() throws Exception {
        clearSystemProperties();
        assertTrue("Logger was not initialized usig default initialization", initializeLogger());
        log.debug("Loggger initialized from the default resource");
        assertFalse("Default resource-configuration has only INFO-prioriry for the de.dbo-loggers but the logger has isDebugEnabled=true"
                , log.isDebugEnabled());
    }

    /**
     * path of the configuration-file is given in the system-properties.
     * Logger should be initialized from the given configuration-file
     * @see LoggerInitialization#LOGGER_CONFIG_PATH_PROPERTY
     *
     * @throws Exception
     */
    @Test
    public void testIntializationFromGivenFile() throws Exception {
        clearSystemProperties();
        final String path = "src/test/resources/data/dbo2.log4j.properties";
        System.setProperty(LOGGER_CONFIG_PATH_PROPERTY, path);
        assertTrue("Logger was not initialized from configuration-file [" + path + "]", initializeLogger());
        log.debug("Loggger initialized from custom configuration-file [" + path + "] with DEBUG-priority");
        assertTrue("Test configuration [" + path + "] has DEBUG-prioriry for the de.dbo-loggers but the logger has isDebugEnabled=false"
                , log.isDebugEnabled());
    }

    /**
     * path of the configuration-resource is given in the system-properties.
     * Logger should be initialized from the given configuration-resource
     * @see LoggerInitialization#LOGGER_CONFIG_RESOURCE_PROPERTY
     *
     * @throws Exception
     */
    @Test
    public void testIntializationFromGivenResource() throws Exception {
        clearSystemProperties();
        final String resource = "data/dbo2.log4j.properties";
        System.setProperty(LOGGER_CONFIG_RESOURCE_PROPERTY, resource);
        assertTrue("Logger was not initialized from custom configuration-resource [" + resource + "]", initializeLogger());
        log.debug("Loggger initialized from custom configuration-resource [" + resource + "] with DEBUG-priority");
        assertTrue("Test configuration  [" + resource + "] has DEBUG-prioriry for the de.dbo-loggers but the logger has isDebugEnabled=false"
                , log.isDebugEnabled());
    }

    /**
     * configuration-path is given in the system-properties but the contents is bad
     *
     * @throws Exception
     */
	@Test
	public void testEmptyFile() throws Exception {
        clearSystemProperties();
        final String path = "src/test/resources/empty.properties";
        System.setProperty(LOGGER_CONFIG_PATH_PROPERTY, path);
        assertFalse("Configuration  [" + path + "] is empty but the logger has been initialized", initializeLogger());
	}

	@Test
	public void testCustomResource() throws Exception {
        clearSystemProperties();
        final String resource = "data/dbo2.log4j.properties";
        assertTrue("Logger was not initialized from custome resource [" + resource + "]", initializeLogger(resource));
        log.debug("Loggger initialized from custome resource [" + resource + "] with DEBUG-priority for the logger");
        assertTrue("Test resource-configuration has DEBUG-prioriry for the de.dbo-loggers but the logger has isDebugEnabled=false"
                , log.isDebugEnabled());
	}

    /**
     * if outAndErrToLog is set, then any message should go to the logger.
     * Test has be verified manually!
     *
     * @throws Exception
     */
    @Test
    public void testSystemErrorPrintToInitalizedLogger() throws Exception {
        clearSystemProperties();
        initializeLogger();
        outAndErrToLog(true);
        System.out.println("Message to System.out");
        System.err.println("Message to System.err");
        outAndErrToLog(false);
    }

    /**
     * if outAndErrToLog is set, then printing exception stack trace should go to logger
     * Test has be verified manually!
     *
     * @throws Exception
     */
    @Test
    public void testDirectExceptionPrintStackTraceToInitalizedLogger() throws Exception {
        clearSystemProperties();
        initializeLogger();
        outAndErrToLog(true);
        new Exception("Test exception with printing the stack trace directly").printStackTrace();
        outAndErrToLog(false);
    }

    @Test
    public void ExceptionPrintStackTraceToInitalizedLogger() throws Exception {
        clearSystemProperties();
        initializeLogger();
        outAndErrToLog(true);
        try {
            throw new Exception("Test exception");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            outAndErrToLog(false);
        }

    }

    /**
     * logger availability test.
     * If the logger is reset, then it should not be available.
     * On the other hand, if logger has been successfully initialized,
     * the logger should be available
     *
     * @throws Exception
     */
    @Test
    public void testLoggerIsavailable() throws Exception {
        clearSystemProperties();
        LogManager.resetConfiguration();
        assertFalse("Logger is available after reset", isAvailable());
        if (initializeLogger()) {
            assertTrue("Logger is not available after successfull initialization", isAvailable());
        }
    }

    //
    // HELPERS
    //

    private static final void clearSystemProperties() {
        System.getProperties().remove(LOGGER_CONFIG_PATH_PROPERTY);
        System.getProperties().remove(LOGGER_CONFIG_RESOURCE_PROPERTY);
    }

}
