package org.cy3sabiork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.work.SynchronousTaskManager;
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
			// temp file
    	    File temp = File.createTempFile("sabiork-temp", ".xml"); 
    	    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
    	    bw.write(sbml);
    	    bw.close();
    	    
    	    // execute task
    		TaskIterator taskIterator = factory.createTaskIterator(temp);
    		taskManager.execute(taskIterator);
    		
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
	}
}
