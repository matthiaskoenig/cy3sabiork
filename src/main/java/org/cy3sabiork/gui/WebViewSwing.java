package org.cy3sabiork.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.fxml.FXMLLoader;
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
import javafx.scene.control.SplitPane;

import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.cy3sabiork.gui.FXMLController;


@SuppressWarnings("restriction")
public class WebViewSwing {
	public static File appDirectory;
	
	private static void initAndShowGUI(final JFrame parentFrame) {
        // This method is invoked on the EDT thread
		
        JFrame frame = new JFrame("Swing and JavaFX");
        JDialog dialog = new JDialog(parentFrame);
        dialog.setTitle("SABIO-RK Web Services");
        
        
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

    private static void initFX(JFXPanel fxPanel, final int width, final int height) {
        // This method is invoked on the JavaFX thread
        
    	// Set browser
    	// Scene scene = new Scene(new Browser(appDirectory),1200, 800, Color.web("#666970"));
        // fxPanel.setScene(scene);
        
        // Set GUI
        SplitPane root;
		try {
			root = FXMLLoader.load(WebViewSwing.class.getResource("/gui/test.fxml"));
		    Scene scene = new Scene(root);
		    fxPanel.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
        
    }
	
    
    public static void launch(JFrame parentFrame, File appDirectory){
    	WebViewSwing.appDirectory = appDirectory;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI(parentFrame);
            }
        });
    }
    
    public static void main(String[] args) {
    	File appDirectory = new File("src/main/resources");
    	System.out.println(appDirectory.getAbsolutePath());
    	launch(null, appDirectory);
    }
}
