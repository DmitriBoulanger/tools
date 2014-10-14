package de.dbo.tools.maven.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.springframework.core.io.Resource;

import de.dbo.tools.resources.SpringPathResources;
import edu.emory.mathcs.backport.java.util.Collections;

/**
 *  Hash-map group --  list[POM-instances, POM-instances, ... ]
 *
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and
 *           only incidentally for computers to execute
 *
 */
public final class PomCollection extends HashMap<String, PomInstances>  {
	private static final long serialVersionUID = 5288975098326000671L;

	/**
	 * creates POM-Collection from all pom.xml files matching the pattern
	 * @param pattern to search pom.xml files
	 * @return instance created using found pom.xml files
	 * @throws IOException
	 * @throws PomException
	 */
	public static final PomCollection newInstance(final String pattern) throws IOException, PomException  {
        return newInstance(pattern, new PomFilter());
	}

    public static final PomCollection newInstance(final String pattern, final PomFilter pomFilter) throws IOException, PomException {
        final PomCollection pomCollection = new PomCollection();
        pomCollection.add(pattern, pomFilter);
        return pomCollection;
    }

	/**
	 * creates new empty POM-Collection
	 * @return
	 * @throws IOException
	 * @throws PomException
	 */
	public static final PomCollection newInstance() throws IOException, PomException  {
		return new PomCollection();
	}

	private PomCollection() {
		// only internal usage
	}

    public void add(final String pattern, final PomFilter pomFilter) throws IOException, PomException {
		final List<Resource> items = SpringPathResources.resoucesInFilesystem(pattern);
		for (Resource resource:items) {
			final File file = resource.getFile();
			final Pom pom = new Pom(file);
            if (pomFilter.isMinus(pom)) {
                continue;
            }
            if (!pomFilter.isPlus(pom)) {
                continue;
            }
			addArtifact(pom);
			final MavenProject project = pom.getMavenProject();
			if (null!=project) {
				final List<Pom> dependencies = PomResolver.dependencies(project);
				if (!dependencies.isEmpty()) {
					for (final Pom dependency: dependencies) {
						addArtifact(dependency);
					}
				}
                final List<Pom> management = pom.dependencyManagement();
                if (null != management && !management.isEmpty()) {
                    for (final Pom dependency : management) {
                        addArtifact(dependency);
                    }
                }
			}
		}
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

	/**
	 * groups in this collection
	 * @return sorted list
	 */
	public List<String> groups() {
		final List<String> groups = new ArrayList<String>(keySet());
		Collections.sort(groups);
		return groups;
	}

	/**
	 * POM-IDs in the specified group
	 * @param group
	 * @return sorted list
	 */
	public List<PomId> pomIds(final String group) {
		final PomInstances poms =  get(group);
		final List<PomId> ids = new ArrayList<PomId>(poms.keySet());
		Collections.sort(ids);
		return ids;
	}
}
