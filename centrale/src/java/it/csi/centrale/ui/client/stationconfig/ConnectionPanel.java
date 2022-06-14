/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for connection panel
// Change log:
//   2014-05-21: initial version
// ----------------------------------------------------------------------------
// $Id: ConnectionPanel.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.ui.client.stationconfig;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.CentraleUI;
import it.csi.centrale.ui.client.CentraleUIConstants;
import it.csi.centrale.ui.client.NumericKeyPressHandler;
import it.csi.centrale.ui.client.PanelButtonWidget;
import it.csi.centrale.ui.client.data.StInfoObject;

/**
 * Class for connection panel
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class ConnectionPanel extends VerticalPanel {

	private TextBox textIpAddress;
	private TextBox textIpPort;
	private ListBox listBoxUseModem;
	private TextBox textPhoneNumber;
	private TextBox textRouterIpAddress;
	private PanelButtonWidget panelButtons;

	ConnectionPanel(ClickHandler undoHandler, ClickHandler saveHandler, ClickHandler downloadHandler) {
		// Label and panel for connection info
		Label connectionTitle = new Label();
		connectionTitle.setText(CentraleUI.getMessages().lbl_station_connection_info());
		connectionTitle.setStyleName("gwt-Label-title");

		// panel that contains the connection grid
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.setStyleName("gwt-post-boxed");
		FlexTable connectionGrid = new FlexTable();
		contentPanel.add(connectionGrid);

		// format the grid connection info
		connectionGrid.getCellFormatter().setWidth(0, 0, "150px");
		connectionGrid.getCellFormatter().setWidth(0, 1, "320px");
		connectionGrid.getCellFormatter().setWidth(0, 2, "150px");
		connectionGrid.getCellFormatter().setWidth(0, 3, "320px");
		connectionGrid.setCellSpacing(5);
		connectionGrid.setCellPadding(5);

		// Put values in the connection grid cells.
		String message = CentraleUI.getMessages().lbl_station_ip_address();
		connectionGrid.setText(0, 0, message);
		textIpAddress = new TextBox();
		textIpAddress.setStyleName("gwt-bg-text-orange");
		textIpAddress.setMaxLength(80);
		textIpAddress.setWidth("300px");
		connectionGrid.setWidget(0, 1, textIpAddress);

		message = CentraleUI.getMessages().lbl_station_ip_port();
		connectionGrid.setText(0, 2, message);
		textIpPort = new TextBox();
		textIpPort.setStyleName("gwt-bg-text-orange");
		textIpPort.setMaxLength(5);
		textIpPort.setWidth("300px");
		connectionGrid.setWidget(0, 3, textIpPort);
		textIpPort.addKeyPressHandler(new NumericKeyPressHandler(true, true));

		message = CentraleUI.getMessages().lbl_station_router_ip_address();
		connectionGrid.setText(1, 2, message);
		textRouterIpAddress = new TextBox();
		textRouterIpAddress.setStyleName("gwt-bg-text-orange");
		textRouterIpAddress.setMaxLength(15);
		textRouterIpAddress.setWidth("300px");
		connectionGrid.setWidget(1, 3, textRouterIpAddress);
		textRouterIpAddress.addKeyPressHandler(new NumericKeyPressHandler(true));

		message = CentraleUI.getMessages().lbl_station_use_modem();
		connectionGrid.setText(1, 0, message);
		listBoxUseModem = new ListBox();
		listBoxUseModem.setWidth("100px");
		listBoxUseModem.addItem(CentraleUIConstants.MODEM, CentraleUIConstants.MODEM);
		listBoxUseModem.addItem(CentraleUIConstants.ROUTER, CentraleUIConstants.ROUTER);
		listBoxUseModem.addItem(CentraleUIConstants.LAN, CentraleUIConstants.LAN);
		listBoxUseModem.addItem(CentraleUIConstants.PROXY, CentraleUIConstants.PROXY);
		connectionGrid.setWidget(1, 1, listBoxUseModem);

		message = CentraleUI.getMessages().lbl_station_phone_number();
		connectionGrid.setText(2, 0, message);
		textPhoneNumber = new TextBox();
		textPhoneNumber.setStyleName("gwt-bg-text-orange");
		textPhoneNumber.setMaxLength(15);
		textPhoneNumber.setWidth("300px");
		connectionGrid.setWidget(2, 1, textPhoneNumber);
		textPhoneNumber.addKeyPressHandler(new NumericKeyPressHandler(true, ','));

		// create buttons for download, undo and save
		panelButtons = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		panelButtons.addButton(CentraleUIConstants.UNDO, undoHandler);
		panelButtons.addButton(CentraleUIConstants.SAVE, saveHandler);
		panelButtons.addButton(CentraleUIConstants.DOWNLOAD, downloadHandler);

		// panel that contains button for connection info
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setStyleName("gwt-button-panel");
		hPanel.add(panelButtons);
		hPanel.setSpacing(10);
		hPanel.setCellHorizontalAlignment(panelButtons, HasHorizontalAlignment.ALIGN_CENTER);
		contentPanel.add(hPanel);

		add(connectionTitle);
		add(contentPanel);
	}

	void showSaveButton(boolean visible) {
		panelButtons.setVisibleButton(CentraleUIConstants.SAVE, visible);
	}

	void enableInput(boolean enabled) {
		textIpAddress.setEnabled(enabled);
		textIpPort.setEnabled(enabled);
		listBoxUseModem.setEnabled(enabled);
		textPhoneNumber.setEnabled(enabled);
		textRouterIpAddress.setEnabled(enabled);
	}

	boolean isInputEnabled() {
		return textIpAddress.isEnabled();
	}

	String getStationHost() {
		return textIpAddress.getText().trim();
	}

	void setStationHost(String host) {
		textIpAddress.setText(host);
	}

	Integer getStationPort() throws NumberFormatException {
		String strPort = textIpPort.getText().trim();
		return strPort.isEmpty() ? null : new Integer(strPort);
	}

	void setStationPort(Integer port) {
		textIpPort.setText(port == null ? "" : port.toString());
	}

	String getStationPhoneNumber() {
		return textPhoneNumber.getText().trim();
	}

	void setStationPhoneNumber(String value) {
		textPhoneNumber.setText(value);
	}

	String getRouterIP() {
		return textRouterIpAddress.getText().trim();
	}

	void setRouterIP(String ip) {
		textRouterIpAddress.setText(ip);
	}

	String getCommunicationDevice() {
		return listBoxUseModem.getValue(listBoxUseModem.getSelectedIndex());
	}

	void setCommunicationDevice(String device) {
		for (int i = 0; i < listBoxUseModem.getItemCount(); i++) {
			if (listBoxUseModem.getValue(i).equals(device))
				listBoxUseModem.setSelectedIndex(i);
		}
	}

	void setConnectionInfo(StInfoObject stationInfo) {
		setStationHost(stationInfo.getIpAddress());
		setStationPort(stationInfo.getIpPort());
		setRouterIP(stationInfo.getRouterIpAddress());
		if (stationInfo.isUseModem())
			setCommunicationDevice(CentraleUIConstants.MODEM);
		else if (stationInfo.isLan())
			setCommunicationDevice(CentraleUIConstants.LAN);
		else if (stationInfo.isProxy())
			setCommunicationDevice(CentraleUIConstants.PROXY);
		else
			setCommunicationDevice(CentraleUIConstants.ROUTER);
		setStationPhoneNumber(stationInfo.getPhoneNumber());
	}

	void setConnectionDefaults() {
		setStationHost("");
		setStationPort(null);
		setRouterIP("");
		setCommunicationDevice(CentraleUIConstants.ROUTER);
		setStationPhoneNumber("");
	}

}
