package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomPrint.print;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomTest  {
	private static final Logger log = LoggerFactory.getLogger(PomTest.class);

	private static final File JAR_FILE =
			new File("X:/fa/ga/ga/ga/repo-XX-sitory/org/springframework/spring-core/3.1.2.RELEASE/spring-core-3.1.2.RELEASE.jar");

	private static final String DEPENDENCY_MANAGEMENT_PATH = "src/test/resources/test-pom_dependency-management.xml";


	/**
	 * check that specified JAR_TYPE-file has the given POM-ID and specified version
	 */
	@Test
    public void testPomFile() {
		final PomId pomId = new PomId("org.springframework", "spring-core","jar");
		assertTrue( PomFile.hasIt(pomId,JAR_FILE) );
		assertEquals("3.1.2.RELEASE", PomFile.pomVersion(JAR_FILE));
	}

	/**
	 * check that JAR_TYPE-file occurs in the dependency-management of the specified POM.
	 * The POM is supposed to have a dependency-management section
	 */
	@Test
    public void testPomDependecyManagementFile() throws Exception {
		final Pom pom = new Pom(DEPENDENCY_MANAGEMENT_PATH);
        assertNotNull(PomFile.isManaged(pom.dependencyManagement(), JAR_FILE));
	}

	/** print */
	@Test
    public void testPomCollectionPrint() throws Exception {
		final String pattern = "../../**/pom.xml";
        final Pom management = new Pom("src/test/resources/test-pom_dependency-management.xml");
        final PomCollection pomCollection = PomCollection.newInstance(pattern);
        final PomPrint pomPrint = new PomPrint();
        log.info("POM Collection for pattern [" + pattern + "]: " + pomPrint.print(pomCollection, management));
        pomPrint.error(log);
        pomPrint.warn(log);
	}

    @Test
    public void testJarToPom() throws Exception {
        final Pom pom = PomFile.pom("de\\com\\mchange\\mchange-commons-java\\0.2.3.3\\mchange-commons-java-0.2.3.3.jar");
        log.info("POM from JAR: " + PomPrint.print(pom));
    }

    @Test
    public void testPomFilter() throws Exception {
        final String groupPlus = "group-plus";
        final String artifactPlus = "atrtifact-plus";
        final String groupMinus = "group-minus";
        final String artifactMinus = "atrtifact-minus";

        final PomFilter pomFilter = new PomFilter();
        pomFilter.setGroupPlus(groupPlus);
        pomFilter.setArtifactPlus(artifactPlus);
        pomFilter.setGroupMinus(groupMinus);
        pomFilter.setArtifactMinus(artifactMinus);

        assertTrue(pomFilter.isGroupPlus("fafafa-" + groupPlus + ".hdhdhdh.gddgdg"));
        assertTrue(pomFilter.isArtifactPlus("fajaj-" + artifactPlus + "-kaevs"));

        assertTrue(pomFilter.isGroupMinus("fafafa-" + groupMinus + ".hdhdhdh.gddgdg"));
        assertTrue(pomFilter.isArtifactMinus("fajaj-" + artifactMinus + "-kaevs"));

        assertFalse(pomFilter.isGroupPlus("fafafa-" + groupMinus + ".hdhdhdh.gddgdg"));
        assertFalse(pomFilter.isArtifactPlus("fajaj-" + artifactMinus + "-kaevs"));

        assertFalse(pomFilter.isGroupMinus("fafafa-" + groupPlus + ".hdhdhdh.gddgdg"));
        assertFalse(pomFilter.isArtifactMinus("fajaj-" + artifactPlus + "-kaevs"));

        assertFalse(pomFilter.isGroupPlus("group-xxxx"));
        assertFalse(pomFilter.isArtifactPlus("atrtifact-cxcxcx"));
    }

    @Test
    public void testPomFilterEmpty() throws Exception {

        final String groupPlus = "group-plus";
        final String artifactPlus = "atrtifact-plus";
        final String groupMinus = "group-minus";
        final String artifactMinus = "atrtifact-minus";

        final PomFilter pomFilter = new PomFilter();

        assertTrue(pomFilter.isGroupPlus("fafafa-" + groupPlus + ".hdhdhdh.gddgdg"));
        assertTrue(pomFilter.isArtifactPlus("fajaj-" + artifactPlus + "-kaevs"));

        assertFalse(pomFilter.isGroupMinus("fafafa-" + groupMinus + ".hdhdhdh.gddgdg"));
        assertFalse(pomFilter.isArtifactMinus("fajaj-" + artifactMinus + "-kaevs"));

        assertTrue(pomFilter.isGroupPlus("fafafa-" + groupMinus + ".hdhdhdh.gddgdg"));
        assertTrue(pomFilter.isArtifactPlus("fajaj-" + artifactMinus + "-kaevs"));
        assertFalse(pomFilter.isGroupMinus("fafafa-" + groupPlus + ".hdhdhdh.gddgdg"));
        assertFalse(pomFilter.isArtifactMinus("fajaj-" + artifactPlus + "-kaevs"));

        assertTrue(pomFilter.isGroupPlus("group-xxxx"));
        assertTrue(pomFilter.isArtifactPlus("atrtifact-cxcxcx"));
    }

    @Test
    public void testPomFilterPomId() throws Exception {
        final String groupPlus = "group-plus";
        final String artifactPlus = "atrtifact-plus";
        final String groupMinus = "group-minus";
        final String artifactMinus = "atrtifact-minus";

        final PomFilter pomFilter = new PomFilter();
        pomFilter.setGroupPlus(groupPlus);
        pomFilter.setArtifactPlus(artifactPlus);
        pomFilter.setGroupMinus(groupMinus);
        pomFilter.setArtifactMinus(artifactMinus);

        assertTrue(pomFilter.isPlus(new PomId(groupPlus, artifactPlus, "test")));
        assertFalse(pomFilter.isPlus(new PomId(groupPlus, "gdgdgd", "test")));
        assertFalse(pomFilter.isPlus(new PomId("hshshs", artifactPlus, "test")));
        assertFalse(pomFilter.isPlus(new PomId("hshshs", "jsajsajsjas", "test")));

        final PomFilter pomFilter2 = new PomFilter();
        pomFilter2.setGroupPlus(groupPlus);
        pomFilter2.setGroupMinus(groupMinus);

        assertTrue(pomFilter2.isPlus(new PomId(groupPlus, artifactPlus, "test")));
        assertTrue(pomFilter2.isPlus(new PomId(groupPlus, "gdgdgd", "test")));
        assertFalse(pomFilter2.isPlus(new PomId("hshshs", artifactPlus, "test")));
        assertFalse(pomFilter2.isPlus(new PomId("hshshs", "jsajsajsjas", "test")));
    }

	/** print */
	@Test
    public void testPomPrint() throws Exception {
		final String path = "pom.xml";
		final Pom pom = new Pom(path);
		log.info("POM: " + print(pom));
	}

	/** print */
	@Test
    public void testPomDependecyManagementPrint() throws Exception {
		final Pom pom = new Pom(DEPENDENCY_MANAGEMENT_PATH);
		log.info("POM with Dependency management : " + print(pom));
	}


}
