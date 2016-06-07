package org.cy3sabiork;

import java.util.HashMap;
import java.util.Map;

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
 */
public class SabioQuery {
	private static final Logger logger = LoggerFactory.getLogger(SabioQuery.class);
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";

	
	/**
	 * Parses the path and the parameters from the query string.
	 */
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
	
	
	/* Create the client and perform a query with a given URL. */
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
	
	

}
