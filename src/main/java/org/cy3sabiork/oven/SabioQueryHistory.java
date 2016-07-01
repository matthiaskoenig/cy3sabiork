package org.cy3sabiork.oven;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * History of the last SABIO-RK queries.
 */
@SuppressWarnings("restriction")
public class SabioQueryHistory {
	ObservableList<String> items;
	
	public SabioQueryHistory(){
		items = FXCollections.observableArrayList(
				"kineticLaws/14792",
				"searchKineticLaws/sbml?q=Organism:%22Homo%20sapiens%22%20AND%20Pathway:%22galactose%20metabolism%22"
		);
	}
	
	/** Add query string at beginning of history list. */
	public void add(String element){
		items.add(0, element);
	}
	
	public void get(){
		items.get(0);
	}
	
}
