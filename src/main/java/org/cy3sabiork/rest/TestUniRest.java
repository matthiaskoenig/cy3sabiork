package org.cy3sabiork.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

public class TestUniRest {
	/** Create client and perform query. 
	 * @throws UnirestException */
	public static void newQuery(String query) {
		try {
			/**
			HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
					  .header("accept", "application/json")
					  .queryString("apiKey", "123")
					  .field("parameter", "value")
					  .field("foo", "bar")
					  .asJson();
			JsonNode json = jsonResponse.getBody();
			String output = json.toString();
			*/
			
			URI uri = SabioQuery.uriFromQuery(query);
			System.out.println(uri.toString());
			HttpResponse<String> response = Unirest.get(uri.toString())
										.asString();
					  				
			System.out.println(response.getStatus());
			String output = response.getBody();
					  
			
			
			System.out.println("--------------------------------------------");
			System.out.println(output);
			System.out.println("--------------------------------------------");
			Unirest.shutdown();
		} catch (UnirestException | URISyntaxException | IOException e){
			e.printStackTrace();
		}
	}
	

	/* Test the Restful API. */
	public static void main(String[] args){				
		newQuery("kineticLaws/123");
		// newQuery("searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"");
		// newQuery("searchKineticLaws/sbml?q=Tissue:spleen%20AND%20Organism:%22homo%20sapiens%22");
	}
}
