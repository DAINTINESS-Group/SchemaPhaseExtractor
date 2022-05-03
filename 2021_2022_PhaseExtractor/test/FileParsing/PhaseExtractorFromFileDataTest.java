package FileParsing;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import Reports.Reporter;
import commons.Phase;
import commons.TimeSeries;
import commons.Transition;
import segmentation.PhaseExtractor;


public class PhaseExtractorFromFileDataTest {
	@Test
	public void testLabelAllMeasurementsHappyDayAcgit() {
		String filePath = "testFiles/accgit__acl_MonthlySchemaStats_TSV.tsv";
		TSVReader handler = new TSVReader();
		handler.importFile(filePath);
		
		System.out.println("File: " + filePath);
		assertEquals(handler.getFile().getFileType(), FileType.TSV);
		
		handler.readFile();
		handler.convertToTimePoints();
		
		double[] acgitPrjActivity = handler.getActivityData();
		double[] acgitPrjTime = handler.getMonthData();
		System.out.println(acgitPrjActivity.length + " " + acgitPrjTime.length);
		assertEquals(acgitPrjActivity.length, acgitPrjTime.length);
		
		for(int i=0;i<acgitPrjActivity.length;i++) 
		{
			System.out.println(acgitPrjTime[i]+", "+acgitPrjActivity[i]);
		}

		TimeSeries ts = new TimeSeries("acgit", acgitPrjTime, acgitPrjActivity);
		PhaseExtractor phaseExtractor = new PhaseExtractor();
		
		//Phase 1: compute the transitions from the incoming data
		List<Transition> transitions = phaseExtractor.computeOriginalTransitionsAndLabels(ts);
		assertEquals(14, transitions.size());

		//... and tell us what happened
		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",transitions);	
		Reporter.reportPhases("ORG. PHASES\n----------------------------", ts);

		//TODO Phase 3: now, we need to further merge the resulting phases
		phaseExtractor.iterativeMerging(ts);
		//... and tell us what happened
		Reporter.reportTransitions("\n ITERATIVE MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n ITERATIVE MERGE PHASES\n----------------------------", ts);
	}//end happyPathAcgit


	
	
}
