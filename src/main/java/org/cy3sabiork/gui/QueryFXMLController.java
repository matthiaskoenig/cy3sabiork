package org.cy3sabiork.gui;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.TreeSet;

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

import netscape.javascript.JSObject;

import org.cytoscape.util.swing.OpenBrowser;
import org.controlsfx.control.textfield.TextFields;
import org.cy3sabiork.QuerySuggestions;
import org.cy3sabiork.ResourceExtractor;
import org.cy3sabiork.SabioKineticLaw;
import org.cy3sabiork.SabioQuery;
import org.cy3sabiork.SabioQueryResult;
import org.cy3sabiork.SabioSBMLReader;


/** 
 * The javafx controller for the GUI.
 * 
 * The GUI is created with JavaFX SceneBuilder from the 
 *  /gui/query.fxml
 * For GUI changes load the fxml in the SceneBuilder and update it.
 * 
 * The HTML part can be debugged separately, i.e. with respective 
 * HTML/JS/CSS tools.
 */
@SuppressWarnings("restriction")
public class QueryFXMLController implements Initializable{
	private WebViewSwing webViewSwing;
	
	// browser
	@FXML private ImageView imageSabioLogo;
	@FXML private ImageView imageSabioSearch;
	@FXML private ImageView imageHelp;
	@FXML private WebView webView;
	
	// -- Log --
	@FXML private TextArea log;
	private Logger logger;
	
	// --- Query Builder ---
    @FXML private TextField keyword;
    @FXML private ListView<String> keywordList;
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
    // @FXML private TextField history;
    
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
    
    
    /** 
     * Adds keyword:searchTerm to the query.
     */
    @FXML protected void handleAddKeywordAction(ActionEvent event) {
    	// String selectedItem = (String) keywordList.getSelectionModel().getSelectedItem();
    	String selectedItem = keyword.getText();
    	String searchTerm = term.getText();
    	
    	if (selectedItem == null || selectedItem.length()==0){
    		logger.warn("No keyword selected. Select keyword and search term in the Query Builder.");
    		return;
    	}
    	
    	String addition = selectedItem + ":\"" + searchTerm + "\"";
    	String query = queryText.getText();
    	if (searchTerm.length() == 0){
    		logger.warn("No search term provided. Select keyword and search term in the Query Builder.");
    		return;
    	}
    	if (query.contains(addition)){
    		logger.info("keyword:term already in query.");
    		return;
    	}
    	
    	if (query.startsWith(SabioQuery.PREFIX_QUERY)){
    		queryText.setText(query + SabioQuery.CONNECTOR_AND + addition);
    	} else {
    		queryText.setText(SabioQuery.PREFIX_QUERY + addition);
    	}
    	logger.info("<" + addition +"> added to query");
    }
    
    /**
     * Add kinetic law entries to the query.
     */
    @FXML protected void handleAddEntryAction(ActionEvent event) {
    	String text = entry.getText();
    	if (text == null || text.length() == 0){
    		logger.warn("A list of Kinetic Law Ids is required.");
    		return;
    	}
  
    	// parse ids
    	HashSet<Integer> ids = SabioKineticLaw.parseIds(text);
    	if (ids.isEmpty()){
    		logger.error("No Kinetic Law Ids could be parsed from input: <" + entry.getText() + ">. Ids should be separated by ' ', ',', or ';'.");
    		return;
    	}
    	
    	// query for ids
    	queryText.setText(SabioQuery.queryStringFromIds(ids));
    }
    
    /**
     * Run SABIO-RK web service query.
     */
    @FXML protected void handleQueryAction(ActionEvent event) {
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
        			logger.info("GET COUNT <"+ queryString + ">");
                	Integer count = SabioQuery.performCountQuery(queryString);
                	setEntryCount(count);
                	logger.info("<" + count + "> Kinetic Law Entries for query in SABIO-RK.");
        		}
            	
