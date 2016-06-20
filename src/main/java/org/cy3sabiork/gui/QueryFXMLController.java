package org.cy3sabiork.gui;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;


import javafx.concurrent.Worker.State;

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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;

import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.cytoscape.util.swing.OpenBrowser;

import org.cy3sabiork.ResourceExtractor;
import org.cy3sabiork.SabioKineticLaw;
import org.cy3sabiork.SabioQuery;
import org.cy3sabiork.SabioQueryResult;
import org.cy3sabiork.SabioSBMLReader;

import netscape.javascript.JSObject;
// import com.sun.webkit.dom.JSObject;


// TODO: get terms and respective suggestions from file, i.e.
//		restrict the keywords and searchTerms to available values
// TODO: add example queries in HTML

@SuppressWarnings("restriction")
public class QueryFXMLController implements Initializable{
		
	// minimal logger
	public static final String LOG_INFO = "[INFO]";
	public static final String LOG_WARNING = "[WARNING]";
	public static final String LOG_ERROR = "[ERROR]";
	public final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); // "yyyy/MM/dd HH:mm:ss"
	
	public OpenBrowser openBrowser;
	public SabioSBMLReader sbmlReader;
	
	
	public enum LogType {
		INFO, WARNING, ERROR, DEBUG
	}
	
	// browser
	@FXML private ImageView imageSabioLogo;
	@FXML private ImageView imageSabioSearch;
	@FXML private ImageView imageHelp;
	@FXML private WebView webView;
	
	// -- Log --
	@FXML private TextArea log;
	
	
	// --- Query Builder ---
    @FXML private TextField keyword;
    @FXML private ListView keywordList;
    @FXML private TextField term;
    @FXML private Text termDescription;
    @FXML private Button addKeywordButton;
    
    // --- Kinetic Law entries ---
    @FXML private TextArea entry;
    @FXML private Button addEntryButton;
    
    // -- REST Query --
    @FXML private TextArea queryText;
    @FXML private Button queryButton;
    @FXML private Button clearButton;
    
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Text statusCode;
    @FXML private Text statusCodeLabel;
    @FXML private Text time;
    @FXML private Text timeLabel;
    
    // -- REST Results --
    @FXML private Text entryLabel;
    @FXML private TableView entryTable;
    
    @FXML private TableColumn countCol;
    @FXML private TableColumn idCol;
    @FXML private TableColumn organismCol;
    @FXML private TableColumn tissueCol;
    @FXML private TableColumn reactionCol;
    @FXML private Button loadButton;
    
    private SabioQueryResult queryResult;
    Thread queryThread = null;
    
    public void initData(OpenBrowser openBrowser, SabioSBMLReader sbmlReader){
    	System.out.println("QueryFXMLController data initialized.");
    	this.openBrowser = openBrowser;
    	this.sbmlReader = sbmlReader;
    }
    
    /** 
     * Adds keyword:searchTerm to the query.
     */
    @FXML protected void handleAddKeywordAction(ActionEvent event) {
    	String selectedItem = (String) keywordList.getSelectionModel().getSelectedItem();
    	String searchTerm = term.getText();
    	
    	if (selectedItem == null){
    		logWarning("No keyword selected. Select keyword and search term in the Query Builder.");
    		return;
    	}
    	
    	String addition = selectedItem + ":\"" + searchTerm + "\"";
    	String query = queryText.getText();
    	if (searchTerm.length() == 0){
    		logWarning("No search term provided. Select keyword and search term in the Query Builder.");
    		return;
    	}
    	if (query.contains(addition)){
    		logInfo("keyword:term already in query.");
    		return;
    	}
    	
    	if (query.startsWith(SabioQuery.PREFIX_QUERY)){
    		queryText.setText(query + SabioQuery.CONNECTOR_AND + addition);
    	} else {
    		queryText.setText(SabioQuery.PREFIX_QUERY + addition);
    	}
    	logInfo("<" + addition +"> added to query");
    }
    
    /**
     * Add kinetic law entries to the query.
     */
    @FXML protected void handleAddEntryAction(ActionEvent event) {
    	String text = entry.getText();
    	if (text == null || text.length() == 0){
    		logWarning("A list of Kinetic Law Ids is required.");
    		return;
    	}
    	HashSet<Integer> ids = parseIds(text);
    	if (ids.isEmpty()){
    		logError("No Kinetic Law Ids could be parsed from input: <" + entry.getText() + ">. Ids should be separated by ' ', ',', or ';'.");
    	}
	
    	// generate query from ids
		if (ids.size() == 1){
			queryText.setText(SabioQuery.PREFIX_LAW + ids.iterator().next());	
		} else {
			String idText = null;
			for (Integer kid: ids){
				if (idText == null){
					idText = kid.toString();
				} else {
					idText += "," + kid.toString();
				}
			}
			queryText.setText(SabioQuery.PREFIX_LAWS + idText);    			
	    }
    }
    

    /**
     * Query the SABIO-RK web services with the current.
     * 
     * Uses the current query string.
     */
    @FXML protected void handleQueryAction(ActionEvent event) {
    	// check if already a query thread is running
    	// FIXME: handle overlapping queries, i.e. a query started before last finished.
    	if (queryThread != null && queryThread.isAlive()){}
    	
    
    	// necessary to run long running request in separate 
    	// thread to allow GUI updates.
    	// GUI updates have to be passed to the JavaFX Thread using runLater()
    	queryThread = new Thread(){
            public void run() {
            	// query to perform
            	String queryString = queryText.getText();
            	
            	// update initial GUI
            	showQueryStatus(true);
            	Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        queryButton.setDisable(true);
                        statusCode.setStyle("-fx-fill: black;");
                		progressIndicator.setStyle("-fx-progress-color: dodgerblue;");
                    }
                });
        		setProgress(-1);
        		
        		// query number of entries in case of q-Query to give
        		// information about long running full queries
        		if (queryString.startsWith(SabioQuery.PREFIX_QUERY)){
        			
        			logInfo("GET COUNT <"+ queryString + ">");
                	
                	Integer count = SabioQuery.performCountQuery(queryString);
                	setEntryCount(count);
                	logInfo("<" + count + "> Kinetic Law Entries for query in SABIO-RK.");
        		}
            	
            	// do the real query
        		long startTime = System.currentTimeMillis();
        		logInfo("GET <"+ queryString + ">");
        		logInfo("... waiting for SABIO-RK response ...");
        		queryResult = SabioQuery.performQuery(queryString);
        		Integer restReturnStatus = queryResult.getStatus();
        		long endTime = System.currentTimeMillis();
        		long duration = (endTime - startTime);
        		
            	Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                    	statusCode.setText(restReturnStatus.toString());
                		if (restReturnStatus != 200){
                			if (restReturnStatus == 404){
                				logWarning("No kinetic laws found for query in SABIO-RK.");
                			}
                			logWarning("SABIO-RK returned status <" + restReturnStatus + ">");
                			statusCode.setStyle("-fx-fill: red;");
                			progressIndicator.setStyle("-fx-progress-color: red;");
                		}else {
                			// successful
                			logInfo("SABIO-RK returned status <" + restReturnStatus + "> after " + duration + " [ms]");
                			
                			// handle empty test call
                			final ObservableList<SabioKineticLaw> data;
                			if (queryString == null || queryString.length() == 0){
                				 data = FXCollections.observableArrayList();
                			} else {
                				data = FXCollections.observableArrayList(queryResult.getKineticLaws());	
                			}
                			if (! data.isEmpty()){
                				entryTable.setItems(data);
                				entryTable.setDisable(false);
                    	    	loadButton.setDisable(false);	
                			}
                		}
                		time.setText(duration + " [ms]");    	
                        queryButton.setDisable(false);
                    }
                });
        		setProgress(1);    	
            }
        };
        queryThread.start();

    }
    
    @FXML protected void handleResetAction(ActionEvent event) {
    	logInfo("Query information cleared.");
    	queryText.clear();
    	keyword.clear();
    	term.clear();
    	entry.clear();
    	statusCode.setText("?");
    	showQueryStatus(false);
    	progressIndicator.setStyle("-fx-progress-color: dodgerblue;");
    	
    	// clear table
    	entryTable.setItems(FXCollections.observableArrayList());
    	entryTable.setDisable(true);
    	loadButton.setDisable(true);
    	keywordList.getSelectionModel().clearSelection();
    	
    	setEntryCount(null);
    	setHelp();
    }
    
    /**
     * Load the SABIO-RK entries in Cytoscape.
     */
    @FXML protected void handleLoadAction(ActionEvent event) {
    	logInfo("Loading Kinetic Laws in Cytoscape ...");
    	
    	if (sbmlReader != null){
    		String sbml = queryResult.getSBML();
    		if (sbml != null){
    			logInfo("... loading ...");
    			sbmlReader.loadNetworkFromSBML(sbml);	
    		} else {
    			logError("No SBML in request result.");
    		}
    	} else {
    		logError("No SBMLReader available in controller.");;
    	}
    }
    
    
    
    // JavaScript interface object
    public class JavaApp {
    	public String query;
    	
    	public void setQuery() {
            logInfo("<Upcall WebView> : "+ query);
            clearButton.fire();
            queryText.setText(query);
        }
    }
    
    
    // --------------------------------------------------------------------
    // GUI helpers
    // --------------------------------------------------------------------
    
    /** Set help information. */
    private void setHelp(){
		String infoURI = ResourceExtractor.fileURIforResource("/gui/info.html");
		webView.getEngine().load(infoURI);
    }
    
    /** Focus given scene Node. */
    private void focusNode(Node node){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                term.requestFocus();
            }
        });
    }
    
    /** Set number of entries in the query. */
    private void setEntryCount(Integer count){
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	String text = "SABIO-RK Entries";
            	if (count != null && count != 0){
            		text += " (" + count.toString() + ")";
            	} 
            	entryLabel.setText(text);
            }
    	});	
    }
	
    private void showQueryStatus(Boolean show){
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
		    	statusCode.setVisible(show);
		    	statusCodeLabel.setVisible(show);
		    	time.setVisible(show);
		    	timeLabel.setVisible(show);
            }
        });
    }
    
    private void setProgress(double progress){
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
		    	progressIndicator.setProgress(progress);
            }
        });
    }
    
    private void openURLinExternalBrowser(String text){
    	if (openBrowser != null){
	    	logInfo("Open in external browser <" + text +">");    		  
    		SwingUtilities.invokeLater(new Runnable() {
    		     public void run() {
    		    	 openBrowser.openURL(text);    	 
    		     }
    		});	 
        } else {
       	 	logError("No external browser available.");
        }
    }
    
    // --------------------------------------------------------------------
    // Init
    // --------------------------------------------------------------------
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		imageSabioLogo.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/header-sabiork.png")));
		imageSabioSearch.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/search-sabiork.png")));
		imageHelp.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/icon-help.png")));
		
		
		setProgress(1.0);
		
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
		
		// ---------------------------
		// Table for SabioKineticLaws
		// ---------------------------
		entryTable.setEditable(false);
		
		countCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,Integer>("count"));
		idCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,Integer>("id"));
		organismCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("organism"));
		tissueCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("tissue"));
		reactionCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("reaction"));
		
		entryTable.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<SabioKineticLaw>() {
	                public void changed(ObservableValue<? extends SabioKineticLaw> ov, 
	                    SabioKineticLaw oldValue, SabioKineticLaw newValue) {
	                		Integer kid = newValue.getId();	
	                		String lawURI = SabioQuery.PREFIX_KINETIC_LAW_INFO + kid.toString();
	                		logInfo("Load information for Kinetic Law <" + kid + ">");
	                		webView.getEngine().load(lawURI);
	            }
	        });
		
		//-----------------------
		// Webengine & Webview
		//-----------------------
		WebEngine webEngine = webView.getEngine();
		setHelp();
		webView.setZoom(1.0);
		

		// Handle all links by opening external browser
		// http://blogs.kiyut.com/tonny/2013/07/30/javafx-webview-addhyperlinklistener/
		webEngine.locationProperty().addListener(new ChangeListener<String>(){
             @Override
             public void changed(ObservableValue<? extends String> observable, final String oldValue, final String newValue){
            	 	 // Links to open in external browser
                     if (isExternalLink(newValue)){
                         Platform.runLater(new Runnable(){
                             @Override
                             public void run(){
                            	 // reload the old page
                                 webView.getEngine().load(oldValue);
                             }
                         });
                         // open the destination URl in the default browser
                         openURLinExternalBrowser(newValue);
                     }
                 }
         });

		
		// Handle WebView -> Java upcalls
		// process page loading
        webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {    
                	if (newState == State.SUCCEEDED) {
                		System.out.println("JavaScript object attached.");
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        win.setMember("app", new JavaApp());                
                	}
                }
            }
        );
        //-----------------------
		

		// hide elements on first loading
		showQueryStatus(false);
		
		
		// -- KeyEvents --
		keyword.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER){
					focusNode(term);
				}
            }
        });
		
		term.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER){
					addKeywordButton.fire();
					
				}
            }
        });
		
		imageSabioLogo.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	            openURLinExternalBrowser("http://sabiork.h-its.org/");
	        }
	    });
		
		imageHelp.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	            setHelp();
	        }
	    });
		
		log.textProperty().addListener(new ChangeListener<Object>() {
		    @Override
		    public void changed(ObservableValue<?> observable, Object oldValue,
		            Object newValue) {
		        log.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
		        //use Double.MIN_VALUE to scroll to the top
		    }
		});
		
		// Query SABIO-RK status
		setProgress(-1);
		String status = SabioQuery.getSabioStatus();
		if (status.equals("UP")){
			setProgress(1.0);
		}	
	}
	
	// --- HELPERS ------------------------------------------------------------
	
	/* 
	 * Helper function to decide which links are opened in external
	 * browser.
	 */
	private Boolean isExternalLink(String link){
		Boolean external = true;
		
		if (link.startsWith("http://sabiork.h-its.org/kineticLawEntry.jsp")){
			// Kinetic law information
			external = false;
		} else if (link.startsWith("file:///")){
			// Links to file resources
			external = false;
		}
		return external;
	}
	
	
    /* 
     * Parses the Kinetic Law Ids from given text string. 
     */
    private HashSet<Integer> parseIds(String text){
    	HashSet<Integer> ids = new HashSet<Integer>();
		
    	// unify separators
    	text = text.replace("\n", ",");
    	text = text.replace("\t", ",");
    	text = text.replace(" ", ",");
    	text = text.replace(";", ",");
    	
    	String[] tokens = text.split(",");
    	for (String t : tokens){
        	// single entry parsing
    		if (t.length() == 0){
    			continue;
    		}
    		
        	try {
        		Integer kineticLaw = Integer.parseInt(t);
        		ids.add(kineticLaw);
        	} catch (NumberFormatException e) {
        		logError("Kinetic Law Id could not be parsed from token: <" + t + ">");
        	}
    	}
    	return ids;
    }
	
    // --- LOGGING ------------------------------------------------------------
    private void logText(String text, LogType logType){
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	Calendar now = Calendar.getInstance();            	
            	String newText = "[" + dateFormat.format(now.getTime()) + " " + logType.toString() + "] " + text;  
            	log.setText(newText + "\n" + log.getText());
            	System.out.println(newText);
            }
        });  	
    }
     
    private void logError(String text){
    	logText(text, LogType.ERROR);
    }
    private void logWarning(String text){
    	logText(text, LogType.WARNING);
    }
    private void logDebug(String text){
    	logText(text, LogType.DEBUG);
    }
    private void logInfo(String text){
    	logText(text, LogType.INFO);
    }
    // ------------------------------------------------------------------------
	
}