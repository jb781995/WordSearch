package com.PDFReaderApp2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ReadWordFile implements FileInstance, Runnable {
	private static String filePath;
	private static String fileName;
	private static String userInput = "suseptibal";
	private static int userConfidence = 70;
	private static XWPFDocument document;
	protected static BufferedWriter writer=  null;
	private static int counter;
	private static ReentrantLock counterLock = new ReentrantLock(true);
	protected static HashSet<String> map2 = new HashSet<String>();
	protected static ArrayList<Integer> index = new ArrayList<Integer>();
	private static boolean flag = false;
	private static float threshold;
	private static int qualifyingLength;
	private static int totalPages=1;
	private StringOperations stringOperations = new StringOperations();
	private static List<XWPFParagraph> paragraphs;
	private static boolean isIterationDone= false;
	protected static BooleanProperty completed = new SimpleBooleanProperty();
	
	 public static BooleanProperty completedProperty() {
	    	return completed;
	    }
	    public static void setcompletedProperty(boolean flag) {
	    	completed.set(flag);
	    }
	
	@Override
	public void createFileInstance(String path, String name, String input, int confidence) {
		try {
		filePath = path;
		fileName = name;
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        document = new XWPFDocument(fis);
        
         paragraphs= document.getBodyElements().get(6).getBody().getParagraphs();
       // XWPFDocument document2 = document.getXWPFDocument().getProperties().getCoreProperties().
        
        userInput = input.toLowerCase();
		threshold = (float)((confidence/100.0)* userInput.length());
		qualifyingLength = (int) threshold;
		System.out.println("total pages: "+document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages()+" qualifying length for "+userInput+" = "+qualifyingLength);
		readThisDoc(userInput, qualifyingLength);
        	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
public void readThisDoc(String userInput, int qualifyingLength) {
	List<String> list = new ArrayList<String>();
	HashSet<String> list2 = new HashSet<String>();
	try {
	for(XWPFParagraph para : paragraphs) {
      	 String text = para.getText();
      	 list.addAll(stringOperations.parseString(text));
       }
	System.out.println("parsed words: "+list.size());
	list2 = stringOperations.filterWords(list, userInput, qualifyingLength);
	System.out.println("filtered words: "+list2.size());
	map2 = stringOperations.matchingResults(userInput, list2, qualifyingLength);
	flag = (map2.size()>=1)? true: false;
	if(flag) {
		Iterator<String> it = map2.iterator();
		while(it.hasNext()) {
			String s = it.next();
			System.out.println("-----> "+s);
		}
	}
		
	
	setcompletedProperty(flag); 
} catch(Exception e) {
	}
}
	@Override
	public void startReading() {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void resetForThisDocument() {
		counter =0;
    	threshold=0.0f;
    	userInput = "";
    	userConfidence = 0;
    	flag= false;
    	isIterationDone= false;
    	map2.clear();
    	index.clear();
	}

}
