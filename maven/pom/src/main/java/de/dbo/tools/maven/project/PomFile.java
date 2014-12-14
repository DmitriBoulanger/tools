package de.dbo.tools.maven.project;

import static de.dbo.tools.maven.project.PomResolver.nn;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PomFile {

    /**
     * checks that specified JAR-file has the specified POM-ID
     * @param pomId POM-ID to be found in the file-path
     * @param file JAR-file to be tested
     * @return
     */
	public static final boolean hasIt(final PomId pomId, final File file) {
		if (null==file) {
			return false;
		}
        if (!file.getName().endsWith(".jar")) {
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
        return fileItems.containsAll(groupItems);
	}

    public static final Pom pom(final String jarName) throws PomException {
        if (null == jarName) {
            return null;
        }
        if (!jarName.endsWith(".jar")) {
            return null;
        }

        final File file = new File(jarName);
        final String version = file.getParentFile().getName();
        final String artifact = file.getParentFile().getParentFile().getName();

        final List<String> list = new ArrayList<String>();
        File parent = file.getParentFile().getParentFile().getParentFile();
        while (null != parent) {
            list.add(parent.getName());
            parent = parent.getParentFile();
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = list.size() - 1; -1 < i; i--) {
            sb.append(list.get(i) + " ");
        }
        final String group = sb.toString().trim().replaceAll(" ", ".");
        final Pom pomId = new Pom(group, artifact, PomId.REFERENCE_TYPE, version);
        return pomId;
    }


	/**
	 * checks that specified JAR_TY-file has ID of the specified POM
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
    public static final String hasItComplete(final Pom pom, final File file) {
		if (null==file) {
            return null;
		}
		if (!file.getName().endsWith("jar")) {
            return null;
		}
		final String version = pomVersion(file);
		if (!nn(version)) {
            return null;
        }
        if (hasIt(pom.id(), file) && version.equals(pom.getVersion())) {
            return version;
		}
        return null;
	}

    /**
    * checks that specified JAR-file is managed in the dependencies
    * @param dependecyManagement
    * @param file
    * @return
    */
    public static final String isManaged(final List<Pom> dependecyManagement, final File file) {
		if (null==file) {
            return null;
		}
		if (!file.getName().endsWith("jar")) {
            return null;
		}
		if (null==dependecyManagement || dependecyManagement.isEmpty()) {
            return null;
		}
		for (final Pom pom:dependecyManagement) {
            final String version = hasItComplete(pom, file);
            if (null != version) {
                return version;
            }
		}
        return null;
	}

	    /**
     * extracts version from JAR-file
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
