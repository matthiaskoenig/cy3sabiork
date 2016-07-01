package org.cy3sabiork.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import org.apache.commons.io.IOUtils;
import org.cy3sabiork.SabioQueryResult;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.logging.LoggingFeature;


/**
 * Performing SabioRK queries.
 * 
 * This is the web service client class performing the queries.
 * see https://jersey.java.net/documentation/latest/client.html
 * 
 * A resource in the JAX-RS client API is an instance of the Java class WebTarget
 * and encapsulates an URI. The fixed set of HTTP methods can be invoked based on the WebTarget.
 * 
 * FIXME: reuse the created client instead of static access
 * 		 (client connection building and init creates additional overhead)
 */

public class SabioQuery {
	// private static final Logger logger = LoggerFactory.getLogger(SabioQuery.class);
	public static final String SABIORK_RESTFUL_URL = "http://sabiork.h-its.org/sabioRestWebServices";
	
	public static final String PREFIX_KINETIC_LAW_INFO = "http://sabiork.h-its.org/kineticLawEntry.jsp?viewData=true&kinlawid=";
	public static final String PREFIX_QUERY = "searchKineticLaws/sbml?q=";
	public static final String PREFIX_COUNT = "searchKineticLaws/count?q=";
	public static final String PREFIX_LAW = "kineticLaws/";
	public static final String PREFIX_LAWS = "kineticLaws?kinlawids=";
	public static final String CONNECTOR_AND = " AND ";

	/**
	 * Main function for running the queries.
	 * 
	 * This handles the required query cleaning/escaping and returns the
	 * response of the query.
	 */
	public static String executeQuery(String query){
		try {
			// Create URI after required replacements (as long as SABIO-RK has no proper encoding)
			query = query.replace(" ", "%20");
			query = query.replace("\"", "%22");
			URI uri = new java.net.URI(SabioQuery.SABIORK_RESTFUL_URL + "/" + query);
			
			// create client
			ClientConfig clientConfig = new ClientConfig();
			//clientConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, false);
			clientConfig.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);

			// logging
			Logger logger = Logger.getLogger(SabioQuery.class.getName());			
			ConsoleHandler handler = new ConsoleHandler();
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			
			
			Feature loggingFeature = new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, null);
			Client client = ClientBuilder.newClient(clientConfig)
										 //.register(loggingFeature)
										 .register(StringMessageBodyReader.class);
			
			Invocation.Builder builder = client
					 .target(uri)
					 .request("text/xml;charset=UTF-8")
					 //.accept("application/xml;charset=UTF-8")
					 .header("Content-Type","text/xml;charset=UTF-8");
					 
			// Response response = builder.get();
			String response = builder.get(String.class);
			
			return response;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Parses the path and the parameters from the query string.
	 * 
	 * Query strings are of the form:
	 * 		kineticLaws/123
	 * 		searchKineticLaws/sbml?q=Tissue:spleen AND Organism:\"Homo sapiens\"
	 * 		searchKineticLaws/sbml?q=Tissue:spleen%20AND%20Organism:%22homo%20sapiens%22
	 */
	public static SabioQueryResult performQuery(String query){
		//logger.info("Perform Sabio-RK query");
		
		
		String response = executeQuery(query);
		return new SabioQueryResult(query, 200, response);
		/*
		Response response = executeQuery(query);
		if (response != null){
			Integer status = response.getStatus();
			String xml = null;
			if (status != 200){
				// here String is retrieved from response
				// xml = SabioQuery.readEntityInString(response);
				xml = response.readEntity(String.class);
			}
			return new SabioQueryResult(query, status, xml);	
		}
		return null;
		*/
	}
	
	
	/** 
	 * Read the response entity in string.
	 * Necessary to take care of the encoding, otherwise issues on win7. 
	 * 
	 *  This must probably be handled via an interceptor:
	 *  Interceptors are used primarily for modification of entity input and output streams.
	 *  see https://jersey.java.net/documentation/latest/filters-and-interceptors.html
	 *  
	 *  - InputStream delivers bytes
     *  - Readers deliver chars in some encoding
     *  - new InputStreamReader(inputStream) uses the operating system encoding
     *  - new InputStreamReader(inputStream, "UTF-8") uses the given encoding (here UTF-8)
	 */
	private static String readEntityInString(Response response){
		
		return response.readEntity(String.class);
		
		/*
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntityStream(), StandardCharsets.UTF_8));
		String content = bufferedReader.lines().collect(Collectors.joining(""));
		return content;
		*/
		
		// Somehow there are non UTF-8 characters in the response
		/*
		String content = response.readEntity(String.class);
		CharsetDecoder utf8Decoder = Charset.forName("UTF-8").newDecoder();
		utf8Decoder.onMalformedInput(CodingErrorAction.IGNORE);
		utf8Decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
		ByteBuffer bytes = Charset.forName("UTF-8").encode(content);
		try {
			CharBuffer parsed = utf8Decoder.decode(bytes);
			content = parsed.toString();
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
		*/
		
		/*
		String content = null;
		InputStream inputStream = (InputStream) response.getEntity();

		// String content = getStringFromInputStream(inputStream);
		try {
			content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		*/
		
		// Somehow the above seems still to be in win encoding
		// ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(content);
		
		// Force UTF-8
		// byte[] ptext = content.getBytes(StandardCharsets.UTF_8);
		// content = new String(ptext, StandardCharsets.UTF_8); 
		
		
	}
		
	/** 
	 * Check status of the SABIO-RK webservice. 
	 */
	public static String getSabioStatus(){
		String status = "Down";
		
		status = executeQuery("status");
		/*
		Response response = executeQuery("status");
		if (response != null){
			status = response.readEntity(String.class);
		}
		*/
		return status;
	}
	
	/**
	 * Perform query for count of kineticLaws.
	 */
	public static Integer performCountQuery(String query){
		
	    // convert sbml to count query if required
		if (query.startsWith(SabioQuery.PREFIX_QUERY)){
			query = query.replace(SabioQuery.PREFIX_QUERY, SabioQuery.PREFIX_COUNT);
		}
		
		String response = executeQuery(query);
		Integer count = -1;
		// success
		
		try {
			count = Integer.parseInt(response);
		} catch (NumberFormatException e){
			count = 0;
		}

		return count;
		
		/*
		Response response = executeQuery(query);
		Integer count = -1;
		// success
		if (response != null && response.getStatus() == 200){
			String countString = response.readEntity(String.class);
			try {
				count = Integer.parseInt(countString);
			} catch (NumberFormatException e){
				count = 0;
			}
		}
		return count;
		*/
	}
	
	/** Generate query from ids. */
	public static String queryStringFromIds(Collection<Integer> ids){
		
		if (ids.size() == 1){
			return (SabioQuery.PREFIX_LAW + ids.iterator().next());	
		} else {
			String idText = null;
			for (Integer kid: ids){
				if (idText == null){
					idText = kid.toString();
				} else {
					idText += "," + kid.toString();
				}
			}
			return (SabioQuery.PREFIX_LAWS + idText);
	    }
	}
	
}
