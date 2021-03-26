package de.jadr.test;

import org.json.JSONObject;

import de.jadr.Proton;
import javafx.util.Pair;

public class Porto {

	public static void main(String[] args) throws Exception {
		Proton proton = new Proton(-1, 800,600,"de/jadr/test/http", new Pair<String, Object>("customArg", "pizza"));
		
		proton.getHTTPServer().addHandler("/api/quit", (e) -> {
			proton.shutdownGui();
			System.exit(0);
		});
		

		proton.getHTTPServer().addRestPostAPI("/berechne", (json, e) -> {
			
			String kundenart = json.getString("tfSelected");
			int gewicht = json.getInt("tfGewicht");

			JSONObject res = new JSONObject();
			res.put("porto", (double)Math.round(porto(gewicht, kundenart)) / 100);
			
			return res;
		});
		
	}
	

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
