package de.jadr.local;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import de.jadr.local.exceptions.JarFileReadException;
import de.jadr.local.exceptions.JarFileReadException.JarFileReadExceptionMessage;

public class JarFileReader {
	private static final HashMap<String, String> fileTypes;

	
	public static class JarSubFile{
		private final String httpType;
		private final byte[] bytes;
		private final FileExtensions fe;
		public JarSubFile(String httpType, FileExtensions fe, byte[] bytes) {
			this.httpType = httpType;
			this.bytes = bytes;
			this.fe = fe;
		}
		public String getHTTPType() {
			return httpType;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public FileExtensions getFileExtension() {
			return fe;
		}
		
		public String asString() {
			return new String(bytes);
		}
		
		public Image asImage() throws IOException {
			return ImageIO.read(new ByteArrayInputStream(bytes));
		}
		
		@Override
		public String toString() {
			return "JarSubfile[ (Extension: "+ fe + ") (httpType: " + httpType + ") (Bytes: " + bytes.length  + ")]";
		}
	}
	/**
	 * 
	 * @param path - dont start with src, for example "/de/jadr/test/test.txt"
	 * @return
	 * @throws IOException 
	 * @throws JarFileReadException 
	 */
	public static JarSubFile read(String path) throws IOException, JarFileReadException {
		return readRelative(path, JarFileReader.class);
	}
	
	public static JarSubFile readRelative(String path, Class clazz) throws IOException, JarFileReadException {
		StringBuilder sb = new StringBuilder(path).reverse();
		int i = sb.indexOf(".");
		if(i == -1) {
			throw new JarFileReadException(JarFileReadExceptionMessage.FILE_HAS_NO_EXTENSION);
		}
		String filetype = new StringBuilder(sb.substring(0, i)).reverse().toString();	
		InputStream in = clazz.getResourceAsStream(path);
		if(in == null) {
			throw new JarFileReadException(JarFileReadExceptionMessage.FILE_NOT_FOUND);
		}
		BufferedInputStream bin = new BufferedInputStream(in);
		
		byte[] bytes = new byte[bin.available()];
		bin.read(bytes);
		
		return new JarSubFile(fileTypes.get(filetype),FileExtensions.valueOf(filetype.toUpperCase()), bytes);
	}
	
	
	
	static {
			fileTypes = new HashMap<String, String>();
			fileTypes.put("css", "text/css");
			fileTypes.put("htm", "text/html");
			fileTypes.put("html", "text/html");
			fileTypes.put("jar", "application/jar");

			fileTypes.put("txt", "text/plain");
			fileTypes.put("json", "text/json");
			fileTypes.put("js", "application/javascript");
			fileTypes.put("jpg", "image/jpeg");
			fileTypes.put("jpeg", "image/jpeg");
			fileTypes.put("png", "image/png");
			fileTypes.put("svg", "image/svg+xml");
			fileTypes.put("asc", "text/plain");
			fileTypes.put("gif", "image/gif");

			fileTypes.put("mp3", "audio/mpeg");
			fileTypes.put("m3u", "audio/mpeg-url");
			fileTypes.put("mp4", "video/mp4");
			fileTypes.put("ogv", "video/ogg");
			fileTypes.put("flv", "video/x-flv");
			fileTypes.put("mov", "video/quicktime");
			fileTypes.put("swf", "application/x-shockwave-flash");
			fileTypes.put("xml", "text/xml");
			fileTypes.put("pdf", "application/pdf");
			fileTypes.put("doc", "application/msword");
			fileTypes.put("ogg", "application/x-ogg");
			fileTypes.put("zip", "application/octet-stream");
			fileTypes.put("exe", "application/octet-stream");
			fileTypes.put("class", "application/octet-stream");
	}	
	public static enum FileExtensions{
		CSS,HTM,HTML,JAR,TXT,JSON,JS,JPG,JPEG,PNG,SVG,ASC,GIF,MP3,M3U,MP4,OGV,FLV,MOV,SWF,XML,PDF,DOC,OGG,ZIP,EXE,CLASS
	}
	
}
