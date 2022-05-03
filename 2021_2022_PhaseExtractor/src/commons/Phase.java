package commons;

import java.util.ArrayList;
import java.util.List;

public class Phase {
	private List<Double> timepointsList;
	private List<Double> measurementList;
	private List<Double> deltaList;
	private int startPointInHostSeries;
	private int endPointInHostSeries;
	private int duration;
	private int projectDuration;
	private Double internalAngleInDegrees;
	private ArrayList<Point> newMeasurements;

	
	public Phase() {
		this.timepointsList = new ArrayList<Double>();
		this.measurementList = new ArrayList<Double>();
		this.deltaList = new ArrayList<Double>();
		this.startPointInHostSeries = -1;
		this.endPointInHostSeries = -1;
		this.duration =-1;
		this.projectDuration =-1;
		this.internalAngleInDegrees = Double.NaN;
		this.newMeasurements = new ArrayList<Point>();
		
	}

	public Phase(List<Double> pTimepointsList, 
			List<Double> pMeasurementList, 
			int pStartPointInHostSeries, 
			int pEndPointInHostSeries,
			int projectDuration) {
		this.timepointsList = pTimepointsList;
		this.measurementList = pMeasurementList;
		this.startPointInHostSeries = pStartPointInHostSeries;
		this.endPointInHostSeries = pEndPointInHostSeries;
		this.duration = this.endPointInHostSeries - this.startPointInHostSeries + 1;
		this.projectDuration = projectDuration;
		this.deltaList = new ArrayList<Double>();
		this.newMeasurements = new ArrayList<Point>();
		if(duration == 1) {
			this.deltaList.add(Double.NaN);	
			this.internalAngleInDegrees = Double.NaN;
		}
		else { 
			for (int pos = 0; pos<this.duration; pos++) {
			 double delta = this.measurementList.get(pos) - this.measurementList.get(pos-1); 
			 this.deltaList.add(delta);
			 pos++;
			}
			this.internalAngleInDegrees = Math.toDegrees(Math.atan(
					(this.measurementList.get(duration-1) - this.measurementList.get(0)) / (this.timepointsList.get(duration-1) - this.timepointsList.get(0))) 
					); 
		}
	}
	
	public Phase(Phase pPhase) {	
		this.timepointsList = new ArrayList<Double>();
		this.timepointsList.addAll(pPhase.getTimepointsList());
		
		this.measurementList = new ArrayList<Double>();
		this.measurementList.addAll(pPhase.getMeasurementList());
		
		this.deltaList = new ArrayList<Double>();
		this.deltaList.addAll(pPhase.getDeltaList());
		
		this.startPointInHostSeries = pPhase.startPointInHostSeries;
		this.endPointInHostSeries = pPhase.endPointInHostSeries;
		this.duration = pPhase.duration;
		this.internalAngleInDegrees = pPhase.internalAngleInDegrees;
		
		this.newMeasurements = pPhase.getNewMeasurementsAndTimePoints();
	}


	public Phase append(Phase pPhase) {
		Phase newPhase = new Phase(this);
		
		newPhase.timepointsList.addAll(pPhase.getTimepointsList());
		newPhase.measurementList.addAll(pPhase.getMeasurementList());

		Double d = pPhase.getYFirst() - this.getYLast();
		newPhase.deltaList.add(d);
		newPhase.deltaList.addAll(pPhase.getDeltaList());
		
		newPhase.startPointInHostSeries = this.startPointInHostSeries;
		newPhase.endPointInHostSeries = pPhase.endPointInHostSeries;
		newPhase.duration = newPhase.endPointInHostSeries - newPhase.startPointInHostSeries + 1;

		newPhase.internalAngleInDegrees = Math.toDegrees(Math.atan(
				(newPhase.measurementList.get(newPhase.duration-1) - newPhase.measurementList.get(0)) / (newPhase.timepointsList.get(newPhase.duration-1) - newPhase.timepointsList.get(0))) 
				); 
		return newPhase;
	}
	
	
	/**
	 * @return the timepointsList
	 */
	public List<Double> getTimepointsList() {
		return timepointsList;
	}

	/**
	 * @param timepointsList the timepointsList to set
	 */
	public void setTimepointsList(List<Double> timepointsList) {
		this.timepointsList = timepointsList;
	}

