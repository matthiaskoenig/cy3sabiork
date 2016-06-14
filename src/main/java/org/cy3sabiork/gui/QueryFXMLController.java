package org.cy3sabiork.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
 

// TODO: add SABIO-RK logo
// 
// TODO: add terms to query
// TODO: Feedback about query status code, i.e. 404, ...
// TODO: execute query



@SuppressWarnings("restriction")
public class QueryFXMLController {
	

    @FXML 
    private TextField queryTextField;
    
    @FXML 
    protected void handleQueryButtonAction(ActionEvent event) {
    	System.out.println("Perform query: " +  queryTextField.getText());
        queryTextField.setText("Query button pressed");
    }
}