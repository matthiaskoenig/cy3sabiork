package org.cy3sabiork.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;

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
	
	@FXML
	private ListView termsListView;
	
    @FXML private TextField queryTextField;
    @FXML private TextField termTextField;
    
    @FXML protected void handleQueryButtonAction(ActionEvent event) {
    	System.out.println("Perform query: " +  queryTextField.getText());
        queryTextField.setText("Query button pressed");
        termsListView.getItems();
    }
    
    @FXML protected void handleTermButtonAction(ActionEvent event) {
    	String selectedItem = (String) termsListView.getSelectionModel().getSelectedItem();
    	String term = termTextField.getText();
    	System.out.println("Add query term: " + selectedItem + ":" + term);
        queryTextField.setText("Query button pressed");
        termsListView.getItems();
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
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
		termsListView.setItems(items);
	}
	    
	
	/** Adds term to existing query. */
	private void addTerm(){
		
	}
	
}