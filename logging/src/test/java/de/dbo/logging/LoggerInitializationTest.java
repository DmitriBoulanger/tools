package de.dbo.logging;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggerInitializationTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInitializationTest.class);

    @BeforeClass
    public static void intit() {
        try {
            LoggerInitialization.initializeLogger("mycx-test-logger-initialization.log4j.properties");
            LoggerInitialization.outAndErrToLog(true);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }

    @Test
    public void messages() {
        logger.info("Test info");
        logger.warn("Test warning");
        logger.error("Test errors");
        logger.info("Available loggers and appenders" + LoggerInitialization.printAvailable());
    }

}
