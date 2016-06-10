package org.cy3sabiork;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;


/**
 * Querying the SABIO-RK web service.
 */
public class TestRestful {

	/** 
	 * Create client and perform query.
	 */
	public static int testQuery(String query, Map<String, String> parameters) {
		try {
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget resourceTarget = client.target(SabioQuery.SABIORK_RESTFUL_URL);

			// Add the path to the target
			WebTarget requestTarget = resourceTarget.path(query);
			
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
	
	public static int testQuery(String query){
		return testQuery(query, new HashMap<String, String>());
	}
	
	
	/** 
	 * Create client and perform query.
	 */
	public static int newQuery(String path) {
		try {
			// Create URI after required replacements (as long as SABIO-RK has no proper encoding)
			path = path.replace(" ", "%20");
			path = path.replace("\"", "%22");
			URI uri = new java.net.URI(SabioQuery.SABIORK_RESTFUL_URL + "/" + path);
	
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget requestTarget = client.target(uri);
			
			// WebTarget requestTarget = resourceTarget.path(path);
			System.out.println("URI: " + requestTarget.getUri());			
			
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
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println("CySabioRK[INFO]: TestRESTful SabioRK Connection");
		
		// single kinetic law
		// testQuery("kineticLaws/123");
		
		// newQuery("kineticLaws/123");
		// newQuery("searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"");

		// replacements: 
		// 	' ' -> '%20'
		//	'"' -> '%22'
		
		newQuery("kineticLaws/123");
		newQuery("searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"");
		newQuery("searchKineticLaws/sbml?q=Tissue:spleen%20AND%20Organism:%22homo%20sapiens%22");
		
		/*
		// multiple kinetic laws
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("kinlawids", "18974,18976,18975,22516");
		testQuery("kineticLaws", parameters);

		// liver homo sapiens substrate glucose
		parameters = new HashMap<String, String>();
		parameters.put("q", "Tissue:liver%20AND%20Organism:Homo%20sapiens%20AND%20Substrate:Glucose");
		testQuery("searchKineticLaws/sbml", parameters);
			
		// search kinetic laws for chebi compound
		parameters = new HashMap<String, String>();
		parameters.put("q", URLEncoder.encode("ReactantChebi:17925", "UTF-8"));
		testQuery("searchKineticLaws/sbml", parameters);
		*/
	}
}
