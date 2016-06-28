package org.cy3sabiork;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

import org.cy3sabiork.gui.WebViewSwing;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

/** Manages the available keyword suggestions. */
public class QuerySuggestions implements Serializable {
	public static final String RESOURCE = "/gui/suggestions.ser";
	
	
	private static final long serialVersionUID = 1L;
	private TreeSet<String> keywords;
	private HashMap<String, TreeSet<String>> suggestions;
	
	public QuerySuggestions(){
		this.update();
	}
	
	public TreeSet<String> getKeywords(){
		return keywords;
	}
	
	public TreeSet<String> getSuggestionsForKeyword(String key){
		if (suggestions.containsKey(key)){
			return suggestions.get(key);	
		} else {
			return (new TreeSet<String>());
		}
				
	}
	
	
	/** Update the keyword suggestions by querying all keywords. */
	private void update(){
		keywords = retrieveKeywords();
		
		suggestions = new HashMap<String, TreeSet<String>>();
		
		for (String key: retrieveSuggestionFields()){
			
			TreeSet<String> values = retrieveSuggestionsForField(key);
			String tagName = key.substring(0, (key.length()-1));
			suggestions.put(tagName, values);
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
	
	
	private TreeSet<String> retrieveKeywords(){
		Response response = SabioQuery.executeQuery("searchKineticLaws");
		String xml = response.readEntity(String.class);
		return retrieveFields(xml, "field");
	}
	
	private TreeSet<String> retrieveSuggestionFields(){
		Response response = SabioQuery.executeQuery("suggestions");
		String xml = response.readEntity(String.class);
		return retrieveFields(xml, "field");
	}
	
	
	private TreeSet<String> retrieveFields(String xml, String tagName){
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
	
	private TreeSet<String> retrieveSuggestionsForField(String field){	
		Response response = SabioQuery.executeQuery("suggestions/" + field);
		String xml = response.readEntity(String.class);
		String tagName = field.substring(0, (field.length()-1));
		return retrieveFields(xml, tagName);
	}
	
	
	private void saveToFile(String resource){
		try {
			URL url = WebViewSwing.class.getResource(resource);
			
	        FileOutputStream fileOut;
	        File file = new File(url.toURI());
			fileOut = new FileOutputStream(file.getAbsolutePath());
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(this);
	        out.close();
	        fileOut.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static QuerySuggestions loadFromResource(String resource){
		
		QuerySuggestions suggestions = null;
		// This does not work in the context
		URL url = QuerySuggestions.class.getResource(resource);
		
		File file;
	    InputStream inputStream;
	    ObjectInput input;
		try {
			file = new File(url.toURI());
			inputStream = new FileInputStream(file.getAbsolutePath());
			InputStream buffer = new BufferedInputStream(inputStream);
			input = new ObjectInputStream (buffer);
			
			suggestions = (QuerySuggestions)input.readObject();
		} catch (URISyntaxException e) {
			
				e.printStackTrace();
				
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
		
		if (false){
			// get the current values and store in RESOURCE
			suggestions = new QuerySuggestions();
			suggestions.saveToFile(RESOURCE);			
		}

		suggestions = QuerySuggestions.loadFromResource(RESOURCE);
		suggestions.print();
	}
	
}
