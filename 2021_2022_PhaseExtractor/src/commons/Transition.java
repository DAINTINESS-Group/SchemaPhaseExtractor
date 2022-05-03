package commons;

import java.util.ArrayList;

public class Transition {
	private Phase fromPhase;
	private Phase toPhase;
	private Double angleInDegrees;
	private AngleLabel angleLabel;
	private boolean mergeability;
	private Double infoLoss;
	
	public Transition() {
		this.fromPhase = null;
		this.toPhase = null;
		this.angleInDegrees = Double.NaN;
		angleLabel = AngleLabel.ERROR;
		this.infoLoss = 0.0;
	}
	
	public Transition(Phase from, Phase to) throws NullPointerException{
		if((from == null) || (to == null))
			throw new NullPointerException();
		this.fromPhase = from;
		this.toPhase = to;		
		this.angleInDegrees = computeAngleInDegrees(from, to);
		this.angleLabel = AngleLabel.labelAngle(this.angleInDegrees);
		this.infoLoss = 0.0;
	}

	/** Computes the angle of the transition in Degrees
	 * 
	 * @param from the Phase from which the transition departs
	 * @param to the Phase from which the transition ends
	 * @return the angle of the transition in Degrees 
	 */
	private Double computeAngleInDegrees(Phase from, Phase to) {
		Double deltaY = to.getYFirst() - from.getYLast();
		Double deltaX = to.getXFirst() - from.getXLast();
		if (deltaX < 1e-10 ) {
			System.err.println("Check your input, cannot have so small a Dx. Exit at point " +  " with Dx: " + deltaX + "\n From/To " + from.getXLast() +" " + to.getXFirst());
			System.exit(-1);
		}
		double derivative = deltaY / deltaX;
		return Math.toDegrees(Math.atan(derivative));
	}

	
	public Transition(Transition pTr) {
		this.fromPhase = pTr.fromPhase;
		this.toPhase = pTr.toPhase;
		this.angleInDegrees = pTr.angleInDegrees;
		angleLabel = pTr.angleLabel;
	}
	
	public Transition append(Transition pTr) {
		Transition newTr = new Transition(this);
		newTr.toPhase = pTr.toPhase;
		newTr.angleInDegrees = computeAngleInDegrees(newTr.fromPhase, newTr.toPhase);
		newTr.angleLabel = AngleLabel.labelAngle(newTr.angleInDegrees);
		
		return newTr;
	}
	/**
	 * @return the fromPhase
	 */
	public Phase getFromPhase() {
		return fromPhase;
	}

	/**
	 * @param fromPhase the fromPhase to set
	 */
	public void setFromPhase(Phase fromPhase) {
		this.fromPhase = fromPhase;
	}

	/**
	 * @return the toPhase
	 */
	public Phase getToPhase() {
		return toPhase;
	}

	/**
	 * @param toPhase the toPhase to set
	 */
	public void setToPhase(Phase toPhase) {
		this.toPhase = toPhase;
	}

	/**
	 * @return the angleInDegrees
	 */
	public Double getAngleInDegrees() {
		return angleInDegrees;
	}

	/**
	 * @param angleInDegrees the angleInDegrees to set
	 */
	public void setAngleInDegrees(Double angleInDegrees) {
		this.angleInDegrees = angleInDegrees;
	}

	/**
	 * @return the angleLabel
	 */
	public AngleLabel getAngleLabel() {
		return angleLabel;
	}

	/**
	 * @param angleLabel the angleLabel to set
	 */
	public void setAngleLabel(AngleLabel angleLabel) {
		this.angleLabel = angleLabel;
	}
	
	public boolean isTransitionMergeable()
	{
		Phase firstPhaseFrom  = this.getFromPhase();
		Phase firstPhaseTo  = this.getToPhase();
		
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
		
		//
		return currentPhase.isMergeable() && nextPhase.isMergeable();
	}
	
	public ArrayList<Phase> getPhasesList()
	{
		ArrayList<Phase> phases = new ArrayList<Phase>();
		phases.add(fromPhase);
		phases.add(toPhase);
		return phases;
	}
	
	public void setInfoLoss(double metric) 
	{
		this.infoLoss = metric;
	}
	
	public double getInfoLoss() 
	{
		return this.infoLoss;
	}
	
	public void setMergeability(boolean metric) 
	{
		this.mergeability = metric;
	}
	
	public boolean getMergeability() 
	{
		return this.mergeability;
	}
	
}//end class
