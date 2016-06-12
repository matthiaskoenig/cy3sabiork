package org.cy3sabiork.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
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


public class WebViewSwing {
	
	private static void initAndShowGUI(JFrame parentFrame) {
        // This method is invoked on the EDT thread
		
		
        JFrame frame = new JFrame("Swing and JavaFX");
        JDialog dialog = new JDialog(parentFrame);
        
        int width = 1100;
        int height = 700;
        
        final JFXPanel fxPanel = new JFXPanel();
        
        dialog.add(fxPanel);
        dialog.setSize(width, height);
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(parentFrame);
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel, width, height);
            }
       });
    }

    private static void initFX(JFXPanel fxPanel, int width, int height) {
        // This method is invoked on the JavaFX thread
        Scene scene = new Scene(new Browser(),1200, 800, Color.web("#666970"));
        scene.getStylesheets().add("webviewsample/BrowserToolbar.css"); 
        fxPanel.setScene(scene);
    }
	
    
    public static void  launch(JFrame parentFrame){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI(parentFrame);
            }
        });
    }
    
    public static void main(String[] args) {
    	launch(null);
    }
}
