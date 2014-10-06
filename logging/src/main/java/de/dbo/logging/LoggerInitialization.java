package de.dbo.logging;

import java.io.File;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

public class LoggerInitialization {

    private static final String INTERNAL_LOGGER_CONFIG_NAME = "contex.log4j.properties";

    public static void initialize() {
        org.apache.log4j.LogManager.resetConfiguration();

        String log4jPropertyPath = System.getProperty("contex.log4j.properties");
        if (log4jPropertyPath != null) {
            System.out.println("Initializing Logger with '" + log4jPropertyPath + "'");
            PropertyConfigurator.configureAndWatch(log4jPropertyPath);
        }
        else {
            File defaultConfigFile = new File(System.getProperty("ityx.conf", System.getProperty("user.home")), "contex.log4j.properties");
            if (defaultConfigFile.exists() && defaultConfigFile.canRead()) {
                System.out.println("Initializing Logger with '" + defaultConfigFile.getAbsolutePath() + "'");
                PropertyConfigurator.configureAndWatch(defaultConfigFile.getAbsolutePath());
            }
            else {
                URL logConfig = LoggerInitialization.class.getClassLoader().getResource(INTERNAL_LOGGER_CONFIG_NAME);
                if (logConfig != null) {
                    System.out.println("Initializing Logger with internal default configuration");
                    PropertyConfigurator.configure(logConfig);
                }
                else {
                    System.err.println("No Logger configuration found!");
                }
            }
        }
    }

}

