package de.jadr.ui;


import javax.swing.JPanel;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
  
public class SwingFXWebView {
 
	private JFXPanel panel;
	private WebView browser;
    private WebEngine webEngine;
	
	public SwingFXWebView(String url, JPanel parent, Runnable onLoaded) {
		panel = new JFXPanel();
		Platform.runLater(()->{
			browser = new WebView();
			browser.setPrefWidth(parent.getWidth());
			browser.setPrefHeight(parent.getHeight());
			webEngine = browser.getEngine();
			webEngine.load(url);
			browser.setContextMenuEnabled(false);
			
			Group root = new Group();
			Scene scene = new Scene(root);
			root.getChildren().add(browser);
			panel.setScene(scene);
			if(onLoaded != null)onLoaded.run();
		});
	}
	
	public JFXPanel getPanel() {
		return panel;
	}
	
	public SwingFXWebView(String url, JPanel parent) {
		this(url, parent, null);
	}
	
	public WebView getBrowser() {
		return browser;
	}
	
	public WebEngine getEngine() {
		return webEngine;
	}

}