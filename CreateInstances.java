package com.PDFReaderApp2;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;

public class CreateInstances {
	
	ReadPDFFile2 pdf ;
	FileInstance object;
	String filePath;
	String fileName;
	String userInput;
	int userConfidence;
	CreateInstances(FileInstance object, String filePath, String fileName){
		this.fileName= fileName;
		this.filePath = filePath;
		this.object = object;
		try {
			run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	CreateInstances(FileInstance object, String filePath, String fileName, String userInput, int userConfidence){
		System.out.println("preparing object");
		this.fileName= fileName;
		this.filePath = filePath;
		this.userInput = userInput;
		this.userConfidence = userConfidence;
		this.object = object;
		
		try {
			run();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() throws InterruptedException {
		System.out.println("calling createFileInstance()");
		object.createFileInstance(filePath, fileName, userInput, userConfidence);
		//object.createThreads();
		}
	
	public boolean checkStatus() {
		System.out.println("\n\n\n\n %$%$%$%$     Halfway printing   $%$%$%$%$... SOOOOO BAADDDDDDDDDD.... \n\n\n");
		return ReadPDFFile2.isIterationDone;
	}
	
	public static void main(String[] a) {
		Scanner sc = new Scanner(System.in);
		System.out.println("What type of document you wish to read");
		String s = sc.nextLine();
		if(s.equals("PDF")) {
			new CreateInstances(new ReadPDFFile2(),"E:\\E Books\\Java and Frameworks\\Concurrent_programming_in_Java_design_pr.pdf","Concurrent_programming_in_Java_design_pr.pdf");
		}else if(s.equals("Word")) {
			new CreateInstances(new ReadWordFile(),"C:\\Users\\bhard\\Desktop\\Fall_2019\\Advanced Application Prog in Java\\Java_5220_Project\\design document_final.docx","design document_final.docx");
		    System.out.println();
		}
	}

}
