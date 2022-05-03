package Logs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

public class Logger 
{
	private static File file;
	
	public Logger(String fileAnalysed) throws IOException 
	{
		this.initiateNewFile(fileAnalysed);
		Logger.Log.initiateWriter();
	}
	
	private void initiateNewFile(String title) throws IOException 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");  
	    Date date = new Date();  
		
	   
	    FileSystemView filesys = FileSystemView.getFileSystemView();
		String fileName = filesys.getHomeDirectory()+"\\PE_Results\\"+ title +"___"+formatter.format(date)+"_";
    	
		file = new File(fileName +".txt");
		file.getParentFile().mkdirs();
    	
    	int counter=1;
		while (!file.createNewFile()) 
		{
			file = new File(fileName + "(" +counter +").txt");
			counter++;
		} 
			
	}
	
	public static class Log 
	{
		private static FileWriter myWriter;
		
		public static void initiateWriter() throws IOException 
		{
			myWriter = new FileWriter(file.getPath());
		}
		
		public static void addDetails(String details) throws IOException 
		{
			myWriter.write(details+"\n");
		}
		
		public static void closeWriter() throws IOException 
		{
			myWriter.close();
		}
		
	}
}
