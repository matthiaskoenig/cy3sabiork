package org.cy3sabiork;

import java.util.ArrayList;

/**
 * Result of the given web service query.
 */
public class SabioQueryResult {

	final private String query;
	final private Integer status;
	final private String xml;
	
	public SabioQueryResult(final String query, Integer status, String xml){
		this.query = query;
		this.status = status;
		this.xml = xml;
	}
	
	/** Returns true if the request was successful. */
	public boolean success(){
		return (status == 200);
	}
	
	public String getQuery(){
		return query;
	}
	
	public String getXML(){
		return xml;
	}
	
	public Integer getStatus(){
		return status;
	}
	
	/*
	 * Read the kineticLaws from the given SBML. 
	 * xml must not be SBML, i.e. some query results are xml but not SBML. 
	 */
	public ArrayList<SabioKineticLaw> getKineticLaws(){
		return SabioKineticLaw.parseKineticLaws(xml);
	}
	
}
