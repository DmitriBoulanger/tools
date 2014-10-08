package de.dbo.logging;

import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.restoreOriginalSystemOutputs;
import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.sendSystemOutAndErrToSLF4J;
import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 * Initialization of the Log4j-logger
 *
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and
 *           only incidentally for computers to execute
 *
 */
public class LoggerInitialization {

    public static final String LOGGER_CONFIG_RESOURCE_DEFAULT  = "dbo.log4j.properties";

    /*
     * system properties
     */
    public static final String LOGGER_CONFIG_PATH_PROPERTY     = "log4j.properties.path";
    public static final String LOGGER_CONFIG_RESOURCE_PROPERTY = "log4j.properties.resource";

    /**
     *
     * @return true only if the root-logger has appenders
     */
    public static boolean isAvailable() {
        @SuppressWarnings("unchecked")
        final List<Appender> appenders = Collections.list(LogManager.getRootLogger().getAllAppenders());
        return !appenders.isEmpty();
    }

    /**
     * all console-messages send to the relevant logger.
     * The operation is only done if the root-logger has been already initialized
     *
     * @throws Exception
     */
    public static void outAndErrToLog(boolean yes) throws Exception {
        if (yes && isAvailable()) {
            sendSystemOutAndErrToSLF4J();
            return;
        }
        if (!yes && systemOutputsAreSLF4JPrintStreams()) {
            restoreOriginalSystemOutputs();
        }
    }

    /**
     * initialization from file-path in the system-properties.
     * If such a file is not found, the default resource is used
     *
     * @param resource
     * @return false only if the logger has not been initialized
     * @throws Exception if no resource found
     */
    public static boolean initializeLogger() throws Exception {
        LogManager.resetConfiguration();
        boolean done = false;

        final String log4jConfigurationFilePath = System.getProperty(LOGGER_CONFIG_PATH_PROPERTY);
        final String log4jConfigurationResource = System.getProperty(LOGGER_CONFIG_RESOURCE_PROPERTY);
        if (null != log4jConfigurationFilePath && 0 != log4jConfigurationFilePath.trim().length()) {
            done = initializeFromFile(new File(log4jConfigurationFilePath));
        }
        else if (!done && null != log4jConfigurationResource && 0 != log4jConfigurationResource.trim().length()) {
            done = initializeFromResource(log4jConfigurationResource);
        }
        if (done) {
            return isAvailable();
        }

        // no custom-configuration available ...
        useDefaultConfigurationResource();
        return isAvailable();
    }

    /*
     * should always work if the standard de.dbo-artifact is used!
     */
    private static void useDefaultConfigurationResource() throws Exception {
        if (!initializeFromResource(LOGGER_CONFIG_RESOURCE_DEFAULT)) {
            throw new Exception("Should never happen error: no default configuration-resource found!");
        }
    }

    /**
     * Log4j-initialization from configuration in resource (classpath)
     * @param resource
     * @return false only if the logger has not been initialized
     * @throws Exception if no resource found
     * @see #isAvailable()
     */
    public static boolean initializeLogger(final String resource) throws Exception {
        LogManager.resetConfiguration();
        if (!initializeFromResource(resource)) {
            return false;
        }
        return isAvailable();
    }

    private static boolean initializeFromFile(final File file) {
        if (null == file) {
            return false;
        }
        if (file.exists() && file.canRead()) {
            System.out.println("Initializing Log4j-Logger with configuration file [" + file.getAbsolutePath() + "] ...");
            PropertyConfigurator.configureAndWatch(file.getAbsolutePath());
            return true;
        }
        else {
            System.out.println("Log4j configuration file [" + file.getAbsolutePath() + "] doesn't exist or it is not readble");
            return false;
        }
    }

    private static boolean initializeFromResource(final String resource) throws Exception {
        final URL logConfig = LoggerInitialization.class.getClassLoader().getResource(resource);
        if (logConfig != null) {
             System.out.println("Initializing Log4j-Logger with resource-configuration [" + resource + "] ...");
             PropertyConfigurator.configure(logConfig);
             return true;
        } else {
            System.out.println("Log4j resource-configuration [" + resource + "] not found");
            return false;
        }
    }
}

