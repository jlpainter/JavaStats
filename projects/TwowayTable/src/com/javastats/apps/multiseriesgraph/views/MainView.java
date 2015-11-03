package com.javastats.apps.multiseriesgraph.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.javastats.apps.multiseriesgraph.graph.TwowayTableGraph;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

public class MainView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	private Navigator navigator;

	String xvar;
	String yvar;
	
	public MainView(Navigator navigator) {

		// set entire layout size
		setSizeFull();

		// Create the graph
		final TwowayTableGraph myGraph = new TwowayTableGraph();
		myGraph.setScatterChart();
		myGraph.setXchartTheme();
		myGraph.setTitle("Graph Title");
		myGraph.setYaxis("Count");
		

		// Graphing area
		Panel graphPanel = new Panel();
		graphPanel.setSizeFull();
		
		final VerticalLayout graphDiv = new VerticalLayout();
		graphDiv.setSpacing(false);
		graphDiv.setStyleName("graphDiv");
		graphPanel.setContent(graphDiv);


		//
		// Select links to columns
		// --------------------------------------------------------------------------------
			final NativeSelect yvars = new NativeSelect("Selection Variable");
			yvars.setWidth("100px");
			yvars.setNullSelectionAllowed(false);
			yvars.addListener( new Property.ValueChangeListener() {    //adding listner
	             
		            @Override
		            public void valueChange(ValueChangeEvent event) {
		            	yvar = event.getProperty().getValue().toString();
		            	myGraph.setSplitVariable( yvar );
		            }
		        });
			
			
	
			// Select X & Y variables
			final NativeSelect xvars = new NativeSelect("Split Variable");
			xvars.setWidth("100px");
			xvars.setNullSelectionAllowed(false);
			xvars.addListener( new Property.ValueChangeListener() {    //adding listner
	             
		            @Override
		            public void valueChange(ValueChangeEvent event) {
		            	// Update the x-variable
		            	xvar = event.getProperty().getValue().toString();
		            	myGraph.setSelectionVariable( xvar );
		            }
		        });
		// --------------------------------------------------------------------------------
		
		// Table editor goes with the data division
		final TableEditor te = new TableEditor();
		te.setSizeUndefined();

		
		Label lbl_header = new Label("This two way table analyzer allows you to input a data set, select your variables and then graph the output.");
		lbl_header.setStyleName("header");
		lbl_header.setSizeFull();

		VerticalLayout dataLayout = new VerticalLayout();
		dataLayout.setWidth("100%");
		
			Label lbl_data = new Label("Data Link or Upload");
			lbl_data.setStyleName("navigation");
			lbl_data.setSizeFull();
			lbl_data.setWidth("100%");
			dataLayout.addComponent(lbl_data);

			final TextField urlField = new TextField("Link to Data");
			urlField.setWidth("80%");
			
			// String dataURL = "https://docs.google.com/spreadsheets/d/1kqnQc21K7QOVG34VNyGlerREn9kmvMQgVW8vCX09c_w/pub?gid=1812014686&single=true&output=csv";
			urlField.setValue("URL Link to CSV File - Use A Google Forms Publish Data link for example");
			dataLayout.addComponent(urlField);
			
			Button updateButton = new Button("Update data");
			updateButton.setWidth("150");
			dataLayout.addComponent(updateButton);

			// Data Upload
			
			// Implement both receiver that saves upload in a file and
			// listener for successful upload
			class CsvUploader implements Receiver, SucceededListener {
			    public File file;
			    
			    public OutputStream receiveUpload(String filename,
			                                      String mimeType) {
			        // Create upload stream
			        FileOutputStream fos = null; // Stream to write to
			        try {
			            // Open the file for writing.
			        	// Find the application directory
						String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

						// create a file name
						int minimum = 1234567;
						int maximum = 9876543;
						int randomNum = minimum + (int)(Math.random()*maximum); 
						String dataFileName = basepath + "/WEB-INF/uploads/data/" + randomNum + "_" + filename + ".csv";
			            file = new File(dataFileName);
			            fos = new FileOutputStream(file);
			        } catch (final java.io.FileNotFoundException e) {
			            new Notification("Could not open file<br/>",
			                             e.getMessage(),
			                             Notification.Type.ERROR_MESSAGE)
			                .show(Page.getCurrent());
			            return null;
			        }
			        return fos; // Return the output stream to write to
			    }

			    public void uploadSucceeded(SucceededEvent event) {
			    	
			        // Show the uploaded file in the image viewer
			    	if ( te.hasData() == true )
			    	{
			    		te.reset();
						xvars.removeAllItems();
						yvars.removeAllItems();
			    	}
			    	
			        te.setFilename(file.getAbsolutePath());
			        te.updateData();
			        
					// setup columns to select for 2-way table
					xvars.addItems( te.getColumns() );
					yvars.addItems( te.getColumns() );
					
					xvars.setNullSelectionItemId(1L);
					yvars.setNullSelectionItemId(1L);
			        
			    }
			};
			CsvUploader receiver = new CsvUploader(); 

			// Create the upload with a caption and set receiver later
			Upload upload = new Upload("CSV File", receiver);
			upload.setButtonCaption("Upload");
			upload.addSucceededListener(receiver);
			dataLayout.addComponent(upload);
			// -----------------------------------------------------------------------
		
		Label lbl_navigation = new Label("Graph Editor");
		lbl_navigation.setStyleName("navigation");
		lbl_navigation.setSizeFull();

		Label lbl_content = new Label("This is the content area");
		lbl_content.setStyleName("content");
		lbl_content.setSizeFull();

		Label lbl_footer = new Label("&copy; 2015 JavaStats.com");
		lbl_footer.setWidth("100%");
		lbl_footer.setHeight("50px");
		lbl_footer.setContentMode(ContentMode.HTML);
		lbl_footer.setStyleName("footer");
		
		VerticalLayout workspace = new VerticalLayout();
		workspace.setSizeFull();
		
		// no global spacing
		workspace.setSpacing(false);
		
		HorizontalLayout header = new HorizontalLayout();
		header.setHeight("50px");
		header.setWidth("100%");
		
		HorizontalLayout content = new HorizontalLayout();
		content.setSizeFull();
			
			// Navigation
			// content panel left side - navigation
			VerticalLayout nav = new VerticalLayout();
			nav.setSizeFull();
		
			HorizontalLayout buttonLayout = new HorizontalLayout();
			buttonLayout.setSpacing(true);
			buttonLayout.setSizeFull();
		
			// content areas
			Panel dataDiv = new Panel();
			dataDiv.setSizeFull();
			dataDiv.setStyleName("dataDiv");
		
		HorizontalLayout footer = new HorizontalLayout();
		footer.setHeight("100px");
		footer.setWidth("100%");
		
		workspace.addComponent(header);
		workspace.addComponent(dataLayout);
		workspace.addComponent(lbl_data);


		// ----------------------------------------------------------------------
		// Navigation Dependencies
		// ----------------------------------------------------------------------		
		final GraphEditor ge = new GraphEditor();
		ge.setSizeFull();
		
		// Bind the graph to the editor
		final Item item = createItem(myGraph);
		BeanItem beanProperty = new BeanItem(item, TwowayTableGraph.class);
		final FieldGroup fg = new FieldGroup();
		fg.setItemDataSource(item);
		fg.bindMemberFields(ge);
		
		ge.addComponent(yvars);
		ge.addComponent(xvars);
		
		// Create buttons
		Button saveBtn  = new Button("Save");
		saveBtn.addClickListener(new Button.ClickListener()  {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fg.commit();

					// Update the image
					myGraph.setData(te.getData());
					myGraph.setTheme(ge.getSelectedTheme());
					myGraph.setType( ge.getSelectedType() );
					myGraph.setImageSize(ge.getSelectedSize());
					
					// getUI().showNotification("X-Var: " + xvar);
					graphDiv.removeAllComponents();

					// Output the CSV table
					Label csvData = new Label(myGraph.getHTMLTable());
					csvData.setCaption("Two-Way Table");
					csvData.setContentMode(ContentMode.HTML);
					graphDiv.addComponent(csvData);
					
					Image image = myGraph.getImage();
					graphDiv.addComponent(image);
					
				} catch ( Exception e ){
					Notification.show("error: " + e.getMessage());
				}
			}
		});
		
		
		// Create buttons
		Button cancelBtn  = new Button("Cancel");
		saveBtn.addClickListener(new Button.ClickListener()  {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					fg.discard();
					
				} catch ( Exception e ){
					Notification.show("error: " + e.getMessage());
				}
			}
		});
		
		buttonLayout.addComponent(saveBtn);
		buttonLayout.addComponent(cancelBtn);
		ge.addComponent(buttonLayout);
		
		
		
		// ----------------------------------------------------------------------
		nav.addComponent(ge);
		content.addComponent(nav);
		content.setExpandRatio(nav, 1f);
		// ----------------------------------------------------------------------
		

		// ----------------------------------------------------------------------
		updateButton.addClickListener(new Button.ClickListener()  {
			@Override
			public void buttonClick(ClickEvent event) {
				// finish this part
				String url = urlField.getValue();
				if ( url.trim().length() > 0 )
				{
					if ( te.hasData() )
					{
						te.reset();
						xvars.removeAllItems();
						yvars.removeAllItems();
						graphDiv.removeAllComponents();
						
					}

					te.setURL(url);
					te.updateData();
					
					// setup columns to select for 2-way table
					xvars.removeAllItems();
					yvars.removeAllItems();
					xvars.addItems( te.getColumns() );
					yvars.addItems( te.getColumns() );
					xvars.setNullSelectionAllowed(false);
					yvars.setNullSelectionAllowed(false);						
				}
			}
		});
		// ----------------------------------------------------------------------

		
			
			// content panel right side - content
			final VerticalLayout rightPanel = new VerticalLayout();
			// con.setSizeFull();

			// Book of Vaadin pg 327
			// To enable scrollbars, the size of the panel content
			// must not be relative to the panel size
			rightPanel.setSizeFull();
			
			dataDiv.setContent(te);
			rightPanel.addComponent(dataDiv);

			Image img = myGraph.getImage();
			graphDiv.addComponent(img);
			rightPanel.addComponent(graphPanel);
			
			content.addComponent(rightPanel);
			content.setExpandRatio(rightPanel, 3f);

		// Add all middle content to workspace and take remaining space
		workspace.addComponent(content);
		workspace.setExpandRatio(content, 1);

		// Add our footer
		workspace.addComponent(footer);

		// add labels to panels
		header.addComponent(lbl_header);
		footer.addComponent(lbl_footer);

		// add workspace to UI
		addComponent(workspace);		
			

	}



	@Override
	public void enter(ViewChangeEvent event) {

	}

	private static Item createItem(TwowayTableGraph graph) {
		return new BeanItem<TwowayTableGraph>(graph);
	}
}

