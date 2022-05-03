package FileParsing;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import Reports.Reporter;
import commons.TimeSeries;
import commons.Transition;
import segmentation.PhaseExtractor;
import segmentation.PhaseExtractor_16_4_2022;
import segmentation.PhaseExtractor_6_4_2022;
//CityGrid__twonicorn_MonthlySchemaStats
public class FileReaderTest {
	
	private static final double delta = 1e-15;
	private static double[] acgitPrjActivity;
	private static double[] acgitPrjTime;

	@Test
	public void testFileReading() {
		String filePath = "MONTHLY_STATS/accgit__acl_MonthlySchemaStats.tsv";
		TSVReader handler = new TSVReader();
		handler.importFile(filePath);
		
		System.out.println("File: " + filePath);
		assertEquals(handler.getFile().getFileType(), FileType.TSV);
		
		handler.readFile();
		handler.convertToTimePoints();
		
		acgitPrjActivity = handler.getActivityData();
		acgitPrjTime = handler.getMonthData();
		
		assertEquals(acgitPrjActivity.length, 15);
		
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
		assertEquals(acgitPrjTime[0], 0, delta);
		assertEquals(acgitPrjTime[1], 0.0714285714285714, delta);
		assertEquals(acgitPrjTime[2], 0.1428571428571429, delta);
		assertEquals(acgitPrjTime[3], 0.2142857142857143, delta);
		assertEquals(acgitPrjTime[4], 0.2857142857142857, delta);
		assertEquals(acgitPrjTime[5], 0.3571428571428571, delta);
		assertEquals(acgitPrjTime[6], 0.4285714285714286, delta);
		assertEquals(acgitPrjTime[7], 0.5, delta);
		assertEquals(acgitPrjTime[8], 0.5714285714285714, delta);
		assertEquals(acgitPrjTime[9], 0.6428571428571429, delta);
		assertEquals(acgitPrjTime[10], 0.7142857142857143, delta);
		assertEquals(acgitPrjTime[11], 0.7857142857142857, delta);
		assertEquals(acgitPrjTime[12], 0.8571428571428571, delta);
		assertEquals(acgitPrjTime[13], 0.9285714285714286, delta);
		assertEquals(acgitPrjTime[14], 1, delta);
	}
	
	@Test
	public void checkActivityMetrics() 
	{
		assertEquals(acgitPrjActivity[0], 0.38, delta);
		assertEquals(acgitPrjActivity[1], 0.38, delta);
		assertEquals(acgitPrjActivity[2], 0.78, delta);
		assertEquals(acgitPrjActivity[3], 0.78, delta);
		assertEquals(acgitPrjActivity[4], 1, delta);
		assertEquals(acgitPrjActivity[5], 1, delta);
		assertEquals(acgitPrjActivity[6], 1, delta);
		assertEquals(acgitPrjActivity[7], 1, delta);
		assertEquals(acgitPrjActivity[8], 1, delta);
		assertEquals(acgitPrjActivity[9], 1, delta);
		assertEquals(acgitPrjActivity[10], 1, delta);
		assertEquals(acgitPrjActivity[11], 1, delta);
		assertEquals(acgitPrjActivity[12], 1, delta);
		assertEquals(acgitPrjActivity[13], 1, delta);
		assertEquals(acgitPrjActivity[14], 1, delta);
	}
	
	@Test
	public void phaseExtraction_Algo1() 
	{
		TimeSeries ts = new TimeSeries("acgit", acgitPrjTime, acgitPrjActivity);
		PhaseExtractor_6_4_2022 phaseExtractor = new PhaseExtractor_6_4_2022();
		
		//Phase 1: compute the transitions from the incoming data
		List<Transition> transitions = phaseExtractor.computeOriginalTransitionsAndLabels(ts);
		assertEquals(14, transitions.size());

		//... and tell us what happened
		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",transitions);	
		Reporter.reportPhases("ORG. PHASES\n----------------------------", ts);

		//Phase 2: merge Phases of similar labels
		transitions = phaseExtractor.mergePhasesAfterFirstLabeling(ts);
		//... and tell us what happened
		Reporter.reportTransitions("\n V0 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n V0 MERGE PHASES\n----------------------------", ts);
		
		//Phase 3: merge all phases except steep - V1
		transitions = phaseExtractor.mergePhasesByNotSteep(ts);
		//... and tell us what happened
		Reporter.reportTransitions("\n V1 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n V1 MERGE PHASES\n----------------------------", ts);
	}
	
	@Test
	public void phaseExtraction_Algo2() 
	{
		TimeSeries ts = new TimeSeries("acgit", acgitPrjTime, acgitPrjActivity);
		PhaseExtractor_16_4_2022 phaseExtractor = new PhaseExtractor_16_4_2022();
		
		//Phase 1: compute the transitions from the incoming data
		List<Transition> transitions = phaseExtractor.computeOriginalTransitionsAndLabels(ts);
		assertEquals(14, transitions.size());

		//... and tell us what happened
		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",transitions);	
		Reporter.reportPhases("ORG. PHASES\n----------------------------", ts);

		//Phase 2: merge Phases of similar labels
		transitions = phaseExtractor.mergePhasesAfterFirstLabeling(ts);
		//... and tell us what happened
		Reporter.reportTransitions("\n V0 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n V0 MERGE PHASES\n----------------------------", ts);
		
		//Phase 3: merge all phases except steep - V1
		transitions = phaseExtractor.mergePhasesByNotSteep(ts);
		//... and tell us what happened
		Reporter.reportTransitions("\n V2 MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n V2 MERGE PHASES\n----------------------------", ts);
	}
}
