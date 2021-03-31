package de.jadr.test2;

import org.json.JSONObject;

import de.jadr.Proton;
import javafx.util.Pair;

public class ProtonV2Test {

	public static void main(String[] args) throws Exception {
		Proton proton = new Proton(-1, 800,600,"de/jadr/test2/http", null, new Pair<String, Object>("author", "jadr"));
		JSONObject jo = new JSONObject();
		jo.put("yes", "OK");
		proton.addListener("Default", (json)->{
			
			return jo;
		});
		Thread.sleep(1000L);
		
		proton.callEvent("Ready", jo);

	}
	
//new WebSocket("ws://localhost:" + Proton.getWebSocketPort())
	public static double porto(int gewicht, String kundenart) {
		switch (kundenart) {
		case "rb1":
			return gewicht * 8;
		case "rb2":
			return gewicht * 6;
		case "rb3":
			return gewicht * 4.9;
		case "rb4":
			return gewicht * 3.5;
		}
		return 0;
	}

	
}
