package org.cy3sabiork;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.osgi.framework.BundleContext;

import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.SynchronousTaskManager;

import org.cy3sbml.BundleInformation;
import org.cy3sabiork.gui.SabioPanel;
import org.cy3sabiork.SabioAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main entry point for OSGI. */
public class CyActivator extends AbstractCyActivator {
	private static Logger logger;
	
	public CyActivator() {
		super();
	}
	
	public void start(BundleContext bc) {
		try {
			BundleInformation bundleInfo = new BundleInformation(bc);
			
			// app directory 
			final CyApplicationConfiguration config = getService(bc, CyApplicationConfiguration.class);
			final File cyDirectory = config.getConfigurationDirectoryLocation();
			final File appDirectory = new File(cyDirectory, bundleInfo.getName());
			
			if(appDirectory.exists() == false) {
				appDirectory.mkdir();
			}
			// log file
			File logFile = new File(appDirectory, bundleInfo.getInfo() + ".log");
			System.setProperty("logfile.name", logFile.getAbsolutePath());
			logger = LoggerFactory.getLogger(CyActivator.class);
			
			logger.info("----------------------------");
			logger.info("Start " + bundleInfo.getInfo());
			logger.info("----------------------------");
			logger.info("directory = " + appDirectory.getAbsolutePath());
			logger.info("logfile = " + logFile.getAbsolutePath());
			
			// get services
			final CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
			final OpenBrowser openBrowser = getService(bc, OpenBrowser.class);
			
			// SBML reader
			final SynchronousTaskManager synchronousTaskManager = getService(bc, SynchronousTaskManager.class);
			final LoadNetworkFileTaskFactory loadNetworkFileTaskFactory = getService(bc, LoadNetworkFileTaskFactory.class);
			SabioSBMLReader sbmlReader = new SabioSBMLReader(loadNetworkFileTaskFactory, synchronousTaskManager);
		
			// init actions
			SabioAction sabioAction = new SabioAction(cySwingApplication, openBrowser, sbmlReader);
			registerService(bc, sabioAction, CyAction.class, new Properties());
		
			// Extract resource file if necessary
			final ResourceExtractor resourceHandler = new ResourceExtractor(bc, appDirectory);
			resourceHandler.test();
			
			// Sabio Panel
			SabioPanel sabioPanel = SabioPanel.getInstance(cySwingApplication, sabioAction);
			registerService(bc, sabioPanel, CytoPanelComponent.class, new Properties());
			SabioPanel.getInstance().activate();
			
			logger.info("----------------------------");
		} catch (Throwable e){
			logger.error("Could not start server!", e);
			e.printStackTrace();
		}
	}
}