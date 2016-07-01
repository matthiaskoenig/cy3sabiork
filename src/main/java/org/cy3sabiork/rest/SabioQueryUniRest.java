package org.cy3sabiork.rest;

import java.net.URI;

import org.cy3sabiork.SabioQueryResult;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

public class SabioQueryUniRest extends SabioQuery{

	@Override
	public SabioQueryResult performQuery(String query){
		HttpResponse<String> response = executeQuery(query);
		if (response != null){
			Integer status = response.getStatus();
			String xml = null;
			if (status == 200){
				xml = response.getBody();
			}
			return new SabioQueryResult(query, status, xml);	
		}
		return null;
	}

	@Override
	public Integer performCountQuery(String query) {
		query = convertToCountQuery(query);
		
		HttpResponse<String> response = executeQuery(query);
		Integer count = -1;
		// success
		if (response != null && response.getStatus() == 200){
			String countString = response.getBody();
			try {
				count = Integer.parseInt(countString);
			} catch (NumberFormatException e){
				count = 0;
			}
		}
		return count;
	}

	@Override
	public String getSabioStatus() {
		String status = "Down";
		HttpResponse<String> response = executeQuery("status");
		if (response != null){
			status = response.getBody();
		}
		return status;
	}
	
	private HttpResponse<String> executeQuery(String query){
		try {
			URI uri = uriFromQuery(query);
			HttpResponse<String> response = Unirest.get(uri.toString())
												   .asString();
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
