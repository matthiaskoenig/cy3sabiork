package org.cy3sabiork;

import java.util.ArrayList;
import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;

import org.cy3sabiork.rest.SabioQuery;

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
			// Create SBML from response
			this.sbml = SabioQuery.readEntityInString(this.response);
			
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
	
	public Response getResponse(){
		return response;
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
