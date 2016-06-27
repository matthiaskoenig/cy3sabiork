package org.cy3sabiork.gui;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;


import org.cy3sabiork.ResourceExtractor;
import org.cy3sabiork.SabioQueryHistory;
import org.cy3sabiork.SabioSBMLReader;
import org.cy3sabiork.gui.QueryFXMLController;
import org.cytoscape.util.swing.OpenBrowser;

/** 
 * 
 * TODO: make singleton class which can store the state (for history of calls)
 * TODO: provide possibility to close
 *
 */

@SuppressWarnings("restriction")
public class WebViewSwing {
	public static OpenBrowser openBrowser;
	public static SabioSBMLReader sbmlReader;
	public static SabioQueryHistory queryHistory;
	
	private static void initAndShowGUI(final JFrame parentFrame) {
        // This method is invoked on the EDT thread
		
        JDialog dialog = new JDialog(parentFrame);
        dialog.setTitle("SABIO-RK Web Services");
        
        // use values from Scene Builder
        int width = 1400;
        int height = 1000;
        
        final JFXPanel fxPanel = new JFXPanel();
        
        dialog.add(fxPanel);
        dialog.setSize(width, height);
        dialog.setVisible(true);
        dialog.setBackground(new Color(255, 255, 255));
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
       });
    }

    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        
		try {
			// Set browser
	    	// Scene scene = new Scene(new Browser(appDirectory));
	        // fxPanel.setScene(scene);
			
			// Load FXML GUI scence
			// see : http://blog.admadic.com/2013/03/javafx-fxmlloader-with-osgi.html
			FXMLLoader.setDefaultClassLoader(WebViewSwing.class.getClassLoader());
			
			FXMLLoader loader = new FXMLLoader(WebViewSwing.class.getResource("/gui/query.fxml"));
			ScrollPane root = loader.load();

			QueryFXMLController controller = loader.getController();
			
			// controller.initData();
		    Scene scene = new Scene(root);
		    
		    // from appDirectory
		    String cssFile = ResourceExtractor.fileURIforResource("/gui/query.css");
		    scene.getStylesheets().add(cssFile);
		    
		    fxPanel.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    /** 
     * Start the JFXPanel. 
     * Main entry point to open the GUI.
     */
    public static void launch(JFrame parentFrame, OpenBrowser openBrowser, SabioSBMLReader sbmlReader){
    	WebViewSwing.openBrowser = openBrowser;
    	WebViewSwing.sbmlReader = sbmlReader;
    	
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI(parentFrame);
            }
        });
    }
    
    /** 
     * Testing the GUI without Cytoscape specific functionality.
     * Run this main to use the SABIO-RK webservice without the
     * Cytoscape backend.
     * Allows to test functionality which currently cannot be backed
     * in the OSGI bundles. 
     */
    public static void main(String[] args) {
    	// It is ecessary to provide access to file resources in a consistent
    	// way in bundle and non-bundle context.
    	// The solution is the use of the ResourceExtractor to use local files.
    	
    	File appDirectory = new File("src/main/resources");
    	ResourceExtractor.setAppDirectory(appDirectory);
    	
    	// GUI launch without Cytoscape
    	launch(null, null, null);
    }
}
