package org.cy3sabiork;

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

	
	/* Create the client and perform a query with a given URL. */
	public String performQuery(String query) {
		logger.info("Perform Sabio-RK query");

		
		String output = null;
		try {			
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget resourceTarget = client.target(SabioQuery.SABIORK_RESTFUL_URL);

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
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
}
