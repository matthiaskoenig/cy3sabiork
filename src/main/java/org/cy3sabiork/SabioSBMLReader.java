package org.cy3sabiork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import org.cytoscape.task.read.LoadNetworkFileTaskFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SabioSBMLReader {
	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);
	
	private LoadNetworkFileTaskFactory factory;
	private SynchronousTaskManager taskManager;
	
	/* Helper class to read SBML graphs. */
	public SabioSBMLReader(LoadNetworkFileTaskFactory factory, SynchronousTaskManager taskManager){
		this.factory = factory;
		this.taskManager = taskManager;
	}
	
	/** Create Cytoscape graphs from SBML string. */
	public void loadNetworkFromSBML(String sbml){
		logger.info("Load SBML for kinetic information");
		// InputStream instream = new ByteArrayInputStream(sbml.getBytes(StandardCharsets.UTF_8));
		
		// Load the network via a LoadNetworkFileTaskFactory
		try{
			// temp file
    	    File temp = File.createTempFile("sabiork-temp", ".xml"); 
    	    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
    	    bw.write(sbml);
    	    bw.close();
    	    
    	    // execute task
    		TaskIterator taskIterator = factory.createTaskIterator(temp);
    		taskManager.execute(taskIterator);
    		
    		// Creates a task to read the SBML file
    		
    		// SBMLReaderTask sbmlReaderTask = new SBMLReaderTask(instream, null, networkFactory, viewFactory, viewManager);
    		// TaskIterator taskIterator = new TaskIterator(sbmlReaderTask);
    		
    	}catch(IOException e){
    	    e.printStackTrace();
    	}
	}
}
