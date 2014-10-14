package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomResolver.nn;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PomFile {
	
	/**
	 * checks that specified JAR_TYPE-file has the POM-ID
	 * @param pomId POm-ID to be found in the file-path
	 * @param file JAR_TYPE-file to be tested
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
	
	/**
	 * checks that specified JAR_TYPE-file has ID of the specified POM
	 * @param pom POM whose ID is to be found in the file-path
	 * @param file JAR_TYPE-file to be tested
	 * @return
	 */
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
	
	/**
	 * checks that specified JAR_TYPE-file has ID and version of the specified POM
	 * @param pom POM whose ID is to be found in the file-path
	 * @param file JAR_TYPE-file to be tested
	 * @return
	 */
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
	
	/**
	 * checks that specified JAR_TYPE-file is managed in the dependencies
	 * @param dependecyManagement
	 * @param file
	 * @return
	 */
	public static final boolean isManaged(final List<Pom> dependecyManagement, final File file) {
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
	
	/**
	 * extracts version from JAR_TYPE-file
	 * @param file
	 * @return
	 */
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
