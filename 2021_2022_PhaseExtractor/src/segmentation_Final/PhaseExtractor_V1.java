package segmentation_Final;

import java.util.ArrayList;
import java.util.List;

import Reports.Reporter;
import commons.AngleLabel;
import commons.Phase;
import commons.TimeSeries;
import commons.Transition;

public class PhaseExtractor_V1 extends PhaseExtractor  
{

	public PhaseExtractor_V1(TimeSeries timeseries) 
	{
		super(timeseries);
	}
	
	@Override
	public void run() 
	{
		super.run();
		this.mergePhases();
		
		//... and tell us what happened
		Reporter.reportTransitions("\n V1 MERGE TRANSITIONS\n----------------------------",this.timeseries.getTransitions());
		Reporter.reportPhases("\n V1 MERGE PHASES\n----------------------------", this.timeseries);
	}
	
	@Override
	public boolean areTransitionsMergeable(Transition currentTransition,Transition nextTransition) 
	{
		try 
		{
			if(!currentTransition.getToPhase().isMergeableSimple()) 
			{
				return false;
			}
			
			if(currentTransition.getAngleLabel() == AngleLabel.STEEP ||
			   nextTransition.getAngleLabel() == AngleLabel.STEEP	)
		    {
				return false;
			}
			                                             
			return true;
		}
		catch(Exception ex) 
		{
			System.err.println("Error: " + ex.getMessage());
			return false;
		}
	}

}
