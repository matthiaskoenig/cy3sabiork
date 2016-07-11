package org.cy3sabiork.rest;


import org.cy3sabiork.rest.SabioQueryJersey;

import javax.ws.rs.core.Response;

/** Querying the SABIO-RK web service. */
public class TestJersey {
			
	/** Create client and perform query. */
	public static void newQuery(String query) {
		try {
			
			Response response = (new SabioQueryJersey()).executeQuery(query);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			
			System.out.println("MediaType of response: <" + response.getMediaType() + ">");
			
			String output = response.readEntity(String.class);
			System.out.println("--------------------------------------------");
			System.out.println(output);
			System.out.println("--------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/* Test the Restful API. */
	public static void main(String[] args){				
		newQuery("kineticLaws/123");
		// newQuery("searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"");
		// newQuery("searchKineticLaws/sbml?q=Tissue:spleen%20AND%20Organism:%22homo%20sapiens%22");
	}
}
