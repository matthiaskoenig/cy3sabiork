package org.cy3sabiork.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.util.swing.OpenBrowser;

import org.cy3sabiork.SabioAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SabioPanel.
 * Registered as Results panel.
 */
public class SabioPanel extends JPanel implements CytoPanelComponent, HyperlinkListener{
	private static final Logger logger = LoggerFactory.getLogger(SabioPanel.class);
	private static final long serialVersionUID = 1L;

	private static SabioPanel uniqueInstance;
	private CySwingApplication cySwingApplication;
	private CytoPanel cytoPanelEast;
	private JEditorPaneSabio textPane;
	private SabioAction sabioAction;
	private OpenBrowser openBrowser;

	/** Singleton. */
	public static synchronized SabioPanel getInstance(CySwingApplication cySwingApplication, SabioAction sabioAction){
		if (uniqueInstance == null){
			logger.debug("ResultsPanel created");
			uniqueInstance = new SabioPanel(cySwingApplication, sabioAction);
		}
		return uniqueInstance;
	}
	public static synchronized SabioPanel getInstance(){
		return uniqueInstance;
	}
	
	/** Constructor */
	private SabioPanel(CySwingApplication cySwingApplication, SabioAction sabioAction){
		this.cySwingApplication = cySwingApplication;
		this.openBrowser = sabioAction.getOpenBrowser(); 
		this.sabioAction = sabioAction;
		this.cytoPanelEast = this.cySwingApplication.getCytoPanel(CytoPanelName.EAST);
		
		// SBML information area
		setLayout(new BorderLayout(0, 0));
		
		textPane = new JEditorPaneSabio();
		textPane.addHyperlinkListener(this);
		
		JScrollPane annotationScrollPane = new JScrollPane();
		annotationScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		annotationScrollPane.setViewportView(textPane);
		this.add(annotationScrollPane);
	}
	
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}
	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(getClass().getResource("/gui/images/icon-sabiork.png"));
	}

	@Override
	public String getTitle() {
		return "";  // title in tab
	}

	public boolean isActive(){
		return (cytoPanelEast.getState() != CytoPanelState.HIDE);
	}

    public void activate(){
		// If the state of the cytoPanelWest is HIDE, show it
		if (cytoPanelEast.getState() == CytoPanelState.HIDE) {
			cytoPanelEast.setState(CytoPanelState.DOCK);
		}	
		// Select panel
		select();
    }
		
	public void deactivate(){
		// Test if still other Components in Panel, otherwise hide the complete panel
		if (cytoPanelEast.getCytoPanelComponentCount() == 1){
			cytoPanelEast.setState(CytoPanelState.HIDE);
		}
	}

	public void changeState(){
		if (isActive()){
			deactivate();
		} else {
			activate();
		}
	}
	
	public void select(){
		int index = cytoPanelEast.indexOfComponent(this);
		if (index == -1) {
			return;
		}
		cytoPanelEast.setSelectedIndex(index);
	}
		
	public JEditorPaneSabio getTextPane(){
		return textPane;
	}
	
	/** Set help information. */
	public void setHelp(){
		textPane.setHelp();
	}
	
	/////////////////// HANDLE EVENTS ///////////////////////////////////

	/** 
	 * Handle hyperlink events in the textPane.
	 * Either opens browser for given hyperlink or triggers Cytoscape actions
	 * for subsets of special hyperlinks.
	 * 
	 * This provides an easy solution for integrating app functionality
	 * with click on hyperlinks.
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		/* Open link in browser. */
		URL url = evt.getURL();
		if (url != null) {
			if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
				// nothing
			} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
				// nothing
			} else if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				// decide what to do depending on URL
				String s = url.toString();
				
				// menu action
				if (s.equals("http://sabiork-query")){
					sabioAction.actionPerformed(null);
				} else {
					// handle the HTML links
					this.openBrowser.openURL(url.toString());	
				}
			}
		}
	}
	
}
