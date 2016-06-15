package org.cy3sabiork.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
 

// TODO: add SABIO-RK logo
// 
// TODO: add terms to query
// TODO: Feedback about query status code, i.e. 404, ...
// TODO: execute query
// TODO: add example queries



@SuppressWarnings("restriction")
public class QueryFXMLController implements Initializable{
	
	// --- Query Builder ---
	
    @FXML private TextField keyword;
    @FXML private ListView keywordList;
    @FXML private TextField term;
    @FXML private Text termDescription;
    
    @FXML private TextField queryKeywordText;
    
    @FXML private Button addKeyword;
    @FXML private Button queryKeyword;
    @FXML private Button clearKeyword;
    
   
    // --- Kinetic Law entries ---
    @FXML private TextField entry;
    @FXML private ListView entryList;
    
    @FXML private TextField queryEntryText;
    
    @FXML private Button addEntry;
    @FXML private Button queryEntry;
    @FXML private Button clearEntry;
    
    // --- REST response ---
    
    
    
    @FXML protected void handleAddKeywordAction(ActionEvent event) {
    	System.out.println("<handleAddKeywordAction>");
    	String selectedItem = (String) keywordList.getSelectionModel().getSelectedItem();
    	System.out.println("Add query term: " + selectedItem + ":" + term.getText());
        queryKeywordText.setText("Query button pressed");
    }
    
    @FXML protected void handleQueryKeywordAction(ActionEvent event) {
    	System.out.println("<handleQueryKeywordAction>");
    }
    
    @FXML protected void handleClearKeywordAction(ActionEvent event) {
    	System.out.println("<handleClearKeywordAction>");
    }
    
    @FXML protected void handleAddEntryAction(ActionEvent event) {
    	System.out.println("<handleAddEntryAction>");
    }
    
    @FXML protected void handleQueryEntryAction(ActionEvent event) {
    	System.out.println("<handleQueryEntryAction>");
    }
    
    @FXML protected void handleClearEntryAction(ActionEvent event) {
    	System.out.println("<handleClearEntryAction>");
    }
    

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// ListView<String> termsListView = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList (
		    "EntryID", "Pathway", 
		    "Tissue", "Organism", "CellularLocation",
		    "AnyRole", "Substrate", "Product", "Inhibitor", "Catalyst", "Cofactor", "Activator", "OtherModifier",
		    "Enzymename", "ECNumber", 
		    "Parametertype", "KineticMechanismType", "AssociatedSpecies",
		    "SabioReactionID", "SabioCompoundID", "InChI", "KeggReactionID", "PubChemID",
		    "KeggCompoundID", "ChebiID", "UniProtKB_AC", "GOTerm", "SBOTerm",
		    "Title", "Author", "Year", "Organization", "PubMedID", "DataIdentifier",
		    "SignallingEvent", "SignallingModification");
		keywordList.setItems(items);
	}
	    
	
	/** Adds term to existing query. */
	private void addTerm(){
		
	}
	
}