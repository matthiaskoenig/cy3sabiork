package org.cy3sabiork;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Main entry point for OSGI.
 */
public class CyActivator extends AbstractCyActivator {
	private static Logger logger;
	
	public CyActivator() {
		super();
	}
	
	public void start(BundleContext bc) {
		try {
			BundleInformation bundleInfo = new BundleInformation(bc);
			

			// app directory 
			CyApplicationConfiguration configuration = getService(bc, CyApplicationConfiguration.class);
			File cyDirectory = configuration.getConfigurationDirectoryLocation();
			File appDirectory = new File(cyDirectory, bundleInfo.getName());
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
			

			File ftest = bc.getDataFile("gui/info.html");
			System.out.println(ftest.getAbsolutePath());
			System.out.println("File exists: " + ftest.exists());
			
			
			// copying the file does not work
			// Files.copy(ftest.toPath(), new File(appDirectory + "/" + "info.html").toPath(), 
			//		StandardCopyOption.REPLACE_EXISTING);
			
			
			URL infoURL = getClass().getResource("/gui/info.html");
			System.out.println(infoURL);
			InputStream inputStream = infoURL.openStream();
			OutputStream outputStream = new FileOutputStream(new File(appDirectory + "/" + "info.html"));

			
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			
			
			/*
			Bundle bundle = bc.getBundle();
			@SuppressWarnings("unchecked")
			Enumeration<String> e = bundle.getEntryPaths("/gui/");
			while(e.hasMoreElements()){
				String path = e.nextElement();
				System.out.println(path);
				
				// skip directories
				if (path.endsWith("/")){
					continue;
				}
				File file = bc.getDataFile(path);
				System.out.println(file.getAbsolutePath());
				
				// copy to app folder
				Path src = file.toPath();	
				Path des = new File(appDirectory.toPath() + "/" + path).toPath();
				System.out.println(src + " -> " + des);
				Files.copy(file.toPath(), des, StandardCopyOption.REPLACE_EXISTING);	
			}
			*/
			
			
			CySwingApplication cySwingApplication = getService(bc, CySwingApplication.class);
			OpenBrowser openBrowser = getService(bc, OpenBrowser.class);
			
			// SBML reader
			SynchronousTaskManager synchronousTaskManager = getService(bc, SynchronousTaskManager.class);
			LoadNetworkFileTaskFactory loadNetworkFileTaskFactory = getService(bc, LoadNetworkFileTaskFactory.class);
			SabioSBMLReader sbmlReader = new SabioSBMLReader(loadNetworkFileTaskFactory, synchronousTaskManager);
		
			// init actions
			SabioAction sabioAction = new SabioAction(cySwingApplication);	
			SabioAction.setSabioSBMLReader(sbmlReader);
			registerService(bc, sabioAction, CyAction.class, new Properties());
		
			// Sabio Panel
			SabioPanel sabioPanel = SabioPanel.getInstance(cySwingApplication, openBrowser, sabioAction);
			registerService(bc, sabioPanel, CytoPanelComponent.class, new Properties());
			SabioPanel.getInstance().activate();
			
			logger.info("----------------------------");
		} catch (Throwable e){
			logger.error("Could not start server!", e);
			e.printStackTrace();
		}
	}
}