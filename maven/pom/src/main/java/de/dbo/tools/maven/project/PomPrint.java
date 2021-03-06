package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomId.JAR_TYPE;
import static de.dbo.tools.maven.project.PomId.POM_TYPE;
import static de.dbo.tools.maven.project.PomId.REFERENCE_TYPE;
import static de.dbo.tools.maven.project.PomResolver.NULL_VERSION;
import static de.dbo.tools.maven.project.PomResolver.dependencies;
import static de.dbo.tools.maven.project.PomResolver.toPoms;
import static de.dbo.tools.utils.print.Print.line;
import static de.dbo.tools.utils.print.Print.lines;
import static de.dbo.tools.utils.print.Print.padRight;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;

import edu.emory.mathcs.backport.java.util.Collections;

public final class PomPrint {
    private final List<String>  warn                 = new ArrayList<String>();
    private final List<String>  error                = new ArrayList<String>();
    private final List<String>  info                 = new ArrayList<String>();

    private static final int    TYPE_PRINT_WIDTH     = 3;
    private static final int    GROUP_PRINT_WIDTH    = 40;
    private static final int    ARTIFCAT_PRINT_WIDTH = 40;
    private static final int    VERSION_PRINT_WIDTH  = 20;
    private static final String COUNTER_DF           = "00";

    private static final String MANGEMENT_TITLE      = "MANAGEMENT";

    public void warn(final Logger log) {
        for (final String message : warn) {
            log.warn(message);
        }
        warn.clear();
    }

    public void error(final Logger log) {
        for (final String message : error) {
            log.error(message);
        }
        error.clear();
    }

    public void info(final Logger log) {
        for (final String message : info) {
            log.info(message);
        }
        info.clear();
    }

	/**
	 * Pretty-print of a POM
	 * @return several table-lines
	 * @throws PomException
	 */
	public static StringBuilder print(final Pom pom) throws PomException {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\t - File                 : " + pom.getFile());
		sb.append("\n\t - id                   : " + pom.id());
		sb.append("\n\t - GroupId              : " + pom.getGroup());
		sb.append("\n\t - ArtifactId           : " + pom.getArtifact());
		sb.append("\n\t - Version              : " + pom.getVersion());
		sb.append("\n\t - Type                 : " + pom.getType());
		if (null==pom.getMavenProject()) {
			sb.append("\n\t - Maven Project        : " + "NULL");
		}
		else {
			final MavenProject mavenProject = pom.getMavenProject();
			sb.append("\n\t - Maven Project Packaging            : " + mavenProject.getPackaging());
			sb.append("\n\t - Maven Project Parent               : " + mavenProject.getParent());
			sb.append("\n\t - Maven Project Artifact             : " + mavenProject.getArtifact());
			sb.append("\n\t - Maven Project Properties           : " + lines(mavenProject.getProperties(),2));
			sb.append("\n\t - Maven Project Properties Preserved : " + lines(mavenProject.getPreservedProperties(),2));
			sb.append("\n\t - Maven Project Dependencies         : " + printDependencies(mavenProject));
			sb.append("\n\t - Maven Project DependencyManagement : " + printDependencyManagement(mavenProject));
		}
        return sb;
	}

	    /**
     * Pretty-print of a POM-Collection
     *
     * @return possibly large table as a string
     * @throws PomException
     */
    public StringBuilder print(final PomCollection pomCollection, final Pom management) throws PomException {
        return print(pomCollection, new PomFilter(), management);
	}

    public StringBuilder print(final PomCollection pomCollection, final PomFilter pomFilter, final Pom management) throws PomException {
        final StringBuilder ret = new StringBuilder();
        for (final String group : pomCollection.groups()) {
            if (pomFilter.isGroupMinus(group)) {
                continue;
            }
            if (!pomFilter.isGroupPlus(group)) {
                continue;
            }
            final PomInstances poms = pomCollection.get(group);
            final List<PomId> ids = pomCollection.pomIds(group);
            for (final PomId id : ids) {
                final String artifact = id.getArtifact();
                if (pomFilter.isArtifactMinus(artifact)) {
                    continue;
                }

                ret.append("\n -");
                ret.append(" " + padRight(group, GROUP_PRINT_WIDTH));
                ret.append(" " + padRight(artifact, ARTIFCAT_PRINT_WIDTH));
                ret.append(" " + printManagementTypeVersions(id, management, poms));
                //                ret.append(" " + printVersions(id, poms));
            }
        }
        return ret;
    }

