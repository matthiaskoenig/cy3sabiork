package org.cy3sabiork.gui;

import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JEditorPaneSabio extends JEditorPane{
	private static final Logger logger = LoggerFactory.getLogger(JEditorPaneSabio.class);
	private static final long serialVersionUID = 1L;

	
	public JEditorPaneSabio(){
		super();
		logger.info("JEditorPaneSBML created");
		setEditable(false);
		setFont(new Font("Dialog", Font.PLAIN, 11));
		setContentType("text/html");
		setHelp();
	}
	
	public void setHelp(){
		setHTMLResource("/info.html");
	}

	/**
	 * Set given URL in the ResultsPanel.
	 */
	private void setHTMLResource(String resource){	
		try {
			// here static HTML is set 
			URL url = new URL(SabioPanel.class.getResource(resource).toString());
			// access to outer class methods
			JEditorPaneSabio.this.setPage(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
	}
	
   /**
    * Set URL content in textPane. 
	* To force a document reload it is necessary to clear the
    * stream description property of the document.
    */
	public void setPage(URL page){
		// Necessary to use invokeLater to handle the Swing GUI update
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				Document doc = JEditorPaneSabio.this.getDocument();
			    doc.putProperty(Document.StreamDescriptionProperty, null);
			    // call the super of outer class
				try {
					JEditorPaneSabio.super.setPage(page);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}    
		});
	}
	
   /**
    * set text
    */
	public void setText(String text){
		// Necessary to use invokeLater to handle the Swing GUI update
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				JEditorPaneSabio.super.setText(text);
			} 
		});
	}
}
