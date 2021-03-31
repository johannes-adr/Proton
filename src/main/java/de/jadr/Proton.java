package de.jadr;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.json.JSONObject;

import com.sun.javafx.webkit.WebConsoleListener;
//import com.codebrig.journey.JourneyBrowserView;
import com.sun.net.httpserver.HttpExchange;

import de.jadr.local.JarFileReader;
import de.jadr.local.JarFileReader.JarSubFile;
import de.jadr.net.JadrHTTPServer;
import de.jadr.net.ProtonWebSocket;
import de.jadr.net.ProtonWebSocket.PacketHandler;
import de.jadr.net.JadrHTTPServer.HTTPHandler;
import de.jadr.ui.JadrFrame;
import de.jadr.ui.SwingFXWebView;
import javafx.util.Pair;

public class Proton {

	private JadrHTTPServer server;
	// $wsport
	private ProtonWebSocket webSocket;
	private JadrFrame jdrFrame;
	private SwingFXWebView browser;
	public static final Logger LOGGER = Logger.getLogger("Proton");
	static {
		LOGGER.setLevel(Level.OFF);
	}

	@SafeVarargs
	public Proton(int port, int width, int height, String httpPath, Pair<String, Object>... args) throws IOException {
		System.err.println("Starting proton...");
		server = new JadrHTTPServer(port).start();
		server.addDefaultWebServer(httpPath);
		LOGGER.info("Proton webserver started at port " + server.getPort());

		initWebSocketServer();
		LOGGER.info("Proton websocket started at port " + webSocket.getPort());

		jdrFrame = new JadrFrame(width, height, false);
		String indexURL = "http://localhost:" + server.getPort() + "/web/index.html";

		JSONObject startArgs = new JSONObject();
		if (args != null) {
			for (Pair<String, Object> pair : args) {
				if (pair != null) {
					startArgs.put(pair.getKey(), pair.getValue());
					LOGGER.info("Registered start arg [key: " + pair.getKey() + " val: " + pair.getValue());
				}

			}
		}

		try {
			{
				// Register web startscript hook for proton
//				JarSubFile jsf = JarFileReader.readRelative("ProtonWebLauncher.html", Proton.class);
//				final String jsStart = jsf.asString().replace("$jsonargs", startArgs.toString());
//				server.addHandler("/proton/launcher", new HTTPHandler() {
//
//					@Override
//					public void handle(HttpExchange e) throws Exception {
//						e.setAttribute("Content-Type", jsf.getHTTPType());
//						e.setAttribute("Content-Length", jsStart.length());
//						e.sendResponseHeaders(HttpURLConnection.HTTP_OK, jsStart.length());
//						e.getResponseBody().write(jsStart.getBytes());
//					}
//				});
			}
			{
				// Register proton util file
				JarSubFile jsfProton = JarFileReader.readRelative("Proton.js", Proton.class);
				final String js = jsfProton.asString().replace("$port", server.getPort() + "")
						.replace("$jsonargs", startArgs.toString()).replace("$wsport", webSocket.getPort() + "")
						.replace("$token", "'" + webSocket.getToken() + "'");

				server.addHandler("/web/Proton.js", (e) -> {
					e.setAttribute("Content-Type", JarFileReader.fileTypes.get("js"));
					e.setAttribute("Content-Length", js.length());
					e.sendResponseHeaders(HttpURLConnection.HTTP_OK, js.length());
					e.getResponseBody().write(js.getBytes());
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		browser = new SwingFXWebView(indexURL, jdrFrame.getContentPane());
		LOGGER.info("FX Browser started!");
		
		WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
			System.err.println("[JavaScript Console]=> " + message + " [at " + lineNumber + "] " + sourceId);
		});

		jdrFrame.getContentPane().add(browser.getPanel());

		jdrFrame.getFrameHead().setCloseHandler(() -> {
				System.exit(0);	
		});

		jdrFrame.getFrame().setVisible(true);
		
		System.err.println("Proton started!");
	}

	public void addListener(String adress, PacketHandler handler) {
		webSocket.getHandlers().put(adress, handler);
	}

	public void callEvent(String name, JSONObject payload, boolean queue) {
		webSocket.callEvent(name, payload, queue);
	}

	public void callEvent(String name, JSONObject payload) {
		this.callEvent(name, payload, true);
	}

	public void callEvent(String name) {
		this.callEvent(name, null);
	}

	public void removeListener(String adress) {
		webSocket.getHandlers().remove(adress);
	}

	private void initWebSocketServer() throws IOException {
		int startPort = server.getPort();
		for (int i = startPort + 1; i < startPort + 1000; i++) {
			
			InetSocketAddress inet = new InetSocketAddress(i);
			if (inet.getAddress().isReachable(50)) {
				LOGGER.info("Port " + i + " isn't avaible");;
				continue;
			}
			LOGGER.info("Port " + i + " is open! - starting webSocket...");	
			webSocket = new ProtonWebSocket(inet);
			break;
		}
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
