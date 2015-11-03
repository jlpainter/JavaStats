package com.javastats.apps.multiseriesgraph.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class Dataset {

	private ArrayList<DataElement> dataset;
	
	public Dataset()
	{
		this.dataset = new ArrayList<DataElement>();
	}
	
	/**
	 * Add data element
	 * @param label
	 * @param value
	 */
	public void addData(Object variable1, Object variable2)
	{
		DataElement d = new DataElement();
		d.setVariable1(variable1);
		d.setVariable2(variable2);
		this.dataset.add(d);
	}

	/**
	 * Compute the two way table values
	 * @return
	 */
	private int[][] getTableValues()
	{
		int [][] twoWay = new int[this.getSelectionValues().size()][this.getSplitValues().size()];
		for ( int i = 0; i < this.getSelectionValues().size(); i++ ){
			for ( int j = 0; j < this.getSplitValues().size(); j++ )
			{
				// init the entry
				twoWay[i][j] = 0;
				
				for ( DataElement d : this.dataset )
				{
					if ( d.getVariable1().equals(this.getSelectionValues().get(i)) )
					{
						if ( d.getVariable2().equals(this.getSplitValues().get(j)))
						{
							twoWay[i][j] = twoWay[i][j] + 1;
						}
					}
				}
			}
		}
		return twoWay;
	}
	
	public String getHTMLTable()
	{
		int [][] dataTable = this.getTableValues();
		
		StringBuilder csv = new StringBuilder();
		csv.append("<table border=\"1\" cellpadding=\"2\"> <tr> <th> &nbsp; </th> ");
		for ( String x : this.getSplitValues() )
		{
			csv.append( " <th> " + x + "</th>" );
		}
		String header = csv.toString() + "</tr>\n";
		
		// Get row entries
		csv = new StringBuilder();
		for ( int i = 0; i < this.getSelectionValues().size(); i++ )
		{
			csv.append( "<tr> <td> " + this.getSelectionValues().get(i).toString() + "</td>" );
			for ( int j = 0; j < this.getSplitValues().size(); j++ ) {
				csv.append( "<td>" + dataTable[i][j] + "</td>" );
			}
			csv.append("</tr>\n");
		}
		
		String rows = csv.toString();
		return ( header + rows );
	}
	
	public String getCSVOutput()
	{
		int [][] dataTable = this.getTableValues();
		
		StringBuilder csv = new StringBuilder();
		csv.append(",");
		for ( String x : this.getSplitValues() )
		{
			csv.append( x + "," );
		}
		String header = csv.toString().substring(0,  csv.length() - 1) + "\n";
		
		// Get row entries
		csv = new StringBuilder();
		for ( int i = 0; i < this.getSelectionValues().size(); i++ )
		{
			csv.append( this.getSelectionValues().get(i).toString() + "," );
			for ( int j = 0; j < this.getSplitValues().size(); j++ ) {
				csv.append( dataTable[i][j] + "," );
			}
			csv.append("\n");
		}
		
		String rows = csv.toString();
		return ( header + rows );
	}

	public List<String> getSelectionValues()
	{
		HashMap<String, Boolean> splits = new HashMap<String, Boolean>();
		List<String> xseries = new Vector<String>();
		for ( DataElement d : this.dataset )
		{
			splits.put((String)d.getVariable1(), true);

		}
		for ( String key : splits.keySet() )
		{
			if ( key.length() > 0 )
				xseries.add(key);
		}
		
		
		java.util.Collections.sort(xseries);
		
		/*xseries
				.stream()
				.sorted()
				.collect(Collectors.toList());*/
		
		return xseries;
		
	}

	public List<String> getSplitValues()
	{
		HashMap<String, Boolean> splits = new HashMap<String, Boolean>();
		List<String> xseries = new Vector<String>();
		for ( DataElement d : this.dataset )
		{
			splits.put((String)d.getVariable2(), true);

		}
		for ( String key : splits.keySet() )
		{
			if ( key.length() > 0 )
				xseries.add(key);
		}
		
		
		java.util.Collections.sort(xseries);
		
		/*xseries
				.stream()
				.sorted()
				.collect(Collectors.toList());*/
		
		return xseries;

	}


	/**
	 * Get count for a series in the data
	 * @param series
	 * @return
	 */
	public ArrayList<Number> getCounts(Object series)
	{
		// System.out.println("get counts for: " + series.toString()); 
		int rowIdx = 0;
		for ( int i = 0; i < this.getSelectionValues().size(); i++ )
		{
			if ( this.getSelectionValues().get(i).equals(series) )
			{
				rowIdx = i;
			}
		}
		
		int [][] dataTbl = this.getTableValues();
		ArrayList<Number> counts = new ArrayList<Number>();
		for ( int j = 0; j < this.getSplitValues().size();  j++ )
		{
			int value = dataTbl[rowIdx][j];
			// System.out.println("Total for: " + this.getSplitValues().get(j).toString() + " " + value);
			counts.add( value );
		}

		return counts;
	}

	/**
	 * Create a two-way table from the data elements we have added
	 * @return
	 */
	public HashMap<Object, HashMap<Object, Integer>> getTwoWayTable()
	{
		HashMap<Object, HashMap<Object, Integer>> twoWayTable = new HashMap<Object, HashMap<Object, Integer>>();
		for ( String category : this.getSelectionValues() )
		{
			for ( DataElement de : this.dataset )
			{
				// look for initial category
				if ( de.getVariable1().equals(category) )
				{
					// Have we seen this categorical variable yet?
					if ( twoWayTable.containsKey(category) == false )
					{
						HashMap<Object, Integer> innerTbl = new HashMap<Object, Integer>();
						innerTbl.put( de.getVariable2(),  1);
						twoWayTable.put(category,  innerTbl);
						
					} else {
						
						// Have we seen the split category yet?
						if ( twoWayTable.get(category).containsKey(de.getVariable2()) == false )
						{
							// Set the initial count
							HashMap<Object, Integer> innerTbl = new HashMap<Object, Integer>();
							innerTbl.put(de.getVariable2(), 1);
							twoWayTable.put(category,  innerTbl);
						} else {
	
							// Update the count
							HashMap<Object, Integer> innerTbl = twoWayTable.get(category);
							innerTbl.put( de.getVariable2(), innerTbl.get(de.getVariable2()) + 1 );
							twoWayTable.put(category,  innerTbl);
						}
					}
				}
			}
		}
		return twoWayTable;
	}

	
}
