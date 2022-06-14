/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: entry point for connection to Periferico application
 * Change log:
 *   2008-10-27: initial version
 * ----------------------------------------------------------------------------
 * $Id: ConnPerif.java,v 1.13 2015/10/22 13:54:23 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.connperif.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point for connection to Periferico application
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ConnPerif implements EntryPoint {

	private static RootPanel slotPage = RootPanel.get("page");
	static MessageBundleClient messages = null;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Load internationalization
		messages = (MessageBundleClient) GWT.create(MessageBundleClient.class);
		String locale = Window.Location.getParameter("locale");
		String stationId = Window.Location.getParameter("stationId");
		Window.setTitle(Window.Location.getParameter("stationName"));

		// Create perifericoWidget
		PerifericoWidget perifericoWidget = new PerifericoWidget(new Integer(
				stationId), locale);

		Window.addWindowClosingHandler(perifericoWidget);
		perifericoWidget.setVisible(true);
		slotPage.add(perifericoWidget);

	}// end onModuleLoad

}// end class
