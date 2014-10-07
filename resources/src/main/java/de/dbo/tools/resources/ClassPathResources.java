package de.dbo.tools.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ClassPathResources {

    /**
     * collects names of all resources in the classpath
     *
     * @param pattern (see pattern examples in the test)
     * @return list with names of the found resources
     * @throws IOException
     */
    public static List<URL> resourceFilenames(final String pattern) throws IOException {
        final PathMatchingResourcePatternResolver patternResourceResolver = new PathMatchingResourcePatternResolver();
        final Resource[] resources = patternResourceResolver.getResources("classpath:" + pattern);
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
    public static List<File> resourceFiles(final String pattern) throws IOException {
        final PathMatchingResourcePatternResolver patternResourceResolver = new PathMatchingResourcePatternResolver();
        final Resource[] resources = patternResourceResolver.getResources("classpath:" + pattern);
        final List<File> ret = new ArrayList<File>();
        for (final Resource resource : resources) {
            ret.add(resource.getFile());
        }
        return ret;
    }
}
