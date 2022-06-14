/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: Page for configuring modem informations.
 * Change log:
 *   2009-05-04: initial version
 * ----------------------------------------------------------------------------
 * $Id: ModemWidget.java,v 1.6 2014/09/18 09:46:57 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.ModemInfo;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for configuring modem informations.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ModemWidget extends UIPage {

	private TextBox deviceIdTextBox;

	private CheckBox sharedLineCheckBox;

	private TextBox phonePrefixTextBox;

	String deviceId;

	public ModemWidget() {

		PanelButtonWidget panelButton = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);
		ClickHandler backClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.goToPreviousPage();
			}
		};
		panelButton.addButton(CentraleUIConstants.BACK, backClickHandler);
		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#conf_modemWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().modem_title(), panelButton, false);

		Label modemLabel = new Label();
		modemLabel.setStyleName("gwt-Label-title");
		modemLabel.setText(CentraleUI.getMessages().lbl_modem_conf());

		// panel that contains info for modem
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		FlexTable modemTable = new FlexTable();
		// format the grid
		modemTable.getCellFormatter().setWidth(0, 0, "250px");
		modemTable.getCellFormatter().setWidth(0, 1, "220px");
		modemTable.getCellFormatter().setWidth(0, 2, "250px");
		modemTable.getCellFormatter().setWidth(0, 3, "220px");
		modemTable.setCellSpacing(5);
		modemTable.setCellPadding(5);

		modemTable.setText(0, 0, "* " + CentraleUI.getMessages().device_id());
		deviceIdTextBox = new TextBox();
		deviceIdTextBox.setStyleName("gwt-bg-text-orange");
		deviceIdTextBox.setWidth("100px");
		modemTable.setWidget(0, 1, deviceIdTextBox);
		modemTable.setText(0, 2, CentraleUI.getMessages().shared_line());
		sharedLineCheckBox = new CheckBox();
		sharedLineCheckBox.setStyleName("gwt-bg-text-orange");
		modemTable.setWidget(0, 3, sharedLineCheckBox);

		modemTable.setText(1, 0, CentraleUI.getMessages().phone_prefix());
		phonePrefixTextBox = new TextBox();
		phonePrefixTextBox.setStyleName("gwt-bg-text-orange");
		phonePrefixTextBox.setWidth("100px");
		TextBoxKeyPressHandler positiveKeyPressHandler = new TextBoxKeyPressHandler(
				true, ',');
		phonePrefixTextBox.addKeyPressHandler(positiveKeyPressHandler);
		modemTable.setWidget(1, 1, phonePrefixTextBox);

		panel.add(modemTable);

		// create buttons for undo and save for modem
		PanelButtonWidget panelButton3 = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		// button cancel for modem
		ClickHandler cancelClickHandler2 = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
				setField();
			}
		};
		panelButton3.addButton(CentraleUIConstants.UNDO, cancelClickHandler2);

		// button send/verify for modem
		ClickHandler sendVerifyClickHandler2 = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (deviceIdTextBox.getText().equals(""))
					Window.alert(CentraleUI.getMessages().device_id_mandatory());
				else {
					ModemInfo modemInfo = new ModemInfo();
					modemInfo.setDeviceId(deviceIdTextBox.getText());
					modemInfo.setSharedLine(sharedLineCheckBox.getValue());
					modemInfo.setPhonePrefix(phonePrefixTextBox.getText());

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
							+ "uiservice");
					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String errStr) {
							if (errStr != null) {
								Window.alert(errStr);
								reset();
								setField();
							} else {
								if (deviceId.equals(""))
									Window.alert(CentraleUI.getMessages()
											.modem_inserted());
								else
									Window.alert(CentraleUI.getMessages()
											.modem_updated());
								CentraleUI
										.setCurrentPage(CentraleUI.listModemWidget);
							}// end else
						}// end onSuccess
					};
					if (deviceId.equals("")) {
						// case of new modem
						centraleService.insertModem(modemInfo, callback);
					} else {
						// case of existing modem
						centraleService.updateModem(deviceId, modemInfo,
								callback);
					}
				}// end else

			}// end onClick
		};
		panelButton3.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler2);

		// panel that contains button for modem
		HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setStyleName("gwt-button-panel");
		hPanel3.add(panelButton3);
		hPanel3.setSpacing(10);
		hPanel3.setCellHorizontalAlignment(panelButton3,
				HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(hPanel3);
		panel.add(new Label(CentraleUI.getMessages().mandatory()));

		externalPanel.add(modemLabel);
		externalPanel.add(panel);

		initWidget(externalPanel);

	}

	private void setField() {
		if (!this.deviceId.equals("")) {
			CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
					.create(CentraleUIService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
			AsyncCallback<ModemInfo> callback3 = new UIAsyncCallback<ModemInfo>() {
				public void onSuccess(ModemInfo modemInfo) {
					deviceIdTextBox.setText(modemInfo.getDeviceId());
					sharedLineCheckBox.setValue(modemInfo.isSharedLine());
					phonePrefixTextBox.setText(modemInfo.getPhonePrefix());
				}// end onSuccess
			};
			centraleService.readModem(this.deviceId, callback3);
		}// end if
	}// end setField

	@Override
	protected void reset() {
		deviceIdTextBox.setText("");
		sharedLineCheckBox.setValue(false);
		phonePrefixTextBox.setText("");
	}

	@Override
	protected void loadContent() {
		setField();

	}

	public void setParameter(String device) {
		this.deviceId = device;

	}

}// end class
