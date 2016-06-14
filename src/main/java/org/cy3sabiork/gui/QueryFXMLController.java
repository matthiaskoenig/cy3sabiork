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



@SuppressWarnings("restriction")
public class QueryFXMLController implements Initializable{
	
	@FXML
	private ListView termsListView;
	
    @FXML 
    private TextField queryTextField;
    
    @FXML 
    protected void handleQueryButtonAction(ActionEvent event) {
    	System.out.println("Perform query: " +  queryTextField.getText());
        queryTextField.setText("Query button pressed");
        termsListView.getItems();
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		// ListView<String> termsListView = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList (
		    "Single", "Double", "Suite", "Family App",
		    "Single", "Double", "Suite", "Family App",
		    "Single", "Double", "Suite", "Family App");
		termsListView.setItems(items);
	}
}