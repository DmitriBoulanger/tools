package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomResolver.nn;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PomFile {
	
	/**
	 * checks that specified JAR-file has this POM-ID
	 * @param file
	 * @return
	 */
	public static final boolean hasIt(final PomId pomId, final File file) {
		if (null==file) {
			return false;
		}
		if (!file.getName().endsWith("jar")) {
			return false;
		}
		final String artifact = file.getParentFile().getParentFile().getName();
		if (!artifact.equals(pomId.getArtifact())) {
			return false;
		}
		// compare with the file-path: check the group
		final List<String> groupItems = Arrays.asList(pomId.getGroup().split("\\."));
		final Set<String> fileItems = new HashSet<String>();
		split(file.getParentFile().getParentFile().getParentFile(),fileItems);
		return  fileItems.containsAll(groupItems);
	}
	
	public static final boolean hasIt(final Pom pom, final File file) {
		if (null==file) {
			return false;
		}
		if (!file.getName().endsWith("jar")) {
			return false;
		}
		final PomId pomId = pom.id();
		return hasIt(pomId,file);
	}
	
	public static final boolean hasItComplete(final Pom pom, final File file) {
		if (null==file) {
			return false;
		}
		if (!file.getName().endsWith("jar")) {
			return false;
		}
		final String version = pomVersion(file);
		if (!nn(version)) {
			return false;
		}
		return hasIt(pom.id(), file) && version.equals(pom.getVersion());
	}
	
	public static final boolean isManaged(List<Pom> dependecyManagement, final File file) {
		if (null==file) {
			return false;
		}
		if (!file.getName().endsWith("jar")) {
			return false;
		}
		if (null==dependecyManagement || dependecyManagement.isEmpty()) {
			return false;
		}
		for (final Pom pom:dependecyManagement) {
			if (hasItComplete(pom,file)) {
				return true;
			}
		}
		return false;
	}
	
	public static final String pomVersion(final File file) {
		if (null==file) {
			return null;
		}
		if (!file.getName().endsWith("jar")) {
			return null;
		}
		return file.getParentFile().getName();
	}
	
	private static void split(final File file, final Set<String> items) {
		if (null==file) {
			return;
		}
		items. add(file.getName());
		split(file.getParentFile(), items);
	}
	

}
