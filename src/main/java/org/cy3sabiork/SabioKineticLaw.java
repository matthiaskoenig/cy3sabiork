package org.cy3sabiork;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;


@SuppressWarnings("restriction")
public class SabioKineticLaw {
	
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty organism;
    private final SimpleStringProperty tissue;
 
    public SabioKineticLaw(Integer id, String organism, String tissue) {
        this.id = new SimpleIntegerProperty(id);
        this.organism = new SimpleStringProperty(organism);
        this.tissue = new SimpleStringProperty(tissue);
    }
 
    public Integer getId(){
    	return id.get();
    }
       
}