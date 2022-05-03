package FileParsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class TSVReader extends FileHandler {

	
	public TSVReader()
	{
		super();
		type = FileType.TSV;
	}
	
	@Override
	public void importFile(String path) 
	{
		this.file = new FileEntity(path, type);
		
	}

	@Override
	public void readFile() 
	{
		ArrayList<String[]> data = new ArrayList<>(); 
	    try (BufferedReader TSVReader = new BufferedReader(new FileReader(this.file.getFile().getAbsolutePath()))) 
	    {
	        String line = TSVReader.readLine();
	        while (line != null) 
	        {
	            String[] lineItems = line.split("\t");
	            data.add(lineItems); 
	            
	            line = TSVReader.readLine();
	        }
	        
	        setDataFromFile(data);
	    } 
	    catch (Exception e) 
	    {
	        System.out.println("Error reading TSVFile: "+ e.getMessage());
	    }
	    
	    
	}

}
