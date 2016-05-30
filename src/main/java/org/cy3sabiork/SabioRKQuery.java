package main.java.org.cy3sabiork;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.cy3sbml.SBMLReader;

/**
 * Performing SabioRK queries.
 *
 * TODO: proxy settings for the query
 */
public class SabioRKQuery {
	private static final Logger logger = LoggerFactory.getLogger(SabioRKQuery.class);
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";

	public String performQuery(String queryURL, Boolean proxySet, String proxyHost, 
					Integer proxyPort) {
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
			// Create the client
			Client client = new Client();
			WebResource resource = client.resource(queryURL);			
			ClientResponse response = resource.get(ClientResponse.class);
			//ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get(
			//		ClientResponse.class);
			
			if (response.getStatus() != 200) {
				// first argument the Cytpscape application
				JOptionPane.showMessageDialog(null,
					queryURL + "\n" +
					"SabioRK REST Failed : HTTP error code : "
								+ response.getStatus(), "SabioRK REST Error",
				    JOptionPane.ERROR_MESSAGE);
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			output = response.getEntity(String.class);
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
