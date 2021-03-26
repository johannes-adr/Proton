package de.jadr.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Random;

import javax.swing.JTextField;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import de.jadr.local.JarFileReader;
import de.jadr.local.JarFileReader.JarSubFile;
import de.jadr.local.exceptions.JarFileReadException;
import de.jadr.local.exceptions.JarFileReadException.JarFileReadExceptionMessage;

public class JadrHTTPServer {
	public static enum Method{
		GET,POST,PUT,DELETE,PATCH
	}
	private int port;
	private HttpServer server;
	public JadrHTTPServer(int port) throws IOException {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	/**
	 * 
	 * @param javapackage f.e "de/jadr/test/http"
	 * @param clazz
	 */
	public void addDefaultWebServer(String javapackage, Class clazz) {
		this.addHandler("/web", new HTTPHandler() {
			public void handle(HttpExchange e) throws Exception {
				String reqUrl = javapackage+e.getRequestURI().toString().replaceFirst("web", "");
				JarSubFile jsf = null;
				try {
					jsf = JarFileReader.readRelative("/"+reqUrl, clazz);
				}catch(JarFileReadException ex) {
					
					String notfound = "404 Not found";
					if(ex.message == JarFileReadExceptionMessage.FILE_HAS_NO_EXTENSION) {
						try {
							String f = "/"+reqUrl+"index.html";
							System.out.println(f);
							jsf = JarFileReader.read(f);
						}catch(JarFileReadException exx) {
							e.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, notfound.getBytes().length);
							e.getResponseBody().write(notfound.getBytes());
							return;
						}
					}
					else {
						e.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, notfound.getBytes().length);
						e.getResponseBody().write(notfound.getBytes());
						return;
					}
					
				}
				e.setAttribute("Content-Type", jsf.getHTTPType());
				e.setAttribute("Content-Length", jsf.getBytes().length);
				e.sendResponseHeaders(HttpURLConnection.HTTP_OK, jsf.getBytes().length);
				e.getResponseBody().write(jsf.getBytes());
			}
		});
	}
	/**
	 * 
	 * @param apiurl f.e /users/1
	 */
	public void addRestPostAPI(String apiurl, RestAPIHandler rh) {
		addHandler("/api"+apiurl, (e)->{
			StringBuilder sb = new StringBuilder();
			InputStream in = e.getRequestBody();
			int lenght = Integer.parseInt(e.getRequestHeaders().get("Content-Lenght").get(0));
			byte[] buffer = new byte[lenght];
			in.read(buffer);
			JSONObject requestJSON = new JSONObject(new String(buffer));
			//Execute custom code
			JSONObject response = rh.responde(requestJSON, e);

			String res = response.toString();
			e.setAttribute("Content-Type", "application/json");
			e.sendResponseHeaders(200, res.length());
			e.getResponseBody().write(res.getBytes());
		});
		
	}
	
	public JadrHTTPServer start() throws IOException {
		if(port == -1) {
			boolean checking = true;
			while(checking) {
				try {
					int rNumber = new Random().nextInt(10000)+1000;
					for(int i = rNumber;i < rNumber+1000;i++) {
						server = HttpServer.create(new InetSocketAddress(i), 0);
						port = i;
						checking = false;
						break;
					}
				}catch(Exception e) {}
			}
		}else {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		}
		System.out.println("Server started at port " + port);
		server.start();
		return this;
	}
	

	public void addHandler(String url, final HTTPHandler h) {
		server.createContext(url, new HttpHandler(){
			public void handle(HttpExchange e) throws IOException {
				try {
					h.handle(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.getRequestBody().close();
				e.getResponseBody().close();
				e.close();
			}
		});
	}
	
	public static interface HTTPHandler{
		public void handle(HttpExchange e)throws Exception;
	}

	public static interface RestAPIHandler {
		public JSONObject responde(JSONObject request, HttpExchange exchange);
	}
	
}
