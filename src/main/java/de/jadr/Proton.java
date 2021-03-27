package de.jadr;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONObject;

//import com.codebrig.journey.JourneyBrowserView;
import com.sun.net.httpserver.HttpExchange;

import de.jadr.local.JarFileReader;
import de.jadr.local.JarFileReader.JarSubFile;
import de.jadr.net.JadrHTTPServer;
import de.jadr.net.JadrHTTPServer.HTTPHandler;
import de.jadr.ui.JadrFrame;
import de.jadr.ui.SwingFXWebView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;


public class Proton {

	private JadrHTTPServer server;
	private JadrFrame jdrFrame;
	private SwingFXWebView browser;
	
	@SafeVarargs
	public Proton(int port, int width, int height, String httpPath, Pair<String, Object>... args) throws IOException {
		server = new JadrHTTPServer(port).start();
		server.addDefaultWebServer(httpPath);
		
		jdrFrame = new JadrFrame(width, height, false);
		String protonlauncherUrl = "http://localhost:" + server.getPort() + "/proton/index.html";
		

		
		JSONObject startArgs = new JSONObject();
		startArgs.put("port", server.getPort());
		for (Pair<String, Object> pair : args) {
			startArgs.put(pair.getKey(), pair.getValue());
		}
		
		try {
			JarSubFile jsf = JarFileReader.readRelative("ProtonWebLauncher.html", Proton.class);
			String jsStart = jsf.asString().replace("$jsonargs", startArgs.toString());
			server.addHandler("/proton", new HTTPHandler() {
				
				@Override
				public void handle(HttpExchange e) throws Exception {
					e.setAttribute("Content-Type", jsf.getHTTPType());
					e.setAttribute("Content-Length", jsStart.length());
					e.sendResponseHeaders(HttpURLConnection.HTTP_OK, jsStart.length());
					e.getResponseBody().write(jsStart.getBytes());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		browser = new SwingFXWebView(protonlauncherUrl, jdrFrame.getContentPane());
		jdrFrame.getContentPane().add(browser);  

		jdrFrame.getFrameHead().setCloseHandler(() -> {
			jdrFrame.getFrame().dispose();
			System.exit(0);
		});
		
		jdrFrame.getFrame().setVisible(true);
	}
	
	public void shutdownGui() {
		jdrFrame.getFrame().dispose();
	}
	
	public SwingFXWebView getBrowser() {
		return browser;
	}
	
	public JadrFrame getWindow() {
		return jdrFrame;
	}
	
	public JadrHTTPServer getHTTPServer() {
		return server;
	}
}
