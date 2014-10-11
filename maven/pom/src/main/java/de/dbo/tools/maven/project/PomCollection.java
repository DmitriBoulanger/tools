package de.dbo.tools.maven.project;

import static de.dbo.tools.utils.print.Print.*;

import de.dbo.tools.resources.SpringPathResources;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

/**
 *  Hash-map group --  list<pom-versions>
 *   
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and 
 *           only incidentally for computers to execute 
 *
 */
public class PomCollection extends HashMap<String, PomInstances>  {
	private static final long serialVersionUID = 5288975098326000671L;
	
	public static final PomCollection newInstance(final String pattern) throws IOException, PomException  {
		final List<Resource> items = SpringPathResources.resoucesInFilesystem(pattern);
		final PomCollection pomCollection = new PomCollection();
		for (Resource resource:items) {
			final File file = resource.getFile();
			final Pom pom = new Pom(file);
			pomCollection.addArtifact(pom);
			final MavenProject project = pom.getMavenProject();
			if (null!=project) {
				final List<Pom> dependencies = Pom.dependencies(project);
				if (!dependencies.isEmpty()) {
					for (final Pom dependency: dependencies) {
						pomCollection.addArtifact(dependency);
					}
				}
			}
		}
		return pomCollection;
	}
	
	private PomCollection() {
		
	}

	public void addArtifact(final Pom pom) {
		final String group = pom.getGroup();
		final PomInstances poms;
		if ( !containsKey(group) ) {
			poms = new PomInstances();
			put(group, poms);
		} else {
			poms = get(group);
		}
		poms.addPom(pom);
	}
	
	public StringBuilder print() {
		final StringBuilder ret = new StringBuilder();
		final List<String> groups = new ArrayList<String>(keySet());
		Collections.sort(groups);
		for (final String group:groups) {
			final PomInstances poms =  get(group);
			final List<PomId> ids = new ArrayList<PomId>(poms.keySet());
			Collections.sort(ids); 
			for (final PomId id:ids) {
				ret.append("\n - " + padRight(id.getGroup(),35) 
						+ " " + padRight(id.getArtifact(),35) 
						+ " " + poms.print(id));
			}
		} 
		return ret;
	}
}