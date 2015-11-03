package com.javastats.apps.multiseriesgraph.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

public class GraphEditor extends FormLayout {

	TextField title;
	TextField xaxis;
	TextField yaxis;

	// OptionGroup types;
	// OptionGroup themes;
	OptionGroup imgSize;

	String selectedType;
	String selectedTheme;
	String selectedSize;

	@SuppressWarnings("deprecation")
	public GraphEditor()
	{
		super();
		setSpacing(false);
		
		title = new TextField("Graph Title");
		xaxis = new TextField("X Axis Label");
		yaxis = new TextField("Counts");
		
		// Set these as the defaults
		selectedType  = "Bar Chart";
		selectedTheme = "GGPlot2";

		
		NativeSelect imgSize = new NativeSelect("Graph Size");
		imgSize.addItems("Small", "Medium", "Large");
		imgSize.addListener( new Property.ValueChangeListener() {    //adding listner
            @Override
            public void valueChange(ValueChangeEvent event) { 
            	selectedSize = event.getProperty().getValue().toString();
            }
        });

        
		addComponent(title);
		addComponent(xaxis);
		addComponent(yaxis);
		addComponent(imgSize);
		
		/*
		 * If you want to enable configuring the graph, you can turn this on
		 * but for this type of analysis, the bar chart is the only appropriate
		 * presentation of a two-way table analysis
		 * 
 
			NativeSelect types = new NativeSelect("Chart Type");
			types.addItems("Bar Chart", "Line Chart", "Scatter Plot");
			types.setNullSelectionAllowed(false);
			types.addListener( new Property.ValueChangeListener() {    //adding listner
	             
		            @Override
		            public void valueChange(ValueChangeEvent event) {
		            	selectedType = event.getProperty().getValue().toString();
		            }
		        });
	
			
			NativeSelect themes = new NativeSelect("Chart Theme");
			themes.addItems("GGPlot2", "MATLAB", "XChart" );
			themes.addListener( new Property.ValueChangeListener() {    //adding listner
	            @Override
	            public void valueChange(ValueChangeEvent event) { 
	            	selectedTheme = event.getProperty().getValue().toString();
	            }
	        });
	        
	        addComponent(types);
			addComponent(themes);
	     */
	
	}
	
	
	
	public static BufferedReader getText(String url)  {
		try {
	        URL website = new URL(url);
	        URLConnection connection = website.openConnection();
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                    connection.getInputStream()));
	
	        return in;
		} catch ( Exception e ) {
			return null;
		}
    }
	
	
	public String getSelectedType()
	{
		return selectedType;
	}
	
	public String getSelectedTheme()
	{
		return selectedTheme;
	}
	
	public String getSelectedSize()
	{
		return selectedSize;
	}


}
