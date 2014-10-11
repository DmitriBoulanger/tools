package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomId.JAR;
import static de.dbo.tools.utils.print.Print.padRight;

import edu.emory.mathcs.backport.java.util.Collections;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

/**
 * Parser/resolver for POM-files
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
	
	public static final int GROUP_PRINT_WIDTH = 40;
	public static final int ARTIFCAT_PRINT_WIDTH = 40;
	
	private final String group;
	private final String artifact;
	private final String version;
	private final String type;
	
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
	 * creates parsed POM-instance from maven-project
	 * @param project maven-project instance
	 * @throws PomException
	 */
	private Pom(final MavenProject project) throws PomException {
		this(trim(project.getGroupId()), trim(project.getArtifactId())
				,  trim(project.getPackaging())
				,  trim(project.getVersion())
				, project.getFile(), project);
	}

	@Override
	public final int compareTo(Pom o) {
		return this.id().compareTo(o.id());
	}
	
	@Override
	public final String toString() {
		return id() + PomId.SEPARATOR + version;
	}
	
	public PomId id() {
		return new PomId(group,artifact,type);
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
	
	private Pom(final String group, final String artifact, final String type, final String version
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
		this.type = type;
		this.version = (!nn(version) || version.equals(VERSION_PARAMETER) )? NULL_VERSION : version;
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
	
	public StringBuilder print() throws PomException {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\t - File                 : " + mavenProject.getFile().getAbsolutePath());
		sb.append("\n\t - id                   : " + id());
		sb.append("\n\t - Version              : " + version);
		sb.append("\n\t - GroupId              : " + mavenProject.getGroupId());
		sb.append("\n\t - ArtifactId           : " + mavenProject.getArtifactId());
		sb.append("\n\t - Packaging            : " + mavenProject.getPackaging());
		sb.append("\n\t - Version              : " + mavenProject.getVersion());
		sb.append("\n\t - Parent               : " + mavenProject.getParent());
		sb.append("\n\t - Artifact             : " + mavenProject.getArtifact());
		sb.append("\n\t - Properties           : " + print(mavenProject.getProperties()));
		sb.append("\n\t - Dependencies         : " + printDependencies(mavenProject));
		sb.append("\n\t - DependencyManagement : " + printDependencyManagement(mavenProject));
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
			final String group = resolve(trim(dependency.getGroupId()),mavenProject) ;
			final String artifact = trim(dependency.getArtifactId());
			final String version = resolve(trim(dependency.getVersion()),mavenProject);
			final Pom pom = new Pom(group,artifact,JAR,version, null, null);
			poms.add(pom);
		}
		return poms;
	}
	
	private static final String resolve(final String parameter, final MavenProject mavenProject) {
		final String value;
		if (!nn(parameter)) {
			value = null;
		}
		else if (parameter.equals(PROJECT_VERSION_PARAMETER) && nn(mavenProject.getVersion()))  {
			value = mavenProject.getVersion();
		} 
		else if (parameter.equals(PROJECT_GROUP_PARAMETER) && nn(mavenProject.getGroupId()))  {
			value = mavenProject.getGroupId();
		} 
		else if (isParameter(parameter) && null!=mavenProject.getProperties() 
			 && !mavenProject.getProperties().isEmpty() )  {
			value = tryToResolve(parameter, mavenProject);
		}
		else {
			value = parameter;
		}
		return value;
	}
	
	private static final String  tryToResolve(final String version, final MavenProject mavenProject) {
		final Properties properties = mavenProject.getProperties();
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
	
	private static final StringBuilder printDependencies(final MavenProject mavenProject) throws PomException {
		@SuppressWarnings("unchecked")
		final List<Dependency> dependencies = (List<Dependency>) mavenProject.getDependencies();
		return printDependencies(dependencies, mavenProject);
	}
	
	private static final StringBuilder printDependencies(final List<Dependency> dependencies, final MavenProject mavenProject) 
			throws PomException {
		final StringBuilder sb = new StringBuilder();
		if (null==dependencies) {
			sb.append("NULL");
			return sb;
		}
		if (dependencies.isEmpty()) {
			sb.append("{}");
			return sb;
		}  
		final List<Pom> poms = new ArrayList<Pom>();
		for (final Dependency dependency:dependencies) {
			final String group = resolve(dependency.getGroupId(),mavenProject);
			final String artifact = dependency.getArtifactId();
			final String version = resolve(dependency.getVersion(),mavenProject);
			final Pom pom = new Pom(group,artifact,JAR,version,null,null);
			poms.add(pom);
		}
		Collections.sort(poms);
		for (final Pom pom:poms) {
			sb.append("\n\t   -- " + padRight(pom.getGroup(),GROUP_PRINT_WIDTH)
					+  padRight(pom.getArtifact(),ARTIFCAT_PRINT_WIDTH) 
					+ " " + pom.getVersion());
		}
		return sb;
	}
	
	private static final StringBuilder printDependencyManagement(final MavenProject mavenProject) 
			throws PomException {
		final StringBuilder sb = new StringBuilder();
		final DependencyManagement dependencyManagement =  mavenProject.getDependencyManagement();
		if (null==dependencyManagement) {
			sb.append("NULL");
			return sb;
		}
		final List<Dependency> dependencies = (List<Dependency>) dependencyManagement.getDependencies();
		return printDependencies(dependencies, mavenProject);
	}
	
	// HELPERS
	
	private static final StringBuilder print(final Properties properties) {
		final StringBuilder sb = new StringBuilder();
		if (null==properties) {
			sb.append("NULL");
			return sb;
		}
		if (properties.isEmpty()) {
			sb.append("[]");
			return sb;
		} 

		final List<String> keys = new ArrayList<String>();
		for (final Object key: properties.keySet()) {
			keys.add((String) key);
		}
		Collections.sort(keys);
		for (final String key: keys) {
			sb.append("\n\t\t -- " + key +  " = " + properties.getProperty(key));
		}

		return sb;
	}
	
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
		return x.replace(PREFIX_PARAMETER, "").replace("{", "").replace("}","");
	}
	
	private static final String set(final String x) {
		if (null==x) {
			return null;
		}
		return "{" + trim(x)+ "}";
	}
	
	private static final boolean nn(String x) {
		return null!=x && 0!=x.trim().length();
	}
	
	private static final boolean isParameter(final String x) {
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
