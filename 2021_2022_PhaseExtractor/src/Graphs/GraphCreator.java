package Graphs;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import commons.Point;
import commons.TimeSeries;

public class GraphCreator extends JPanel
{
	private static final long serialVersionUID = 1L;
	private ArrayList<Point> points;
	private XYDataset dataSet;
	
	public void createGraphFromTimeseries(TimeSeries timeSeries) 
	{
		this.points = timeSeries.getPoints();
		createDataset();
		addGraphToPanel();
	}
	
	private void createDataset() 
	{
		this.dataSet = new XYSeriesCollection();
		XYSeries series = new XYSeries("series");
		for(int i=0;i<points.size();i++) 
		{
			series.add(points.get(i).getX(), points.get(i).getY());
		}
		
		((XYSeriesCollection) dataSet).addSeries(series);
	}

	private void addGraphToPanel() 
	{
		JFreeChart graph = ChartFactory.createXYLineChart("title", "x", "y", dataSet,PlotOrientation.VERTICAL, true, true, false);
		ChartPanel chartPanel = new ChartPanel(graph);
		this.add(chartPanel);
	}
}
