package org.cy3sabiork.gui;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import org.cy3sabiork.ResourceExtractor;
import org.cy3sabiork.SabioKineticLaw;
import org.cy3sabiork.SabioQuery;
import org.cy3sabiork.SabioQueryResult;
import org.cy3sabiork.SabioSBMLReader;
import org.cytoscape.util.swing.OpenBrowser;

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
import javafx.scene.image.ImageView;
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
 

// TOOD: get terms and respective suggestions from file
// TODO: add example queries in HTML

@SuppressWarnings("restriction")
public class QueryFXMLController implements Initializable{
	
	public static final String PREFIX_KINETIC_LAW_INFO = "http://sabiork.h-its.org/kineticLawEntry.jsp?viewData=true&kinlawid=";
	
	public static final String PREFIX_QUERY = "searchKineticLaws/sbml?q=";
	public static final String PREFIX_LAW = "kineticLaws/";
	public static final String PREFIX_LAWS = "kineticLaws?kinlawids=";
	public static final String CONNECTOR_AND = " AND ";
	
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
	@FXML private ImageView sabioLogo;
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
    @FXML private Button loadButton;
    @FXML private TableView entryTable; 
    @FXML private TableColumn idCol;
    @FXML private TableColumn organismCol;
    @FXML private TableColumn tissueCol;
    
    private SabioQueryResult queryResult;
    Thread queryThread = null;
    
    public void initData(OpenBrowser openBrowser, SabioSBMLReader sbmlReader){
    	System.out.println("QueryFXMLController data initialized.");
    	this.openBrowser = openBrowser;
    	this.sbmlReader = sbmlReader;
    }
    
    
    @FXML protected void handleAddKeywordAction(ActionEvent event) {
    	// TODO: only use the allowed keywords
    	
    	
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
    	
    	if (query.startsWith(PREFIX_QUERY)){
    		queryText.setText(query + CONNECTOR_AND + addition);
    	} else {
    		queryText.setText(PREFIX_QUERY + addition);
    	}
    	logInfo("<" + addition +"> added to query");
    }
    
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
			queryText.setText(PREFIX_LAW + ids.iterator().next());	
		} else {
			String idText = null;
			for (Integer kid: ids){
				if (idText == null){
					idText = kid.toString();
				} else {
					idText += "," + kid.toString();
				}
			}
			queryText.setText(PREFIX_LAWS + idText);    			
	    }
		
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
    
    @FXML protected void handleQueryAction(ActionEvent event) {
    	// check if already a query thread is running
    	if (queryThread != null && queryThread.isAlive()){}
    	
    
    	// necessary to run long running request in separate 
    	// thread to allow GUI updates.
    	// GUI updates have to be pased to the JavaFX Thread using runLater()
    	queryThread = new Thread(){
            public void run() {

            	String queryString = queryText.getText();
            	
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
        		logInfo("GET <"+ queryString + ">");
        		logInfo("... waiting for SABIO-RK response ...");
        		
        		long startTime = System.currentTimeMillis();
        		
        		SabioQuery query = new SabioQuery();
        		queryResult = query.performQuery(queryString);
        			
        		long endTime = System.currentTimeMillis();
        		long duration = (endTime - startTime);
        		

        		Integer restReturnStatus = queryResult.getStatus();
        		
            	Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                    	statusCode.setText(restReturnStatus.toString());
                		if (restReturnStatus != 200){
                			if (restReturnStatus == 404){
                				logWarning("No kinetic laws found for query in SABIO-RK.");
                			}
                			logWarning("SABIO-RK query returned with status <" + restReturnStatus + ">");
                			statusCode.setStyle("-fx-fill: red;");
                			progressIndicator.setStyle("-fx-progress-color: red;");
                		}else {
                			// successful
                			logInfo("SABIO-RK query returned with status <" + restReturnStatus + "> after " + duration + "[ms]");
                			
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
    
    @FXML protected void handleClearAction(ActionEvent event) {
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
    	setHelp();
    }
    
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
    
    
    /* Sets the help information. */
    private void setHelp(){
		// Initialize the webengine
		String infoURI = ResourceExtractor.fileURIforResource("/gui/info.html");
		webView.getEngine().load(infoURI);
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
	    	logInfo("Opening address in external browser: <" + text +">");    		  
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
		
		// Table for SabioKineticLaws
		entryTable.setEditable(false);
		
		idCol.setCellValueFactory(
			new PropertyValueFactory<SabioKineticLaw,Integer>("id")
		);
		
		organismCol.setCellValueFactory(
		    new PropertyValueFactory<SabioKineticLaw,String>("organism")
		);
		tissueCol.setCellValueFactory(
		    new PropertyValueFactory<SabioKineticLaw,String>("tissue")
		);
		

		entryTable.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<SabioKineticLaw>() {
	                public void changed(ObservableValue<? extends SabioKineticLaw> ov, 
	                    SabioKineticLaw oldValue, SabioKineticLaw newValue) {
	                		Integer kid = newValue.getId();	
	                		String lawURI = PREFIX_KINETIC_LAW_INFO + kid.toString();
	                		logInfo("Load information for Kinetic Law <" + kid + "> from <" + lawURI +">" );
	                		webView.getEngine().load(lawURI);
	            }
	        });
		
		setHelp();
		webView.setZoom(0.9);
		
		// Handle all links by opening external browser
		// http://blogs.kiyut.com/tonny/2013/07/30/javafx-webview-addhyperlinklistener/

		// FIXME: this opens all links in external locations.
		webView.getEngine().locationProperty().addListener(new ChangeListener<String>(){
             @Override
             public void changed(ObservableValue<? extends String> observable, final String oldValue, final String newValue){
            	 // Links to open in external browser
                     if (newValue.startsWith("http")){
                         Platform.runLater(new Runnable(){
                             @Override
                             public void run(){
                                 webView.getEngine().load(oldValue);
                             }
                         });
                         // open the destination URl in the default browser
                         openURLinExternalBrowser(newValue);
                     }
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
		
		term.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER){
					System.out.println("KeyCode == ENTER on term");
					addKeywordButton.fire();
					
				}
            }
        });
		
		sabioLogo.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) {
	            openURLinExternalBrowser("http://sabiork.h-its.org/");
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
    // --------------------------------------------------------------------
	
	
    // --- LOGGING ---
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
    // --------------
	
}