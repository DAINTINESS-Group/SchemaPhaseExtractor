package segmentation_Final;

import java.util.ArrayList;
import java.util.List;

import Reports.Reporter;
import commons.Phase;
import commons.TimeSeries;
import commons.Transition;

public abstract class PhaseExtractor 
{
	protected TimeSeries timeseries;
	
	PhaseExtractor(TimeSeries timeseries)
	{
		this.timeseries=timeseries;
	}
	
	public void run() 
	{
		this.computeOriginalTransitionsAndLabels();
		//... and tell us what happened
		Reporter.reportTransitions("ORG. TRANSITIONS\n----------------------------",this.timeseries.getTransitions());	
		Reporter.reportPhases("ORG. PHASES\n----------------------------", this.timeseries);
		
		this.mergePhasesAfterFirstLabeling();
		//... and tell us what happened
		Reporter.reportTransitions("\n V0 MERGE TRANSITIONS\n----------------------------",this.timeseries.getTransitions());
		Reporter.reportPhases("\n V0 MERGE PHASES\n----------------------------", this.timeseries);
	}
	
	public TimeSeries getNewTimeseries() 
	{
		return timeseries;
	}
	
	public void computeOriginalTransitionsAndLabels() 
	{
		List<Transition> transitions = new ArrayList<Transition>(); 
		int	numOriginalData = timeseries.getOriginalData().size(); 
		for(int i=0;i<numOriginalData-1;i++) { 
			Transition tr = new Transition(timeseries.getOriginalData().get(i),
					timeseries.getOriginalData().get(i+1)); 
			transitions.add(tr); 
		}
		timeseries.setTransitions(transitions);
	}
	
	public void mergePhasesAfterFirstLabeling() {	

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
		
		setNewTimeSeriesAndPrint(newTransitions);
		
	}
	
	protected void setNewTimeSeriesAndPrint(List<Transition> newTransitions) 
	{
		List<Phase> newPhases = new ArrayList<Phase>();
		timeseries.setTransitions(newTransitions);
		
		System.out.println(" ~~~ NEW TIMESERIES ~~~ ");
		for(Transition tr: newTransitions) {
			Phase ph = tr.getToPhase();
			System.out.println(" ### " + ph.getLongDescription());			
			newPhases.add(tr.getToPhase());
		}
		timeseries.setPhases(newPhases);
		return;
	}
	
	//merging like mergePhasesAfterFirstLabeling
	protected Transition doMerge(Transition current, Transition next) 
	{
		Phase prevPhase =  current.getToPhase();
		Phase nextPhase = next.getToPhase();
		System.err.println("Merging " + current.getAngleLabel() + " with " + next.getAngleLabel());
		
		prevPhase = prevPhase.append(nextPhase);
		current.setToPhase(prevPhase);
		System.err.println("\t Prev: " + current.getToPhase().getStartPointInHostSeries() + " " + current.getToPhase().getEndPointInHostSeries());					
		
		return current;
	}
	
	public void mergePhases() 
	{	
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
		setNewTimeSeriesAndPrint(newTransitions);
	}
	
	protected abstract boolean areTransitionsMergeable(Transition currentTransition,Transition nextTransition);
}
