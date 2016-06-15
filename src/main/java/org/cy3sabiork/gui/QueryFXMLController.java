package org.cy3sabiork.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;

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
	public static final String PREFIX_QUERY = "searchKineticLaws/sbml?q=";
	public static final String PREFIX_LAW = "kineticLaws/";
	public static final String PREFIX_LAWS = "kineticLaws?kinlawids=";
	public static final String CONNECTOR_AND = " AND ";
	
	
	// --- Query Builder ---
	
    @FXML private TextField keyword;
    @FXML private ListView keywordList;
    @FXML private TextField term;
    @FXML private Text termDescription;
    @FXML private Button addKeywordButton;
    
    // --- Kinetic Law entries ---
    @FXML private TextField entry;
    @FXML private Button addEntryButton;
    
    // -- REST response --
    @FXML private TextArea queryText;
    @FXML private Button queryButton;
    @FXML private Button clearButton;
    
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Text statusCode;
    
    
    
    @FXML protected void handleAddKeywordAction(ActionEvent event) {
    	System.out.println("<handleAddKeywordAction>");
    	
    	String selectedItem = (String) keywordList.getSelectionModel().getSelectedItem();
    	String searchTerm = term.getText();
    	System.out.println("Add query term: " + selectedItem + ":" + searchTerm);
    	
    	
    	String addition = selectedItem + "\"" + searchTerm + "\"";
    	String query = queryText.getText();
    	if (searchTerm.length() == 0){
    		System.out.println("No term defined, not added");
    		return;
    	}
    	if (query.startsWith(PREFIX_QUERY)){
    		queryText.setText(query + CONNECTOR_AND + addition);
    	} else {
    		queryText.setText(PREFIX_QUERY + addition);
    	}
    }
    
    @FXML protected void handleAddEntryAction(ActionEvent event) {
    	System.out.println("<handleAddEntryAction>");
    }
    
    @FXML protected void handleQueryAction(ActionEvent event) {
    	System.out.println("<handleQueryKeywordAction>");
    	statusCode.setText("404");
    }
    
    @FXML protected void handleClearAction(ActionEvent event) {
    	System.out.println("<handleClearKeywordAction>");
    	queryText.setText("");
    	statusCode.setText("?");
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
		
		keywordList.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, 
                    String oldValue, String newValue) {
                		// set keyword in field
                        keyword.setText(newValue);
                        // request the term
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                term.requestFocus();
                            }
                        });
            }
        });
		
		
	}
	
}