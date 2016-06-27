package org.cy3sabiork;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

import javax.ws.rs.core.Response;

/** Manages the available keyword suggestions. */
public class QuerySuggestions {
	private HashMap<String, HashSet<String>> items;
	
	public QuerySuggestions(){
		this.update();
	}
	
	/** Update the keyword suggestions by querying all keywords. */
	private void update(){
		items = new HashMap<String, HashSet<String>>();
		HashSet<String> keywords = getKeywords();
		for (String key: keywords){
			HashSet<String> suggestions = getSuggestionsForKeyword(key);
			items.put(key, suggestions);
		}
	}
	
	public void print(){
		for (String key: items.keySet()){
			System.out.print("<" + key + "> : ");
			HashSet<String> suggestions = items.get(key);
			for (String s: suggestions){
				System.out.print(s + ", ");
			}
			System.out.println();
		}
		
	}
	
	private HashSet<String> getKeywords(){
		HashSet<String> keywords = new HashSet<String>();

		Response response = SabioQuery.executeQuery("searchKineticLaws");
		String xml = response.readEntity(String.class);
		InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(stream);
			// get the fields
			NodeList nList = doc.getElementsByTagName("field");
			for (int k=0; k<nList.getLength(); k++){
				Node n = nList.item(k);
				String keyword = n.getTextContent();
				keywords.add(keyword);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return keywords;
	}
	
	private HashSet<String> getSuggestionsForKeyword(String keyword){
		HashSet<String> suggestions = new HashSet<String>();
		// TODO: implement
		
		return suggestions;
	}
	
	
	private void saveToFile(){
		// TODO: implement		
	}
	
	public static QuerySuggestions loadFromFile(){
		// TODO: implement		
		QuerySuggestions suggestions = null;
		
		return suggestions;
	}
	
	
	public static void main(String[] args){
		QuerySuggestions suggestions = new QuerySuggestions();
		suggestions.print();
		
	}
	
}
