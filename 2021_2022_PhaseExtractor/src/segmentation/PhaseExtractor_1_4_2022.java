package segmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import commons.Phase;
import commons.Point;
import commons.TimeSeries;
import commons.Transition;

public class PhaseExtractor_1_4_2022 
{

	//big algo v2
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
	}//end labelAllMeasurements()


	public void mergePhasesAfterFirstLabeling(TimeSeries timeseries) {	

		List<Phase> newPhases = new ArrayList<Phase>();
		List<Transition> transitions = timeseries.getTransitions();
		List<Transition> newTransitions = new ArrayList<Transition>();
		int durationTransitions = transitions.size();
		

		//add V0 solo
		newPhases.add(timeseries.getPhases().get(0));
		
		int currentPos = 0;		
		while(currentPos<durationTransitions-1) {
			
			Transition firstTr = transitions.get(currentPos);
			int secondTrPos = currentPos+1;
			Transition secondTr = transitions.get(secondTrPos);
			
			while(firstTr.getAngleLabel() == secondTr.getAngleLabel() ) {
					Phase prevPhase =  firstTr.getToPhase();
					Phase nextPhase = secondTr.getToPhase();
System.err.println("Merging " + firstTr.getAngleLabel() + " with " + secondTr.getAngleLabel());

					prevPhase = prevPhase.append(nextPhase);
					firstTr.setToPhase(prevPhase);
System.err.println("\t Prev: " + firstTr.getToPhase().getStartPointInHostSeries() + " " + firstTr.getToPhase().getEndPointInHostSeries());					
					secondTrPos++;					
					secondTr = transitions.get(secondTrPos);
			}//while

			//newPhases.add(firstTr.getToPhase());
			newTransitions.add(firstTr);			
			currentPos = secondTrPos;
		}//while
		
		timeseries.setTransitions(newTransitions);
		
		for(Transition tr: newTransitions) {
			Phase ph = tr.getToPhase();
System.out.println(" ### " + ph.getLongDescription());			
			newPhases.add(tr.getToPhase());
		}
		timeseries.setPhases(newPhases);
	}//end mergePhasesAfterFirstLabeling()


	
	
