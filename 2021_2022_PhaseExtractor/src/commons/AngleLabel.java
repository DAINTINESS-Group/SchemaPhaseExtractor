package commons;

public enum AngleLabel{
	AT_V0, FLAT, LOW, REGULAR, STEEP, ERROR;
	
	/**
	 * All thresholds for angle labels in Degrees
	 */
	public static final double THRESHOLD_TO_ZERO = 1e-8;
	public static final double THRESHOLD_TO_LOW = 30;
	public static final double THRESHOLD_TO_REGULAR = 60;
	public static final double THRESHOLD_TO_STEEP = 90;

	public static AngleLabel labelAngle(double angleInDegrees) {
		if(angleInDegrees <= THRESHOLD_TO_ZERO )
			return AngleLabel.FLAT;
		else if(angleInDegrees <= THRESHOLD_TO_LOW)
			return AngleLabel.LOW;
		else if(angleInDegrees <= THRESHOLD_TO_REGULAR)
			return AngleLabel.REGULAR;
		else if(angleInDegrees <= THRESHOLD_TO_STEEP)
			return AngleLabel.STEEP;

		return AngleLabel.ERROR;
	}//end labelAngle()

}//end enum