package org.cy3sabiork;

import java.util.ArrayList;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;

import org.cy3sabiork.rest.SabioQuery;
import org.glassfish.jersey.client.ClientResponse;

/**
 * Result of the given web service query.
 */
public class SabioQueryResult {

	final private String query;
	final private Integer status;
	final private String sbml;
	
	public SabioQueryResult(final String query, final Response response){
		this.query = query;
		this.status = response.getStatus();
		
		if (success()) {
			// Create SBML from response
			this.sbml = SabioQuery.readEntityInString(response);
			
		} else {
			System.out.println("Request failed with status code: " + status);
			
			this.sbml = null;
		}
	}
	
	/** Returns true if the request was successful. */
	public boolean success(){
		return (status == 200);
	}
	
	public String getQuery(){
		return query;
	}
	
	
	public String getSBML(){
		return sbml;
	}
	
	public Integer getStatus(){
		return status;
	}
	
	/** Read the kineticLaws from the given SBML; 
	 * @throws XMLStreamException */
	public ArrayList<SabioKineticLaw> getKineticLaws(){
		return SabioKineticLaw.parseKineticLaws(sbml);
	}
	
	
}
