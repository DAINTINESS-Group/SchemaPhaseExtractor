package Logs;

import java.io.IOException;

import org.junit.Test;

import FileParsing.TSVReader;


public class LoggerTest {

	@Test
	public void testLabelAllMeasurementsHappyDayAcgit() throws IOException 
	{
		String filePath = "MONTHLY_STATS/CityGrid__twonicorn_MonthlySchemaStats.tsv";
		TSVReader handler = new TSVReader();
		handler.importFile(filePath);
		
		Logger log= new Logger(handler.getFile().getFileTitle());
		
		Logger.Log.addDetails("Hello");
		
		Logger.Log.addDetails("World");
		
		Logger.Log.closeWriter();
		
	}
}

