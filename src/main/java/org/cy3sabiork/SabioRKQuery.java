package org.cy3sabiork;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cy3sbml.SBMLReader;

/**
 * Performing SabioRK queries.
 *
 * TODO: proxy settings for the query
 * TODO: use SBMLReader
 */
public class SabioRKQuery {
	private static final Logger logger = LoggerFactory.getLogger(SabioRKQuery.class);
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";

	public String performQuery(String queryURL, Boolean proxySet, String proxyHost, 
					Integer proxyPort) {
		System.out.println("******************************************");
		System.out.println("Perform Sabio-RK query");
		System.out.println("******************************************");
		
    	// Set the proxy properties - handled by CySBML
		if (proxySet==true && proxyHost!=null && proxyPort!=null){ 
			Properties props= new Properties(System.getProperties());
			props.put("http.proxySet", proxySet.toString());
			props.put("http.proxyHost", proxyHost.toString());
			props.put("http.proxyPort", proxyPort.toString());
			Properties newprops = new Properties(props);
			System.setProperties(newprops);
		}
		
		// Do the query
		String output = null;
		try {
			// Example query 
			String query = "kineticLaws/123";
			
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget resourceTarget = client.target(SabioRKQuery.SABIORK_RESTFUL_URL);

			// Add the path to the target
			WebTarget requestTarget = resourceTarget.path(query);
			
			// invocation of request
			Invocation.Builder invocationBuilder = requestTarget.request(MediaType.TEXT_XML_TYPE);
			Response response = invocationBuilder.get();
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			output = response.readEntity(String.class);
			

			System.out.println("******************************************");
			System.out.println(output);
			System.out.println("******************************************");
			loadNetworkFromSBML(output);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	/* Create the client and perform a query with a given URL */
	public String performQuery(String queryURL) {
		return performQuery(queryURL, false, null, null);
	}
	
	public void loadNetworkFromSBML(String sbml){
		logger.info("Load SBML for kinetic information");
		InputStream instream;
		try {
			instream = new ByteArrayInputStream(sbml.getBytes("UTF-8"));
			
			// TODO: load the SBML as a network with cy3sbml
			// Cytoscape.createNetwork(new SBMLGraphReader(instream),true, null);
			System.out.println("READ SBML Results");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
