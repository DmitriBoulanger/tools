package de.dbo.logging;

import java.io.File;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.LogManager;

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

    public static final String LOGGER_CONFIG_RESOURCE_DEFAULT = "dbo.log4j.properties";
    public static final String LOGGER_CONFIG_PROPERTY = "dbo.log4j.properties";
    
    /**
     * initialization from resource
     * @param resource
     * @throws Exception if no resource found
     */
    public static boolean initializeLogger(final String resource) throws Exception {
    	 LogManager.resetConfiguration();
    	 return doInitialize(resource);
    }
    
    /**
     * initialization from file-path in the system-properties.
     * If such a file is not found, the default resource is used
     * 
     * @param resource
     * @throws Exception if no resource found
     */
    public static void initializeLogger() throws Exception {
    	boolean done = false;
        LogManager.resetConfiguration();

        final String log4jPropertyPath = System.getProperty(LOGGER_CONFIG_PROPERTY);
        if (log4jPropertyPath != null) {
        	done = doInitialize( new File(log4jPropertyPath) );
        }
        if (done) {
        	return;
        }
        done = doInitialize( LOGGER_CONFIG_RESOURCE_DEFAULT );
        
        if (!done) {
        	throw new Exception("No Logger configuration found!"); 
        }
    }
    
    private static boolean doInitialize(final File file)  {
    	if (null==file) {
    		return false;
    	}
    	if ( file.exists() && file.canRead() ) {
    		System.out.println("Initializing Log4j-Logger with configuration file [" + file.getAbsolutePath() + "] ...");
    		PropertyConfigurator.configureAndWatch(file.getAbsolutePath());
    		return true;
    	} else {
    		System.out.println("Log4j configuration file ["+file.getAbsolutePath()+"] doesn't exist or it is not readble");
    		return false;
    	}
    }
    
    private static boolean doInitialize(final String resource) throws Exception {
    	final URL logConfig = LoggerInitialization.class.getClassLoader().getResource(resource);
        if (logConfig != null) {
             System.out.println("Initializing Log4j-Logger with resource-configuration [" + resource + "] ...");
             PropertyConfigurator.configure(logConfig);
             return true;
        } else {
        	System.out.println("Log4j resource-configuration ["+resource+"] not found");
        	return false;
        }
    }
}

