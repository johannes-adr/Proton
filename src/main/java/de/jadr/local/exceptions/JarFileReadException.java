package de.jadr.local.exceptions;

public class JarFileReadException extends Exception{
	public static enum JarFileReadExceptionMessage{
		FILE_HAS_NO_EXTENSION,FILE_NOT_FOUND
	}
	public final JarFileReadExceptionMessage message;
	public JarFileReadException(JarFileReadExceptionMessage j) {
		this.message = j;
	}
	
}
