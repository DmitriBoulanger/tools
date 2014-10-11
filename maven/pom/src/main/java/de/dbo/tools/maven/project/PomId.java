package de.dbo.tools.maven.project;

public class PomId implements Comparable<PomId>  {
	
	public static final String SEPARATOR = ":";
	
	private final String group;
	private final String artifact;
	private final int hashCode;
	private final String stringValue;
	
	public PomId(final String group, final String artifact) {
		this.group = group;
		this.artifact= artifact;
		this.stringValue = group + SEPARATOR + artifact;
		this.hashCode = stringValue.hashCode();
	}

	public String getGroup() {
		return group;
	}

	public String getArtifact() {
		return artifact;
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
	
}
