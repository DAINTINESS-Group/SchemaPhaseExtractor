package segmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import commons.Phase;
import commons.Point;
import commons.TimeSeries;
import commons.Transition;


//in progress
public class PhaseMerger 
{
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

	
	public void iterativeMerging(TimeSeries timeseries) {
		System.out.println("/n~~~~~~~~~~~~~~~~~~~~iterativeMerging~~~~~~~~~~~~~~~~~~~~~~~");
		List<Transition> transitions = timeseries.getTransitions();
		List<Transition> newTransitions = new ArrayList<Transition>();
		int durationTransitions = transitions.size();
		
		Transition currentTransition = null;
		boolean continueIteration = true;
		int currentPos = 0;
		
		while(continueIteration) {
			
			List<Transition> candidateTransitionsForMerge = new ArrayList<Transition>();
			
			double minRunningCost = Double.MAX_VALUE;
			System.out.println("Transition size: "+ transitions.size()+ " currentPos: " +currentPos );
			
			while(currentPos<durationTransitions) {
				currentTransition = transitions.get(currentPos);
				
				Double postMergeConfigurationPrice = decidePhaseMerging(currentTransition);
				
				System.out.println("~~~ PostMergeConfigurationPrice = "+postMergeConfigurationPrice + "");
									//" - PostMergeConfigurationPriceNew = "+postMergeConfigurationPriceNew);
				
				if(postMergeConfigurationPrice!=Double.NaN && postMergeConfigurationPrice.doubleValue() < minRunningCost)
				{
					minRunningCost = postMergeConfigurationPrice;
					candidateTransitionsForMerge.add(currentTransition);
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
					currentTransition=mergeTransitions(currentTransition,candidateTransitionsForMerge.get(i), true);
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
	
	//are phases mergeable?
	private Double decidePhaseMerging(Transition currentTransition) 
	{
		//can they be merged
		if(!isTransitionMergeable(currentTransition)) 
		{
			System.out.println("Phase Merging: failed");
			return Double.NaN;
		}
		
		
		//+++calculate cost+++
		
		//Find new points first
		HashMap<Phase, ArrayList<Point>> newPoints = computeNewPointKeepingXStable(currentTransition);
		if(newPoints == null) 
		{
			System.out.println("New points: failed");
			return Double.NaN;
		}
		
		//calculate info Loss
		Double infoLoss = calculateInfoLoss(newPoints);
		if(infoLoss == Double.NaN) 
		{
			System.out.println("InfoLoss: failed");
			return Double.NaN;
		}
		
		//calculate cost
		return infoLoss;
		//is cost reasonable?
		
		
	}
	
	
	private boolean isTransitionMergeable(Transition currentTransition)
	{
		Phase firstPhaseFrom  = currentTransition.getFromPhase();
		Phase firstPhaseTo  = currentTransition.getToPhase();
		
		boolean isfirstPhaseMergeable = arePhasesMergeable(firstPhaseFrom,firstPhaseTo);
		
		return isfirstPhaseMergeable;
	}
	
	
	//are phases mergeable?
	private boolean arePhasesMergeable(Phase currentPhase, Phase nextPhase)
	{
		
		System.out.println("Current Phase: "+currentPhase.getLongDescription());
		System.out.println("Next Phase: "+nextPhase.getLongDescription());
		
		//bigger phases should attract smaller ones
		//arent smaller phases already small -> regarding NaN angle
		return currentPhase.isMergeable() && nextPhase.isMergeable();
	}
	
	
	//Default
	private HashMap<Phase, ArrayList<Point>> computeNewPointKeepingXStable(Transition currentTransition) 
	{
		HashMap<Phase, ArrayList<Point>> map = new HashMap<Phase, ArrayList<Point>>();
		
		Phase fromPhase  = currentTransition.getFromPhase();
		ArrayList<Point> newfromPhasePoints = computeNewPoints(fromPhase,0);
		map.put(fromPhase, newfromPhasePoints);
		
		Phase toPhase = currentTransition.getToPhase();
		ArrayList<Point> newToPhasePoints = computeNewPoints(toPhase,0);
		map.put(toPhase, newToPhasePoints);
		
		return map;
	}
	
	private ArrayList<Point> computeNewPoints(Phase Phase, int position) 
	{
		ArrayList<Point> newPhasePoints = new ArrayList<Point>();
		ArrayList<Point> firstPhasePoints = Phase.getMeasurementsAndTimePointsAsPoints();
		
		for(int i=0; i<firstPhasePoints.size();i++) 
		{
			Double newYPoint = Math.tan(firstPhasePoints.get(i).getX() - firstPhasePoints.get(position).getX()) + firstPhasePoints.get(position).getY();
			Point p = new Point(firstPhasePoints.get(i).getX(),newYPoint);
			newPhasePoints.add(p);
		}
		
		return newPhasePoints;
	}
	
	private Double calculateInfoLoss(HashMap<Phase, ArrayList<Point>> map)
	{
		double finalCost = 0;
		for(Phase p: map.keySet()) 
		{
			
			double sum = 0;
			ArrayList<Point> initialTimePoints = p.getMeasurementsAndTimePointsAsPoints();
			ArrayList<Point> newtimePoints = map.get(p);
			
			int timePointsSize = newtimePoints.size();
			if(timePointsSize == 0) 
			{
				return Double.NaN;
			}
			
			for(int i=0 ;i<timePointsSize; i++) 
			{
				double initialY = initialTimePoints.get(i).getY();
				double newY = newtimePoints.get(i).getY();
				sum += Math.pow((initialY - newY),2);
			}
			
			finalCost = finalCost + sum/(double)timePointsSize;
		}
		return finalCost;
	}
	
	//merging like mergePhasesAfterFirstLabeling
		private Transition mergeTransitions(Transition current, Transition next, boolean showMessages) 
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
