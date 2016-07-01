package org.cy3sabiork.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import java.net.URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.cy3sabiork.ResourceExtractor;
import org.cy3sabiork.SabioQueryResult;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


/** 
 * Manage SABIO-RK query suggestions. 
 *
 * For given query keywords the available suggestions are retrieved
 * from SABIO-RK. The suggestions are used in the auto-completion 
 * of search queries. 
 * 
 * QuerySuggestions are precalculated and shipped with the application.
 * Updates are required with new SABIO-RK releases.
 */
public class QuerySuggestions implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String RESOURCE = "/gui/suggestions.ser";
	private static final Map<String, String> KEYWORD_MAP;
	// private static final Set<String> ENZYMETYPE_SUGGESTIONS;
	
	public static enum Mode {
		SAVE, LOAD
	}
	
	static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("AnyRole", "Compound");
        map.put("Substrate", "Compound");
        map.put("Product", "Compound");
        map.put("Inhibitor", "Compound");
        map.put("Catalyst", "Compound");
        map.put("Cofactor", "Compound");
        map.put("Activator", "Compound");
        map.put("OtherModifier", "Compound");
        map.put("AnyRole", "Compound");
        map.put("Enzymename", "Enzyme");
        map.put("PubMedID", "PubmedID");
        map.put("KeggCompoundID", "KEGGCompoundID");
        map.put("KeggReactionID", "KEGGReactionID");
        map.put("SabioCompoundID", "SABIOCompoundID");
        map.put("SabioReactionID", "SABIOReactionID");
        map.put("ChebiID", "CHEBICompoundID");
        map.put("PubChemID", "PUBCHEMCompoundID");
        KEYWORD_MAP = Collections.unmodifiableMap(map);
       
        /*
        Set<String> set = new HashSet<String>();
        set.add("wildtype");
        set.add("mutant");
        ENZYMETYPE_SUGGESTIONS = Collections.unmodifiableSet(set);
        */
    }
	
	private TreeSet<String> keywords;
	private HashMap<String, TreeSet<String>> suggestions;
	
	/* 
	 * Creates new QuerySuggestions by retrieving from web service. 
	 * Only a single instance should be calculated and stored.
	 * Subsequent instances should be loaded from the serialized
	 * version.  
	 */
	public QuerySuggestions(){
		this.retrieve();
	}
	
	/** Get keywords for which suggestions exist. */
	public TreeSet<String> getKeywords(){
		return keywords;
	}
	
	/** Suggestions. */
	public TreeSet<String> getSuggestionsForKeyword(String key){
		return suggestions.get(key);				
	}
	
	/** Load. */
	public static QuerySuggestions loadFromResource(String fileURI){	
		QuerySuggestions suggestions = null;
	
		try {
			InputStream inputStream = new URL(fileURI).openStream();
			InputStream buffer = new BufferedInputStream(inputStream);
			ObjectInput input = new ObjectInputStream(buffer);
			suggestions = (QuerySuggestions) input.readObject();
				
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (ClassNotFoundException e3) {
			e3.printStackTrace();
		}
		return suggestions;
	}
	
	/** Save. */
	private void saveToFile(String path){
		try {
	        File file = new File(path);
	        FileOutputStream fileOut = new FileOutputStream(file.getAbsolutePath());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(this);
	        out.close();
	        fileOut.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/** Print suggestion information to console. */
	public void print(){
		System.out.println("-------------------------------------------------");
		for (String s: keywords){
			System.out.println(s);
		}
		System.out.println("-------------------------------------------------");
		
		for (String key: new TreeSet<String>(suggestions.keySet())){
			System.out.print(key + " : " + suggestions.get(key).size());
			System.out.println();
		}
	}
	
	/** Retrieve keyword suggestions by querying all keywords. */
	private void retrieve(){
		keywords = retrieveKeywords();
		suggestions = new HashMap<String, TreeSet<String>>();
		
		for (String key: retrieveSuggestionFields()){
			
			TreeSet<String> values = retrieveSuggestionsForField(key);
			String tagName = key.substring(0, (key.length()-1));
			
			// store suggestions under original id
			suggestions.put(tagName, values);
			// use the mapping to distribute suggestions to all 
			// relevant keywords
			for (String keyword: KEYWORD_MAP.keySet()){
				String skey = KEYWORD_MAP.get(keyword);
				if (skey.equals(tagName)){
					suggestions.put(keyword, values);
				}
			}
		}
	}
	
	private TreeSet<String> retrieveKeywords(){
		SabioQueryResult result = new SabioQueryJersey().performQuery("searchKineticLaws");
		return parseXMLFields(result.getXML(), "field");
	}
	
	private TreeSet<String> retrieveSuggestionFields(){
		SabioQueryResult result = new SabioQueryJersey().performQuery("suggestions");
		return parseXMLFields(result.getXML(), "field");
	}
	
	private TreeSet<String> retrieveSuggestionsForField(String field){	
		SabioQueryResult result = new SabioQueryJersey().performQuery("suggestions/" + field);
		String tagName = field.substring(0, (field.length()-1));
		return parseXMLFields(result.getXML(), tagName);
	}
	
	/** Parse entries for given tag name. */
	private TreeSet<String> parseXMLFields(String xml, String tagName){
		TreeSet<String> fields = new TreeSet<String>();
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			// get the fields
			NodeList nList = doc.getElementsByTagName(tagName);
			for (int k=0; k<nList.getLength(); k++){
				Node n = nList.item(k);
				String keyword = n.getTextContent();
				fields.add(keyword);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return fields;
	}
		
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Retrieve suggestions and serialize to file resource.
	 */
	public static void main(String[] args){
    	File appDirectory = new File("src/main/resources");
    	ResourceExtractor.setAppDirectory(appDirectory);
    	
    	String fileURI = ResourceExtractor.fileURIforResource(RESOURCE);
    	System.out.println(fileURI);
		
		// Change mode for loading or saving
		//Mode mode = Mode.SAVE;
		Mode mode = Mode.LOAD;
		
		System.out.println("-------------------------------------------------");
		System.out.println(mode);
		System.out.println("-------------------------------------------------");
		QuerySuggestions suggestions = null; 
		if (mode == Mode.SAVE){
			// get current values and store in RESOURCE
			suggestions = new QuerySuggestions();
			suggestions.saveToFile("/home/mkoenig/git/cy3sabiork/src/main/resources" + RESOURCE);			
		} else if (mode == Mode.LOAD){
			suggestions = QuerySuggestions.loadFromResource(fileURI);	
		}
		suggestions.print();
	}
	
}
