package org.cy3sabiork;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;

import javafx.beans.property.SimpleIntegerProperty;


@SuppressWarnings("restriction")
public class SabioKineticLaw {
	
	private final SimpleIntegerProperty count;
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty organism;
    private final SimpleStringProperty tissue;
    private final SimpleStringProperty reaction;
    
 
    public SabioKineticLaw(Integer count, Integer id, String organism, String tissue, String reaction) {
    	this.count = new SimpleIntegerProperty(count);
    	this.id = new SimpleIntegerProperty(id);
        this.organism = new SimpleStringProperty(organism);
        this.tissue = new SimpleStringProperty(tissue);
        this.reaction = new SimpleStringProperty(reaction);
    }
 
    public Integer getCount(){
    	return count.get();
    }
    public Integer getId(){
    	return id.get();
    }
    public String getOrganism(){
    	return organism.get();
    }
    public String getTissue(){
    	return tissue.get();
    }
    public String getReaction(){
    	return reaction.get();
    }
    
	/** Read the kineticLaws from the given SBML; 
	 * @throws XMLStreamException */
	public static ArrayList<SabioKineticLaw> parseKineticLaws(String sbml){
		ArrayList<SabioKineticLaw> list = new ArrayList<SabioKineticLaw>();
		if (sbml == null){
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
		Integer count = 1;
		for (Reaction r : model.getListOfReactions()){
			KineticLaw law = r.getKineticLaw();
			// FIXME: bad hack to get the id, necessary to read the XML
			String metaId = law.getMetaId();
			String[] tokens = metaId.split("_");
			Integer kid = Integer.parseInt(tokens[tokens.length-1]);
			
			
			// BQB_HAS_TAXON in RDF
			String organism = "-";
			String tissue = "-";
			
			String reaction = r.toString();
			
			r.toString();
			
			list.add(new SabioKineticLaw(count, kid, organism, tissue, reaction));
			
			count++;
		}
		return list;
	}
       
}