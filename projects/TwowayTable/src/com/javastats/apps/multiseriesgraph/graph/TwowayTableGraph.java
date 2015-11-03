package com.javastats.apps.multiseriesgraph.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.javastats.apps.multiseriesgraph.data.Dataset;
import com.vaadin.data.Item;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.ChartType;

/**
 *
 */
public class TwowayTableGraph  
{

	private String  title;
	private String  xaxis;
	private String  yaxis;
	private Set<String> types;
	private Set<String> themes;

	private ChartTheme myTheme;
	private ChartType  myChartType;
	
	// what are the variables that we are subselecting on?
	private String selectionVariable;
	private String splitVariable;
	
	private String imgSize;
	
	private Dataset twoWayTable;
	private List<String> filenames;

	public TwowayTableGraph()
	{
		// Default settings
		this.twoWayTable = new Dataset();
		this.myTheme = ChartTheme.GGPlot2;
		this.myChartType = ChartType.Bar;
		this.xaxis = "X-Axis";
		this.yaxis = "Y-Axis";
		this.title = "Title";
		this.filenames = new Vector<String>();
	}

	public Set<String> getTypes() {
		return types;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	public void setImageSize(String size)
	{
		this.imgSize = size;
	}
	
	public String getImageSize()
	{
		return this.imgSize;
	}
	
	private void removeOldImages()
	{
		if ( this.filenames.size() > 0 )
		{
			for ( String filename : this.filenames )
			{
				File f = new File(filename);
				if ( f.exists() == true )
				{
					f.delete();
				}
			}
		}
		
		// clear filenames
		this.filenames = new Vector<String>();
	}
	
	public Image getImage()
	{
		try 
		{
			// keep image set clean
			this.removeOldImages();
			
			// Find the application directory
			String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

			// create a file name
			int minimum = 1234567;
			int maximum = 9876543;
			int randomNum = minimum + (int)(Math.random()*maximum); 
			String filename = basepath + "/WEB-INF/images/generated/" + randomNum + ".png";
			this.filenames.add(filename);
			
			// Save it
			Chart chart = this.getChart();
			BitmapEncoder.saveBitmap(chart, filename, BitmapFormat.PNG);
			
			// Image as a file resource
			FileResource resource = new FileResource(new File(filename));
			
			// Show the image in the application
			return new Image(this.getTitle(), resource);
			
	  	} catch ( Exception e ) {
	  		System.out.println("Error creating image: " + e.toString());
	  		return new Image("None", null);
	  	}
	          
	}

	
	public void removeData()
	{
		this.twoWayTable = new Dataset();
		this.xaxis = "X-Axis";
		this.yaxis = "Y-Axis";
		this.title = "Title";
	}
	
	public String getCSVTable()
	{
		return this.twoWayTable.getCSVOutput();
	}
	
	public String getHTMLTable()
	{
		return this.twoWayTable.getHTMLTable();
	}
	
	/**
	 * Set the data
	 * @param dataTable
	 */
	public void setData(Table dataTable)
	{
		try {
			System.out.println("Set Data called!");
			
			if ( dataTable != null )
			{
				// Reset the data for the series
				this.twoWayTable = new Dataset();

				if ( this.getSelectionVariable().trim().length() > 0 )
				{
					if ( this.getSplitVariable().trim().length() > 0 )
					{
						// Add the rows to our data
						for ( int i = 0; i < dataTable.size(); i++)
						{

							// get a row in the data table
							Item row = dataTable.getItem(i);
							if ( row != null )
							{
								Object xvar = row.getItemProperty( this.getSelectionVariable() ).getValue().toString();
								Object yvar = row.getItemProperty( this.getSplitVariable() ).getValue().toString();
								this.twoWayTable.addData(xvar, yvar);
							}
						}

					}
				}
				
			} else {
				System.out.println("Data table was null!");
			}
			
		} catch ( Exception e ) {
			System.out.println("Error setting data: " + e.toString());
		}
	}
	
	
	private void addData(String selectVariable, String splitVariable)
	{
		if ( this.twoWayTable != null )
			this.twoWayTable.addData(selectVariable, splitVariable);
	}
  
	public Chart getChart() 
	{
		int width = 800;
		int height = 600;
		
		if ( this.getImageSize().equals("Small") )
		{
			width = 600;
			height = 400;
		}

		if ( this.getImageSize().equals("Medium") )
		{
			width = 800;
			height = 600;
		}

		if ( this.getImageSize().equals("Large") )
		{
			width = 1280;
			height = 1024;
		}

	    // Create Chart
		//System.out.println("Build chart base");
	    Chart chart = new ChartBuilder()
	    		.chartType( this.getChartType() )
	    		.width(width)
	    		.height(height)
	    		.title( this.getTitle() )
	    		.xAxisTitle( this.getXaxis())
	    		.yAxisTitle( this.getYaxis() ) 
	    		.theme( this.getChartTheme() )
	    		.build();
	    
	    //System.out.println("Build chart base complete");
	    for ( Object series : this.twoWayTable.getSelectionValues() )
	    {
	    	// add each series to the chart
	    	chart.addSeries((String) series, this.twoWayTable.getSplitValues(), this.twoWayTable.getCounts(series) );
	    }

	    return chart;
	}
	  
  
	public ChartTheme getChartTheme()
	{
		return this.myTheme;
	}
	
	public void setGGPlotTheme()
	{
		this.myTheme = ChartTheme.GGPlot2;
	}
	
	public void setMatlabTheme()
	{
		this.myTheme = ChartTheme.Matlab;
	}
	
	public void setXchartTheme()
	{
		this.myTheme = ChartTheme.XChart;
	}

	public ChartType getChartType()
	{
		return this.myChartType;
	}
	
	public void setBarChart()
	{
		this.myChartType = ChartType.Bar;
	}
	
	public void setLineChart()
	{
		this.myChartType = ChartType.Line;
	}
		
	public void setScatterChart()
	{
		this.myChartType = ChartType.Scatter;
	}
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getXaxis() {
		return xaxis;
	}

	public void setXaxis(String xaxis) {
		this.xaxis = xaxis;
	}

	public String getYaxis() {
		return yaxis;
	}

	public void setYaxis(String yaxis) {
		this.yaxis = yaxis;
	}

	public Set<String> getThemes() {
		
		return themes;
	}

	public void setThemes(Set<String> chartTheme) {
		this.themes = chartTheme;
	}
	
	public void setType(String chartType)
	{
		if ( chartType.equals("Bar Chart") )
		{
			this.setBarChart();
		}
		
		if ( chartType.equals("Line Chart") )
		{
			this.setLineChart();
		}
		
		if ( chartType.equals("Scatter Plot") )
		{
			this.setScatterChart();
		}
	}
	
	public void setTheme(String theme)
	{
		if ( theme.equals("GGPlot2") )
		{
			this.setGGPlotTheme();
		}
		
		if ( theme.equals("MATLAB") )
		{
			this.setMatlabTheme();
		}
		
		if ( theme.equals("XChart") )
		{
			this.setXchartTheme();
		}
	}

	// set the variables of interest
	public String getSelectionVariable() {
		return selectionVariable;
	}

	public void setSelectionVariable(String variable1) {
		this.selectionVariable = variable1;
	}

	public String getSplitVariable() {
		return splitVariable;
	}

	public void setSplitVariable(String variable1) {
		this.splitVariable = variable1;
	}


	
}
