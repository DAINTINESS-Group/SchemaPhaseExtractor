package FileParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class FileHandler 
{
	
	protected LinkedHashMap<String,ArrayList<String>> data;
	protected FileEntity file;
	protected FileType type;
	
	private double[] monthData;
	private double[] activityData;
	
	
	FileHandler(){
		data = new LinkedHashMap<String,ArrayList<String>>();
	}
	
	public abstract void importFile(String path);
	
	public abstract void readFile();
	
	public void setDataFromFile(ArrayList<String[]> fileData) 
	{
		//first line indicates the titles of columns
		String[] titles = fileData.get(0);
		
		for(int i=0;i<titles.length;i++) 
		{
			data.put(titles[i].toString(), new ArrayList<String>());
		}
		
		Object[] titlesFromMap = data.keySet().toArray();
		
		//insert items of columns
		for(int linePos = 1; linePos<fileData.size(); linePos++) 
		{
			String[] lineData = fileData.get(linePos);
			for(int titlePos = 0; titlePos<lineData.length; titlePos++) 
			{
				data.get(titlesFromMap[titlePos]).add(lineData[titlePos]);
			}
		}
	}
	
	public void convertToTimePoints() 
	{
		//String[] titlesFromMap = (String[]) data.keySet().toArray();

		for(Object title: data.keySet().toArray()) 
		{
			if(title.toString().equals("mID")) 
			{
				setMonthData(data.get(title));
			}
			
			else if(title.toString().equals("TotalAttrActivity"))
			{
				setActivityData(data.get(title));
			}
		}
	}
	
	private void setMonthData(ArrayList<String> monthDataFromMap) 
	{
		int months = monthDataFromMap.size();
		this.monthData = new double[months];
		
		for(int i =0;i<months; i++) 
		{
			double calculation = (i/((double)months-1));
			monthData[i]=calculation;
		}
	}
	
	private void setActivityData(ArrayList<String> activityDataFromMap) 
	{
		int months = activityDataFromMap.size();
		this.activityData = new double[months];
		double sumActivities = 0;
		for(int i =0;i<months; i++) 
		{
			double value = Double.parseDouble(activityDataFromMap.get(i));
			sumActivities += value;
			activityData[i]=value;
		}
		
		double activityDataCurrentSum = 0;
		for(int i =0;i<months; i++) 
		{
			activityDataCurrentSum += activityData[i];
			double finalizedValue = activityDataCurrentSum/(double)sumActivities;
			activityData[i]=finalizedValue;
		}
	}

	public double[] getMonthData()
	{
		return this.monthData;
	}
	
	public double[] getActivityData()
	{
		return this.activityData;
	}
	
	public FileEntity getFile() 
	{
		return this.file;
	}
}
