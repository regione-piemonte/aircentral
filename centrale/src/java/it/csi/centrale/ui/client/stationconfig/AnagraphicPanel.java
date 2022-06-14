/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for panel of anagraphic information
// Change log:
//   2014-05-20: initial version
// ----------------------------------------------------------------------------
// $Id: AnagraphicPanel.java,v 1.4 2015/07/29 09:54:22 vespa Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.ui.client.stationconfig;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.CentraleUI;
import it.csi.centrale.ui.client.data.StInfoObject;

/**
 * Class for panel of anagraphic information
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class AnagraphicPanel extends VerticalPanel {

	// private TextBox textShortname;
	private CheckBox checkBoxUseGps;
	FlexTable anagraphicGrid = new FlexTable();

	AnagraphicPanel() {
		// Label and panel for anagraphic info
		Label anagraphicTitle = new Label();
		anagraphicTitle.setText(CentraleUI.getMessages().lbl_station_title());
		anagraphicTitle.setStyleName("gwt-Label-title");
		// contentPanel contains the anagraphic grid
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.setStyleName("gwt-post-boxed");
		// Grids for station information.
		contentPanel.add(anagraphicGrid);
		// format the grid for anagraphic info
		anagraphicGrid.getCellFormatter().setWidth(0, 0, "150px");
		anagraphicGrid.getCellFormatter().setWidth(0, 1, "320px");
		anagraphicGrid.getCellFormatter().setWidth(0, 2, "150px");
		anagraphicGrid.getCellFormatter().setWidth(0, 3, "320px");
		anagraphicGrid.setCellPadding(5);
		anagraphicGrid.setCellSpacing(5);
		// Put values in the anagraphic grid cells.
		anagraphicGrid.setText(0, 0, CentraleUI.getMessages().lbl_station_shortname());
		anagraphicGrid.setText(1, 0, CentraleUI.getMessages().lbl_station_name());
		anagraphicGrid.setText(2, 0, CentraleUI.getMessages().lbl_station_location());
		anagraphicGrid.setText(3, 0, CentraleUI.getMessages().lbl_station_address());
		anagraphicGrid.setText(4, 0, CentraleUI.getMessages().lbl_station_city());
		anagraphicGrid.setText(5, 0, CentraleUI.getMessages().lbl_station_province());
		anagraphicGrid.setText(0, 2, CentraleUI.getMessages().lbl_virtual_cop());
		anagraphicGrid.setText(1, 2, CentraleUI.getMessages().lbl_station_userNotes());
		anagraphicGrid.getFlexCellFormatter().setRowSpan(1, 3, 6);
		anagraphicGrid.getFlexCellFormatter().setAlignment(1, 3, HasHorizontalAlignment.ALIGN_LEFT,
				HasVerticalAlignment.ALIGN_TOP);
		anagraphicGrid.setText(6, 0, CentraleUI.getMessages().lbl_use_gps());
		checkBoxUseGps = new CheckBox();
		checkBoxUseGps.setEnabled(false);
		anagraphicGrid.setWidget(6, 1, checkBoxUseGps);
		add(anagraphicTitle);
		add(contentPanel);
		clearStationInfo();
	}

	void setStationInfo(StInfoObject stationInfo) {
		anagraphicGrid.setText(0, 1, stationInfo.getShortName());
		anagraphicGrid.setText(1, 1, stationInfo.getName());
		anagraphicGrid.setText(2, 1, stationInfo.getLocation());
		anagraphicGrid.setText(3, 1, stationInfo.getAddress());
		anagraphicGrid.setText(4, 1, stationInfo.getCity());
		anagraphicGrid.setText(5, 1, stationInfo.getProvince());
		anagraphicGrid.setText(0, 3, stationInfo.getVirtualCopInfo().getCopName());
		anagraphicGrid.setText(1, 3, stationInfo.getNotes());
		checkBoxUseGps.setValue(stationInfo.isUseGps());
	}

	void clearStationInfo() {
		anagraphicGrid.setText(0, 1, "");
		anagraphicGrid.setText(1, 1, "");
		anagraphicGrid.setText(1, 3, "");
		anagraphicGrid.setText(2, 1, "");
		anagraphicGrid.setText(3, 1, "");
		anagraphicGrid.setText(4, 1, "");
		anagraphicGrid.setText(5, 1, "");
		anagraphicGrid.setText(0, 3, "");
		checkBoxUseGps.setValue(false);
	}

}
