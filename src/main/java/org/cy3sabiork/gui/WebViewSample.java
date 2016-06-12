package org.cy3sabiork.gui;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class WebViewSample extends Application {
	private Scene scene;
	@Override public void start(Stage stage) {
		// create the scene
		stage.setTitle("Web View");
		scene = new Scene(new Browser(),900,600, Color.web("#666970"));
		stage.setScene(scene);
		scene.getStylesheets().add("webviewsample/BrowserToolbar.css");        
		stage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
}

