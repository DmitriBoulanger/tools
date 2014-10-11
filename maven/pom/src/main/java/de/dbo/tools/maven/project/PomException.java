package de.dbo.tools.maven.project;

public final class PomException extends Exception {
	private static final long serialVersionUID = -3614581449064825993L;

	public PomException(final String message) {
		super(message);
	}
	
	public PomException(final String message, final Throwable e) {
		super(message, e);
	}
}
