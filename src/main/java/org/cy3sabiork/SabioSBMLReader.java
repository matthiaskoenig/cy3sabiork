package org.cy3sabiork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helper class to provide SBML reader functionality. */
public class SabioSBMLReader {
	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);
	
	private LoadNetworkFileTaskFactory factory;
	@SuppressWarnings("rawtypes")
	private TaskManager taskManager;
	
	/* Helper class to read SBML graphs. */
	@SuppressWarnings("rawtypes")
	public SabioSBMLReader(LoadNetworkFileTaskFactory factory, TaskManager taskManager){
		this.factory = factory;
		this.taskManager = taskManager;
	}
	
	/** Create Cytoscape graphs from SBML string. */
	public void loadNetworkFromSBML(String sbml){
		logger.debug("Load SBML for kinetic information");
		try{
			// write with encoding
			File tmpFile = File.createTempFile("test", ".xml");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8"));
	    	try {
	    	    out.write(sbml);
	    	} finally {
	    	    out.close();
	    	}
			
    	    // execute task
    		TaskIterator taskIterator = factory.createTaskIterator(tmpFile);
    		taskManager.execute(taskIterator);
    		
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
	}
}
