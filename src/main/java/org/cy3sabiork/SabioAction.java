package org.cy3sabiork;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cy3sabiork.gui.SabioDialog;

/**
 * Test access to the cy3sbml instance information.
 */
public class SabioAction extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SabioAction.class);
	private CySwingApplication cySwingApplication;
	
	public static SabioSBMLReader sbmlReader;
	
	public SabioAction(CySwingApplication cySwingApplication){
		super("SabioRKAction");
		this.cySwingApplication = cySwingApplication;
		
		/*
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo-sabiork.png"));
		putValue(LARGE_ICON_KEY, icon);
		
		this.putValue(SHORT_DESCRIPTION, "cyfluxviz action");
		setToolbarGravity((float) 500.0);
		*/
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
		// Open the dialog
		JFrame frame = this.cySwingApplication.getJFrame();
		SabioDialog sabioRKDialog = new SabioDialog(frame, sbmlReader);
	    sabioRKDialog.setVisible(true);
	}
	
	public static void setSabioSBMLReader(SabioSBMLReader sbmlReader){
		SabioAction.sbmlReader = sbmlReader;
	}
}