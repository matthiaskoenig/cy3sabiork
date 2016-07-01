package org.cy3sabiork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.sbml.jsbml.JSBML;
import org.cytoscape.application.CyApplicationConfiguration;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.TaskManager;
import org.cy3sbml.BundleInformation;
import org.cy3sabiork.SabioAction;
import org.cy3sabiork.rest.SabioQueryJersey;
import org.cy3sabiork.rest.SabioQueryUniRest;
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
			final  TaskManager taskManager = getService(bc, TaskManager.class);
			final LoadNetworkFileTaskFactory loadNetworkFileTaskFactory = getService(bc, LoadNetworkFileTaskFactory.class);
			SabioSBMLReader sbmlReader = new SabioSBMLReader(loadNetworkFileTaskFactory, taskManager);
		
			// init actions
			SabioAction sabioAction = new SabioAction(cySwingApplication, openBrowser, sbmlReader);
			registerService(bc, sabioAction, CyAction.class, new Properties());
		
			// Extract all resource files for JavaFX (no bundle access)
			final ResourceExtractor resourceHandler = new ResourceExtractor(bc, appDirectory);
			resourceHandler.extract();
			logger.info("----------------------------");
			
			
			// ----------------- Testing --------------------------
			// SabioQueryResult result = new SabioQueryJersey().performQuery("kineticLaws/123");
			SabioQueryResult result = new SabioQueryUniRest().performQuery("kineticLaws/123");
			
			String sbml = result.getXML();
			
			// Read SBML
    	    JSBML.readSBMLFromString(sbml);
			
    	    /*
    	    File testFile = new File(appDirectory, "testEncoding.xml");
    	    System.out.println("-->" + testFile.getAbsolutePath());
    	    BufferedWriter writer = null;
    	    try{
    	        writer = new BufferedWriter( new FileWriter(testFile.getAbsolutePath()));
    	        writer.write(sbml);
    	    }
    	    catch ( IOException e){
    	    	e.printStackTrace();
    	    }
    	    finally {
    	        try {
    	            if ( writer != null)
    	            writer.close( );
    	        }
    	        catch ( IOException e){
    	        }
    	    }
    	    */
    	    
			
		} catch (Throwable e){
			logger.error("Could not start server!", e);
			e.printStackTrace();
		}
	}

}