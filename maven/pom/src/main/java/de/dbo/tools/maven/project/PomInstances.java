package de.dbo.tools.maven.project;

import static de.dbo.tools.utils.print.Print.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hash-map: pomId -- list[POM,POM, ...]
 * 
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and 
 *           only incidentally for computers to execute 
 *
 */
public final class PomInstances extends HashMap<PomId, List<Pom>> {
	private static final long serialVersionUID = 4735042994556671385L;
	
	private final Map<PomId, Integer> counters = new HashMap<PomId, Integer>();

	public void addPom(final Pom pom) {
		final PomId id = pom.id();
		final List<Pom> pomInstances;
		if (!containsKey(id)) {
			pomInstances = new ArrayList<Pom>();
			put(id,pomInstances);
		} else {
			pomInstances = get(id);
		}
		
		if ( ! pomInstances.contains(pom)) {
			pomInstances.add(pom);
		}
		
		final Integer counter;
		if (!counters.containsKey(id)) {
			counter = new Integer(1);
			counters.put(id,counter);
		} else {
			counter = counters.get(id);
			counters.put(id, new Integer(counter.intValue()  + 1));
		}
		
		
	}
	
	public StringBuilder print(final PomId id) {
		final List<Pom> poms = get(id);
		final List<String> versions = new ArrayList<String>();
		for (final Pom pom: poms) {
			final String version = pom.getVersion();
			if (versions.contains(version)) {
				continue;
			}
			versions.add(version);
		}
		Collections.sort(versions);
		final StringBuilder sb = new StringBuilder();
		sb.append(new DecimalFormat("00").format(counters.get(id)));
		sb.append("  ");
		sb.append(line(versions));
		return sb;
	}
}
