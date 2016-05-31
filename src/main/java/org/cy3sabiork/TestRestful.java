package org.cy3sabiork;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Testing the SabioRK restful interface for queries.
 */
public class TestRestful {


	/** Create the client and perform a query with a given URL to test
	 * the SabioRK RESTful connection.
	 */
	public static int testQuery(String query) {
		try {
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
			
			String output = response.readEntity(String.class);
			System.out.println("--------------------------------------------");
			System.out.println(output);
			System.out.println("--------------------------------------------");

			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	/** Get list of available fields for querying. 
	 * FIXME: not implemented
	 * */
	public static List<String> getQueryFields(){
		SabioRKQuery sQuery = new SabioRKQuery();
		java.lang.String queryURL = SabioRKQuery.SABIORK_RESTFUL_URL + "/searchKineticLaws";
		String res = sQuery.performQuery(queryURL);
		
		// get the possible query fields for the Sabio RESTful
		// webservice
		List<String> fields = new LinkedList<String>();
		// TODO: do the magic
	
		return fields;
	}
	
	/* Test the Restful API */
	public static void main(String[] args) {
		System.out.println("CySabioRK[INFO]: TestRESTful SabioRK Connection");
		testQuery("kineticLaws/123");
	}
}
