package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomResolver.ARTIFACT_PARAMETER;
import static de.dbo.tools.maven.project.PomResolver.GROUP_PARAMETER;
import static de.dbo.tools.maven.project.PomResolver.NULL_ARTIFACT;
import static de.dbo.tools.maven.project.PomResolver.NULL_GROUP;
import static de.dbo.tools.maven.project.PomResolver.NULL_VERSION;
import static de.dbo.tools.maven.project.PomResolver.VERSION_PARAMETER;
import static de.dbo.tools.maven.project.PomResolver.resolveParameter;
import static de.dbo.tools.maven.project.PomResolver.newMavenProject;
import static de.dbo.tools.maven.project.PomResolver.pomFile;
import static de.dbo.tools.maven.project.PomResolver.trim;
import static de.dbo.tools.maven.project.PomResolver.nn;

import java.io.File;

import org.apache.maven.project.MavenProject;

/**
 * POM Representation
 * 
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and 
 *           only incidentally for computers to execute 
 *
 */
public final class Pom implements Comparable<Pom> {
	
	private final String group;
	private final String artifact;
	private final String version;
	private final String type;
	
	private final PomId pomId;
	private final String stringValue;
	
	/* File and/or MavenProject can be null! */
	private final File file;
	private final MavenProject mavenProject;
	
	/**
	 * creates parsed POM-instance
	 * @param path path of a pom.xml 
	 * @throws PomException
	 */
	public Pom(final String path) throws PomException {
		this(pomFile(path));
	}
	
	/**
	 * creates parsed POM-instance
	 * @param pomFile file of a pom.xml 
	 * @throws PomException
	 */
	public Pom(final File pomFile) throws PomException {
		this(newMavenProject(pomFile));
	}
	
	/**
	 * creates virtual POM-instance (no file neither project are available)
	 * @param group
	 * @param artifact
	 * @param type
	 * @param version
	 * @throws PomException
	 */
	Pom(final String group, final String artifact, final String type, final String version) throws PomException{
		this(group,artifact,type,version, null, null);
	}
	
	/**
	 * creates parsed POM-instance from maven-project
	 * @param project maven-project instance
	 * @throws PomException
	 */
	private Pom(final MavenProject project) throws PomException {
		this(trim(project.getGroupId())
				, trim(project.getArtifactId())
				, trim(project.getPackaging())
				, trim(project.getVersion())
				, project.getFile()
				, project);
	}

	/**
	 * creates an instance if at least group and artifact are available
	 * @param group
	 * @param artifact
	 * @param type
	 * @param version
	 * @param file
	 * @param mavenProject
	 * @throws PomException
	 */
	private Pom(final String group
			, final String artifact
			, final String type
			, final String version
			, final File file
			, final MavenProject mavenProject) throws PomException {
		if (!nn(group) || !nn(artifact)) {
			throw new PomException("Incomplete POM " + group + ":" + artifact + ":" + version + " File: " + file);
		}
		this.mavenProject = mavenProject;
		this.file = file;
		this.type = trim(type);

		if (null==this.mavenProject) {
			this.group = group.equals(GROUP_PARAMETER) ? NULL_GROUP : group;
			this.artifact = artifact.equals(ARTIFACT_PARAMETER) ? NULL_ARTIFACT: artifact;
			this.version = (!nn(version) || version.equals(VERSION_PARAMETER)) ? NULL_VERSION: version;
		} else {
			this.group = resolveParameter(group, mavenProject);
			this.artifact = resolveParameter(artifact, mavenProject);
			this.version = resolveParameter(version, mavenProject);
		}
		this.pomId = new PomId(this.group, this.artifact, this.type);
		this.stringValue = this.id() + PomId.SEPARATOR + this.version;
	}
	 
	@Override
	public final int compareTo(Pom o) {
		return this.id().compareTo(o.id());
	}
	
	@Override
	public final String toString() {
		return stringValue;
	}
	
	public PomId id() {
		return pomId;
	}
	
	public String getGroup() {
		return group;
	}

	public String getArtifact() {
		return artifact;
	}
	
	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}
	
	public MavenProject getMavenProject() {
		return mavenProject;
	}

	public File getFile() {
		return file;
	}
}