            	// do real query
        		long startTime = System.currentTimeMillis();
        		logger.info("GET <"+ queryString + ">");
        		logger.info("... waiting for SABIO-RK response ...");
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
                				logger.warn("No kinetic laws found for query in SABIO-RK.");
                			}
                			logger.warn("SABIO-RK returned status <" + restReturnStatus + ">");
                			statusCode.setStyle("-fx-fill: red;");
                			progressIndicator.setStyle("-fx-progress-color: red;");
                		}else {
                			// successful
                			logger.info("SABIO-RK returned status <" + restReturnStatus + "> after " + duration + " [ms]");
                			
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
                        
                        // add query to history
                        WebViewSwing.queryHistory.add(queryString);
                        logger.info("query added to history: <" + queryString +">");
                    }
                });
        		setProgress(1);    	
            }
        };
        queryThread.start();
    }
    
    /*
     * Reset GUI to original state.
     */
    @FXML protected void handleResetAction(ActionEvent event) {
    	logger.info("Reset GUI.");
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
     * Load SABIO-RK entries in Cytoscape.
     */
    @FXML protected void handleLoadAction(ActionEvent event) {
    	logger.info("Loading Kinetic Laws in Cytoscape ...");
    	if (webViewSwing.sbmlReader != null){
    		String sbml = queryResult.getSBML();
    		if (sbml != null){
    			logger.info("... loading ...");
    			webViewSwing.sbmlReader.loadNetworkFromSBML(sbml);
    			logger.info("Networks loaded in Cytoscape. Close Dialog for exploring.");
    		} else {
    			logger.error("No SBML in request result.");
    		}
    	} else {
    		logger.error("No SBMLReader available in controller.");
    	}
    }
    
    // --------------------------------------------------------------------
    // JavaScript interface object
    // --------------------------------------------------------------------
    public class JavaApp {
    	public String query;
    	
    	public void setQuery() {
            logger.info("<Upcall WebView> : "+ query);
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
    
    /** Get information for KineticLaw in WebView. */
	private void setInfoForKineticLaw(Integer kid){
		String lawURI = SabioQuery.PREFIX_KINETIC_LAW_INFO + kid.toString();
		logger.info("Load information for Kinetic Law <" + kid + ">");
		webView.getEngine().load(lawURI);
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
	
    /** Set GUI elements for query status visible. */
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
    
    /** Set progress in progress indicator. */
    private void setProgress(double progress){
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
		    	progressIndicator.setProgress(progress);
            }
        });
    }
    
    /** Open url in external browser. */
    private void openURLinExternalBrowser(String url){
    	if (webViewSwing.openBrowser != null){
	    	logger.info("Open in external browser <" + url +">");    		  
    		SwingUtilities.invokeLater(new Runnable() {
    		     public void run() {
    		    	 webViewSwing.openBrowser.openURL(url);    	 
    		     }
    		});	 
        } else {
       	 	logger.error("No external browser available.");
        }
    }
    
    /* 
	 * Check if given link is an external link.
	 * File links, and links to kineticLawInformation are opened in the WebView.
	 */
	private Boolean isExternalLink(String link){
		Boolean external = true;
		
		if (link.startsWith("http://sabiork.h-its.org/kineticLawEntry.jsp")){
			external = false;
		} else if (link.startsWith("file:///")){
			external = false;
		}
		return external;
	}
    
    // --------------------------------------------------------------------
    // Initialize
    // --------------------------------------------------------------------
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger = new Logger(this.log);
		
		// ---------------------------
		// Images
		// ---------------------------
		imageSabioLogo.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/header-sabiork.png")));
		imageSabioSearch.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/search-sabiork.png")));
		imageHelp.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/icon-help.png")));
		
		
		QuerySuggestions suggestions = QuerySuggestions.loadFromResource(QuerySuggestions.RESOURCE);
		suggestions.print();
		TreeSet<String> keywordSet = suggestions.getKeywords();
		
		// ---------------------------
		// ListView of Keywords
		// ---------------------------
		ObservableList<String> items = FXCollections.observableArrayList(keywordSet);
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
		
		TextFields.bindAutoCompletion(keyword, keywordSet);
		
		// ---------------------------
		// Table for SabioKineticLaws
		// ---------------------------
		entryTable.setEditable(false);
		
		countCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,Integer>("count"));
		idCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,Integer>("id"));
		organismCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("organism"));
		tissueCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("tissue"));
		reactionCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("reaction"));
		
		entryTable.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
		        	Object selected = entryTable.getSelectionModel().getSelectedItem();
		        	if (selected != null){
		        		Integer kid = ((SabioKineticLaw) selected).getId();
		        		setInfoForKineticLaw(kid);
		        	}                   
		        }
		    }
		});
		
		// SelectionChange Listener (Important if selection via error keys change)
		entryTable.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<SabioKineticLaw>() {
	                public void changed(ObservableValue<? extends SabioKineticLaw> ov, 
	                    SabioKineticLaw oldValue, SabioKineticLaw newValue) {
	                		Integer kid = newValue.getId();
	                		setInfoForKineticLaw(kid);
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
		// FIXME: this is a bad hack, should behave similar to HyperLinkListener in JTextPane
		webEngine.locationProperty().addListener(new ChangeListener<String>(){
             @Override
             public void changed(ObservableValue<? extends String> observable, final String oldValue, final String newValue){
        	 	 // Links to open in external browser
                 if (isExternalLink(newValue)){
                     Platform.runLater(new Runnable(){
                         @Override
                         public void run(){
                        	 // reload old page
                             webView.getEngine().load(oldValue);
                         }
                     });
                     // open url
                     openURLinExternalBrowser(newValue);
                 }
             }
         });

		// WebView Javascript -> Java upcalls using JavaApp
		// FIXME: currently not working due to netscape.javascript issue
		//     see: https://github.com/matthiaskoenig/cy3sabiork/issues/12
        webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {    
                	if (newState == State.SUCCEEDED) {
                		try{
                			JSObject win = (JSObject) webEngine.executeScript("window");
                            win.setMember("app", new JavaApp());	
                		} catch(NoClassDefFoundError e){
                			System.out.println("netscape.javascript not accessible in Cytoscape: see https://groups.google.com/forum/#!topic/cytoscape-helpdesk/Sl_MwfmLTx0");
                		}
                		
                                                     
                	}
                }
            }
        );
        
		//-----------------------
		// KeyEvents
		//-----------------------
		keyword.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
            	
				if (ke.getCode() == KeyCode.ENTER){
					focusNode(term);
					// TODO: set keywords for searchTerm
					String key = keyword.getText();
					TreeSet<String> termSet = suggestions.getSuggestionsForKeyword(key);
					
					logger.info("Autocomplete set for <" + key + ">");
					System.out.println(termSet);
					TextFields.bindAutoCompletion(term, termSet);
				}else{
					// check if keyword in list, if yes select
					String key = keyword.getText();
					if (suggestions.getKeywords().contains(keyword.getText()) ){
						keywordList.getSelectionModel().select(key);
					}
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
		    	// FIXME: this is not working like expected
		        log.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
		        //use Double.MIN_VALUE to scroll to the top
		    }
		});
		
		//-----------------------
		// SabioStatus
		//-----------------------
		showQueryStatus(false);

		setProgress(-1);
		String status = SabioQuery.getSabioStatus();
		if (status.equals("UP")){
			setProgress(1.0);
		}	
	}
	
}