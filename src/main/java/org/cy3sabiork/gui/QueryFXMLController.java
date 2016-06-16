package org.cy3sabiork.gui;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import org.cy3sabiork.SabioQuery;
import org.cy3sabiork.SabioQueryResult;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

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
    @FXML private Text statusCodeLabel;
    @FXML private Text time;
    @FXML private Text timeLabel;
    
    private HashSet<Integer> kineticLaws;
    
    
    
    @FXML protected void handleAddKeywordAction(ActionEvent event) {
    	// TODO: only use the allowed keywords
    	
    	
    	System.out.println("<handleAddKeywordAction>");
    	
    	String selectedItem = (String) keywordList.getSelectionModel().getSelectedItem();
    	String searchTerm = term.getText();
    	
    	if (selectedItem == null){
    		System.out.println("No keyword selected, not added !");
    		return;
    	}
    	
    	String addition = selectedItem + ":\"" + searchTerm + "\"";
    	String query = queryText.getText();
    	if (searchTerm.length() == 0){
    		System.out.println("Empty term defined, not added !");
    		return;
    	}
    	if (query.contains(addition)){
    		System.out.println("keyword:term already in query !");
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
    	Integer kineticLaw = null;
    	try {
    		kineticLaw = Integer.parseInt(entry.getText());
    		queryText.setText(PREFIX_LAW + kineticLaw.toString());
    	} catch (NumberFormatException e) {
    		
    	}
    }
    
    @FXML protected void handleQueryAction(ActionEvent event) {
    	System.out.println("<handleQueryKeywordAction>");
    	showQueryStatus(true);
    	
 
		String queryString = queryText.getText();
		System.out.println("Perform query: GET "+ queryString);
		System.out.println("working ...");
		progressIndicator.setProgress(-1);
		
		long startTime = System.currentTimeMillis();
		
		SabioQuery query = new SabioQuery();
		SabioQueryResult queryResult = query.performQuery(queryString);
			
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		
		// TODO: read the SBML
		// sbmlReader.loadNetworkFromSBML(xml);
		statusCode.setText(queryResult.getStatus().toString());
		time.setText(duration + " [ms]");
		progressIndicator.setProgress(1);
    }
    
    @FXML protected void handleClearAction(ActionEvent event) {
    	System.out.println("<handleClearKeywordAction>");
    	queryText.setText("");
    	statusCode.setText("?");
    }
    
    /** Focus the given scene Node. */
    private void focusNode(Node node){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                term.requestFocus();
            }
        });
    }
    
    private void showQueryStatus(Boolean show){
    	progressIndicator.setVisible(show);
    	statusCode.setVisible(show);
    	statusCodeLabel.setVisible(show);
    	time.setVisible(show);
    	timeLabel.setVisible(show);
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		progressIndicator.setProgress(1.0);
		
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
                        
                        // focus term field
                        focusNode(term);
            }
        });
		
		// hide elements on first loading
		showQueryStatus(false);
		
		
		// -- KeyEvents --
		keyword.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER){
					System.out.println("KeyCode == ENTER on keyword");
					focusNode(term);
				}
            }
        });
		
		// -- KeyEvents --
		term.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER){
					System.out.println("KeyCode == ENTER on term");
					addKeywordButton.fire();
					
				}
            }
        });
		
		// Query the status
		progressIndicator.setProgress(-1);
		String status = SabioQuery.getSabioStatus();
		if (status.equals("UP")){
			progressIndicator.setProgress(1.0);
		}
		
	}
	
}