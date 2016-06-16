package org.cy3sabiork;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Performing SabioRK queries.
 * Necessary to give feedback to the user about the status of the query.
 */
public class SabioQuery {
	// private static final Logger logger = LoggerFactory.getLogger(SabioQuery.class);
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";

	
	/** Check status of the SABIO-RK webservice. */
	public static String getSabioStatus(){
		String status = "Down";
		String query = "status";
		try {
			URI uri = new java.net.URI(SabioQuery.SABIORK_RESTFUL_URL + "/" + query);
	
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget requestTarget = client.target(uri);
			
			// WebTarget requestTarget = resourceTarget.path(path);
			System.out.println("URI: " + requestTarget.getUri());			
			
			// invocation of request
			Invocation.Builder invocationBuilder = requestTarget.request(MediaType.TEXT_PLAIN);
			Response response = invocationBuilder.get();
			status = response.readEntity(String.class);
	
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return status;
	}
	
	
	
	/**
	 * Parses the path and the parameters from the query string.
	 * 
	 * Query strings are of the form:
	 * 		kineticLaws/123
	 * 		searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"
	 * 		searchKineticLaws/sbml?q=Tissue:spleen%20AND%20Organism:%22homo%20sapiens%22
	 */
	public static SabioQueryResult performQuery(String query){
		//logger.info("Perform Sabio-RK query");

		String output = null;
		try {	
			// Create URI after required replacements (as long as SABIO-RK has no proper encoding)
			query = query.replace(" ", "%20");
			query = query.replace("\"", "%22");
			URI uri = new java.net.URI(SabioQuery.SABIORK_RESTFUL_URL + "/" + query);
	
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget requestTarget = client.target(uri);
			
			// WebTarget requestTarget = resourceTarget.path(path);
			System.out.println("URI: " + requestTarget.getUri());			
			
			// invocation of request
			Invocation.Builder invocationBuilder = requestTarget.request(MediaType.TEXT_XML_TYPE);
			Response response = invocationBuilder.get();

			return new SabioQueryResult(query, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Parses the path and the parameters from the query string.
	 
	public String performQuery(String query){
		HashMap<String, String> parameters = new HashMap<String, String>();
		String[] parts = query.split("\\?");
		String path = parts[0];
		
		// parameters
		if (parts.length > 1){
			String[] tokens = parts[1].split("=");
			parameters.put(tokens[0], tokens[1]);
		}
		return performQuery(path, parameters);
	}
	*/
	
	/* Create the client and perform a query with a given URL. 
	 * 
	 * This is the correct way to handle the query parameters, 
	 * but due to URL encoding problems in SABIO-RK this can currently not
	 * be used.
	 
	public String performQuery(String path, Map<String, String> parameters) {
		logger.info("Perform Sabio-RK query");

		String output = null;
		try {			
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget resourceTarget = client.target(SabioQuery.SABIORK_RESTFUL_URL);

			// Add the path to the target
			WebTarget requestTarget = resourceTarget.path(path);
			
			// add query parameters
			for (String key: parameters.keySet()){
				requestTarget = requestTarget.queryParam(key, parameters.get(key));
			}
			System.out.println("URI: " + requestTarget.getUri());
			
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
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	*/
	

}
