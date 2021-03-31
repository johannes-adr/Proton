package de.jadr.net;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import de.jadr.utils.JUtils;

public class ProtonWebSocket extends WebSocketServer{
	public static final Logger LOGGER = Logger.getLogger("ProtonWebServer");
	static {
		LOGGER.setLevel(Level.OFF);
	}
	
	private final String token = JUtils.getRandomString(32, JUtils.CHARACTERS);
	private final HashMap<String, PacketHandler> handlers = new HashMap<>();
	private final ArrayList<JSONObject> events = new ArrayList<JSONObject>();
	
	public ProtonWebSocket(InetSocketAddress inetAdress) {
		super(inetAdress);
		start();
	}
	
	public HashMap<String, PacketHandler> getHandlers() {
		return handlers;
	}
	
	public static interface PacketHandler {
		public JSONObject handle(JSONObject in);
	}
	
	public String getToken() {
		return token;
	}
	
	private FrontEndSocket frontEnd = new FrontEndSocket();
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		LOGGER.info("New websocket connection");
		if(frontEnd.socket == null) {
			frontEnd.socket = conn;
			LOGGER.info("Registered connection as potetially frontend socket");
		}else {
			LOGGER.severe("SECURITY_ISSUE - unauthorized websocket connection (" + conn.getRemoteSocketAddress()+ ")");
			conn.close(-1, "UNAUTHORIZED");
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		LOGGER.warning("The frontend socket connection broked up! Got the connection closed by remote: " + remote + ", Reason: " + reason + " Code: " + code);
		if(frontEnd.socket == conn) {
			frontEnd = new FrontEndSocket();
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		LOGGER.info("Got message...");
		if(conn.equals(frontEnd.socket)) {
			JSONObject packet = new JSONObject(message);
			if(!frontEnd.accepted) {
				LOGGER.info("This message is a token verify request!");
				String token = packet.getString("token");
				LOGGER.info("given: [" + token + "] local: [" + getToken() + "] equals: " + (getToken().equals(token)));
				if(token != null && getToken().equals(token)) {
					frontEnd.accepted = true;
					LOGGER.fine("Frontend socket verified!");
					for (JSONObject ev : events) {
						conn.send(ev.toString());
						
						JSONObject payload = ev.getJSONObject("payload");
						LOGGER.info("sending queued event request '" + ev.getString("event") + "' with a payload of " + payload==null?"null":payload.toString().getBytes().length+"" + " bytes");
					}
					events.clear();
				}else {
					LOGGER.warning("Frontend socket used wrong token!");
					conn.close();
					frontEnd = new FrontEndSocket();
					return;
				}
			}			
			
			if(packet.keySet().contains("id")) {
				String id = packet.getString("id");
				String adress = packet.getString("adress");
				LOGGER.info("Got fetch packet with id: " + id + " \n Requesting for handler: " + adress);
				
				PacketHandler handler = handlers.get(adress);
				JSONObject ret = new JSONObject();
				ret.put("id", id);
				if(handler != null) {
					try {
						ret.put("body", handler.handle(packet.getJSONObject("body")));
						frontEnd.socket.send(ret.toString());
						LOGGER.fine("answered packet with id " + id);
						return;
					} catch (Exception e) {
						LOGGER.severe(Arrays.toString(e.getStackTrace()));
						ret.put("error", e.toString());
						frontEnd.socket.send(ret.toString());
						return;
					}
				}
				ret.put("error", "Handler not found");
				LOGGER.warning("requested handler " + adress +" not found!");
				frontEnd.socket.send(ret.toString());
				return;
			}
		}else {
			System.out.println("Closed!");
			conn.close();
		}
	}
	
	public void callEvent(String name, JSONObject payload, boolean queue) {
		JSONObject jo = new JSONObject();
		jo.put("event", name);
		jo.put("body", payload);
		if(frontEnd.socket != null) {
			LOGGER.info("sending event request '" + name + "' with a payload of " + payload==null?"null":payload.toString().getBytes().length+"" + " bytes");
			frontEnd.socket.send(jo.toString());
		}else if(queue){
			LOGGER.info("queuing event request '" + name + "' with a payload of " + payload==null?"null":payload.toString().getBytes().length+"" + " bytes");
			events.add(jo);
		}
		
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		LOGGER.severe("An unexcpeted error occured - " + Arrays.toString(ex.getStackTrace()));
	}

	@Override
	public void onStart() {
		LOGGER.fine("Started ProtonWebSocketServer!");
	}
	
	public class FrontEndSocket{
		WebSocket socket = null;
		boolean accepted = false;
	}

}
