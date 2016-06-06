package org.cy3sabiork;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.cy3sbml.CyActivator;
import org.cy3sbml.SBMLReaderTask;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SabioSBMLReader {
	private static final Logger logger = LoggerFactory.getLogger(CyActivator.class);
	
	private CyNetworkFactory networkFactory;
	private CyNetworkViewFactory viewFactory;
	private CyNetworkViewManager viewManager;
	private SynchronousTaskManager taskManager;
	
	
	/* Helper class to read SBML graphs. */
	public SabioSBMLReader(CyNetworkFactory networkFactory, CyNetworkViewFactory viewFactory, 
			CyNetworkViewManager viewManager, SynchronousTaskManager taskManager){
		this.networkFactory = networkFactory;
		this.viewFactory = viewFactory;
		this.viewManager = viewManager;
		this.taskManager = taskManager;
	}
	
	/** Create Cytoscape graphs from SBML string. */
	public void loadNetworkFromSBML(String sbml){
		logger.info("Load SBML for kinetic information");
		InputStream instream = new ByteArrayInputStream(sbml.getBytes(StandardCharsets.UTF_8));
			
		// Creates a task to read the SBML file
		SBMLReaderTask sbmlReaderTask = new SBMLReaderTask(instream, null, networkFactory, viewFactory, viewManager);
		TaskIterator taskIterator = new TaskIterator(sbmlReaderTask);
		taskManager.execute(taskIterator);
	}
}
