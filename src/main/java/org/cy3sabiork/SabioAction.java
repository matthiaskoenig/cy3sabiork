package org.cy3sabiork;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cy3sabiork.gui.WebViewSwing;

/**
 * Test access to the cy3sbml instance information.
 */
public class SabioAction extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SabioAction.class);
	private CySwingApplication cySwingApplication;
	private File appDirectory;
	
	public static SabioSBMLReader sbmlReader;
	
	public SabioAction(CySwingApplication cySwingApplication, File appDirectory){
		super("SabioRKAction");
		this.cySwingApplication = cySwingApplication;
		this.appDirectory = appDirectory;
		
		ImageIcon icon = new ImageIcon(getClass().getResource("/gui/images/logo-sabiork.png"));
		putValue(LARGE_ICON_KEY, icon);
		
		this.putValue(SHORT_DESCRIPTION, "SABIO-RK web services");
		setToolbarGravity((float) 500.0);
	
	}
	
	public boolean insertSeparatorBefore(){
		return true;
	}
	
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		logger.debug("SabioAction performed.");
		JFrame frame = this.cySwingApplication.getJFrame();
		
		// Open JavaFX
		WebViewSwing.launch(frame, appDirectory);
		
		// Open dialog
		// SabioDialog.launch(frame, sbmlReader);
	}
	
	public static void setSabioSBMLReader(SabioSBMLReader sbmlReader){
		SabioAction.sbmlReader = sbmlReader;
	}
}