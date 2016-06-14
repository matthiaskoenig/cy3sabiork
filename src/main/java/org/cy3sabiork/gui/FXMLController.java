package org.cy3sabiork.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
 
@SuppressWarnings("restriction")
public class FXMLController {
    @FXML 
    private Text actiontarget;
    
    @FXML 
    protected void handleQueryButtonAction(ActionEvent event) {
        actiontarget.setText("Query button pressed");
    }
}