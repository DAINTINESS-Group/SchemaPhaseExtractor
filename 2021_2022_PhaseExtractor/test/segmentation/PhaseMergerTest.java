package segmentation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import Reports.Reporter;
import commons.TimeSeries;
import commons.Transition;

public class PhaseMergerTest {

	@Test
	public void testMeasurementsAcgit() 
	{
		
		double[] acgitPrjMonthly = new double[]{0.050847458, 0.062711864, 0.225423729, 0.36779661, 0.615254237, 0.638983051, 0.638983051, 0.640677966, 0.769491525, 0.769491525, 0.769491525, 0.776271186, 0.822033898, 0.849152542, 0.971186441, 0.979661017, 0.981355932, 0.981355932, 0.981355932, 0.996610169, 0.996610169, 1} ;
		double[] acgitPrjTime = new double[]{0, 0.047619048, 0.095238095, 0.142857143, 0.19047619, 0.238095238, 0.285714286, 0.333333333, 0.380952381, 0.428571429, 0.476190476, 0.523809524, 0.571428571, 0.619047619, 0.666666667, 0.714285714, 0.761904762, 0.80952381, 0.857142857, 0.904761905, 0.952380952, 1};
		System.out.println(acgitPrjMonthly.length + " " + acgitPrjTime.length);
		assertEquals(acgitPrjMonthly.length, acgitPrjTime.length);
		
		TimeSeries ts = new TimeSeries("acgit", acgitPrjTime, acgitPrjMonthly);
		PhaseMerger phaseMerger = new PhaseMerger();
		
		//Phase 1: compute the transitions from the incoming data
		List<Transition> transitions = phaseMerger.computeOriginalTransitionsAndLabels(ts);
		assertEquals(21, transitions.size());

		//... and tell us what happened
		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",transitions);	
		Reporter.reportPhases("ORG. PHASES\n----------------------------",ts);	
		
		//TODO Phase 3: now, we need to further merge the resulting phases
		phaseMerger.iterativeMerging(ts);
		//... and tell us what happened
		Reporter.reportTransitions("\n ITERATIVE MERGE TRANSITIONS\n----------------------------",ts.getTransitions());
		Reporter.reportPhases("\n ITERATIVE MERGE PHASES\n----------------------------",ts);
		
	}//end happyPathAcgit


}
