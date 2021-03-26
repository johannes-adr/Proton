package de.jadr.utils;



import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

public class JUtils {

	public static String getFileContent(File f) {
		StringBuilder sb = new StringBuilder();
		Scanner sc;
		try {
			sc = new Scanner(f);
			while (sc.hasNext()) {
				sb.append(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static void delay(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean openWebpage(URL url) {
		try {
			return openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void setFileContent(File f, String s) throws IOException {
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.close();
	}

	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = output.createGraphics();

		// This is what we want, but it only does hard-clipping, i.e. aliasing
		// g2.setClip(new RoundRectangle2D ...)

		// so instead fake soft-clipping by first drawing the desired clip shape
		// in fully opaque white with antialiasing enabled...
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));

		// ... then compositing the image on top,
		// using the white shape from above as alpha source
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}

	public static String getWebsiteContent(String s) throws IOException {
		URLConnection inp = new URL(s).openConnection();
		inp.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(inp.getInputStream())));

		StringBuilder content = new StringBuilder();

		while (true) {
			String line = br.readLine();
			if (line != null) {
				content.append(line + "\n");
			} else {
				break;
			}
		}

		return content.toString();
	}


	public static void deepCopy(File sourceFolder, File destFolder) throws IOException {
		byte b;
		int i;
		File[] arrayOfFile;
		for (i = (arrayOfFile = sourceFolder.listFiles()).length, b = 0; b < i;) {
			File f = arrayOfFile[b];
			File fCopied = new File(destFolder + File.separator + f.getName());
			Files.copy(f.toPath(), fCopied.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
			if (f.isDirectory())
				deepCopy(f, new File(destFolder.getAbsoluteFile() + File.separator + f.getName()));
			b++;
		}
	}

	public static void deepDelete(File folder) {
		if (folder.isDirectory()) {
			byte b;
			int i;
			File[] arrayOfFile;
			for (i = (arrayOfFile = folder.listFiles()).length, b = 0; b < i;) {
				File f = arrayOfFile[b];
				deepDelete(f);
				b++;
			}
			folder.delete();
		} else {
			folder.delete();
		}
	}

}
