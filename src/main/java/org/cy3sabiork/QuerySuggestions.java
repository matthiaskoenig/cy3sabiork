package org.cy3sabiork;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

import org.cy3sbml.SBMLManager;
import org.cy3sbml.mapping.SBML2NetworkMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import javax.ws.rs.core.Response;

/** Manages the available keyword suggestions. */
public class QuerySuggestions implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private HashSet<String> keywords;
	private HashMap<String, HashSet<String>> suggestions;
	
	public QuerySuggestions(){
		this.update();
	}
	
	/** Update the keyword suggestions by querying all keywords. */
	private void update(){
		keywords = getKeywords();
		
		suggestions = new HashMap<String, HashSet<String>>();
		
		for (String key: getSuggestionFields()){
			HashSet<String> values = getSuggestionsForField(key);
			suggestions.put(key, values);
		}
	}
	
	public void print(){
		System.out.println("-------------------------------------------------");
		for (String s: keywords){
			System.out.println(s);
		}
		System.out.println("-------------------------------------------------");
		
		for (String key: suggestions.keySet()){
			System.out.print("<" + key + "> : " + suggestions.get(key).size());
			System.out.println();
		}
	}
	
	
	private HashSet<String> getKeywords(){
		Response response = SabioQuery.executeQuery("searchKineticLaws");
		String xml = response.readEntity(String.class);
		return getFields(xml, "field");
	}
	
	private HashSet<String> getSuggestionFields(){
		Response response = SabioQuery.executeQuery("suggestions");
		String xml = response.readEntity(String.class);
		return getFields(xml, "field");
	}
	
	
	private HashSet<String> getFields(String xml, String tagName){
		HashSet<String> fields = new HashSet<String>();
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
	
	
	private HashSet<String> getSuggestionsForField(String field){	
		Response response = SabioQuery.executeQuery("suggestions/" + field);
		String xml = response.readEntity(String.class);
		
		String tagName = field.substring(0, (field.length()-1));
		HashSet<String> suggestions = getFields(xml, tagName);
		return suggestions;
	}
	
	
	private void saveToFile(String path){
		try {
			File file = new File(path);
	        FileOutputStream fileOut;
			
			fileOut = new FileOutputStream(file.getAbsolutePath());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(this);
	        out.close();
	        fileOut.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public static QuerySuggestions loadFromFile(String path){
			
		QuerySuggestions suggestions = null;
		File file = new File(path);
		
	    InputStream inputStream;
	    ObjectInput input;
		try {
			inputStream = new FileInputStream(file.getAbsolutePath());
			InputStream buffer = new BufferedInputStream(inputStream);
			input = new ObjectInputStream (buffer);
			
			suggestions = (QuerySuggestions)input.readObject();
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (ClassNotFoundException e3) {
			e3.printStackTrace();
		}
		return suggestions;
	}
	
	
	/** Create the latest suggestions and serialize to file. */
	public static void main(String[] args){
		QuerySuggestions suggestions = null; 

		String path = "/home/mkoenig/git/cy3sabiork/src/main/resources/suggestions.txt";
		
		suggestions = new QuerySuggestions();
		suggestions.saveToFile(path);
		
		suggestions = QuerySuggestions.loadFromFile(path);
		suggestions.print();
	}
	
}
