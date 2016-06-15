package org.cy3sabiork;

import javax.ws.rs.core.Response;

/**
 * Result of the given web service query.
 */
public class SabioQueryResult {

	final private String query;
	final private Response response;
	final private Integer status;
	final private String sbml;
	
	public SabioQueryResult(final String query, final Response response){
		this.query = query;
		this.response = response;
		this.status = response.getStatus();
		
		if (success()) {
			this.sbml = response.readEntity(String.class);	
			System.out.println("--------------------------------------------");
			System.out.println(this.sbml);
			System.out.println("--------------------------------------------");
		} else {
			System.out.println("Request failed with status code: " + status);
			// throw new RuntimeException("Failed : HTTP error code : "
			//		+ response.getStatus());
			this.sbml = null;
		}
		// TODO: read kinetic entries if available.
		
	}
	
	/** Returns true if the request was successful. */
	public boolean success(){
		return (status == 200);
	}
	
	public String getQuery(){
		return query;
	}
	
	public Response getResponse(){
		return response;
	}
	
	public String getSBML(){
		return sbml;
	}
	
	public Integer getStatus(){
		return status;
	}
}
