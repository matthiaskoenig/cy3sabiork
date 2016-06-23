package org.cy3sabiork.oven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Browser for displaying HTML.
 */
public class Browser extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();
	final File appDirectory; 

	public Browser(File appDirectory) {
		this.appDirectory = appDirectory;
		//apply the styles
		getStyleClass().add("browser");
		
		// external URLs work
		// webEngine.load("http://sabiork.h-its.org/newSearch/index");
		
		// String content works
        // webEngine.loadContent("<html><h1>Hello world</h1></html>");
		
		// Resource content does not work
		// URL queryURL = getClass().getResource("/gui/query.html");
		// System.out.println(queryURL);    
        // loadPage(queryURL.toString());
        
		/*
        // load local resource
        File file = new File(appDirectory + "/gui/query.html");
		URI fileURI = file.toURI();
		System.out.println(fileURI);    
        loadPage(fileURI.toString());
        
        System.out.println("app directory: " + appDirectory.getAbsolutePath());
    	*/
        
        webEngine.load("http://sabiork.h-its.org/kineticLawEntry.jsp?viewData=true&kinlawid=14792");
		
		//add the web view to the scene
		getChildren().add(browser);

	}
	
	private void loadPage(String url) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                webEngine.load(url);

            }
        });
    }
	
	
	private Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	@Override protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
	}

	@Override protected double computePrefWidth(double height) {
		return 900;
	}

	@Override protected double computePrefHeight(double width) {
		return 600;
	}
}