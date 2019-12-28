package com.PDFReaderApp2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.print.attribute.Size2DSyntax;
import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UI extends Application {
	Text text = new Text(50, 50, "");
	Label filePath ;
	Label pageNumIncerement = new Label("reading page: ");
	protected File selectedFile;
	protected Stage ps;
	protected static String fileNameIV=" ";
	protected static String fileLocation=" ";
	protected int userConfidence;
	protected static String fileType="";
	protected static ComboBox<Integer> confidenceSelection = new ComboBox<Integer>();
	protected static ToggleGroup tg = new ToggleGroup();
	protected static BorderPane paneForComboBox = new BorderPane();
	protected static boolean flag = false;
	protected static boolean haveHadDisplay = false;
	
	protected static FileInstance object = null;
	protected ReadPDFFile2 pdf;
	protected ReadWordFile word;
	protected StringOperations stringOperations = new StringOperations();
	// this refers to total pages in PDF or total pages in a DOCX file
	protected static Integer docLength;
	
	// UI Elements declarations
	protected static HBox paneForButtons;
	protected static HBox paneForResultView;
	
	protected static FileChooser file;
	protected static Button chooseFile;
	protected static Button search;
	protected static Button reset;
	protected static BorderPane pane;
	protected static BorderPane resultPane;
	
	protected static Pane paneForText;
	 
	protected static VBox paneForRadioButtons;
	protected static VBox paneForComboNSavedWords;
	protected static VBox viewPort = new VBox();
	
	protected static RadioButton listView;
	protected static RadioButton pageView;
	protected static ToggleGroup group2;
	protected static RadioButton pdfFile;
	protected static RadioButton wordFile;
	protected static ToggleGroup group;
	 
	protected static BorderPane paneForTextField;
	protected static TextField tf;
	protected static String userQuery;
	protected static ScrollPane sp = new ScrollPane(); 
	
	private  HashSet<String> savedDOCXWords = new HashSet<String>();
	private static int savedDOCXSize;
	private  HashSet<String> savedPDFWords = new HashSet<String>();
	private static int savedPDFSize;
	protected static BooleanProperty docTypeChanged = new SimpleBooleanProperty();
	
	 public static BooleanProperty completedProperty() {
	    	return docTypeChanged;
	    }
	    public static void setcompletedProperty(boolean flag) {
	    	docTypeChanged.set(flag);
	    }
	 
	//
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
      launch(args);
	}
	
	public BorderPane getPane() throws IOException {
		paneForButtons = new HBox(20);
		
		savedDOCXWords = stringOperations.giveSavedDOXCWords();
		savedPDFWords = stringOperations.giveSavedPDFWords();
		savedDOCXSize = savedDOCXWords.size();
		savedPDFSize = savedPDFWords.size();
		
		// ----------------- Adding Three buttons Below ----------------------------------------//	
		
		  chooseFile = new Button("Browse files");
		  search = new Button("Search");
		  reset = new Button("Reset");
		 chooseFile.setDisable(true);
		 search.setDisable(true);
		 reset.setDisable(true);
	
		 paneForButtons.setAlignment(Pos.CENTER);
		 paneForButtons.setStyle("-fx-border-color: green");
		
		 pane = new BorderPane();
		 resultPane = new BorderPane();
		 pane.setBottom(paneForButtons);
		 
		    paneForText = new Pane();
			 paneForText.getChildren().add(text);
			 pane.setCenter(paneForText);
		 
			 filePath = new Label("");
		     chooseFile.setOnAction(e->{ 
			 if(object!=null) {
				 object.resetForThisDocument();
			 }
			 
			  selectedFile = file.showOpenDialog(this.ps);
			  fileLocation = selectedFile.getAbsolutePath(); 
			  fileNameIV="";
			  fileNameIV= StringOperations.getFileName(fileLocation); 
			  int fileNameLen = fileNameIV.length();
			  filePath.setText(fileNameIV);
			  if(!fileLocation.equals(" ") && !fileNameIV.equals(" ")) {
				tf.setDisable(false);
				confidenceSelection.setDisable(false);
				search.setDisable(false);
				
			  }
			});
		
		
		  paneForButtons.getChildren().addAll(search, reset, chooseFile, filePath);
		  
		 paneForRadioButtons = new VBox(20);
		 paneForRadioButtons.setPadding(new Insets(5, 5, 5, 5));
		 paneForRadioButtons.setStyle("-fx-border-color: green");
		 paneForRadioButtons.setStyle("-fx-border-width: 2px; -fx-border-color: green");
		 
		
		 
		 pdfFile = new RadioButton("PDF");
		 pdfFile.setUserData("PDF");
		 wordFile = new RadioButton("Word");
		 wordFile.setUserData("Word");
	
		 paneForRadioButtons.getChildren().addAll(pdfFile, wordFile);
		
		 pane.setLeft(paneForRadioButtons);
	
		 group = new ToggleGroup();
		 pdfFile.setToggleGroup(group);
		 wordFile.setToggleGroup(group);
		 
		
		 
		  //   ------------------------ Adding TextField below -------------------------------------
		 
		 paneForTextField = new BorderPane();
		 paneForTextField.setPadding(new Insets(5, 5, 5, 5));
		 paneForTextField.setStyle("-fx-border-color: green");
		 paneForTextField.setLeft(new Label("Enter search word: "));
		
		 tf = new TextField(" ");
		 tf.setDisable(true);
		 tf.setAlignment(Pos.BOTTOM_RIGHT);
		 paneForTextField.setCenter(tf);
		 pane.setTop(paneForTextField);
		
		 tf.setOnAction(e -> text.setText(tf.getText()));
		 
		 
		 
		 // ------------------------------ Adding ComboBox below -----------------------------------------
		 
		 Integer[] confidencePercent = {10, 20, 30, 40, 50, 60, 70, 80, 90};
			paneForComboBox.setPadding(new Insets(20, 20, 20, 20));
		    paneForComboBox.setLeft(new Label("How confident you are?"));
			 paneForComboBox.setRight(confidenceSelection); 
			
			  
			  ObservableList<Integer> items =FXCollections.observableArrayList(confidencePercent);
			  confidenceSelection.getItems().addAll(items); 
			  confidenceSelection.setDisable(true);
			  
			  confidenceSelection.setOnAction(e -> {
				  userConfidence= confidenceSelection.getValue();
			  });
			  pane.setRight(paneForComboBox);
			     
			  
		// ----------------------  SENDING details required to read ----------------------------------  
			  group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			      public void changed(ObservableValue<? extends Toggle> ov,
			              Toggle old_toggle, Toggle new_toggle) {
			            if (group.getSelectedToggle() != null) {
			            	
			            	fileType = group.getSelectedToggle().getUserData().toString();
			            	setcompletedProperty(true);
			            	if(fileType.equals("PDF")) {
			                showSavedWords(fileType);
			            	object = new ReadPDFFile2();
			            	}
			            	else {
			            	showSavedWords(fileType);
			            	object = new ReadWordFile();
			            	
			            	}
			            	selectedFileType(fileType);
			            	
			              chooseFile.setDisable(false);
			            }
			          }
			        });
				 
	          int replaceReadingStatus = filePath.getText().length();
			  search.setOnAction(e -> { 
				  try {
						userQuery = tf.getText();
						if(fileType.equals("PDF")) {
							System.out.println("Going to read "+fileNameIV);
							 new CreateInstances(object, fileLocation, fileNameIV, userQuery, userConfidence); 
						 }
						else if(fileType.equals("Word")) {
							System.out.println("Going to read "+fileNameIV); 
					        new CreateInstances(object, fileLocation, fileNameIV, userQuery, userConfidence);	
						}
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}); 
			  
			  
			  ReadPDFFile2.completedProperty().addListener((observable, oldValue, newValue) -> {
				  // Only if completed
	                if (newValue) { 
	                    reset.setDisable(false);
	                	paneForResultView = new HBox(40);
	                    paneForResultView.setPadding(new Insets(15,15,15,15));
	                    listView = new RadioButton("List View");
	                    listView.setUserData("List View");
	                   
	                    pageView = new RadioButton("Page View");
	                    pageView.setUserData("Page View");
	                    
	                    listView.setOnAction(e ->{
	                    	showListView();
	                    });
	                    pageView.setOnAction(e -> {
	                    	showPageView();
	                    });
	                   
	                    group2 = new ToggleGroup();
	                    listView.setToggleGroup(group2);
	                    pageView.setToggleGroup(group2);
	                    
	                    paneForResultView.getChildren().addAll(listView, pageView);
	                    resultPane.setTop(paneForResultView);
	                
	                	pane.setCenter(resultPane);
	                  
	                	
	                    }
	            });
			  
			  ReadWordFile.completedProperty().addListener((observable, oldValue, newValue) -> {
	                // Only if completed
	                if (newValue) { 
	                    reset.setDisable(false);
	                	paneForResultView = new HBox(40);
	                    paneForResultView.setPadding(new Insets(15,15,15,15));
	                    RadioButton wordListView = new RadioButton("List View");
	                    wordListView.setUserData("List View");
	                    
	                    wordListView.setOnAction(e ->{
	                    	showDOCXListView();
	                    });
	                   
	                    group2 = new ToggleGroup();
	                    wordListView.setToggleGroup(group2);
	 
	                    paneForResultView.getChildren().addAll(wordListView);
	                    resultPane.setTop(paneForResultView);
	                    
	                	pane.setCenter(resultPane);
	              
	                	
	                    }
	            });
			  
			  reset.setOnAction(e -> { 
				 object.resetForThisDocument();
					 tf.clear();

					 if(fileType.equals("PDF")) {
						 
						 if(savedPDFWords.size()!= savedPDFSize) {
							 System.out.println("YES, the size changed from "+savedPDFSize+" to "+savedPDFWords.size());
							 stringOperations.saveSetOfWords(savedPDFWords, fileType);
						 }
						 }
					 else {
						 if(savedDOCXWords.size()!= savedDOCXSize)
							 stringOperations.saveSetOfWords(savedDOCXWords, fileType);
					 }
					 showSavedWords(fileType);
				
				});  
			
			
			return pane;
		
	}
	
	public void selectedFileType(String type) {
		if(type.equals("PDF")) {
       file = new FileChooser();
		
		file.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
			);
		}
		else {
        file = new FileChooser();
	    file.getExtensionFilters().addAll(
			     new FileChooser.ExtensionFilter("Word Files", "*.docx")
			);
		}
	}
	
	public  void showDOCXListView() {
		VBox root = new VBox();
		int i=0;
		TreeSet<String> listView = StringOperations.printDOCXListView(ReadWordFile.map2);
		System.out.println("-------------------------------------->> "+ listView.size());
		Iterator<String> it = listView.iterator();
		 while(it.hasNext()) { 
				  String word = it.next();
				  System.out.println("-------------------------------------->> "+ word);
				  Label lbl = new Label(word);
				  Button save = new Button("Save");
				  save.setId(word);
				  save.setOnAction(e ->{
				    	
						 saveWords(save.getId());
					 });
				  
				  root.getChildren().addAll(lbl, save);
			      
		  }
		 System.out.println("# buttons created: "+(i+1));
		sp.setContent(root);
		sp.setPrefSize(150, 255);
	    //sp.setContent(viewport);
	    sp.setPannable(true);
	    sp.fitToWidthProperty().set(true);
	    sp.fitToHeightProperty().set(true);
	    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	    resultPane.setCenter(sp);
	    
	    
	}
	
	
	public  void showListView() {
		VBox root = new VBox();
		int i=0;
		TreeSet<String> listView = StringOperations.printListView(ReadPDFFile2.map2);
		System.out.println("-------------------------------------->> "+ listView.size());
		Iterator<String> it = listView.iterator();
		 while(it.hasNext()) { 
				  String word = it.next();
				  System.out.println("-------------------------------------->> "+ word);
				  Label lbl = new Label(word);
				  Button save = new Button("Save");
				  save.setId(word);
				  save.setOnAction(e ->{
				    	
						 saveWords(save.getId());
					 });
				  
				  root.getChildren().addAll(lbl, save);
			      
		  }
		 System.out.println("# buttons created: "+(i+1));
		sp.setContent(root);
		sp.setPrefSize(150, 255);
	   
	    sp.setPannable(true);
	    sp.fitToWidthProperty().set(true);
	    sp.fitToHeightProperty().set(true);
	    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	    resultPane.setCenter(sp);
	    
	    
	}
	
	public static void showPageView() {
		VBox root = new VBox();
		HashMap<Integer, HashSet<String>> map =StringOperations.printPageView(ReadPDFFile2.map2);
		List<Integer> index = ReadPDFFile2.index;
		Iterator<String> it;
		 for(int i=0;i<index.size();i++) { 
			  HashSet<String> values =map.get((Integer)index.get(i)); //System.out.println("Words at page "+i);
			  it = values.iterator();
			  Label lb1Pg = new Label("Words on Page: "+index.get(i));
			  root.getChildren().add(lb1Pg);
			  while(it.hasNext()) {
				  String word = it.next();
				  
				  Label lbl = new Label(word);
				  root.getChildren().add(lbl);
			  }
			  Label lblSeprator = new Label("_____________________________________________");
			  root.getChildren().add(lblSeprator);
		  }
		sp.setContent(root);
		sp.setPrefSize(150, 255);
	    //sp.setContent(viewport);
	    sp.setPannable(true);
	    sp.fitToWidthProperty().set(true);
	    sp.fitToHeightProperty().set(true);
	    sp.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
	    resultPane.setCenter(sp);
	}
	
	public void saveWords(String word) {
		if(fileType.equals("Word")) {
			String s = word + ", "+ fileNameIV;
			savedDOCXWords.add(s);
			
		}
		else {
			String s = word + ", "+ fileNameIV;
			savedPDFWords.add(s);
			System.out.println("save "+s +", size now: "+savedPDFWords.size());
		}
	}
    
	public void showSavedWords(String fileType) {
		Iterator<String> it;
		ScrollPane spSaved= new ScrollPane();
		VBox paneForSavedWords= new VBox(30);
		paneForSavedWords.setPadding(new Insets(25, 25, 25, 25));
		paneForSavedWords.setStyle("-fx-border-color: green");
		paneForSavedWords.setStyle("-fx-border-width: 2px; -fx-border-color: green");
	    if(fileType.equals("PDF")){
	     it = savedPDFWords.iterator();
	     System.out.println("show "+fileType+" saved words: "+savedPDFWords.size());
	     while(it.hasNext()){
	          String s = it.next();
	          System.out.println(s+"---- will be displayed");
	          Label sw = new Label(s);
	          paneForSavedWords.getChildren().add(sw);
	          }
	    }
	    else{
	       it = savedDOCXWords.iterator();
	       System.out.println("show "+fileType+" saved words: "+savedDOCXWords.size());
	     while(it.hasNext()){
	          String s = it.next();
	          Label sw = new Label(s);
	          paneForSavedWords.getChildren().add(sw);
	          }
	    }
	    
	        spSaved.setContent(paneForSavedWords);
			spSaved.setPrefSize(350, 275);
		    spSaved.setPannable(true);
		    spSaved.fitToWidthProperty().set(true);
		    spSaved.fitToHeightProperty().set(true);
		    spSaved.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		    paneForComboBox.setBottom(spSaved);
		   
	}
    
	
	
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) throws IOException {
	 // Create a scene and place it in the stage
	 this.ps = primaryStage;	
	 Scene scene = new Scene(getPane(), 750, 400);
	 primaryStage.setTitle("UI for Project"); // Set the stage title
	 primaryStage.setScene(scene); // Place the scene in the stage
	 primaryStage.show(); // Display the stage
	 }

}
