package org.cy3sabiork.oven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.JSBML;


// uses unirest for reading
// maven dependencies listed here : http://unirest.io/java.html
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class JSBMLBugUTF8 {
	
	public static void testReading(){
		
		try {
			// going via the inputstream of the response to avoid all native charset encodings,
			HttpResponse<InputStream> ioResponse = Unirest.get("http://sabiork.h-its.org/sabioRestWebServices/kineticLaws/123")
														  .asBinary();
			InputStream inputStream = ioResponse.getRawBody();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String sbml = bufferedReader.lines().collect(Collectors.joining("\n"));
			
			// everything is UTF-8 here, now be sure to also write the file in UTF-8
			File tmpFile = File.createTempFile("test", ".xml");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8"));
	    	try {
	    	    out.write(sbml);
	    	} finally {
	    	    out.close();
	    	}
    	    
			// Read SBML 
	    	System.out.println("> read SBML from file");
	    	JSBML.readSBMLFromFile(tmpFile.getAbsolutePath());
	    	
	    	// read the sbml UTF-8 string directly will fail on win7
	    	// at some point things are converted to native ISO-
	    	// see http://www.i18nqa.com/debug/bug-utf-8-latin1.html
	    	// the degree ° sign will brake the reader
	    	// -> always use UTF-8 charsets and avoid any conversion using native encoding
	    	// no problem on linux due to native UTF-8 encoding
	    	System.out.println("> read SBML from string");
    	    JSBML.readSBMLFromString(sbml);
			
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnirestException{
		testReading();

	}
		
}
