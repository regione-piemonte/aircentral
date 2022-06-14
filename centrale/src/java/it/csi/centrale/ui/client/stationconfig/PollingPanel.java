/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for polling panel
// Change log:
//   2014-05-21: initial version
// ----------------------------------------------------------------------------
// $Id: PollingPanel.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.ui.client.stationconfig;

import it.csi.centrale.ui.client.CentraleUI;
import it.csi.centrale.ui.client.CentraleUIConstants;
import it.csi.centrale.ui.client.NumericKeyPressHandler;
import it.csi.centrale.ui.client.PanelButtonWidget;
import it.csi.centrale.ui.client.data.StInfoObject;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * Class for polling panel
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
class PollingPanel extends VerticalPanel {

	private CheckBox checkBoxEnabled;
	private TextBox textForcePollingTime;
	private CheckBox checkBoxMinPollingDate;
	private TextBox textMinPollingDate;
	private CheckBox checkDownloadSampleData;
	private DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

	PollingPanel(ClickHandler undoHandler, ClickHandler saveHandler) {
		// Label and panel for polling info
		Label pollingTitle = new Label();
		pollingTitle.setText(CentraleUI.getMessages().lbl_station_polling_info());
		pollingTitle.setStyleName("gwt-Label-title");

		// panel that contains the polling grid
		VerticalPanel contentPanel = new VerticalPanel();
		contentPanel.setStyleName("gwt-post-boxed");

		// format the grid polling info
		FlexTable pollingGrid = new FlexTable();
		contentPanel.add(pollingGrid);
		pollingGrid.getCellFormatter().setWidth(0, 0, "450px");
		pollingGrid.getCellFormatter().setWidth(0, 1, "470px");
		pollingGrid.setCellSpacing(5);
		pollingGrid.setCellPadding(5);

		pollingGrid.setText(0, 0, CentraleUI.getMessages().lbl_enabled());
		checkBoxEnabled = new CheckBox();
		pollingGrid.setWidget(0, 1, checkBoxEnabled);

		pollingGrid.setText(1, 0, CentraleUI.getMessages()
				.lbl_download_sample_data_enabled());
		HorizontalPanel hPanel3 = new HorizontalPanel();
		checkDownloadSampleData = new CheckBox();
		hPanel3.add(checkDownloadSampleData);
		Label warning = new Label();
		warning.setText(CentraleUI.getMessages().lbl_warnig_download_sample_data());
		hPanel3.add(warning);
		pollingGrid.setWidget(1, 1, hPanel3);

		pollingGrid.setText(2, 0, CentraleUI.getMessages().lbl_min_polling_date());
		HorizontalPanel hPanel = new HorizontalPanel();
		checkBoxMinPollingDate = new CheckBox();
		hPanel.add(checkBoxMinPollingDate);
		textMinPollingDate = new TextBox();
		textMinPollingDate.setStyleName("gwt-bg-text-orange");
		textMinPollingDate.setWidth("200px");
		textMinPollingDate.setVisible(false);
		hPanel.add(textMinPollingDate);
		final Label dayFormat = new Label();
		dayFormat.setText(CentraleUI.getMessages().day_format());
		dayFormat.setVisible(false);
		hPanel.add(dayFormat);
		pollingGrid.setWidget(2, 1, hPanel);
		checkBoxMinPollingDate
				.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							textMinPollingDate.setVisible(true);
							dayFormat.setVisible(true);
						} else {
							textMinPollingDate.setVisible(false);
							dayFormat.setVisible(false);
						}
					}
				});

		pollingGrid.setText(3, 0, CentraleUI.getMessages().lbl_force_polling_time());
		textForcePollingTime = new TextBox();
		textForcePollingTime.setStyleName("gwt-bg-text-orange");
		textForcePollingTime.setWidth("40px");
		HorizontalPanel forcePollingPanel = new HorizontalPanel();
		forcePollingPanel.add(textForcePollingTime);
		Label minuteFormat = new Label();
		minuteFormat.setText(CentraleUI.getMessages().minute_format());
		forcePollingPanel.add(minuteFormat);
		pollingGrid.setWidget(3, 1, forcePollingPanel);
		textForcePollingTime.addKeyPressHandler(new NumericKeyPressHandler(
				true, true));

		// create buttons for undo and save
		PanelButtonWidget panelButton3 = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		// button cancel for polling info
		panelButton3.addButton(CentraleUIConstants.UNDO, undoHandler);

		// button send/verify
		panelButton3.addButton(CentraleUIConstants.SAVE, saveHandler);

		// panel that contains button
		HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setStyleName("gwt-button-panel");
		hPanel4.add(panelButton3);
		hPanel4.setSpacing(10);
		hPanel4.setCellHorizontalAlignment(panelButton3,
				HasHorizontalAlignment.ALIGN_CENTER);
		contentPanel.add(hPanel4);

		add(pollingTitle);
		add(contentPanel);
	}

	boolean isPollingEnabled() {
		return checkBoxEnabled.getValue();
	}

	void setPollingEnabled(boolean value) {
		checkBoxEnabled.setValue(value);
	}

	boolean isSampleDataDownloadEnabled() {
		return checkDownloadSampleData.getValue();
	}

	void setSampleDataDownloadEnabled(boolean value) {
		checkDownloadSampleData.setValue(value);
	}

	Integer getCustomPollingPeriod() throws NumberFormatException {
		String txtPeriod = textForcePollingTime.getText();
		if (txtPeriod == null || txtPeriod.trim().isEmpty())
			return null;
		return new Integer(txtPeriod.trim());
	}

	void setCustomPollingPeriod(Integer period) {
		textForcePollingTime.setText(period == null ? "" : period.toString());
	}

	Date getMinimumDateForDownload() throws IllegalArgumentException {
		if (!checkBoxMinPollingDate.getValue())
			return null;
		return dtf.parseStrict(textMinPollingDate.getText().trim());
	}

	void setMinimumDateForDownload(Date date) {
		if (date == null) {
			checkBoxMinPollingDate.setValue(false, true);
			textMinPollingDate.setText(dtf.format(new Date()));
		} else {
			checkBoxMinPollingDate.setValue(true, true);
			textMinPollingDate.setText(dtf.format(date));
		}
	}

	void setPollingInfo(StInfoObject stationInfo) {
		setPollingEnabled(stationInfo.isEnabled());
		setCustomPollingPeriod(stationInfo.getForcePollingTime());
		setMinimumDateForDownload(stationInfo.getMinTimestampForPolling());
		setSampleDataDownloadEnabled(stationInfo.isDownloadSampleDataEnabled());
	}

	void setPollingDefaults() {
		setPollingEnabled(true);
		setCustomPollingPeriod(null);
		setSampleDataDownloadEnabled(false);
		setMinimumDateForDownload(null);
	}

}
