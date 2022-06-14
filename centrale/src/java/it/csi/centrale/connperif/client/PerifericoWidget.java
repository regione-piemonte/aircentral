/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: widget for displaying graphic interface of Periferico
* application
* Change log:
*   2008-10-27: initial version
* ----------------------------------------------------------------------------
* $Id: PerifericoWidget.java,v 1.19 2014/05/30 10:03:42 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.connperif.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget for displaying graphic interface of Periferico
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class PerifericoWidget extends Composite implements ClosingHandler {

	private static final int DELAY = 5000;
	private Label msgLabel;
	private Frame frame;
	private String locale;
	private Integer proxyId = null;
	private Timer timer = null;

	public PerifericoWidget(int stationId, String locale) {
		this.locale = locale;

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		msgLabel = new Label(ConnPerif.messages.loading());
		hPanel.add(msgLabel);
		hPanel.setCellHorizontalAlignment(msgLabel, HasHorizontalAlignment.ALIGN_LEFT);

		Button closeButton = new Button();
		closeButton.setStyleName("gwt-button-close-blue");
		closeButton.setTitle(ConnPerif.messages.close());
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Utils.closeWindow();
			}
		});
		hPanel.add(closeButton);
		hPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		panel.add(hPanel);
		frame = new Frame();
		frame.setWidth("1024px");
		frame.setHeight("723px");
		frame.setVisible(true);
		panel.add(frame);
		makeConnection(stationId);

		initWidget(panel);
	}

	/**
	 * Generate connection to periferico
	 * 
	 * @param stationId
	 */
	private void makeConnection(int stationId) {
		ConnPerifServiceAsync connPerifService = (ConnPerifServiceAsync) GWT.create(ConnPerifService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) connPerifService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "connperif");
		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

			public void onSuccess(Integer result) {
				proxyId = result;
				msgLabel.setText(ConnPerif.messages.connected());
				String url = "./proxy/station_" + proxyId + "/PerifericoUI.html?" + "locale=" + locale;
				frame.setUrl(url);
				msgLabel.setText(ConnPerif.messages.connected());
				timer = new Timer() {
					@Override
					public void run() {
						ConnPerifServiceAsync connPerifService = (ConnPerifServiceAsync) GWT
								.create(ConnPerifService.class);
						ServiceDefTarget endpoint = (ServiceDefTarget) connPerifService;
						endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "connperif");
						AsyncCallback<Object> callback = new AsyncCallback<Object>() {
							public void onSuccess(Object result) {
								msgLabel.setText(ConnPerif.messages.connected());
							}

							public void onFailure(Throwable caught) {
								// TODO internazionalizzare
								msgLabel.setText(
										"Collegamento con la stazione" + " interrotto: " + caught.getMessage());
								cancel();
							}
						};

						connPerifService.isConnectionAlive(proxyId, callback);
					}
				};
				timer.scheduleRepeating(DELAY);
			}

			public void onFailure(Throwable caught) {
				// TODO internazionalizzare
				String msg = "Impossibile effettuare il collegamento con la" + " stazione: " + caught.getMessage();
				Window.alert(msg);
				msgLabel.setText(msg);
			}
		};

		connPerifService.makeConnection(stationId, callback);
	}

	@Override
	public void onWindowClosing(ClosingEvent event) {
		if (proxyId == null)
			return;
		if (timer != null)
			timer.cancel();
		ConnPerifServiceAsync connPerifService = (ConnPerifServiceAsync) GWT.create(ConnPerifService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) connPerifService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "connperif");
		AsyncCallback<Object> callback = new AsyncCallback<Object>() {
			public void onSuccess(Object result) {
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		};

		connPerifService.stopStationUi(proxyId, callback);
	}

}
