package com.PDFReaderApp2;

public interface FileInstance {
	
	void createFileInstance(String filePath, String fileName, String userInput, int userConfidence);
	void startReading();
	void resetForThisDocument();
}
