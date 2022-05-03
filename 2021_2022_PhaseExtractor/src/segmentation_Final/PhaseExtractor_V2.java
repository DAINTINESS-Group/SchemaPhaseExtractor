package segmentation_Final;

import Reports.Reporter;
import commons.AngleLabel;
import commons.TimeSeries;
import commons.Transition;

public class PhaseExtractor_V2 extends PhaseExtractor  
{

	PhaseExtractor_V2(TimeSeries timeseries) 
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
	protected boolean areTransitionsMergeable(Transition currentTransition, Transition nextTransition) {
		try 
		{
			if(!currentTransition.getToPhase().isMergeable()) 
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
