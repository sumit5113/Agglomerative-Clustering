package com.movielens.imports;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 * 
 * @author sumit
 *
 */
public abstract class AbstractImport {
	BufferedReader bfrReader = null;
	BufferedWriter bfrWriter = null;

	public void readFromFile(String filename) throws FileNotFoundException {
		bfrReader = new BufferedReader(new FileReader(filename));
	}

	public void writeToFile(String filename) throws IOException {
		bfrWriter = new BufferedWriter(new FileWriter(filename, true));
	}

	public void logError(String error) throws IOException {
		bfrWriter.append(error);
		bfrWriter.newLine();
	}

	public String getPath(String filepath) {
		return (filepath.substring(0, filepath.lastIndexOf("\\") + 1));
	}

	public void closeReader() throws IOException {
		bfrReader.close();
	}

	public void closeWriter() throws IOException {
		bfrWriter.close();
	}

	public abstract void doImport(String filename) throws Exception;
}
