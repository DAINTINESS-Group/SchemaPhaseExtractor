package segmentationFinal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import FileParsing.FileType;
import FileParsing.TSVReader;
import Logs.Logger;
import Reports.Reporter;
import commons.AngleLabel;
import commons.TimeSeries;
import commons.Transition;
import segmentation_Final.*;

public class CityGrid_newTest 
{
	
	private static final double delta = 0.000001;//1e-15;
	private static double[] acgitPrjActivity;
	private static double[] acgitPrjTime;

	@Test
	public void testFileReading() throws IOException {
		
		String filePath = "MONTHLY_STATS/CityGrid__twonicorn_MonthlySchemaStats.tsv";
		TSVReader handler = new TSVReader();
		handler.importFile(filePath);
		
		Logger log= new Logger(handler.getFile().getFileTitle());
		
		System.out.println("File: " + filePath);
		assertEquals(handler.getFile().getFileType(), FileType.TSV);
		
		handler.readFile();
		handler.convertToTimePoints();
		
		acgitPrjActivity = handler.getActivityData();
		acgitPrjTime = handler.getMonthData();
		
		assertEquals(acgitPrjActivity.length, 7);
		
		System.out.println(acgitPrjActivity.length + " " + acgitPrjTime.length);
		assertEquals(acgitPrjActivity.length, acgitPrjTime.length);
		
		
		for(int i=0;i<acgitPrjActivity.length;i++) 
		{
			System.out.println(acgitPrjTime[i]+", "+acgitPrjActivity[i]);
		}
		
		
	}
	
	
	@Test
	public void checkTimeMetrics() 
	{
		assertEquals(0,acgitPrjTime[0], delta);
		assertEquals(0.1666666666666667,acgitPrjTime[1], delta);
		assertEquals(0.3333333333333333,acgitPrjTime[2], delta);
		assertEquals(0.5,acgitPrjTime[3], delta);
		assertEquals(0.6666666666666667, acgitPrjTime[4], delta);
		assertEquals(0.8333333333333333, acgitPrjTime[5], delta);
		assertEquals(1, acgitPrjTime[6], delta);
	}
	
	@Test
	public void checkActivityMetrics() 
	{
		assertEquals(0.5333333333333333, acgitPrjActivity[0], delta);
		assertEquals(0.7933333333333333, acgitPrjActivity[1], delta);
		assertEquals(0.7933333333333333, acgitPrjActivity[2], delta);
		assertEquals(0.8666666666666666, acgitPrjActivity[3], delta);
		assertEquals(0.9399999999999999, acgitPrjActivity[4], delta);
		assertEquals(0.9399999999999999, acgitPrjActivity[5], delta);
		assertEquals(1, acgitPrjActivity[6], delta);
	}
	
