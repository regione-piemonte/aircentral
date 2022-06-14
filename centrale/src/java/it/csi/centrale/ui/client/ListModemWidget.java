/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: Page for show modem configuration.
 * Change log:
 *   2009-05-04: initial version
 * ----------------------------------------------------------------------------
 * $Id: ListModemWidget.java,v 1.2 2014/09/18 09:46:58 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for show modem configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 */
public class ListModemWidget extends UIPage {

	private VerticalPanel externalPanel;

	private PanelButtonWidget panelButton;

	Label newModemLabel;

	Label modemLabel;

	private FlexTable headerTable = new FlexTable();

	private FlexTable table = new FlexTable();

	private HashMap<Button, String> map = new HashMap<Button, String>();

	public ListModemWidget() {

		panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#conf_modemWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);

		externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().modem_title(), panelButton, false);

		newModemLabel = new Label();
		newModemLabel.setStyleName("gwt-Label-title");
		newModemLabel.setText(CentraleUI.getMessages().new_modem());

		// panel that contains info for new physical dimension
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		panel.add(hPanel);

		Button newModemButton = new Button();
		newModemButton.setStyleName("gwt-button-new-orange");
		newModemButton.setTitle(CentraleUI.getMessages().lbl_new());
		newModemButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.modemWidget.setParameter("");
				CentraleUI.setCurrentPage(CentraleUI.modemWidget);
			}// end onClick

		});
		hPanel.add(newModemButton);

		externalPanel.add(newModemLabel);
		externalPanel.add(panel);

		modemLabel = new Label();
		modemLabel.setStyleName("gwt-Label-title");
		modemLabel.setText(CentraleUI.getMessages().modem_title());

		// panel that contains info for new modem
		VerticalPanel panel2 = new VerticalPanel();
		panel2.setStyleName("gwt-post-boxed");

		headerTable.setText(0, 0, CentraleUI.getMessages().lbl_modem());
		headerTable.setText(0, 1, CentraleUI.getMessages().lbl_modify());
		headerTable.setText(0, 2, CentraleUI.getMessages().lbl_cancel());
		headerTable.setStyleName("gwt-table-header");
		headerTable.getCellFormatter().setWidth(0, 0, "400px");
		headerTable.getCellFormatter().setWidth(0, 1, "50px");
		headerTable.getCellFormatter().setWidth(0, 2, "50px");
		for (int j = 0; j < 3; j++) {
			headerTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}

		panel2.add(headerTable);
		panel2.add(table);

		externalPanel.add(modemLabel);
		externalPanel.add(panel2);

		initWidget(externalPanel);
	}

	private void setField() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		// load station matched
		AsyncCallback<List<String>> callback = new UIAsyncCallback<List<String>>() {
			public void onSuccess(List<String> modemList) {

				Iterator<String> iterator = modemList.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					String deviceId = iterator.next();
					table.setText(i, 0, deviceId);
					// modify button
					Button buttonModify = new Button();
					buttonModify.setStyleName("gwt-button-modify");
					buttonModify.setTitle(CentraleUI.getMessages().lbl_modify());
					buttonModify.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							CentraleUI.modemWidget.setParameter(map.get(event
									.getSource()));
							CentraleUI.setCurrentPage(CentraleUI.modemWidget);

						}// end onClick

					});
					table.setWidget(i, 1, buttonModify);
					table.getCellFormatter().setHorizontalAlignment(i, 1,
							HasHorizontalAlignment.ALIGN_CENTER);
					map.put(buttonModify, deviceId);

					// delete button
					Button buttonCancel = new Button();
					buttonCancel.setStyleName("gwt-button-delete");
					buttonCancel.setTitle(CentraleUI.getMessages().delete());
					buttonCancel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
									.create(CentraleUIService.class);
							ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
							endpoint2.setServiceEntryPoint(GWT
									.getModuleBaseURL() + "uiservice");

							AsyncCallback<String> callback2 = new UIAsyncCallback<String>() {
								public void onSuccess(String errStr) {
									if (errStr == null) {
										Window.alert(CentraleUI.getMessages()
												.deleted_modem());
									} else
										Window.alert(errStr);
									reset();
									setField();
								}// end onSuccess

							};
							centraleService2.deleteModem(
									map.get(event.getSource()), callback2);

						}// end onClick

					});
					table.setWidget(i, 2, buttonCancel);
					table.getCellFormatter().setHorizontalAlignment(i, 2,
							HasHorizontalAlignment.ALIGN_CENTER);
					map.put(buttonCancel, deviceId);

					for (int j = 0; j < 3; j++) {
						table.getCellFormatter().setStyleName(i, j,
								"gwt-table-data");
					}// end for
					i++;
				}// end while

			}// end onSuccess
		};
		centraleService.getModemtList(callback);

	}// end setField

	@Override
	protected void reset() {
		Utils.clearTable(table);
		table.getCellFormatter().setWidth(0, 0, "400px");
		table.getCellFormatter().setWidth(0, 1, "50px");
		table.getCellFormatter().setWidth(0, 2, "50px");
	}

	@Override
	protected void loadContent() {
		setField();

	}

}// end class
