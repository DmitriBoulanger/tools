package de.dbo.tools.maven.Project;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomTest  {
	private static final Logger log = LoggerFactory.getLogger(PomTest.class);

	public PomTest() {
		 
	}
	
	@Test
	public void test() throws Exception {
		final Pom pom = new Pom("pom.xml");
		log.info("Maven project: " + pom.print());
	}
}
