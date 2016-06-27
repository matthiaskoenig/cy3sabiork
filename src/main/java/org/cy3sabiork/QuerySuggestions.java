package org.cy3sabiork;

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
		}
	}
	
	public void print(){
		for (String key: items.keySet()){
			System.out.println("<" + key + ">: ");
		}
		
	}
	
	private HashSet<String> getKeywords(){
		HashSet<String> keywords = new HashSet<String>();
		// TODO: implement
		Response response = SabioQuery.performQuery("searchKineticLaws");
		
		
		
		
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
