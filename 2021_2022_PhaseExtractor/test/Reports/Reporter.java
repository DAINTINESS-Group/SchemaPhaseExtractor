package Reports;

import java.io.IOException;
import java.util.List;

import Logs.Logger;
import commons.Phase;
import commons.TimeSeries;
import commons.Transition;

public class Reporter 
{
	/** Helper to report what is in the transition list
	 * 
	 * @param transitions
	 */
	private static boolean showOnConsole = false;
	
	public static void reportTransitions(String title, List<Transition> transitions) 
	{
		try 
		{
			writeTransitions(title,transitions);
		} 
		catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		
		if(!showOnConsole) {
			return;
		}
		
		System.out.println(title);
		System.out.println("\n\nTRANSITIONS.to(x,y_{from}, x,y_{to})\n----------------------------");
		for(int i = 0; i< transitions.size(); i++) { 
			Transition tr =	transitions.get(i);
			System.out.println(
					"(" + String.format("%.2f",tr.getFromPhase().getXFirst()) + " " +
					String.format("%.2f",tr.getFromPhase().getYFirst()) + ") -> (" +
					String.format("%.2f",tr.getToPhase().getXLast()) + "  " +
					String.format("%.2f",tr.getToPhase().getYLast()) + ")  " +
					tr.getAngleLabel().toString()); 
		} 
		System.out.println("\n");
	}
	
	public static void reportPhases(String title, TimeSeries ts) 
	{
		try 
		{
			writePhases(title, ts);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		if(!showOnConsole) {
			return;
		}
		
		System.out.println(title);
		for(Phase p: ts.getPhases())
			System.out.println(p.getLongDescription());
		System.out.println("\n");
	}
	
	public static void writeTransitions(String title, List<Transition> transitions) throws IOException 
	{
		Logger.Log.addDetails(title);
		Logger.Log.addDetails("\n\nTRANSITIONS.to(x,y_{from}, x,y_{to})\n----------------------------");
		for(int i = 0; i< transitions.size(); i++) { 
			Transition tr =	transitions.get(i);
			Logger.Log.addDetails(
					"(" + String.format("%.2f",tr.getFromPhase().getXFirst()) + " " +
					String.format("%.2f",tr.getFromPhase().getYFirst()) + ") -> (" +
					String.format("%.2f",tr.getToPhase().getXLast()) + "  " +
					String.format("%.2f",tr.getToPhase().getYLast()) + ")  " +
					tr.getAngleLabel().toString()); 
		} 
		Logger.Log.addDetails("\n");
	}
	
	public static void writePhases(String title, TimeSeries ts) throws IOException 
	{
		Logger.Log.addDetails(title);
		for(Phase p: ts.getPhases())
			Logger.Log.addDetails(p.getLongDescription());
		Logger.Log.addDetails("\n");
	}
}
