package org.cy3sabiork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.core.Response;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
	
	/** Read the kineticLaws from the given SBML; 
	 * @throws XMLStreamException */
	public ArrayList<SabioKineticLaw> getKineticLaws(){
		ArrayList<SabioKineticLaw> list = new ArrayList<SabioKineticLaw>();
		if (this.sbml == null){
			return list;
		}
		
		// parse the entries from the SBML
		SBMLDocument doc;
		try {
			doc = JSBML.readSBMLFromString(sbml);
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return list;
		}
		
		// necessary to read information from annotations
		Model model = doc.getModel();
		for (Reaction r : model.getListOfReactions()){
			KineticLaw law = r.getKineticLaw();
			// FIXME: bad hack to get the id, necessary to read the XML
			String metaId = law.getMetaId();
			String[] tokens = metaId.split("_");
			Integer kid = Integer.parseInt(tokens[tokens.length-1]);
			list.add(new SabioKineticLaw(kid, "not parsed", "not parsed"));
		}
		return list;
	}
	
	
}
