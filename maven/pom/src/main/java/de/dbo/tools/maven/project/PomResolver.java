package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomId.UNKNOWN_TYPE;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser/resolver for POM-files
 *
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and
 *           only incidentally for computers to execute
 *
 */
final class PomResolver  {
	private static final Logger log = LoggerFactory.getLogger(PomResolver.class);

	public static final String NULL_GROUP    = "null";
	public static final String NULL_ARTIFACT = "null";
	public static final String NULL_VERSION  = "null";
	public static final String NULL_VALUE    = "null";

	private static final String PREFIX_PARAMETER    = "$";
	private static final String NAMEBEGIN_PARAMETER = "{";
	private static final String NAMEEND_PARAMETER   = "}";

	static final String GROUP_PARAMETER    = parameter("groupId");
	static final String ARTIFACT_PARAMETER = parameter("artifactId");
	static final String VERSION_PARAMETER  = parameter("version");

	private static final String PROJECT_VERSION_PARAMETER  = parameter("project.version");
	private static final String PROJECT_GROUP_PARAMETER    = parameter("project.groupId");

	public static final File pomFile(final String path) throws PomException {
		if (!nn(path)) {
			throw new PomException("POM-path empty string or null!");
		}
		final File pomFile = new File(path);
		if(!pomFile.exists()) {
            throw new PomException("Pom-file doesn't exist: " + pomFile.getAbsolutePath());
		}
		if(!pomFile.canRead()) {
            throw new PomException("Pom-file is not readable: " + pomFile.getAbsolutePath());
		}
		return new File(pomFile.getAbsolutePath());
	}

	static final MavenProject newMavenProject(final File pomFile) throws PomException {
		if (null==pomFile) {
			throw new PomException("POM-fie is null!");
		}
		final MavenProject ret;
		try {
			final FileInputStream is = new FileInputStream(pomFile);
			ret = newMavenProject(is);
			ret.setFile(pomFile);
			return ret;
		} catch (Throwable e) {
			throw new PomException("Can't create Maven-Project from POM-fie " + pomFile + ": ", e);
		}
	}

	private static final MavenProject newMavenProject(final InputStream is) throws PomException{
		if (null==is) {
			throw new PomException("POM input-stream is NULL while creating Maven Project!");
		}
		final Model model;
		final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try {
			model = mavenreader.read(is);
		} catch (Throwable e) {
			throw new PomException("Can't create model from POM input-stream: ", e);
		}  finally {
				try {
					is.close();
				} catch (Throwable e) {
					throw new PomException("Can't close POM input-stream after creating Maven Project: ", e);
				}
		}
		return new MavenProject(model);
	}

	static final List<Pom> dependencies(final MavenProject mavenProject) throws PomException {
		return asDependenciesToPoms(mavenProject.getDependencies(), mavenProject);
	}

	static final List<Pom> asDependenciesToPoms(final List<?> dependencies, final MavenProject mavenProject)
			throws PomException {
		if (null==dependencies) {
			return null;
		}
		final List<Pom> poms = new ArrayList<Pom>();
		if (dependencies.isEmpty()) {
			return poms;
		}
		for (final Object o:dependencies) {
			final Dependency dependency;
			try {
				dependency = (Dependency) o;
			} catch (Throwable e) {
				throw new PomException("Can't cast dependecy-objet "+ o + " into " + Dependency.class.getName() + "-instance");
			}
			poms.add(toPom(dependency,mavenProject));
		}
		return poms;
	}

	static final List<Pom> toPoms(final List<Dependency> dependencies, final MavenProject mavenProject)
			throws PomException {
		if (null==dependencies) {
			return null;
		}
		final List<Pom> poms = new ArrayList<Pom>();
		if (dependencies.isEmpty()) {
			return poms;
		}
		for (final Dependency dependency:dependencies) {
			poms.add(toPom(dependency,mavenProject));
		}
		return poms;
	}

	private static Pom toPom(final Dependency dependency, final MavenProject mavenProject) throws PomException {
		final String group = resolveParameter(trim(dependency.getGroupId()),mavenProject) ;
		final String artifact = trim(dependency.getArtifactId());
		final String version = resolveParameter(trim(dependency.getVersion()),mavenProject);
		final Pom pom = new Pom(group,artifact,UNKNOWN_TYPE,version); /* virtual instance! */
		return pom;
	}

	static final String resolveParameter(final String parameter, final MavenProject mavenProject) {
		if (!nn(parameter)) {
			return NULL_VALUE;
		}
		if (!isParameter(parameter) )  {
			return parameter;
		}

		// General parameter: property?

		if (null==mavenProject) {
            log.warn("Parameter " + parameter + " is not resolvable: Maven-project is null");
			return parameter;
		}

		if (parameter.equals(PROJECT_VERSION_PARAMETER) && nn(mavenProject.getVersion()))  {
			return mavenProject.getVersion();
		}
		if (parameter.equals(PROJECT_GROUP_PARAMETER) && nn(mavenProject.getGroupId()))  {
			return mavenProject.getGroupId();
		}
        if (null == mavenProject.getProperties() && mavenProject.getProperties().isEmpty()) {
            log.warn("Parameter " + parameter + " is not resolvable: Maven-properties are not available");
            return parameter;
        }

        final Properties mavenProperties = mavenProject.getProperties();
        final String name = parameterName(parameter);
        if (!nn(name)) {
            log.error("Parameter " + parameter + " is not resolvable: name is null or empty");
            return parameter;
        }

        // resolve with maven-properties
        final String value = mavenProperties.getProperty(name);
        if (!nn(value)) {
            log.warn("Parameter " + parameter + " is not resolvable: value is null or empty in Maven-properties");
            return parameter;
        }

        return isParameter(value) && !value.equals(parameter) ? resolveParameter(value, mavenProject) : value;
	}

	// HELPERS

	/**
	 * checks that staring is not-null and non-empty
	 * @param x
	 * @return
	 */
	static final boolean nn(final String x) {
		return null != x && 0 != x.trim().length();
	}

	/**
	 * drops all white spaces
	 * @param x
	 * @return string without white spaces
	 */
	static final String trim(final String x) {
		if ( null==x ) {
			return null;
		}
		return x.trim().replaceAll(" ", "");
	}

	private static final String parameter(final String x) {
		if (null==x) {
			return null;
		}
		return PREFIX_PARAMETER + parameterBody(x);
	}

	private static final String parameterName(final String x) {
		if (null==x) {
			return null;
		}
		if (!x.startsWith(PREFIX_PARAMETER)) {
			return null;
		}
        return x.replace(PREFIX_PARAMETER, "").replace(NAMEBEGIN_PARAMETER, "").replace(NAMEEND_PARAMETER, "");
	}

	private static final String parameterBody(final String x) {
		if (null==x) {
			return null;
		}
		return NAMEBEGIN_PARAMETER + trim(x)+ NAMEEND_PARAMETER;
	}

	private static final boolean isParameter(final String x) {
		if (null==x) {
			return false;
		}
		return x.startsWith(PREFIX_PARAMETER);
	}
}
