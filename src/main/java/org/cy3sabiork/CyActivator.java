package org.cy3sabiork;

import java.io.File;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.util.swing.OpenBrowser;
import org.cy3sabiork.gui.SabioPanel;
import org.cy3sbml.BundleInformation;
import org.cy3sbml.gui.ResultsPanel;

/**
 * cy3sabiork activator. 
 * Display reaction kinetics information via WebServices in Cytoscape. 
 */
public class CyActivator extends AbstractCyActivator {
	private static Logger logger;
	
	public CyActivator() {
		super();
	}
	
	public void start(BundleContext bc) {
		try {
			BundleInformation bundleInfo = new BundleInformation(bc);
			
			// Default configuration directory used for all cy3sbml files 
			// Used for retrieving
			CyApplicationConfiguration configuration = getService(bc, CyApplicationConfiguration.class);
			File cyDirectory = configuration.getConfigurationDirectoryLocation();
			File appDirectory = new File(cyDirectory, bundleInfo.getName());
			
			if(appDirectory.exists() == false) {
				appDirectory.mkdir();
			}
			// store bundle information (for display of dependencies, versions, ...)
			File logFile = new File(appDirectory, bundleInfo.getInfo() + ".log");
			System.setProperty("logfile.name", logFile.getAbsolutePath());
			logger = LoggerFactory.getLogger(CyActivator.class);
			
			logger.info("----------------------------");
			logger.info("Start " + bundleInfo.getInfo());
			logger.info("----------------------------");
			logger.info("directory = " + appDirectory.getAbsolutePath());
			logger.info("logfile = " + logFile.getAbsolutePath());
								
			CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
			OpenBrowser openBrowser = getService(bc, OpenBrowser.class);
			
			
			// init actions
			SabioRKAction action = new SabioRKAction(cySwingApplication);
			registerService(bc, action, CyAction.class, new Properties());
			
			// Sabio Panel
			SabioPanel sabioPanel = SabioPanel.getInstance(cySwingApplication, openBrowser);
			registerService(bc, sabioPanel, CytoPanelComponent.class, new Properties());
			SabioPanel.getInstance().activate();
			
			logger.info("----------------------------");
		} catch (Throwable e){
			logger.error("Could not start server!", e);
			e.printStackTrace();
		}
	}
}