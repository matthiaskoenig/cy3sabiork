package cysabiork.restful;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TestRestful {


	/** Create the client and perform a query with a given URL to test
	 * the SabioRK RESTful connection.
	 */
	public static int test() {
		try {
			// Create the client
			ClientConfig clientConfig = new DefaultClientConfig();
			Client client = Client.create(clientConfig);

			// Get the properties & set() properties if necessary
			Map<String, Object> clientProperties = client.getProperties();

			// Get Webresource
			WebResource resource = client.resource(SabioRKQuery.SABIORK_RESTFUL_URL);

			ClientResponse response = resource.accept("application/xml").get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			String output = response.getEntity(String.class);
			System.out.println("Server connection SabioRK established");
			
			// System.out.println("Output from Server .... \n");
			// System.out.println(output);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	public static int testQuery() {
		try {
			// Create the client
			ClientConfig clientConfig = new DefaultClientConfig();
			Client client = Client.create(clientConfig);

			// Get the properties & set() properties if necessary
			Map<String, Object> clientProperties = client.getProperties();

			// Get Webresource
			WebResource resource = client.resource(SabioRKQuery.SABIORK_RESTFUL_URL);

			ClientResponse response = resource.accept("application/xml").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			
			// Example query 
			String query = "kineticLaws/123";
			String xml = resource.path(query).get(String.class);
			System.out.println("Query:" + query);
			System.out.println(xml);
		
			System.out.println("Server connection SabioRK established");
			
			// System.out.println("Output from Server .... \n");
			// System.out.println(output);
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
		test();
		testQuery();
	}
}
