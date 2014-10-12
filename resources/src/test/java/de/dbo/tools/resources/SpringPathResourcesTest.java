package de.dbo.tools.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import de.dbo.logging.LoggerInitialization;

public class SpringPathResourcesTest {
    private static final Logger log = LoggerFactory.getLogger(SpringPathResourcesTest.class);

    @BeforeClass
    public static void initLogger() {
        try {
            LoggerInitialization.initializeLogger("de-dbo-tools-resources-log4j.properties");
        }
        catch(Exception e) {
            throw new RuntimeException("Can't initilize Log4-logger", e);
        }
    }
    
    @Test
    public final void test_resoucesInFilesystem() throws Exception {
        final String pattern = "./**/*.xml";
        final List<Resource> items = SpringPathResources.resoucesInFilesystem(pattern);
        log.info(print("Filesystem Resource names for pattern [" + pattern + "]", items));
    }
    
    @Test
    public final void test_resoucesInFilesystem2() throws Exception {
        final String pattern = "D:/JAVA/WORKSPACES/ws/samples.git/**/pom.xml";
        final List<Resource> items = SpringPathResources.resoucesInFilesystem(pattern);
        log.info(print("Filesystem Resource names for pattern [" + pattern + "]", items));
    }

    @Test
    public final void test_ResourceNames_AsFiles() throws Exception {
        final String pattern = "/.gitkeep";
        final List<URL> items = SpringPathResources.resourceFilenamesInClasspath(pattern);
        log.info(print("Classpath Resource names for pattern [" + pattern + "]", items));
        assertFalse(items.isEmpty());
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public final void test_Files_AsResourceNames_FileNotFoundException() throws Exception {
        final String pattern = "META-INF/*.MF"; //
        SpringPathResources.resourceFilesInClasspath(pattern);
    }

    @Test
    public final void test_ResourceNames_Manifest() throws Exception {
        final String pattern = "META-INF/*.MF"; //
        final List<URL> items = SpringPathResources.resourceFilenamesInClasspath(pattern);
        log.info(print("Classpath Resource names for pattern [" + pattern + "]", items));
        assertFalse(items.isEmpty());
    }

    @Test
    public final void test_ResourceNames_Manifest_XmlContent() throws Exception {
        final String pattern = "/META-INF/maven/**/*.xml";
        final List<URL> items = SpringPathResources.resourceFilenamesInClasspath(pattern);
        log.info(print("Classpath Resource names for pattern [" + pattern + "]", items));
        assertFalse(items.isEmpty());
    }

    @Test
    public final void test_ResourceNames_Manifest_PropertiesContent() throws IOException {
        final String pattern = "/META-INF/maven/**/*.properties";
        final List<URL> items = SpringPathResources.resourceFilenamesInClasspath(pattern);
        log.info(print("Classpath Resource names for pattern [" + pattern + "]", items));
        assertFalse(items.isEmpty());
    }

    @Test
    public final void test_ResourceNames_Classes() throws Exception {
        final String pattern = "/org/springframework/core/**/*.class"; // in spring-core-3.1.2.RELEASE.jar
        final List<URL> urls = SpringPathResources.resourceFilenamesInClasspath(pattern);
        assertFalse(urls.isEmpty());
        final List<String> paths = new ArrayList<String>();
        for (final URL url : urls) {
            final String urlString = url.toString();
            final int idx = urlString.indexOf("!") + 1;
            paths.add(urlString.substring(idx));
        }
        if (log.isDebugEnabled()) {
            Collections.sort(paths);
            log.debug(print("Classpath Resource paths for pattern [" + pattern + "]", paths));
        }
        final String prefix = "/org/springframework/core";
        assertTrue(paths.contains(prefix + "/AliasRegistry.class")); // in core
        assertTrue(paths.contains(prefix + "/annotation/AnnotationAttributes.class")); // in core.annotation
        assertTrue(paths.contains(prefix + "/serializer/support/DeserializingConverter.class")); // in core.serializer.support
    }

    @Test
    public final void test_Files_DeProperties() throws Exception {
        final String pattern = "/de/**/*.properties";
        final List<File> files = SpringPathResources.resourceFilesInClasspath(pattern);
        log.info(print("Classpath Files for pattern [" + pattern + "]", files));
        assertEquals(1, files.size());
    }

    @Test
    public final void test_Files_AnyProperties() throws Exception {
        final String pattern = "/**/*.properties";
        final List<File> files = SpringPathResources.resourceFilesInClasspath(pattern);
        log.info(print("Classpath Files for pattern [" + pattern + "]", files));
        assertEquals(2, files.size());
    }

    @Test
    public final void test_Files_DeXml() throws Exception {
        final String pattern = "/de/**/*.xml";
        final List<File> files = SpringPathResources.resourceFilesInClasspath(pattern);
        log.info(print("Classpath Files for pattern [" + pattern + "]", files));
        assertFalse(files.isEmpty());
    }

    @Test
    public final void test_Files_AnyXml() throws Exception {
        final String pattern = "/**/*.xml";
        final List<File> files = SpringPathResources.resourceFilesInClasspath(pattern);
        log.info(print("Classpath Files for pattern [" + pattern + "]", files));
        assertEquals(2, files.size());
    }

    // HELPERS
    
    private static final String print(final String title, final List<?> items) {
        final StringBuilder sb = new StringBuilder(title + ":");
        for (final Object item : items) {
            sb.append("\n\t - " + item);
        }
        return sb.toString();
    }

}
