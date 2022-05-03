package commons;

public class Point 
{
	
	private Double x;
	private Double y;
	
	public Point(Double x, Double y)
	{
		this.x=x;
		this.y=y;
	}
	
	public double getX() 
	{
		return this.x.doubleValue();
	}
	
	public double getY() 
	{
		return this.y.doubleValue();
	}
}
