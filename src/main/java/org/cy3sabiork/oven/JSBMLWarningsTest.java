package org.cy3sabiork.oven;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.stream.XMLStreamException;

import org.cy3sbml.SBMLReaderTask;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test bed for JSBML.
 */


public class JSBMLWarningsTest {

	private static final Logger logger = LoggerFactory.getLogger(JSBMLWarningsTest.class);
	private static final int BUFFER_SIZE = 16384;
	
	static String readFile(String path, Charset encoding) 
			  throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
	public static InputStream getInputStream(String name) throws IOException {
		try { 
			return new FileInputStream(name);
		} catch (FileNotFoundException fnf) {
			return null;
		}
	}
	
	/**
	 * Read String from InputStream.
	 */
	public static String readString(InputStream source) throws IOException {
		StringWriter writer = new StringWriter();
		BufferedReader reader = new BufferedReader(new InputStreamReader(source));
		try {
			char[] buffer = new char[BUFFER_SIZE];
			int charactersRead = reader.read(buffer, 0, buffer.length);
			while (charactersRead != -1) {
				writer.write(buffer, 0, charactersRead);
				charactersRead = reader.read(buffer, 0, buffer.length);
			}
		} finally {
			reader.close();
		}
		return writer.toString();
	}
	
	private void sboTest() throws XMLStreamException, IOException{
		
		String path = "/home/mkoenig/git/cy3sabiork/target/classes/test/BIOMD0000000183.xml";
		SBMLDocument doc = JSBML.readSBMLFromFile(path);
		Model model = doc.getModel();
		Compartment c = model.getCompartment("compartment_0");
		System.out.println(c);
		Integer sbo = c.getSBOTerm();
		System.out.println("SBO:" + c.isSetSBOTerm() + " > " + sbo + " " );
		c.getSBOTermID();
		
	}
	
	public static void main(String[] args) throws XMLStreamException, IOException{

		// String xml = readString(stream);
		
		// TODO: store and display JSBML reader warnings
		// SBMLDocument document = JSBML.readSBMLFromString(xml);
		
		// get rid of errors when using JSBML without log4j.properties
		// org.apache.log4j.BasicConfigurator.configure();
		
		// OSGI resource access, replace with path to file for testing
		// URL fileURL = JSBMLWarningsTest.class.getResource("/test/jsbml_sbo_example.xml");
		// System.out.println(fileURL.toString());
		
		//String path = "/home/mkoenig/git/cy3sabiork/target/classes/test/jsbml_sbo_example.xml";
		
		String path = "/home/mkoenig/git/cy3sabiork/target/classes/test/BIOMD0000000162.xml";
		
		// from file okay
		System.out.println("* read from file");
		SBMLDocument doc = JSBML.readSBMLFromFile(path);
		
		
		
		
		//Model model = doc.getModel();
		
		/*
		// form string okay
		System.out.println("* read from string");
		String sbmlString = readFile(path, StandardCharsets.UTF_8);
		doc = JSBML.readSBMLFromString(sbmlString);
		model = doc.getModel();
		// read file to string
		
		// from inputstream
		System.out.println("* read from stream");
		InputStream stream = getInputStream(path);
		String xml = readString(stream);
		doc = JSBML.readSBMLFromString(xml);
		model = doc.getModel();
		logger.info("Testing logger");
		*/
		
	}
	
}
