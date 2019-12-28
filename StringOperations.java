package com.PDFReaderApp2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringOperations {
	private static final int MAX = 1000;
	private static final Pattern PARAGRAPH = Pattern.compile("\\s*^\\s*$\\s*", Pattern.MULTILINE);
	private static final Pattern MULTISPACE = Pattern.compile("\\s+");
	private static ReentrantLock counterLock = new ReentrantLock(true);
	private static int counter =0;
	private List<String> parseList = new ArrayList<String>();
	private HashSet<String> parseList2 = new HashSet<String>();
	private static TreeSet<String> treeSet;
	private static File pdfSavedWords = new File("C:\\Users\\bhard\\eclipse-2019-workspace\\PDFSearchApp\\src\\com\\PDFReaderApp2\\PDF_Words.txt");
	private static File docxSavedWords = new File("C:\\Users\\bhard\\eclipse-2019-workspace\\PDFSearchApp\\src\\com\\PDFReaderApp2\\DOCX_Words.txt");
	private FileWriter writer;
	public static  boolean subsequenceMatch(String userInput, String wordFromPage, int qualifyingLength) {
		boolean flag= false;
        char[] x = userInput.toCharArray();
        int n = x.length;
        char[] y = wordFromPage.toCharArray();
        int m = y.length;
        int length= maxSubsequenceSubstring(x,y,n,m);
        if(length>=qualifyingLength)
        	flag=true;
        else
        	flag= false;
          return flag;
	}
	
	public static int maxSubsequenceSubstring( char[] X, char[] Y, int m, int n ) 
	  { 
	    if (m == 0 || n == 0) 
	      return 0; 
	    if (X[m-1] == Y[n-1]) 
	      return 1 + maxSubsequenceSubstring(X, Y, m-1, n-1); 
	    else
	      return max(maxSubsequenceSubstring(X, Y, m, n-1), maxSubsequenceSubstring(X, Y, m-1, n)); 
	  } 
	  
	  /* Utility function to get max of 2 integers */
	  public static int max(int a, int b) 
	  { 
	    return (a > b)? a : b; 
	  } 

	
	
	public static String getFileName(String filePath) {
		String fileName="";
		int i=0;
		int j=0;
		for(i=0;i<filePath.length();i++) {
			if(filePath.charAt(i)==46) {
				j=i;
				while(filePath.charAt(j)!=92) 
					j--;
				j=j+1;
				break;
			}
			else
				continue;
		}
		fileName= filePath.substring(j,i);
		return fileName;
	}
	
	public int matchingResults(String userInput, HashSet<String> filteredWords, int qualifyingLength, int thisPgNum){
		 HashSet<String> results2 = new HashSet<String>();
		for(String str : filteredWords) {
			boolean flag = subsequenceMatch(userInput, str, qualifyingLength);
			if(flag) {
				results2.add(str);
			}
			}
		synchronized(StringOperations.class) {
			
		  return addInMap(results2, thisPgNum);
		}
		
	}
	
	
	public int addInMap(HashSet<String> results, int threadPgNum) {
		synchronized(StringOperations.class) {
			if(results.size() >=1) {
			
				threadPgNum = threadPgNum +1;
				ReadPDFFile2.index.add((Integer)threadPgNum);
				
				ReadPDFFile2.map2.put(threadPgNum, results);
			}
			 counter ++;
		return counter;
		}
		}
	 
	//overloaded version for DOXC,because we dont need page number and also we dont care about returning any integer
		public HashSet<String> matchingResults(String userInput, HashSet<String> filteredWords, int qualifyingLength) {
			 HashSet<String> results2 = new HashSet<String>();
				for(String str : filteredWords) {
					boolean flag = subsequenceMatch(userInput, str, qualifyingLength);
					if(flag) {
						results2.add(str);
					}
					}
				return results2;
		}
	
	
	
   public HashSet<String> filterWords(List<String> words, String userInput, int qualifyingLength) throws IOException{
		 parseList2.clear();
	   for(int i=0;i <words.size();i++) {
		   if(words.get(i).length()>= qualifyingLength) {
			   parseList2.add(words.get(i));
		   }
	   }
	   return parseList2;
	}
  
   public List<String> parseString(String s) {
	     parseList.clear();
	     s= s.toLowerCase(); 
	     s= s.replaceAll("\\p{Punct}?",""); 
	     String[] array= s.split(" ");
		 parseList.addAll(Arrays.asList(array));
		 return parseList;
   }
   
   public String processString2(String args) {
		counterLock.lock();
		try {
		byte [] b = args.getBytes(StandardCharsets.UTF_8);
		String x= new String(b, StandardCharsets.UTF_8);
		
		x = compactLines(x);
	
		StringBuilder builder = new StringBuilder();
		Matcher m = Pattern.compile("\\((.*?)\\)").matcher(x);
		while (m.find()) {
			x = m.group(1);
			builder = builder.append(x);
		}
		x = builder.toString();
		x = x.replaceAll("[^a-zA-Z]"," ");
	   return x;
		}
		finally {
			counterLock.unlock();
			
		}
  
		
	}

	public String compactLines(String source) {
		counterLock.lock();
		try{
			return Stream.of(PARAGRAPH.split(source))
				   .map(para -> MULTISPACE.matcher(para).replaceAll(" "))
				   .collect(Collectors.joining("\n"));
		}finally {
			counterLock.unlock();
		}
			}
   
  
	public static HashMap<Integer, HashSet<String>> printPageView(HashMap<Integer, HashSet<String>> map) {
		 
		 synchronized(StringOperations.class) {
			 try {
				   for(int i=0;i<ReadPDFFile2.index.size();i++) { 
							  HashSet<String> values =map.get((Integer)ReadPDFFile2.index.get(i)); //System.out.println("Words at page "+i);
							 
						  }
						 
			}catch(EmptyStackException ese) {
				 System.out.println("Exception Occurred............******");
			 }
			 
			 ReadPDFFile2.isIterationDone = true;
			 return ReadPDFFile2.map2;
		 }
		
	 }
   
  public static TreeSet<String> printListView(HashMap<Integer, HashSet<String>> map) {
	  synchronized(StringOperations.class) {
		  treeSet = new TreeSet<String>();
		  System.out.println(Thread.currentThread().getName()+" will iterate and it size: "+map.size()+", Condition:   "+ReadPDFFile2.isIterationDone+ ",  ----> ");
			
			 try {
				   for(int i=0;i<ReadPDFFile2.index.size();i++) { 
					   HashSet<String> values =map.get((Integer)ReadPDFFile2.index.get(i));
					   Iterator<String> it = values.iterator();
					   while(it.hasNext()) {
						String str = it.next();
						ReadPDFFile2.listView.add(str);
						treeSet.add(str);
					   }
					   }
				   
			 }catch(EmptyStackException ese) {
				 System.out.println("Exception Occurred............******");
			 }

			 return treeSet;
	  }
  }
  
  public static TreeSet<String> printDOCXListView(HashSet<String> map) {
	  
		  TreeSet<String>treeSetDOCX = new TreeSet<String>();
		  System.out.println(Thread.currentThread().getName()+" will iterate DOCX and it size: "+map.size());
			
		Iterator<String> it = map.iterator();
					   while(it.hasNext()) {
						String str = it.next();
						ReadPDFFile2.listView.add(str);
						treeSetDOCX.add(str);
					   }
					   
					   return treeSetDOCX;
					   
	  }


  public HashSet<String> giveSavedDOXCWords() throws IOException{
	  File file = new File("C:\\Users\\bhard\\eclipse-2019-workspace\\PDFSearchApp\\src\\com\\PDFReaderApp2\\DOCX_Words.txt");
	  BufferedReader reader = new BufferedReader(new FileReader(file));
	  HashSet<String> saved = new HashSet<String>();
	  String st;
	  while((st = reader.readLine())!= null)
		  saved.add(st);
	  reader.close();
	  return saved;
  }
  
 public HashSet<String> giveSavedPDFWords()throws IOException{
	 File file = new File("C:\\Users\\bhard\\eclipse-2019-workspace\\PDFSearchApp\\src\\com\\PDFReaderApp2\\PDF_Words.txt");
	 BufferedReader reader = new BufferedReader(new FileReader(file));
	 HashSet<String> saved = new HashSet<String>();
	  String st;
	  while((st = reader.readLine())!= null)
		  saved.add(st);
	  reader.close();
	  return saved;
  }
 
  public void saveSetOfWords(HashSet<String> words, String fileType) {
	  String path;
	  try {
		 
	  if(fileType.equals("PDF")) {
		 writer  = new FileWriter(pdfSavedWords);
	  }else {
		  writer  = new FileWriter(docxSavedWords);
	  }
	  System.out.println("words to save this time: "+words.size());
	  Iterator<String> it = words.iterator();
	  while(it.hasNext()) {
		  String s = it.next();
		  System.out.println("trying to write: "+s+" in file");
		  writer.write(s+ System.lineSeparator());
		 }
	  writer.close();
	  
  } catch(Exception e) {
	  
  }
	  
  


}
}
