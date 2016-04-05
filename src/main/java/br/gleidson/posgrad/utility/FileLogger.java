package br.gleidson.posgrad.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger {

	private FileWriter fileWritter;
	private SimpleDateFormat sdf;

	public FileLogger(FileWriter fileWriter) {
		this.fileWritter = fileWriter;
		this.sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");		
	}

	public FileLogger(File outputFile) throws IOException {
		this(new FileWriter(outputFile,true));
	}

	public FileLogger(String outputFile) throws IOException {		  
		this(new FileWriter(new File(outputFile),true));
	}

	public void writeLine(String string) throws IOException {
		fileWritter.write("["+sdf.format(new Date())+"] "+string+"\n");
		fileWritter.flush();
	}
	
	public void write(String string) throws IOException {
		fileWritter.write("["+sdf.format(new Date())+"] "+string);
		fileWritter.flush();
	}
	
	public void cleanWrite(String string) throws IOException {
		fileWritter.write(string);
		fileWritter.flush();
	}
	
	public void cleanWriteLine(String string) throws IOException {
		fileWritter.write(string+"\n");
		fileWritter.flush();
	}
	
	@Override
	protected void finalize() throws Throwable {
		fileWritter.flush();
		fileWritter.close();
		super.finalize();
	}

	public void writeBreak() throws IOException {
		fileWritter.write("\n----------------------------------------------------------------------------------------------\n");
	}

	public void close() throws IOException {
		fileWritter.flush();
		fileWritter.close();
	}
}
