package com.javastats.apps.multiseriesgraph.views;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Theme("twowaytable")
public class TwowaytableUI extends UI {


	private Navigator navigator;
	private VerticalLayout layout;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TwowaytableUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		
		layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		setContent(layout);

		navigator = new Navigator(this, layout);
		navigator.addView("", new MainView(navigator));

	}
}