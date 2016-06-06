package org.cy3sabiork;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Querying the SABIO-RK web service.
 */
public class TestRestful {

	/** 
	 * Create client and perform query.
	 */
	public static int testQuery(String query) {
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
		
	/* Test the Restful API */
	public static void main(String[] args) {
		System.out.println("CySabioRK[INFO]: TestRESTful SabioRK Connection");
		testQuery("kineticLaws/123");
	}
}
