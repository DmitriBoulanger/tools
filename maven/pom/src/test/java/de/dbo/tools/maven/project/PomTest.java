package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomPrint.print;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PomTest  {
	private static final Logger log = LoggerFactory.getLogger(PomTest.class);
	
	private static final File JAR_FILE = 
			new File("X:/fa/ga/ga/ga/repo-XX-sitory/org/springframework/spring-core/3.1.2.RELEASE/spring-core-3.1.2.RELEASE.jar");
	

	/**
	 * check that specified JAR-file has the given POM-ID and specified version
	 */
	@Test 
	public void test00_PomFile() {
		final PomId pomId = new PomId("org.springframework", "spring-core","jar");
		assertTrue( PomFile.hasIt(pomId,JAR_FILE) );
		assertEquals("3.1.2.RELEASE", PomFile.pomVersion(JAR_FILE));
	}
	
	/**
	 * check that JAR-file occurs in the dependency-management of the specified POM.
	 * The POM is supposed to have a dependency-management section
	 */
	@Test
	public void test01_PomDependecyManagementFile() throws Exception {
		final String path = "D:/JAVA/WORKSPACES/ws/root.git/dependency-management/pom.xml";
		final Pom pom = new Pom(path);
		assertTrue(PomFile.isManaged(pom.dependencyManagement(), JAR_FILE));
	}

	@Test
	public void test02_Pom() throws Exception {
		final String path = "pom.xml";
		final Pom pom = new Pom(path);
		log.info("POM: " + print(pom));
	}
	
	@Test
	public void test03_PomDependecyManagement() throws Exception {
		final String path = "D:/JAVA/WORKSPACES/ws/root.git/dependency-management/pom.xml";
		final Pom pom = new Pom(path);
		log.info("POM with Dependency management : " + print(pom));
	}
	
	@Test
	public void test04_PomCollection() throws Exception {
		final String pattern = "../../**/pom.xml";
        final PomCollection pomCollection = PomCollection.newInstance(pattern);
		log.info("POM Collection for pattern [" + pattern +"]: " + print(pomCollection));
	}
}
