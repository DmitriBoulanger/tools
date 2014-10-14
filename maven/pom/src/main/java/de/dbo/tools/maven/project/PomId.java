package de.dbo.tools.maven.project;

/**
 * Id of POM-instance consisting of group and artifact
 *
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and
 *           only incidentally for computers to execute
 *
 */
public class PomId implements Comparable<PomId>  {

	public static final String SEPARATOR = ":";
	public static final String POM = "pom";
    public static final String JAR_TYPE     = "jar";
    public static final String UNKNOWN_TYPE = "   ";

	private final String group;
	private final String artifact;
	private final String type;

	private final int hashCode;
	private final String stringValue;

	public PomId(final String group, final String artifact, final String type) {
		this.group = group;
		this.artifact= artifact;
        this.type = null != type && 3 < type.length() ? type.substring(0, 3) : type; // not more that 3 chars!

        this.stringValue = group + SEPARATOR + artifact;
		this.hashCode = stringValue.hashCode();
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

	@Override
	public final int hashCode(){
	    return hashCode;
	}

	@Override
	public int compareTo(final PomId o) {
		return stringValue.compareTo(o.toString());
	}

	@Override
	public final String toString() {
		return stringValue;
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof PomId) {
			return stringValue.equals(((PomId)o).toString());
		}
		return false;
	}

    public String print() {
        return this.stringValue + SEPARATOR + type;
    }
}
