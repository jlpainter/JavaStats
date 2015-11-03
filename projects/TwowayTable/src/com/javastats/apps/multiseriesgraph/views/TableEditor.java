package com.javastats.apps.multiseriesgraph.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TableEditor extends VerticalLayout {

	private static final long serialVersionUID = 292227254776012217L;
	
	// Where is the data coming from?
	TextField url;
	String dataURL;
	String filename;
	
	// Name of columns imported
	List<String> columns;
	
	private Table dataTbl;
	
	// Flag to determine if data has been loaded or not
	private boolean dataSet;
	
	
	public TableEditor()
	{
		super();
		setSpacing(false);

		this.dataSet = false;
		
		// Create data table
		this.dataTbl = new Table("Data View");
		this.dataTbl.setSizeFull();
		addComponent(dataTbl);		
	}
	
	/**
	 * Reset all the data in this editor 
	 * Note: This does not quite seem to work fully yet
	 */
	public void reset()
	{
		this.dataSet = false;
		this.dataTbl = null;
		this.filename = "";
		this.dataURL = "";
		this.columns = null;
		
		// Rebuild the data table
		this.removeAllComponents();
		this.dataTbl = new Table("Data Editor");
		addComponent(dataTbl);		
	}
	
	
	// Getters/setteres
	/**
	 * Get the series of data based on a column
	 * @param column
	 * @return
	 */
	public Table getData()
	{
		if ( this.dataTbl != null )
			return this.dataTbl;
		else {
			System.out.println("No table to return!");
			return null;
		}
	}

	public boolean hasData()
	{
		return dataSet;
	}
	
	public void setURL(String url)
	{
		this.dataURL = url;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	
	
	public List<String> getColumns()
	{
		List<String> cols = new Vector<String>();
		for ( String c : this.columns )
		{
			if ( c.trim().length() > 0 )
				cols.add(c);
		}
		return cols;
	}
	

	public static BufferedReader getTextFromFile(String filename)
	{
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader in = new BufferedReader(fr);
	        return in;
		} catch ( Exception e ) {
			return null;
		}
		
	}
	
	public static BufferedReader getTextFromLink(String url)  {
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

	
	/**
	 * Update the data tables
	 */
	@SuppressWarnings("unchecked")
	public void updateData()
	{
		try {
			// Parse the data
			BufferedReader br = null;
			boolean useFile = false;
			
			// Is the data coming from an uploaded file or a URL?
			if ( this.filename != null )
			{
				if ( this.filename.trim().length() > 0 )
				{
					br = getTextFromFile(this.filename);
					useFile = true;
				}
			}

			if ( this.dataURL != null && !useFile )
			{
				if ( !useFile && this.dataURL.trim().length() > 0 )
				{
					System.out.println("Loading data from URL: " + this.dataURL);
					br = getTextFromLink(this.dataURL);
				}
			}

			// Was the reader properly set?
			if ( br != null )
			{
				CSVParser parser = new CSVParser(br, CSVFormat.EXCEL.withDelimiter(',').withHeader() );
				
				// Setup table columns
				Map<String, Integer> headerMap = parser.getHeaderMap();
				this.columns = new Vector<String>();
				for ( String column : headerMap.keySet() )
				{
					this.columns.add(column);
					dataTbl.addContainerProperty(column,  String.class, null);
				}
				
				for ( CSVRecord record : parser.getRecords() )
				{
					// do not import empty records
					boolean addRecord = false;
					
					// test that this record has data
					for ( String col : this.columns )
					{
						if ( record.get(col).trim().length() > 0 )
						{
							addRecord = true;
							break;
						}
					}
					
					if ( addRecord )
					{
						// Create a new row entry
						Item rowEntry = dataTbl.getItem(dataTbl.addItem());
						
						// Update the column values
						for ( String col : this.columns )
						{
							rowEntry.getItemProperty(col).setValue(record.get(col));
						}
					}
					
				}
				
				// Set flag that data was loaded
				this.dataSet = true;
				
				// Close the data streams
				parser.close();
				br.close();
			}
		
		} catch ( Exception e ) {
			System.out.println("Could not load data from URL: " + dataURL);
		}
		

	}


}
