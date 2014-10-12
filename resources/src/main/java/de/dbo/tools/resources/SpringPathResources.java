package de.dbo.tools.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class SpringPathResources {
	
	/**
	 * finds resources in the file-system
	 * @param pattern 
	 * @return
	 * @throws IOException
	 */
    public static List<Resource> resourcesInClasspath(final String pattern) throws IOException {
        final PathMatchingResourcePatternResolver patternResourceResolver = new PathMatchingResourcePatternResolver();
        return Arrays.asList(patternResourceResolver.getResources("classpath:" + pattern));
    }
    
    /**
     * finds resources in the class-path
     * @param pattern
     * @return
     * @throws IOException
     */
    public static List<Resource> resoucesInFilesystem(final String pattern) throws IOException {
        final PathMatchingResourcePatternResolver patternResourceResolver = new PathMatchingResourcePatternResolver();
        return Arrays.asList(patternResourceResolver.getResources("file:" + pattern));
    }

    /**
     * collects names of all resources in the classpath
     *
     * @param pattern (see pattern examples in the test)
     * @return list with names of the found resources
     * @throws IOException
     */
    public static List<URL> resourceFilenamesInClasspath(final String pattern) throws IOException {
        final List<Resource> resources = resourcesInClasspath(pattern);
        final List<URL> ret = new ArrayList<URL>();
        for (final Resource resource : resources) {
            ret.add(resource.getURL());
        }
        return ret;
    }

    /**
     * collects files in the classpath.
     * If a matched resource is not a file, e.g. it is located in a JAR, an exception is thrown
     *
     * @param pattern (see pattern examples in the test)
     * @return list with files in the classpath
     * @throws IOException
     */
    public static List<File> resourceFilesInClasspath(final String pattern) throws IOException {
    	final List<Resource> resources = resourcesInClasspath(pattern);
        final List<File> ret = new ArrayList<File>();
        for (final Resource resource : resources) {
            ret.add(resource.getFile());
        }
        return ret;
    }
}
