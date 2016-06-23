package org.cy3sabiork;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Performing SabioRK queries.
 * Necessary to give feedback to the user about the status of the query.
 * 
 * FIXME: reuse the created client instead of static access
 * 		 (client connection building and init creates additional overhead)
 */

public class SabioQuery {
	// private static final Logger logger = LoggerFactory.getLogger(SabioQuery.class);
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";
	
	public static final String PREFIX_KINETIC_LAW_INFO = "http://sabiork.h-its.org/kineticLawEntry.jsp?viewData=true&kinlawid=";
	public static final String PREFIX_QUERY = "searchKineticLaws/sbml?q=";
	public static final String PREFIX_COUNT = "searchKineticLaws/count?q=";
	public static final String PREFIX_LAW = "kineticLaws/";
	public static final String PREFIX_LAWS = "kineticLaws?kinlawids=";
	public static final String CONNECTOR_AND = " AND ";

	/**
	 * Main function for running the queries.
	 * 
	 * This handles the required query cleaning/escaping and returns the
	 * response of the query.
	 */
	private static Response executeQuery(String query){
		try {
			// Create URI after required replacements (as long as SABIO-RK has no proper encoding)
			query = query.replace(" ", "%20");
			query = query.replace("\"", "%22");
			URI uri = new java.net.URI(SabioQuery.SABIORK_RESTFUL_URL + "/" + query);
			
			// Create client
			Client client = ClientBuilder.newClient();
			WebTarget requestTarget = client.target(uri);
			System.out.println("URI: " + requestTarget.getUri());			
			
			// Invoke request
			Invocation.Builder invocationBuilder = requestTarget.request(MediaType.TEXT_XML_TYPE);
			Response response = invocationBuilder.get();
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** 
	 * Check status of the SABIO-RK webservice. 
	 */
	public static String getSabioStatus(){
		String status = "Down";
		Response response = executeQuery("status");
		if (response != null){
			status = response.readEntity(String.class);
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
		Response response = executeQuery(query);
		if (response != null){
			return new SabioQueryResult(query, response);	
		}
		return null;
	}
	
	/**
	 * Perform query for count of kineticLaws.
	 */
	public static Integer performCountQuery(String query){
		
	    // convert sbml to count query if required
		if (query.startsWith(SabioQuery.PREFIX_QUERY)){
			query = query.replace(SabioQuery.PREFIX_QUERY, SabioQuery.PREFIX_COUNT);
		}
		
		Response response = executeQuery(query);
		Integer count = -1;
		// success
		if (response != null && response.getStatus() == 200){
			String countString = response.readEntity(String.class);
			count = Integer.parseInt(countString);
		}
		return count;
	}
	
	/** Generate query from ids. */
	public static String queryStringFromIds(Collection<Integer> ids){
		
		if (ids.size() == 1){
			return (SabioQuery.PREFIX_LAW + ids.iterator().next());	
		} else {
			String idText = null;
			for (Integer kid: ids){
				if (idText == null){
					idText = kid.toString();
				} else {
					idText += "," + kid.toString();
				}
			}
			return (SabioQuery.PREFIX_LAWS + idText);
	    }
	}
	
}
