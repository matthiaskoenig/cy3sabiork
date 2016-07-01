package org.cy3sabiork.rest;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class StringClientResponseFilter implements ClientResponseFilter{

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