	/**
	 * @return the measurementList
	 */
	public List<Double> getMeasurementList() {
		return measurementList;
	}

	/**
	 * @param measurementList the measurementList to set
	 */
	public void setMeasurementList(List<Double> measurementList) {
		this.measurementList = measurementList;
	}

	/**
	 * @return the deltaList
	 */
	public List<Double> getDeltaList() {
		return deltaList;
	}

	/**
	 * @param deltaList the deltaList to set
	 */
	public void setDeltaList(List<Double> deltaList) {
		this.deltaList = deltaList;
	}

	/**
	 * @return the startPointInHostSeries
	 */
	public int getStartPointInHostSeries() {
		return startPointInHostSeries;
	}

	/**
	 * @param startPointInHostSeries the startPointInHostSeries to set
	 */
	public void setStartPointInHostSeries(int startPointInHostSeries) {
		this.startPointInHostSeries = startPointInHostSeries;
	}

	/**
	 * @return the endPointInHostSeries
	 */
	public int getEndPointInHostSeries() {
		return endPointInHostSeries;
	}

	/**
	 * @param endPointInHostSeries the endPointInHostSeries to set
	 */
	public void setEndPointInHostSeries(int endPointInHostSeries) {
		this.endPointInHostSeries = endPointInHostSeries;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the internalAngleInDegrees
	 */
	public Double getInternalAngleInDegrees() {
		return internalAngleInDegrees;
	}

	/**
	 * @param internalAngleInDegrees the internalAngleInDegrees to set
	 */
	public void setInternalAngleInDegrees(Double angleInDegrees) {
		this.internalAngleInDegrees = angleInDegrees;
	}

	// Useful stuff ////////////////////////////////////////
	public Double getYLast() {
		return measurementList.get(duration-1);
	}

	public Double getXLast() {
		return timepointsList.get(duration-1);
	}

	public Double getYFirst() {
		return measurementList.get(0);
	}

	public Double getXFirst() {
		return timepointsList.get(0);
	}
	

	public String getLongDescription() {
		String result = "From " + startPointInHostSeries + " (" + String.format("%.2f",timepointsList.get(0)) + ","  + String.format("%.2f",measurementList.get(0)) + ") to " 
				+ endPointInHostSeries + " (" + String.format("%.2f",timepointsList.get(duration-1)) + ","  + String.format("%.2f",measurementList.get(duration-1)) 
				+ ")\t(dur: " + duration +") " + "\tangle: " + String.format("%.2f",internalAngleInDegrees);
		return result;
	}

	/**
	 * Returns true if the series is not steep and it does not contain the same label for a long period.
	 * 
	 * Long period is 6 months or more, or, for short-lived projects, 30% of their duration.   
	 * @return
	 */
	public boolean isMergeable() {
		
		//System.out.println("Label: "+this.getInternalLabel());
		if (this.getInternalLabel() == AngleLabel.STEEP)
			return false;
		
		//let single point phases pass
		if (this.duration==1)
			return true;
		
		//System.out.println("duration: " + duration);
		//System.out.println("math: " + Long.min(5, Math.round(0.3 * duration)));
		
		//ideally: know that all the series of tiny phases included here are all the same. but anyway...
		if(this.duration > Long.min(5, Math.round(0.3 * this.projectDuration)))
			return false;
		
		return true;
	}
	
	public boolean isMergeableSimple() {
		
		//System.out.println("Label: "+this.getInternalLabel());
		return this.getInternalLabel() != AngleLabel.STEEP;
	}
	
	private AngleLabel getInternalLabel() {
		return AngleLabel.labelAngle(this.internalAngleInDegrees);
	}
	
	
	
	public ArrayList<Point> getMeasurementsAndTimePointsAsPoints() 
	{
		ArrayList<Point> pointList = new ArrayList<Point>();
		
		for(int i=0;i<this.timepointsList.size();i++)
		{
			Point p = new Point(this.timepointsList.get(i), this.measurementList.get(i));
			pointList.add(p);
		}
		
		return pointList;
	}
	
	
	public void setNewMeasurementsAndTimePoints(ArrayList<Point> newMeasurements) 
	{
		this.newMeasurements = newMeasurements;
	}
	
	public ArrayList<Point> getNewMeasurementsAndTimePoints() 
	{
		return this.newMeasurements;
	}
}//end class