    private StringBuilder printManagementTypeVersions(final PomId id, final Pom management, final PomInstances pomInstances) throws PomException {
        final String managedVersion = management.managementVesrion(id);
        final List<String> versions = pomInstances.versions(id);
        versions.remove(NULL_VERSION);
        final String versionsPrint = "[" + line(versions).toString().trim().replaceAll(" ", ", ") + "]";
        if (null != managedVersion && !versions.contains(managedVersion)) {
            final String idPrint = id.getArtifact() + PomId.SEPARATOR + id.getGroup();
            if (!versions.isEmpty()) {
                error.add(padRight(idPrint, 60) + ": managed version " + padRight(managedVersion, 40)
                        + " is not in the version-list " + versionsPrint + " available in the sources");
            }
            else if (-1 == idPrint.indexOf("ityx")) {
                info.add(padRight(idPrint, 60) + ": managed version " + (padRight(managedVersion, 40)) + ": no versions in the source found");
            }
            else {
                warn.add(padRight(idPrint, 60) + ": managed version " + (padRight(managedVersion, 40)) + ": no versions in the source found");
            }
        }

        final List<String> types = pomInstances.types(id);
        if (types.size() > 1) {
            types.remove(REFERENCE_TYPE);
        }
        if (types.size() > 1) {
            final String idPrint = id.getArtifact() + PomId.SEPARATOR + id.getGroup();
            error.add("POM " + idPrint + " has several types: " + line(types));
        }
        final boolean isJarType = types.contains(JAR_TYPE);
        final boolean isPomType = types.contains(POM_TYPE);
        final String typePrint = isJarType ? "JAR" : types.size() > 0 ? types.get(0) : "";

        final StringBuilder sb = new StringBuilder();
        if (!isPomType) {
            sb.append(" " + MANGEMENT_TITLE + ": ");
            if (null != managedVersion) {
                sb.append(padRight(managedVersion, VERSION_PRINT_WIDTH));
            }
            else {
                sb.append(padRight("", VERSION_PRINT_WIDTH));
            }
        }
        else {
            sb.append(padRight("", VERSION_PRINT_WIDTH + MANGEMENT_TITLE.length() + 3));
        }
        sb.append(" " + padRight(typePrint, TYPE_PRINT_WIDTH));
        sb.append(" " + new DecimalFormat(COUNTER_DF).format(pomInstances.counter((id))));
        sb.append(" ");
        if (!versions.isEmpty()) {
            sb.append(versionsPrint);
        }

        return sb;
    }

	private static final StringBuilder printDependencies(final MavenProject mavenProject) throws PomException {
        return print(dependencies(mavenProject));
	}

    public static final StringBuilder print(final List<Pom> poms) throws PomException {
		Collections.sort(poms);
		final StringBuilder sb = new StringBuilder();
		for ( final Pom pom:poms ){
			sb.append("\n\t   --");
			sb.append( " " + padRight(pom.getGroup(),GROUP_PRINT_WIDTH));
			sb.append( " " + padRight(pom.getArtifact(),ARTIFCAT_PRINT_WIDTH));
			sb.append( " " + pom.getVersion());
		}
		return sb;
	}

	private static final StringBuilder printDependencyManagement(final MavenProject mavenProject) throws PomException {
		final StringBuilder sb = new StringBuilder();
		final DependencyManagement dependencyManagement =  mavenProject.getDependencyManagement();
		if ( null==dependencyManagement ){
			sb.append("NULL");
			return sb;
		}
        return print(toPoms(dependencyManagement.getDependencies(), mavenProject));
	}
}