//TODO: fix the next algo!	
	public void iterativeMerging(TimeSeries timeseries) {
		System.out.println("/n~~~~~~~~~~~~~~~~~~~~iterativeMerging~~~~~~~~~~~~~~~~~~~~~~~");
		List<Transition> transitions = timeseries.getTransitions();
		List<Transition> newTransitions = new ArrayList<Transition>();
		int durationTransitions = transitions.size();
		//int currentConfigurationPrice = durationTransitions+1;
		//int currentVocabularySize = currentConfigurationPrice ;
		
		Transition currentTransition = null;
		Transition nextTransition = null;
		boolean continueIteration = true;
		int currentPos = 0;
		
		while(continueIteration) {
			
			List<Transition> candidateTransitionsForMerge = new ArrayList<Transition>();
			
			double minRunningCost = Double.MAX_VALUE;
			System.out.println("Transition size: "+ transitions.size()+ " currentPos: " +currentPos );
			currentTransition = transitions.get(currentPos);
			candidateTransitionsForMerge.add(currentTransition);
			currentPos++;
			
			while(currentPos<durationTransitions) {
				
				nextTransition = transitions.get(currentPos);
				
				boolean allOk = decideTransitionMergingDefault(currentTransition,nextTransition);
				if(!allOk) 
				{
					return;
				}
				double postMergeConfigurationPrice = currentTransition.getInfoLoss()+nextTransition.getInfoLoss();
				System.out.println("~~~ PostMergeConfigurationPrice = "+postMergeConfigurationPrice + "");
									//" - PostMergeConfigurationPriceNew = "+postMergeConfigurationPriceNew);
				
				if(postMergeConfigurationPrice!=Double.NaN && postMergeConfigurationPrice < minRunningCost)
				{
					minRunningCost = postMergeConfigurationPrice;
					candidateTransitionsForMerge.add(nextTransition);
					currentPos++;
				}
				else 
				{
					break;
				}
				
			}//inner while
			
			//when done, two things happen: either we can merge, or we should stop
			if (transitions.size() <= 2 || currentPos>=transitions.size())
			{
				continueIteration = false;
			}
			if(candidateTransitionsForMerge.size()>=2) 
			{
				
				//merge first with final?
				//currentTransition=doMerge(candidateTransitionsForMerge.get(0),candidateTransitionsForMerge.get(candidateTransitionsForMerge.size()-1), true);
				
				//merge 1st with second, then the merged with 3rd etc
				
				for(int i =1 ;i<candidateTransitionsForMerge.size();i++) 
				{
					currentTransition=doMerge(currentTransition,candidateTransitionsForMerge.get(i), true);
				}
			}
			newTransitions.add(currentTransition);
		}//outer while
		timeseries = setNewTimeSeriesAndPrint(timeseries, newTransitions);
	}//method

	
	private TimeSeries setNewTimeSeriesAndPrint(TimeSeries timeseries, List<Transition> newTransitions) 
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
		return timeseries;
	}
	
	//are transitions mergeable?
	
	private boolean decideTransitionMergingDefault(Transition currentTransition,Transition nextTransition) 
	{
		//can they be merged
		if(!currentTransition.isTransitionMergeable()) 
		{
			System.out.println("Current Transition not mergeable");
			return false;
		}
		
		//+++calculate cost+++
		//Find new points for current Transition
		boolean allOK = computeNewPointKeepingXStable(currentTransition);
		if(!allOK) 
		{
			System.out.println("Failure measuring new points of current Transition");
			return false;
		}
		
		//Find new points for next Transition
		if(nextTransition.isTransitionMergeable())
		{
			allOK = computeNewPointKeepingXStable(nextTransition);
		
			if(!allOK) 
			{
				System.out.println("Failure measuring new points of next Transition");
				return false;
			}
		}
		
		//calculate info Loss
		allOK = calculateInfoLoss(currentTransition);
		if(!allOK)
		{
			System.out.println("Failure calculating infoLoss");
			return false;
		}
		
		//calculate info Loss
		allOK = calculateInfoLoss(nextTransition);
		if(!allOK)
		{
			System.out.println("Failure calculating infoLoss");
			return false;
		}
		
		//calculate cost
		return true;
		//is cost reasonable?
		
	}
	
	
	//Default
	private boolean computeNewPointKeepingXStable(Transition currentTransition) 
	{
		Phase currentFromPhase  = currentTransition.getFromPhase();
		ArrayList<Point> newcurrentFromPhasePoints = computeNewPoints(currentFromPhase);
		
		
		Phase currentToPhase  = currentTransition.getToPhase();
		ArrayList<Point> newcurrentToPhasePoints = computeNewPoints(currentToPhase);
		
		if(newcurrentFromPhasePoints == null ||
		   newcurrentFromPhasePoints.size()==0 ||
		   newcurrentToPhasePoints== null ||
		   newcurrentToPhasePoints.size()==0) 
		{
			return false;
		}
		
		currentToPhase.setNewMeasurementsAndTimePoints(newcurrentToPhasePoints);
		currentFromPhase.setNewMeasurementsAndTimePoints(newcurrentFromPhasePoints);
		return true;
	}
	
	
	private ArrayList<Point> computeNewPoints(Phase Phase) 
	{
		ArrayList<Point> newPhasePoints = new ArrayList<Point>();
		ArrayList<Point> firstPhasePoints = Phase.getMeasurementsAndTimePointsAsPoints();
		int finalPosition = firstPhasePoints.size()-1;
		for(int i=0; i<=finalPosition;i++) 
		{
			Double newYPoint = Math.tan(firstPhasePoints.get(i).getX() - firstPhasePoints.get(finalPosition).getX()) + firstPhasePoints.get(finalPosition).getY();
			Point p = new Point(firstPhasePoints.get(i).getX(),newYPoint);
			newPhasePoints.add(p);
		}
		
		return newPhasePoints;
	}
	
	
	private boolean calculateInfoLoss(Transition currentTransition)
	{
		ArrayList<Phase> phases = currentTransition.getPhasesList();
		double finalCost = 0;
		for(Phase p: phases) 
		{
			double sum = 0;
			ArrayList<Point> initialTimePoints = p.getMeasurementsAndTimePointsAsPoints();
			ArrayList<Point> newtimePoints = p.getNewMeasurementsAndTimePoints();
			
			int timePointsSize = newtimePoints.size();
			if(timePointsSize == 0) 
			{
				return false;
			}
			
			for(int i=0 ;i<timePointsSize; i++) 
			{
				double initialY = initialTimePoints.get(i).getY();
				double newY = newtimePoints.get(i).getY();
				sum += Math.pow((initialY - newY),2);
			}
			
			finalCost = finalCost + sum/(double)timePointsSize;
		}
		currentTransition.setInfoLoss(finalCost);
		return true;
	}
	
	

	
	//merging like mergePhasesAfterFirstLabeling
	private Transition doMerge(Transition current, Transition next, boolean showMessages) 
	{
		
		Phase prevPhase =  current.getToPhase();
		Phase nextPhase = next.getToPhase();
		if(showMessages) 
		{
			System.out.println("DO MERGE");
			System.err.println("Merging " + current.getAngleLabel() + " with " + next.getAngleLabel());
		}
		

		prevPhase = prevPhase.append(nextPhase);
		current.setToPhase(prevPhase);
		if(showMessages) 
		{
			System.err.println("\t Prev: " + current.getToPhase().getStartPointInHostSeries() + " " + current.getToPhase().getEndPointInHostSeries());					
		}
		
		return current;
	}

	
}
