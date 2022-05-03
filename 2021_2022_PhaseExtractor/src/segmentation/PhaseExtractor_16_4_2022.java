package segmentation;

import java.util.ArrayList;
import java.util.List;

import commons.AngleLabel;
import commons.Phase;
import commons.TimeSeries;
import commons.Transition;

public class PhaseExtractor_16_4_2022 {


	public List<Transition> computeOriginalTransitionsAndLabels(TimeSeries timeseries) {

		List<Transition> transitions = new ArrayList<Transition>(); 
		int	numOriginalData = timeseries.getOriginalData().size(); 
		for(int i=0;i<numOriginalData-1;i++) { 
			Transition tr = new Transition(timeseries.getOriginalData().get(i),
					timeseries.getOriginalData().get(i+1)); 
			transitions.add(tr); 
		}
		timeseries.setTransitions(transitions);
		return transitions;
	}


	public List<Transition> mergePhasesAfterFirstLabeling(TimeSeries timeseries) {	

		List<Transition> transitions = timeseries.getTransitions();
		List<Transition> newTransitions = new ArrayList<Transition>();
		int durationTransitions = transitions.size();

		int currentPos = 0;		
		boolean continueIteration=true;
		while(currentPos<=durationTransitions-1
			  && continueIteration) {
			
			Transition firstTr = transitions.get(currentPos);
			
			if(currentPos==durationTransitions-1)
			{
				newTransitions.add(firstTr);
				break;
			}
			
			int secondTrPos = currentPos+1;
			Transition secondTr = transitions.get(secondTrPos);
			while(firstTr.getAngleLabel() == secondTr.getAngleLabel()) 
			{
				firstTr = doMerge(firstTr, secondTr); 
				if(secondTrPos == durationTransitions-1)
				{
					continueIteration = false;
					break;
				}
				secondTrPos++;					
				secondTr = transitions.get(secondTrPos);
			}
			currentPos = secondTrPos;
			newTransitions.add(firstTr);			
		}
		
		timeseries = setNewTimeSeriesAndPrint(timeseries, newTransitions);
		return newTransitions;
	}
	
	private TimeSeries setNewTimeSeriesAndPrint(TimeSeries timeseries, List<Transition> newTransitions) 
	{
		List<Phase> newPhases = new ArrayList<Phase>();
		
		//add V0 solo
		newPhases.add(timeseries.getPhases().get(0));
		
		timeseries.setTransitions(newTransitions);
		
		System.out.println(" ~~~ NEW TIMESERIES ~~~ ");
		for(Transition tr: newTransitions) {
			
			Phase phFrom = tr.getFromPhase();
			Phase ph = tr.getToPhase();
			
			System.out.println(" ### " +"FROM PHASE: " +phFrom.getLongDescription() +"\n ### TO PHASE: "+ ph.getLongDescription());			
			newPhases.add(tr.getToPhase());
		}
		timeseries.setPhases(newPhases);
		
		return timeseries;
	}
	
	//merging like mergePhasesAfterFirstLabeling
	private Transition doMerge(Transition current, Transition next) 
	{
		Phase prevPhase =  current.getToPhase();
		Phase nextPhase = next.getToPhase();
		System.err.println("Merging " + current.getAngleLabel() + " with " + next.getAngleLabel());
		
		prevPhase = prevPhase.append(nextPhase);
		current.setToPhase(prevPhase);
		System.err.println("\t Prev: " + current.getToPhase().getStartPointInHostSeries() + " " + current.getToPhase().getEndPointInHostSeries());					
		
		return current;
	}
	
	public List<Transition> mergePhasesByNotSteep(TimeSeries timeseries) {	

		List<Transition> transitions = timeseries.getTransitions();
		List<Transition> newTransitions = new ArrayList<Transition>();
		int durationTransitions = transitions.size();
		
		int currentPos = 0;		
		boolean continueIteration=true;
		
		while(currentPos<=durationTransitions-1
			  && continueIteration) {
			
			Transition firstTr = transitions.get(currentPos);
			if(currentPos==durationTransitions-1)
			{
				newTransitions.add(firstTr);
				break;
			}
			
			int secondTrPos = currentPos+1;
			Transition secondTr = transitions.get(secondTrPos);
			while(areTransitionsMergeable(firstTr,secondTr)) 
			{
				firstTr = doMerge(firstTr, secondTr); 
				if(secondTrPos == durationTransitions-1)
				{
					continueIteration = false;
					break;
				}
				secondTrPos++;					
				secondTr = transitions.get(secondTrPos);
			}
			currentPos = secondTrPos;
			newTransitions.add(firstTr);			
			
		}
		
		timeseries = setNewTimeSeriesAndPrint(timeseries, newTransitions);
		return newTransitions;
	}
	
	public boolean areTransitionsMergeable(Transition currentTransition,Transition nextTransition) 
	{
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
	
}//end class
