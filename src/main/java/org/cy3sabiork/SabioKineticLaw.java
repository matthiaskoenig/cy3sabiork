package org.cy3sabiork;

import java.util.ArrayList;
import java.util.HashSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;


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
    
    
    /* 
     * Parses the Kinetic Law Ids from given text string. 
     */
    public static HashSet<Integer> parseIds(String text){
    	HashSet<Integer> ids = new HashSet<Integer>();
		
    	// unify separators
    	text = text.replace("\n", ",");
    	text = text.replace("\t", ",");
    	text = text.replace(" ", ",");
    	text = text.replace(";", ",");
    	
    	String[] tokens = text.split(",");
    	for (String t : tokens){
        	// single entry parsing
    		if (t.length() == 0){
    			continue;
    		}
    		
        	try {
        		Integer kineticLaw = Integer.parseInt(t);
        		ids.add(kineticLaw);
        	} catch (NumberFormatException e) {
        		System.out.println("Kinetic Law Id could not be parsed from token: <" + t + ">");
        	}
    	}
    	return ids;
    }
    
	/** 
	 * Read SabioKineticLaws from given SBML. 
	 * 
	 * Information to populate the results panel is parsed here. 
	 */
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