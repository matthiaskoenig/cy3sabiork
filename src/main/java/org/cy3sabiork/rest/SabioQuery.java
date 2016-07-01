package org.cy3sabiork.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;


import org.cy3sabiork.SabioQueryResult;

public abstract class SabioQuery{
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";
	
	public static final String PREFIX_KINETIC_LAW_INFO = "http://sabiork.h-its.org/kineticLawEntry.jsp?viewData=true&kinlawid=";
	public static final String PREFIX_QUERY = "searchKineticLaws/sbml?q=";
	public static final String PREFIX_COUNT = "searchKineticLaws/count?q=";
	public static final String PREFIX_LAW = "kineticLaws/";
	public static final String PREFIX_LAWS = "kineticLaws?kinlawids=";
	public static final String CONNECTOR_AND = " AND ";
	
	
	public abstract SabioQueryResult performQuery(String query);
	public abstract Integer performCountQuery(String query);
	public abstract String getSabioStatus();
	
	
	/** 
	 * Create URI from query String.
	 * Performs necessary replacements and sanitation of query, for 
	 * instance fixing issues with encoding.
	 */
	public static URI uriFromQuery(String query) throws URISyntaxException{
		query = query.replace(" ", "%20");
		query = query.replace("\"", "%22");
		URI uri = new java.net.URI(SabioQueryJersey.SABIORK_RESTFUL_URL + "/" + query);
		return uri;
	}
	
	
	/** Generate query from ids. */
	public static String queryStringFromIds(Collection<Integer> ids){
		
		if (ids.size() == 1){
			return (SabioQueryJersey.PREFIX_LAW + ids.iterator().next());	
		} else {
			String idText = null;
			for (Integer kid: ids){
				if (idText == null){
					idText = kid.toString();
				} else {
					idText += "," + kid.toString();
				}
			}
			return (SabioQueryJersey.PREFIX_LAWS + idText);
	    }
	}
	
	
}
