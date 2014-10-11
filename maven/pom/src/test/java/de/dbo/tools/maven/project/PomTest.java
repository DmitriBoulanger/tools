package de.dbo.tools.maven.project;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomTest  {
	private static final Logger log = LoggerFactory.getLogger(PomTest.class);

	@Test
	public void testPom() throws Exception {
		final Pom pom = new Pom("D:/JAVA/WORKSPACES/ws/root.git/dependency-management/pom.xml");
		log.info("POM: " + pom.print());
	}
	
	@Test
	public void testPomCollection() throws Exception {
		final String pattern = "D:/JAVA/WORKSPACES/ws/jbehave.git/**/pom.xml";
        final PomCollection pomCollection = PomCollection.newInstance(pattern);
		log.info("POM Collection: " + pomCollection.print());
	}
}
