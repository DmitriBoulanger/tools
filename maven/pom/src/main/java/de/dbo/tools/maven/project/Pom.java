package de.dbo.tools.maven.project;

import static de.dbo.tools.utils.print.Print.padRight;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

/**
 * Important POM-information
 * 
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and 
 *           only incidentally for computers to execute 
 *
 */
public final class Pom implements Comparable<Pom> {
	
	public final String NULL_VERSION  = "null";
	public final String NULL_GROUP    = "null";
	public final String NULL_ARTIFACT = "null";
	
	private static final String PREFIX_PARAMETER   = "$";
	private static final String GROUP_PARAMETER    = parameter("groupId");
	private static final String ARTIFACT_PARAMETER = parameter("artifactId");
	private static final String VERSION_PARAMETER  = parameter("version");
	
	private static final String PROJECT_VERSION_PARAMETER  = parameter("project.version");
	private static final String PROJECT_GROUP_PARAMETER    = parameter("project.groupId");
	
	private final String group;
	private final String artifact;
	private final String version;
	
	private final File file;
	private final MavenProject mavenProject;
	
	public Pom(final String path) throws PomException {
		this(pomFile(path));
	}
	
	public Pom(final File pomFile) throws PomException {
		this(newMavenProject(pomFile));
	}
	
	public Pom(final MavenProject project) throws PomException {
		this(trim(project.getGroupId()), trim(project.getArtifactId()),  trim(project.getVersion())
				, project.getFile(), project);
	}
	 
	private Pom(final String group, final String artifact, final String version
			, final File file, final MavenProject mavenProject) 
			throws PomException {
		if (!nn(group) || !nn(artifact)) {
			throw new PomException("Incomplte POM "+group+":"+artifact+":"+version
					+ " File: " + file);
		}
		this.mavenProject = mavenProject;
		this.file = file;
		
		this.group = group.equals(GROUP_PARAMETER)? NULL_GROUP : group;
		this.artifact = artifact.equals(ARTIFACT_PARAMETER)? NULL_ARTIFACT : artifact;
		this.version = (!nn(version) || version.equals(VERSION_PARAMETER) )? NULL_VERSION : version;
	}
	
	@Override
	public final int compareTo(Pom o) {
		return this.toString().compareTo(o.toString());
	}
	
	@Override
	public final String toString() {
		return id() + PomId.SEPARATOR + version;
	}
	
	public PomId id() {
		return new PomId(group,artifact);
	}
	
	public String getGroup() {
		return group;
	}

	public String getArtifact() {
		return artifact;
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
	
	public static final File pomFile(final String path)  throws PomException {
		final File pomFile = new File(path);
		if(!pomFile.exists()) {
			throw new PomException("Pom-file doesn't exist: " + path);
		}
		if(!pomFile.canRead()) {
			throw new PomException("Pom-file is not readable: " + path);
		}
		return pomFile;
	}
	
	public static final MavenProject newMavenProject(final File pomFile) throws PomException {
		if (null==pomFile) {
			return null;
		}
		try {
			final MavenProject ret = newMavenProject(new FileInputStream(pomFile));
			ret.setFile(pomFile);
			return ret;
		} catch (Exception e) {
			throw new PomException("Can't process POM-fie " + pomFile + ": ", e);
		}
	}
	
	private static final MavenProject newMavenProject(final InputStream is) throws PomException{
		if (null==is) {
			return null;
		}
		final Model model;
		final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try {
			model = mavenreader.read(is);
		} catch (Exception e) {
			throw new PomException("Can't create model from POM input-stream: ", e);
		}  finally {
				try {
					is.close();
				} catch (Throwable e) {
					throw new PomException("Can't close  POM nput-stream: ", e);
				}
		}
		return new MavenProject(model);
	}
	
	public StringBuilder print() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\t - File         : " + mavenProject.getFile().getAbsolutePath());
		sb.append("\n\t - id           : " + id());
		sb.append("\n\t - Version      : " + version);
		sb.append("\n\t - GroupId      : " + mavenProject.getGroupId());
		sb.append("\n\t - ArtifactId   : " + mavenProject.getArtifactId());
		sb.append("\n\t - Packaging    : " + mavenProject.getPackaging());
		sb.append("\n\t - Version      : " + mavenProject.getVersion());
		sb.append("\n\t - Parent       : " + mavenProject.getParent());
		sb.append("\n\t - Artifact     : " + mavenProject.getArtifact());
		sb.append("\n\t - Properties   : " + printProperties(mavenProject));
		sb.append("\n\t - Dependencies : " + printDependencies(mavenProject));
        return sb;
	}
	
	public static final List<Pom> dependencies(final MavenProject mavenProject) throws PomException {
		
		@SuppressWarnings("unchecked")
		final List<Dependency> dependencies = (List<Dependency>) mavenProject.getDependencies();
		if (null==dependencies) {
			return null;
		}
		final List<Pom> poms = new ArrayList<Pom>();
		if (dependencies.isEmpty()) {
			return poms;
		}
			for (final Dependency dependency:dependencies) {
				
				final String group = trim(dependency.getGroupId());
				final String groupValue;
				if (group.equals(PROJECT_GROUP_PARAMETER) && nn(mavenProject.getVersion()))  {
					groupValue = mavenProject.getGroupId();
				} else {
					groupValue = group;
				}
				
				final String artifact = trim(dependency.getArtifactId());
				final String version = trim(dependency.getVersion());
				final String versionValue;
				if (!nn(version)) {
					versionValue = null;
				}
				else if (version.equals(PROJECT_VERSION_PARAMETER) && nn(mavenProject.getVersion()))  {
					versionValue = mavenProject.getVersion();
				} 
				else if (isParameter(version) && null!=mavenProject.getProperties() 
					 && !mavenProject.getProperties().isEmpty() )  {
					versionValue = tryToResolve(version, mavenProject);
				}
				else {
					versionValue = version;
				}
				
				final Pom pom = new Pom(groupValue,artifact,versionValue, null, null);
				poms.add(pom);
			}
		return poms;
	}
	
	private static final String  tryToResolve(final String version, final MavenProject mavenProject) {
		final Properties properties = mavenProject.getPreservedProperties();
		if (null==properties || properties.isEmpty()) {
			return version;
		}
		final String name = parameterName(version);
		if (!nn(name)) {
			return version;
		}
		final String value =  properties.getProperty(name);
		if (nn(value)) {
			return value;
		}
		
		return version;
		
		
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
				sb.append("\n\t\t -- " + padRight(dependency.getGroupId(),20)
			            +  padRight(dependency.getArtifactId(),20) 
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

	// HELPERS
	
	private static final String parameter(final String x) {
		if (null==x) {
			return null;
		}
		return PREFIX_PARAMETER + set(x);
	}
	
	private static final String parameterName(final String x) {
		if (null==x) {
			return null;
		}
		if (!x.startsWith(PREFIX_PARAMETER)) {
			return null;
		}
		return x.replaceAll(PREFIX_PARAMETER, "").replaceAll("{", "").replaceAll("}","");
	}
	
	private static final String set(final String x) {
		if (null==x) {
			return null;
		}
		return "{" + trim(x )+ "}";
	}
	
	private static final boolean nn(String x) {
		return null!=x && 0!=x.trim().length();
	}
	
	private static final boolean isParameter(String x) {
		if (null==x) {
			return false;
		}
		return x.startsWith(PREFIX_PARAMETER);
	}
	
	
	
	private static final String trim(final String x) {
		if (null==x) {
			return null;
		}
		return x.trim().replace(" ", "");
	}
	

}
