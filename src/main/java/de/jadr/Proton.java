package de.jadr;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.codebrig.journey.JourneyBrowserView;
import com.sun.net.httpserver.HttpExchange;

import de.jadr.local.JarFileReader;
import de.jadr.local.JarFileReader.JarSubFile;
import de.jadr.net.JadrHTTPServer;
import de.jadr.net.JadrHTTPServer.HTTPHandler;
import de.jadr.ui.JadrFrame;
import javafx.util.Pair;


public class Proton {

	private JadrHTTPServer server;
	private JadrFrame jdrFrame;
	private JourneyBrowserView chromium;
	
	@SafeVarargs
	public Proton(int port, int width, int height, String httpPath, Pair<String, Object>... args) throws IOException {
		server = new JadrHTTPServer(port).start();
		server.addDefaultWebServer("de/jadr/test/http", Proton.class);
		
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
		
		JourneyBrowserView browser = new JourneyBrowserView(protonlauncherUrl);
		chromium = browser;

		jdrFrame.getFrameHead().setCloseHandler(() -> {
			browser.getCefApp().dispose();
			jdrFrame.getFrame().dispose();
			System.exit(0);
		});
		
		jdrFrame.getContentPane().add(browser);
		jdrFrame.getFrame().setVisible(true);
	}
	
	public void shutdownGui() {
		chromium.getCefApp().dispose();
		jdrFrame.getFrame().dispose();
	}
	
	public JourneyBrowserView getChromiumInstance() {
		return chromium;
	}
	
	public JadrFrame getWindow() {
		return jdrFrame;
	}
	
	public JadrHTTPServer getHTTPServer() {
		return server;
	}
}
