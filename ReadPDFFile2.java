package com.PDFReaderApp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ReadPDFFile2 extends Thread implements FileInstance  {
	protected static String filePath;
	protected static String fileName;
	protected static PDDocument document;
	// neerstraz
	protected static String userInput = "";
	
	protected static int userConfidence = 0;
	protected static BufferedWriter writer=  null;
	private static int counter;
	private static ReentrantLock counterLock = new ReentrantLock(true);
	//protected static HashSet<Integer, List<String>> map = new HashSet<Integer,List<String>>();
	protected static HashMap<Integer, HashSet<String>> map2 = new HashMap<Integer,HashSet<String>>();
	protected static ArrayList<Integer> index = new ArrayList<Integer>();
	protected static HashSet<String> listView = new HashSet<String>(); 
	private static boolean flag = false;
	private static float threshold;
	private static int qualifyingLength;
	private static int totalPages=1;
	private static int thisPageNum =0;
	private int threadPageNum;
	private StringOperations stringOperations = new StringOperations();
	
	private Thread t;
	protected static boolean isIterationDone= false;
	
	
	 protected static BooleanProperty completed = new SimpleBooleanProperty();
	 protected static IntegerProperty updatePage = new SimpleIntegerProperty(); 
 
	 public static void setUpdatePageProperty(int counter) {
		 updatePage.set(counter);
	 }
	 public static IntegerProperty updatePageProperty() {
		 return updatePage;
	 }
	    public static BooleanProperty completedProperty() {
	    	return completed;
	    }
	    public static void setcompletedProperty(boolean flag) {
	    	completed.set(flag);
	    }
	    
	    public void resetForThisDocument() {
	    	counter =0;
	    	threshold=0.0f;
	    	threadPageNum=0;
	    	thisPageNum =0;
	    	userInput = "";
	    	userConfidence = 0;
	    	flag= false;
	    	isIterationDone= false;
	    	map2.clear();
	    	index.clear();
	    	listView.clear();
	    }

	@Override
	public void createFileInstance(String path, String name, String userQuery, int confidence) {
		filePath = path;
		fileName = name;
		userInput = userQuery;
		userConfidence = confidence;
		File file = new File(filePath);
		try {
		document = PDDocument.load(file);
		if (!document.isEncrypted()) {
		    totalPages = document.getNumberOfPages()-1;
		    UI.docLength = totalPages;
			userInput = userInput.toLowerCase();
			threshold = (float)((userConfidence/100.0)* userInput.length());
			qualifyingLength = (int) threshold;
			System.out.println("Number of pages in this book " + totalPages+".. We have to read "+userInput+", length "+userInput.length()+", with "+userConfidence+"% confidence, Qualifying Length: "+qualifyingLength);
		}
		if(totalPages <= 5) {
			for(int i=0;i<=totalPages;i++) {
				readThisPage(i, userInput, qualifyingLength);
				counter++;
			}
		}
		else {
			createThreads();
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}


	public void createThreads() throws InterruptedException {
		Thread [] readerThreads = new Thread [5];
		for (int i = 0; i <readerThreads.length; i++) {
			readerThreads[i] = new ReadPDFFile2();
			readerThreads[i].setName("Reader" + i);
			System.out.println("created Reader"+i);
		}
		for(int i =0;i<readerThreads.length;i++) {
			readerThreads[i].start();
		}
		
		for(int i=0;i< readerThreads.length;i++) 
			readerThreads[i].join();
		
		 synchronized(ReadPDFFile2.class) { 
			 Collections.sort(index);
			  setcompletedProperty(flag); 
			  System.out.println("-------Finished ------ \n"+index); //
			  StringOperations.printPageView(map2); 
			  }
		
	}

	public void run() {
		System.out.println(Thread.currentThread().getName()+" started running");
		this.startReading();
      }
	
	public int getThisPageNum() {
		thisPageNum = thisPageNum + 1;
		threadPageNum = thisPageNum;
		return thisPageNum;
	}

	public int getPageNumber() {
		counterLock.lock();
		try {
			thisPageNum = getThisPageNum();
			} finally {
			counterLock.unlock();
		}
		return thisPageNum;
	}	
	
	public void startReading() {
		try {
			while(!flag) {
			    getPageNumber();
				readThisPage(threadPageNum, userInput, qualifyingLength);
		}
			document.close();
		} catch (Exception e) {
		}
		
		
	}
   
	public boolean readThisPage( int pageNum, String userInput, int qualifyingLength) throws IOException {
			System.out.println(" reading page num: "+pageNum);
		   InputStream inputStream = document.getPages().get(pageNum).getContents();
		  
		try {		
		
			String text = new String();
		
		text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        text = stringOperations.processString2(text);
		List<String> list = new ArrayList<String>();
		HashSet<String> list2 = new HashSet<String>();
		
        list =stringOperations.parseString(text); 
		list2 = stringOperations.filterWords(list, userInput, qualifyingLength);
		counter = stringOperations.matchingResults(userInput, list2, qualifyingLength, pageNum);
		
	     //System.out.println("the counter....  "+counter + "\n ____________________________________________________________________________________");
		if(counter == totalPages) {
				Collections.sort(index);	 
				 flag = true;
			
		}
	}
		catch(Exception ex)
		{
		}
		finally {
			inputStream.close();
		}
		return flag;
		}
	
    
 	 
	
}
