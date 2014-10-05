package de.dbo.tools.maven.Project;
 
import static de.dbo.tools.utils.print.Print.padRight;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

/**
 * 
 * @author Dmitri Boulanger, Hombach
 *
 *         D. Knuth: Programs are meant to be read by humans and only
 *         incidentally for computers to execute
 *
 */
public final class Pom  {
	
	private final MavenProject mavenProject;
	private final String pomPath;
	
	public Pom(final String path) throws Exception {
		mavenProject = newMavenProject(readPom(path));
		pomPath = readPom(path).getAbsolutePath();
	}
	

	public String getPomPath() {
		return pomPath;
	}

	private static final File readPom(final String path)  throws Exception {
		final File pomFile = new File(path);
		if(!pomFile.exists()) {
			throw new Exception("Pom-file doesn't exist: " + path);
		}
		if(!pomFile.canRead()) {
			throw new Exception("Pom-file is not readable: " + path);
		}
		return pomFile;
	}
	
	public static final MavenProject newMavenProject(final File pomFile) throws Exception{
		if (null==pomFile) {
			return null;
		}
		final Model model;
		FileReader reader = null;
		final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try {
			reader = new FileReader(pomFile);
			model = mavenreader.read(reader);
		} catch (Exception e) {
			throw new Exception("Can't create model from POM " + pomFile.getAbsolutePath(), e);
		}  finally {
			if (null!=reader) {
				try {
					reader.close();
				} catch (Throwable e) {
					throw new Exception("Can't close reader from POM "+ pomFile.getAbsolutePath(), e);
				}
			}
		}
		return new MavenProject(model);
	}
	
	public StringBuilder print() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\t - POM          : " + getPomPath());
		sb.append("\n\t - Id           : " + mavenProject.getId());
		sb.append("\n\t - Version      : " + mavenProject.getVersion());
		sb.append("\n\t - Parent       : " + mavenProject.getParent());
		sb.append("\n\t - Artifact     : " + mavenProject.getArtifact());
		sb.append("\n\t - Properties   : " + printProperties(mavenProject));
		sb.append("\n\t - Dependencies : " + printDependencies(mavenProject));
        return sb;
	}
	
	private static final StringBuilder printDependencies(final MavenProject mavenProject) {
		@SuppressWarnings("unchecked")
		final List<Dependency> dependencies = (List<Dependency>) mavenProject.getDependencies();
		final StringBuilder sb = new StringBuilder();
		if (null==dependencies) {
			sb.append("NULL");
		}
		else if (dependencies.isEmpty()) {
			sb.append("[]");
		} else {
			for (final Dependency dependency:dependencies) {
				sb.append("\n\t\t -- " +  padRight(dependency.getArtifactId(),30) 
						+ " " + dependency.getVersion());
			}
		}
		return sb;
	}
	
	private static final StringBuilder printProperties(final MavenProject mavenProject) {
		final Properties properties =  mavenProject.getProperties();
		final StringBuilder sb = new StringBuilder();
		if (null==properties) {
			sb.append("NULL");
		}
		else if (properties.isEmpty()) {
			sb.append("[]");
		} else {
			for (final Object key: properties.keySet()) {
				sb.append("\n\t\t -- " + key +  " = " + properties.getProperty((String)key));
			}
		}
		return sb;
	}
}