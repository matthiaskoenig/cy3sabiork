package org.cy3sabiork.rest;


import java.net.URI;
import java.net.URISyntaxException;

import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.apache.commons.io.IOUtils;
import org.cy3sabiork.SabioQueryResult;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.logging.LoggingFeature;


/**
 * Performing SabioRK queries.
 * 
 * This is the web service client class performing the queries.
 * see https://jersey.java.net/documentation/latest/client.html
 * 
 * A resource in the JAX-RS client API is an instance of the Java class WebTarget
 * and encapsulates an URI. The fixed set of HTTP methods can be invoked based on the WebTarget.
 * 
 * FIXME: reuse the created client instead of static access
 * 		 (client connection building and init creates additional overhead)
 */

public class SabioQueryJersey extends SabioQuery{

	/**
	 * Parses the path and the parameters from the query string.
	 * 
	 * Query strings are of the form:
	 * 		kineticLaws/123
	 * 		searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"
	 * 		searchKineticLaws/sbml?q=Tissue:spleen%20AND%20Organism:%22homo%20sapiens%22
	 */
	public SabioQueryResult performQuery(String query){

		Response response = executeQuery(query);
		if (response != null){
			Integer status = response.getStatus();
			String xml = null;
			if (status == 200){
				xml = response.readEntity(String.class);
			}
			return new SabioQueryResult(query, status, xml);	
		}
		return null;
	}
	
	/**
	 * Perform query for count of kineticLaws.
	 */
	public Integer performCountQuery(String query){
		query = convertToCountQuery(query);
		
		Response response = executeQuery(query);
		Integer count = -1;
		// success
		if (response != null && response.getStatus() == 200){
			String countString = response.readEntity(String.class);
			try {
				count = Integer.parseInt(countString);
			} catch (NumberFormatException e){
				count = 0;
			}
		}
		return count;
	}
	
	
	/** 
	 * Check status of the SABIO-RK webservice. 
	 */
	public String getSabioStatus(){
		String status = "Down";
		Response response = executeQuery("status");
		if (response != null){
			status = response.readEntity(String.class);
		}
		return status;
	}
	

	/**
	 * Main function for running the queries.
	 * 
	 * This handles the required query cleaning/escaping and returns the
	 * response of the query.
	 * 
	 * This method should not be called from outside.
	 */
	protected Response executeQuery(String query){
		try {
			URI uri = uriFromQuery(query);
			
			// logging
			Logger logger = Logger.getLogger(SabioQueryJersey.class.getName());			
			ConsoleHandler handler = new ConsoleHandler();
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			Feature loggingFeature = new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, null);
			
			// create client
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);
			Client client = ClientBuilder.newClient(clientConfig)
										 .register(loggingFeature);
										 //.register(StringMessageBodyReader.class);
			
			// Invocation.Builder builder = 
			Response response = client
					 .target(uri)
					 .request("text/xml;charset=UTF-8")
					 .header("Content-Type","text/xml;charset=UTF-8")
					 .get();
					 
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

}
