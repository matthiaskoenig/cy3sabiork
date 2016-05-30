package main.java.org.cy3sabiork;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cytoscape.Cytoscape;

/**
 * Test access to the cy3sbml instance information.
 */
public class SabioRKAction extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SabioRKAction.class);
	
	public SabioRKAction(CySwingApplication cySwingApplication){
		super("SabioRKAction");
				
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/logo-sabiork.png"));
		putValue(LARGE_ICON_KEY, icon);
		
		this.putValue(SHORT_DESCRIPTION, "cyfluxviz action");
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
		
		// Open the dialog
		SabioRKDialog sabioRKDialog = new SabioRKDialog((JFrame) Cytoscape.getDesktop());
	    sabioRKDialog.setVisible(true);
	}
}