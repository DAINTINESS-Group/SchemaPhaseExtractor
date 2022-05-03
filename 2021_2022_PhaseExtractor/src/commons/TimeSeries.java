package commons;

import java.util.ArrayList;
import java.util.List;

public class TimeSeries {
	String name;
	private List<Phase> phases;
	private List<Phase> originalData;
	private List<Double> xPoints;
	private List<Double> yPoints;
	private List<Transition> transitions = null;
	private int duration;
	
	public TimeSeries(String pName, double[] xPoints, double[] yPoints) throws IllegalArgumentException{
		this.name = pName;	
		this.phases = new ArrayList<Phase>();
		this.originalData = new ArrayList<Phase>();
		this.xPoints = new ArrayList<Double>();
		this.yPoints = new ArrayList<Double>();
		
		if (xPoints.length != yPoints.length)
			throw new IllegalArgumentException();
		this.duration = xPoints.length;
		for (int i=0; i<this.duration; i++) {
			List<Double> pTimepointsList = new ArrayList<Double>(); pTimepointsList.add(xPoints[i]);
			List<Double> pMeasurementList = new ArrayList<Double>(); pMeasurementList.add(yPoints[i]);
			Phase ph = new Phase(pTimepointsList, pMeasurementList, i, i, duration);
			this.originalData.add(ph);
			//Do NOT add the same original to the phases list, which will change
			Phase phCopy = new Phase(pTimepointsList, pMeasurementList, i, i, duration);
			this.phases.add(phCopy);
			this.xPoints.add(xPoints[i]);
			this.yPoints.add(yPoints[i]);
		}

	}//end constructor
	
	public TimeSeries(String pName, List<Double> pXPoints, List<Double> pYPoints) throws IllegalArgumentException{
		this.name = pName;	
		this.phases = new ArrayList<Phase>();
		this.originalData = new ArrayList<Phase>();

		if (pXPoints.size() != pYPoints.size())
			throw new IllegalArgumentException();
		this.duration = pXPoints.size();
		for (int i=0; i<this.duration; i++) {
			Phase ph = new Phase(pXPoints.subList(i,i), pYPoints.subList(i,i), i, i, duration);
			this.originalData.add(ph);		
			//Do NOT add the same original to the phases list, which will change
			Phase phCopy = new Phase(pXPoints.subList(i,i), pYPoints.subList(i,i), i, i, duration);
			this.phases.add(phCopy);	
		}
		this.xPoints = pXPoints;
		this.yPoints = pYPoints;
	}//end constructor
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the originalData
	 */
	public List<Phase> getOriginalData() {
		return originalData;
	}

	/**
	 * @return the phases
	 */
	public List<Phase> getPhases() {
		return phases;
	}

	/**
	 * @param phases the phases to set
	 */
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}

	/**
	 * @return the xPoints
	 */
	public List<Double> getxPoints() {
		return xPoints;
	}

	/**
	 * @return the yPoints
	 */
	public List<Double> getyPoints() {
		return yPoints;
	}

	/**
	 * @return the transitions
	 */
	public List<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}
	
	public ArrayList<Point> getPoints() 
	{
		ArrayList<Point> pointList = new ArrayList<Point>();
		
		for(int i=0;i<this.xPoints.size();i++)
		{
			Point p = new Point(this.xPoints.get(i), this.yPoints.get(i));
			pointList.add(p);
		}
		
		return pointList;
	}

}//end class