	@Test
	public void phaseExtraction_Algo1() 
	{
		TimeSeries ts = new TimeSeries("acgit", acgitPrjTime, acgitPrjActivity);
		PhaseExtractor_V1 phaseExtractor = new PhaseExtractor_V1(ts);
		
		//Phase 1: compute the transitions from the incoming data
		phaseExtractor.computeOriginalTransitionsAndLabels();
		List<Transition> transitions = phaseExtractor.getNewTimeseries().getTransitions();
		checkOriginalTransitionsAngles(transitions);
		checkOriginalTransitionsLabels(transitions);

		//... and tell us what happened
		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",transitions);	
		Reporter.reportPhases("ORG. PHASES\n----------------------------", ts);

		//Phase 2: merge Phases of similar labels
		phaseExtractor.mergePhasesAfterFirstLabeling();
		transitions = phaseExtractor.getNewTimeseries().getTransitions();
		checkTransitionsAfterFirstLabeling(transitions);
		
		//... and tell us what happened
		Reporter.reportTransitions("\n V0 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n V0 MERGE PHASES\n----------------------------", ts);
		
		//Phase 3: merge all phases except steep - V1
		phaseExtractor.mergePhases();
		transitions = phaseExtractor.getNewTimeseries().getTransitions();
		checkTransitionsAfterNotSteep_Algo1(transitions);
		//... and tell us what happened
		Reporter.reportTransitions("\n V1 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n V1 MERGE PHASES\n----------------------------", ts);
		
		try 
		{
			Logger.Log.closeWriter();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void phaseExtraction_Algo2() 
//	{
//		TimeSeries ts = new TimeSeries("acgit", acgitPrjTime, acgitPrjActivity);
//		PhaseExtractor_16_4_2022 phaseExtractor = new PhaseExtractor_16_4_2022();
//		
//		//Phase 1: compute the transitions from the incoming data
//		List<Transition> transitions = phaseExtractor.computeOriginalTransitionsAndLabels(ts);
//		checkOriginalTransitionsAngles(transitions);
//		checkOriginalTransitionsLabels(transitions);
//
//		//... and tell us what happened
//		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",transitions);	
//		Reporter.reportPhases("ORG. PHASES\n----------------------------", ts);
//
//		//Phase 2: merge Phases of similar labels
//		transitions = phaseExtractor.mergePhasesAfterFirstLabeling(ts);
//		checkTransitionsAfterFirstLabeling(transitions);
//		//... and tell us what happened
//		Reporter.reportTransitions("\n V0 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
//		Reporter.reportPhases("\n V0 MERGE PHASES\n----------------------------", ts);
//		
//		//Phase 3: merge all phases except steep - V1
//		transitions = phaseExtractor.mergePhasesByNotSteep(ts);
//		checkTransitionsAfterNotSteep_Algo2(transitions);
//		//... and tell us what happened
//		Reporter.reportTransitions("\n V2 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
//		Reporter.reportPhases("\n V2 MERGE PHASES\n----------------------------", ts);
//		
//		try {
//			Logger.Log.closeWriter();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private void checkOriginalTransitionsAngles(List<Transition> transitions) 
	{
		assertEquals(6, transitions.size());
		
		assertEquals(57.339087,transitions.get(0).getAngleInDegrees(), delta);
		assertEquals(0,transitions.get(1).getAngleInDegrees(), delta);
		assertEquals(23.749494,transitions.get(2).getAngleInDegrees(), delta);
		assertEquals(23.749494,transitions.get(3).getAngleInDegrees(), delta);
		assertEquals(0,transitions.get(4).getAngleInDegrees(), delta);
		assertEquals(19.798876, transitions.get(5).getAngleInDegrees(), delta);
		
	}
	
	private void checkOriginalTransitionsLabels(List<Transition> transitions) 
	{
		assertEquals(AngleLabel.REGULAR,transitions.get(0).getAngleLabel());
		assertEquals(AngleLabel.FLAT,transitions.get(1).getAngleLabel());
		assertEquals(AngleLabel.LOW,transitions.get(2).getAngleLabel());
		assertEquals(AngleLabel.LOW,transitions.get(3).getAngleLabel());
		assertEquals(AngleLabel.FLAT,transitions.get(4).getAngleLabel());
		assertEquals(AngleLabel.LOW, transitions.get(5).getAngleLabel());
	}
	
	private void checkTransitionsAfterFirstLabeling(List<Transition> transitions) 
	{
		assertEquals(5, transitions.size());
		assertEquals(AngleLabel.REGULAR,transitions.get(0).getAngleLabel());
		assertEquals(AngleLabel.FLAT,transitions.get(1).getAngleLabel());
		assertEquals(AngleLabel.LOW,transitions.get(2).getAngleLabel());
		assertEquals(AngleLabel.FLAT,transitions.get(3).getAngleLabel());
		assertEquals(AngleLabel.LOW, transitions.get(4).getAngleLabel());
	}
	
	private void checkTransitionsAfterNotSteep_Algo1(List<Transition> transitions) 
	{
		assertEquals(1, transitions.size());
		assertEquals(AngleLabel.REGULAR,transitions.get(0).getAngleLabel());
	}
	
	private void checkTransitionsAfterNotSteep_Algo2(List<Transition> transitions) 
	{
	}
}

