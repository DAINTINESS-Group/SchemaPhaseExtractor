package segmentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import commons.Phase;
import commons.Point;
import commons.TimeSeries;
import commons.Transition;

//big algo
public class PhaseExtractor {


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
//		timeseries = iterativeMerging(timeseries);
//		if(true) {
//			return;
//		}
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
				//boolean isMergeOk = areTransitionsMergeable(currentTransition,nextTransition);
				Double postMergeConfigurationPrice = decideTransitionMergingDefault(currentTransition,nextTransition,durationTransitions,minRunningCost);
				//Double postMergeConfigurationPriceNew = decideTransitionMergingNew(currentTransition,nextTransition,durationTransitions,minRunningCost);
				
				System.out.println("~~~ PostMergeConfigurationPrice = "+postMergeConfigurationPrice + "");
									//" - PostMergeConfigurationPriceNew = "+postMergeConfigurationPriceNew);
				
				if(postMergeConfigurationPrice!=Double.NaN && postMergeConfigurationPrice.doubleValue() < minRunningCost)
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
	
	private Double decideTransitionMergingDefault(Transition currentTransition, Transition nextTransition,
			                                      double durationTransitions,double minRunningCost) 
	{
		//can they be merged
		if(!areTransitionsMergeable(currentTransition, nextTransition)) 
		{
			System.out.println("Merging: failed");
			return Double.NaN;
		}
		
		
		//+++calculate cost+++
		
		//Find new points first
		HashMap<Phase, ArrayList<Point>> newPoints = computeNewPointKeepingXStable(currentTransition, nextTransition);
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
	
	private Double decideTransitionMergingNew(Transition currentTransition, Transition nextTransition,
			                                      double durationTransitions,double minRunningCost) 
	{
		//can they be merged
		if(!areTransitionsMergeable(currentTransition, nextTransition)) 
		{
			return Double.NaN;
		}
		
		//calculate cost
		//Find new points first
		HashMap<Phase, ArrayList<Point>> newPoints = computeNewPointAsProjection(currentTransition, nextTransition);
		if(newPoints == null) 
		{
			return Double.NaN;
		}
		//figure line between the points

		//calculate info Loss
		Double infoLoss = calculateInfoLoss(newPoints);
		if(infoLoss == Double.NaN) 
		{
			return Double.NaN;
		}
		
		//calculate cost
		return infoLoss;
		//is cost reasonable?
	}
	
	private boolean areTransitionsMergeable(Transition currentTransition, Transition nextTransition)
	{
		Phase firstPhaseFrom  = currentTransition.getFromPhase();
		Phase firstPhaseTo  = currentTransition.getToPhase();
		Phase finalPhaseFrom = nextTransition.getFromPhase();
		Phase finalPhaseTo = nextTransition.getToPhase();
		
		boolean isfirstPhaseMergeable = arePhasesMergeable(firstPhaseFrom,firstPhaseTo);
		
		
		boolean isfinalPhaseMergeable = arePhasesMergeable(finalPhaseFrom,finalPhaseTo);
		
		
		return isfirstPhaseMergeable || isfinalPhaseMergeable;
	}
	
	
	//are phases mergeable?
	private boolean arePhasesMergeable(Phase currentPhase, Phase nextPhase)
	{
		
		System.out.println("Current Phase: "+currentPhase.getLongDescription());
		System.out.println("Next Phase: "+nextPhase.getLongDescription());
		
		//bigger phases should attract smaller ones
		//arent smaller phases already small -> regarding NaN angle
		
		//
		return currentPhase.isMergeable() && nextPhase.isMergeable();
	}
	
	
	//Default
	private HashMap<Phase, ArrayList<Point>> computeNewPointKeepingXStable(Transition currentTransition, Transition nextTransition) 
	{
		HashMap<Phase, ArrayList<Point>> map = new HashMap<Phase, ArrayList<Point>>();
		
		Phase firstPhase  = currentTransition.getFromPhase();
		ArrayList<Point> newfirstPhasePoints = computeNewPoints(firstPhase,0);
		map.put(firstPhase, newfirstPhasePoints);
		
		Phase finalPhase = nextTransition.getToPhase();
		ArrayList<Point> newfinalPhasePoints = computeNewPoints(finalPhase,finalPhase.getMeasurementsAndTimePointsAsPoints().size()-1);
		map.put(finalPhase, newfinalPhasePoints);
		
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
	
	
	//New
	private HashMap<Phase, ArrayList<Point>> computeNewPointAsProjection(Transition currentTransition, Transition nextTransition) 
	{
		HashMap<Phase, ArrayList<Point>> map = new HashMap<Phase, ArrayList<Point>>();
		
		//find line (e) from firstPhase A to FinalPhase B
		Phase firstPhase  = currentTransition.getFromPhase();
		Phase finalPhase = nextTransition.getToPhase();
		
		Phase connectingPhase = nextTransition.getFromPhase(); // or currentTransition.getToPhase();
		
		Point A = new Point(firstPhase.getXFirst(),firstPhase.getYFirst());
		Point B = new Point(finalPhase.getXLast(),firstPhase.getYLast());
		
		Point C = new Point(connectingPhase.getXFirst(),connectingPhase.getYFirst());
		
		double y = B.getY() - A.getY(); //yb-ya
		double x = B.getX() - A.getX(); //xb-xa
		
		if(x==0 || y==0) 
		{
			return null;
		}
		
		double lamdaE = y/x;
		//y=lamdaE*(x-x1) - y1
		
		//find projections of every inner phase to the line (e)
		//to find projections
		//find line (z)
		//find new point of Phase
		
		double lamdaZ = -1/lamdaE;
		//y=lamdaZ*(x-x1) + y1
		
		//point C belongs to the line Z
		//yCnew = lamdaZ*(xCnew - C.getX()) + C.getY() 
		
		//get line E
		//yCnew = lamdaE*(xCnew - A.getX()) - A.getY() 
		
		Double xCnew;
		Double yCnew;
		//Solve for xCnew
		//lamdaZ*(xCnew - C.getX()) + C.getY() = lamdaE*(xCnew - A.getX()) - A.getY()
		//lamdaZ*xCnew - lamdaZ*C.getX() + C.getY() = lamdaE*xCnew - lamdaE*A.getX() - A.getY()
		//lamdaZ*xCnew - lamdaE*xCnew = lamdaZ*C.getX() - C.getY() - lamdaE*A.getX() - A.getY()
		//xCnew*(lamdaZ-lamdaE) = lamdaZ*C.getX() - C.getY() - lamdaE*A.getX() - A.getY()
		
		if(lamdaZ-lamdaE==0) 
		{
			return null;
		}
		
		//xCnew  = (lamdaZ*C.getX() - C.getY() - lamdaE*A.getX() - A.getY()) / (lamdaZ-lamdaE)
		xCnew = (lamdaZ*C.getX() - C.getY() - lamdaE*A.getX() - A.getY()) / (lamdaZ-lamdaE);
		
		//yCnew = lamdaE*(xCnew - A.getX()) - A.getY()
		yCnew = lamdaE*(xCnew - A.getX()) - A.getY();
		
		//We got the new C'
		Point Cnew = new Point(xCnew, yCnew);
		
		ArrayList<Point> newFirstPhasePoints = computeProjectedPoints(firstPhase, A, lamdaE, lamdaZ);
		map.put(firstPhase, newFirstPhasePoints);
		ArrayList<Point> newFinalPhasePoints = computeProjectedPoints(finalPhase, C, lamdaE, lamdaZ);
		map.put(finalPhase, newFinalPhasePoints);
		
		return map;
		
	}
	
	private ArrayList<Point> computeProjectedPoints(Phase Phase, Point A, double lamdaE, double lamdaZ) 
	{
		ArrayList<Point> newPhasePoints = new ArrayList<Point>();
		ArrayList<Point> firstPhasePoints = Phase.getMeasurementsAndTimePointsAsPoints();
		
		for(int i=1; i<firstPhasePoints.size();i++) 
		{
			Point C = firstPhasePoints.get(i);
			//xCnew  = (lamdaZ*C.getX() - C.getY() - lamdaE*A.getX() - A.getY()) / (lamdaZ-lamdaE)
			Double xCnew = (lamdaZ*C.getX() - C.getY() - lamdaE*A.getX() - A.getY()) / (lamdaZ-lamdaE);
			
			//yCnew = lamdaE*(xCnew - A.getX()) - A.getY()
			Double yCnew = lamdaE*(xCnew - A.getX()) - A.getY();
			
			Point p = new Point(xCnew,yCnew);
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
	
	
//	private double computeCostFromMerge(TimeSeries timeseries, List<Transition> transitions,
//			Transition currentTransition, int originalVocabularyPrice) 
//	{
//		double currentSizeCost = (originalVocabularyPrice - transitions.size()) / (1.0 * originalVocabularyPrice); //TODO normalize
//		double newSizeCost = (originalVocabularyPrice - transitions.size() - 1) / (1.0 * originalVocabularyPrice); 
//		double  currentInfoLossCost = 0; 
//		
//		return 0;
//	}
	
	
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

	


}//end class
