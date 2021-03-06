package org.cy3sabiork.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.xml.stream.XMLStreamException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.input.KeyCode;

import javafx.application.Platform;

import netscape.javascript.JSObject;

import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.cy3sabiork.ResourceExtractor;
import org.cy3sabiork.SabioKineticLaw;
import org.cy3sabiork.SabioQueryHistory;
import org.cy3sabiork.SabioQueryResult;
import org.cy3sabiork.rest.QuerySuggestions;
import org.cy3sabiork.rest.SabioQuery;
import org.cy3sabiork.rest.SabioQueryUniRest;
import org.cy3sbml.util.OpenBrowser;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.TidySBMLWriter;


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
	
	private QuerySuggestions suggestions;
	
	// browser
	@FXML private ImageView imageSabioLogo;
	@FXML private ImageView imageSabioSearch;
	@FXML private ImageView imageHelp;
	@FXML private ImageView imageSBML;
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
    private AutoCompletionBinding<String> termBinding;
    private AutoCompletionBinding<String> keywordBinding;
    
    // --- Kinetic Law entries ---
    @FXML private TextArea entry;
    @FXML private Button addEntryButton;

    // --- Query History ---
    @FXML private ListView<String> historyList;

    // -- REST Query --
    @FXML private TextArea queryText;
    @FXML private Button queryButton;
	@FXML private Button cancelButton;
    @FXML private Button resetButton;

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

    private static SabioQueryHistory queryHistory;
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
    		logger.info("<" + selectedItem + ":" + searchTerm + "> already in query.");
    		return;
    	}
    	
    	if (query.startsWith(SabioQuery.PREFIX_QUERY)){
    		queryText.setText(query + SabioQuery.CONNECTOR_AND + addition);
    	} else {
    		queryText.setText(SabioQuery.PREFIX_QUERY + addition);
    	}
    	logger.info("<" + addition +"> added to query.");
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
                        cancelButton.setDisable(false);
                        statusCode.setStyle("-fx-fill: black;");
                		progressIndicator.setStyle("-fx-progress-color: dodgerblue;");
                    }
                });
        		setProgress(-1);
        		
        		// query number of entries in case of q-Query to give
        		// information about long running full queries
        		if (queryString.startsWith(SabioQuery.PREFIX_QUERY)){
        			logger.info("GET COUNT <"+ queryString + ">");
                	Integer count = new SabioQueryUniRest().performCountQuery(queryString);
                	setEntryCount(count);
                	logger.info("<" + count + "> Kinetic Law Entries for query in SABIO-RK.");
        		}
            	
            	// do real query
        		long startTime = System.currentTimeMillis();
        		logger.info("GET <"+ queryString + ">");
        		logger.info("... waiting for SABIO-RK response ...");
        		queryResult = new SabioQueryUniRest().performQuery(queryString);
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
                                entryTable.getSelectionModel().select(0);
                                imageSBML.setVisible(true);
                            }
                		}
                		time.setText(duration + " [ms]");    	
                        queryButton.setDisable(false);
                        cancelButton.setDisable(true);
                        
                        // add query to history
                        queryHistory.add(queryString);
                        // update the history view

                        logger.info("query added to history: <" + queryString +">");
                        queryHistory.print();
                    }
                });
        		setProgress(1);

            }
        };
        queryThread.start();
    }
    
    /**
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
    	cancelButton.setDisable(true);
        queryButton.setDisable(false);
    	imageSBML.setVisible(false);
    	keywordList.getSelectionModel().clearSelection();

        setProgress(-1);
        String status = new SabioQueryUniRest().getSabioStatus();
        if (status.equals("UP")){
            setProgress(1.0);
        }

        setEntryCount(null);
    	setHelp();
    }

    /**
     * Cancel webservice request.
     * @param event
     */
    @FXML protected void handleCancelAction(ActionEvent event) {
        logger.info("Cancel request thread");
        if (queryThread != null){
            if(queryThread.getState() != Thread.State.TERMINATED){
                // thread exists and is still running
                // FIXME: this is inherently unsafe, but works for now
                queryThread.stop();
                String abortedQuery = queryText.getText();
                handleResetAction(null);
                queryText.setText(abortedQuery);
            }
		}
    }
    
    /**
     * Load SABIO-RK entries in Cytoscape.
     */
    @FXML protected void handleLoadAction(ActionEvent event) {
    	logger.info("Loading Kinetic Laws in Cytoscape ...");
    	if (WebViewSwing.sbmlReader != null){
    	    SBMLDocument doc = queryResult.getSBMLDocument();
            String sbml = null;
            try {
                sbml = JSBML.writeSBMLToString(doc);
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
            if (sbml != null){
    			logger.info("... loading ...");
    			WebViewSwing.sbmlReader.loadNetworkFromSBML(sbml);
    			logger.info("Networks loaded in Cytoscape. Dialog closed.");
    			WebViewSwing.dialog.setVisible(false);
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
            resetButton.fire();
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
		logger.info("Load information for KineticLaw<" + kid + ">");
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
    	if (WebViewSwing.openBrowser != null){
	    	logger.info("Open in external browser <" + url +">");
    		SwingUtilities.invokeLater(new Runnable() {
    		     public void run() {
    		    	 WebViewSwing.openBrowser.openURL(url);
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
		
		String fileURI = ResourceExtractor.fileURIforResource(QuerySuggestions.RESOURCE);
		suggestions = QuerySuggestions.loadFromResource(fileURI);
		queryHistory = new SabioQueryHistory();
		
		// ---------------------------
		// Images
		// ---------------------------
		imageSabioLogo.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/header-sabiork.png")));
		imageSabioLogo.setOnMousePressed(me -> {
			openURLinExternalBrowser("http://sabiork.h-its.org/");
	    });
		
		imageHelp.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/icon-help.png")));
		imageHelp.setOnMousePressed(me -> {
            setHelp();
	    });

		imageSBML.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/logo-sbml.png")));
        imageSBML.setOnMousePressed(me -> {
            logger.info("Open SBML for query");
            SBMLDocument doc = queryResult.getSBMLDocument();
            try {
                // write to tmp file and open in browser
                File temp = File.createTempFile("cy3sabiork", ".xml");
                TidySBMLWriter.write(doc, temp.getAbsolutePath(), ' ', (short) 2);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        OpenBrowser.openURL("file://" + temp.getAbsolutePath());
                    }
                });
            } catch (SBMLException | XMLStreamException| IOException e) {
                logger.error("SBML opening failed.");
                e.printStackTrace();
            }
        });

		imageSabioSearch.setImage(new Image(ResourceExtractor.fileURIforResource("/gui/images/search-sabiork.png")));

		// ---------------------------
		// Table for SabioKineticLaws
		// ---------------------------
		entryTable.setEditable(false);
		
		countCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,Integer>("count"));
		idCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,Integer>("id"));
		organismCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("organism"));
		tissueCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("tissue"));
		reactionCol.setCellValueFactory(new PropertyValueFactory<SabioKineticLaw,String>("reaction"));
		
		entryTable.setOnMousePressed(me -> {
	        if (me.isPrimaryButtonDown() && me.getClickCount() == 1) {
	        	Object selected = entryTable.getSelectionModel().getSelectedItem();
	        	if (selected != null){
	        		Integer kid = ((SabioKineticLaw) selected).getId();
	        		setInfoForKineticLaw(kid);
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

		// Listening to hyperlink events
		WebViewHyperlinkListener eventProcessingListener = event -> {
			System.out.println(WebViews.hyperlinkEventToString(event));

			URL url = event.getURL();
			if (url == null){
			    // for instance if netscape javascript is not available
			    return true;
            }
			if (isExternalLink(url.toString())) {
				openURLinExternalBrowser(url.toString());
				return true;
			}
			// This is a link we should load, do not cancel.
			return false;
		};
		WebViews.addHyperlinkListener(webView, eventProcessingListener, HyperlinkEvent.EventType.ACTIVATED);

		// WebView Javascript -> Java upcalls using JavaApp
        // see https://groups.google.com/forum/#!topic/cytoscape-helpdesk/Sl_MwfmLTx0
        webEngine.getLoadWorker().stateProperty().addListener(
            new ChangeListener<State>() {
                @Override
                public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {    
                	if (newState == State.SUCCEEDED) {
                		try{
                			JSObject win = (JSObject) webEngine.executeScript("window");
                            win.setMember("app", new JavaApp());	
                		} catch(NoClassDefFoundError e){
                			System.out.println("netscape.javascript not accessible in Cytoscape");
                		}                        
                	}
                }
            }
        );
        
		// ---------------------------
		// Keywords (List & Text)
		// ---------------------------
		ObservableList<String> items = FXCollections.observableArrayList(suggestions.getKeywords());
		keywordList.setItems(items);
	
		keywordList.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, 
                    String oldValue, String newValue) {
                		logger.info("Keyword <" + newValue + "> selected.");
                		// set keyword in field
                        keyword.setText(newValue);
                        // focus term field
                        focusNode(term);
            }
        });
		
		// autocomplete on keywords
		keywordBinding = TextFields.bindAutoCompletion(keyword, suggestions.getKeywords());
		keywordBinding.setOnAutoCompleted(e -> {
			// select in list on autocomplete
			keywordList.getSelectionModel().select(keyword.getText());
			}
		);	
		
		keyword.setOnKeyPressed(ke -> {
			// check if keyword in list, if yes select in list
			String key = keyword.getText();
			if (suggestions.getKeywords().contains(keyword.getText()) ){
				keywordList.getSelectionModel().select(key);
			} else if (ke.getCode() == KeyCode.ENTER){
				focusNode(term);
			}
        });
		
		// ---------------------------
		// Term
		// ---------------------------
		// dynamical autocomplete
		term.focusedProperty().addListener(new ChangeListener<Boolean>(){
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean ov, Boolean nv){
		        // textfield focused
		    	if (nv){
		    		// update dynamic autocomplete on terms
		    		TreeSet<String> termSet = suggestions.getSuggestionsForKeyword(keyword.getText());
                    if (termSet != null){
                    	if (termBinding != null){
                    		termBinding.dispose();
                    	}
                    	termBinding = TextFields.bindAutoCompletion(term, termSet);
    					termBinding.setOnAutoCompleted(e -> {
    						// add entry on autocomplete
    						focusNode(addKeywordButton);
    						}
    					);	
                    } else {
                    	if (termBinding != null){
                    		termBinding.dispose();
                    	}
                    }
		        }
		    }
		});
		
		term.setOnKeyPressed(ke -> {
			if (ke.getCode() == KeyCode.ENTER){
				addKeywordButton.fire();
			}
        });

        // ---------------------------
        // History (List)
        // ---------------------------
        historyList.setItems(queryHistory.getAll());

        // on selection update the query term
        historyList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String oldValue, String newValue) {
                        logger.info("History query <" + newValue + "> selected.");
                        // set keyword in field
                        queryText.setText(newValue);
                    }
                }
        );

		// ---------------------------
		// Logging
		// ---------------------------
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
        handleResetAction(null);
	}
	
}