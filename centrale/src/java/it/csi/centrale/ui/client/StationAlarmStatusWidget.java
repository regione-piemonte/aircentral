/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: page for viewing status of the alarms of a selected station
* Change log:
*   2009-01-16: initial version
* ----------------------------------------------------------------------------
* $Id: StationAlarmStatusWidget.java,v 1.18 2014/09/18 09:46:57 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.AlarmStatusObject;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for viewing status of the alarms of a selected station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationAlarmStatusWidget extends UIPage {

	private VerticalPanel externalPanel;

	private final FlexTable table;

	private int stationId = -1;

	private String stationName;

	private HashMap<Button, ButtonAccessories> map = new HashMap<Button, ButtonAccessories>();

	private DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");

	public StationAlarmStatusWidget() {
		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.BLUE);

		panelButton.addButton(CentraleUIConstants.INFORMATIC_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.informaticStatusWidget.setStNameAndId(stationName, stationId);
				CentraleUI.setCurrentPage(CentraleUI.informaticStatusWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.ANALYZER_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.analyzersStatusWidget.setStNameAndId(stationName, stationId);
				CentraleUI.setCurrentPage(CentraleUI.analyzersStatusWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.STATION_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.stationAlarmStatusWidget.setStNameAndId(stationName, stationId);
				CentraleUI.setCurrentPage(CentraleUI.stationAlarmStatusWidget);
			}
		});
		panelButton.setEnabledButton(CentraleUIConstants.STATION_STATUS, false);

		ClickHandler refreshClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.updateCurrentPage(null);
			}
		};
		panelButton.addButton(CentraleUIConstants.REFRESH, refreshClickHandler);
		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#view_stationStatusWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().station_status_title(), panelButton,
				false);
		// Label and panel for subdevice info
		Label title = new Label();
		title.setStyleName("gwt-Label-title-blue");
		title.setText(CentraleUI.getMessages().station_status_alarm_title());

		// panel that contains subdevice info and buttons
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed-blue");

		// Prepare info table's title
		FlexTable headerTable = new FlexTable();
		headerTable.setText(0, 0, CentraleUI.getMessages().station_status_link_history());
		headerTable.setText(0, 1, CentraleUI.getMessages().station_status_alarm_id());
		headerTable.setText(0, 2, CentraleUI.getMessages().station_status_alarm_description());
		headerTable.setText(0, 3, CentraleUI.getMessages().notes());
		headerTable.setText(0, 4, CentraleUI.getMessages().station_status_alarm_value());
		headerTable.setText(0, 5, CentraleUI.getMessages().station_status_alarm_timestamp());
		headerTable.setStyleName("gwt-table-header");
		headerTable.setWidth("100%");
		headerTable.getCellFormatter().setWidth(0, 0, "80px");
		headerTable.getCellFormatter().setWidth(0, 1, "100px");
		headerTable.getCellFormatter().setWidth(0, 2, "250px");
		headerTable.getCellFormatter().setWidth(0, 3, "250px");
		headerTable.getCellFormatter().setWidth(0, 4, "100px");
		headerTable.getCellFormatter().setWidth(0, 5, "200px");
		for (int j = 0; j < 6; j++) {
			headerTable.getCellFormatter().setStyleName(0, j, "gwt-table-header");
		}

		panel.add(headerTable);

		// Prepare table for channel info in a ScrollPanel
		ScrollPanel scrollPanel = new ScrollPanel();
		table = new FlexTable();
		table.setStyleName("gwt-table-data");
		table.setWidth("100%");
		scrollPanel.add(table);
		scrollPanel.setHeight("500px");
		panel.add(scrollPanel);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		externalPanel.add(title);
		externalPanel.add(panel);

		initWidget(externalPanel);
	}

	public void setStNameAndId(String stationName, int stationId) {
		this.stationId = stationId;
		this.stationName = stationName;
	}

	private void setFields() {
		Label pageTitle = (Label) (((HorizontalPanel) (externalPanel.getWidget(0))).getWidget(0));
		pageTitle.setText(stationName);
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<List<AlarmStatusObject>> callback = new UIAsyncCallback<List<AlarmStatusObject>>() {
			public void onSuccess(List<AlarmStatusObject> stationAlarmList) {
				for (int i = 0; i < stationAlarmList.size(); i++) {
					AlarmStatusObject alarmStatusObj = stationAlarmList.get(i);

					// prepare buttonAccessories
					ButtonAccessories buttonAccessories = new ButtonAccessories();
					buttonAccessories.stationName = stationName;
					buttonAccessories.alarmId = alarmStatusObj.getAlarmDbId();
					buttonAccessories.alarmName = alarmStatusObj.getAlarmDescription();
					// create button for link to history page
					Button historyButton = new Button();
					historyButton.setStyleName("gwt-button-history");
					historyButton.setTitle(CentraleUI.getMessages().station_status_link_history());
					table.setWidget(i, 0, historyButton);
					table.getCellFormatter().setAlignment(i, 0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					table.getCellFormatter().setStyleName(i, 0, "gwt-table-data");
					historyButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Utils.blockForPopup("popup");
							// create and show popup of ChoosePeriodWidget
							ButtonAccessories accessories = map.get(event.getSource());
							new ChoosePeriodWidget(false, true, accessories, ChoosePeriodWidget.STATION_ALARM_HISTORY);
						}
					});

					table.setText(i, 1, alarmStatusObj.getAlarmId());
					table.setText(i, 2, alarmStatusObj.getAlarmDescription());
					table.setText(i, 3, alarmStatusObj.getNotes());

					// set status icons
					if ("1".equals(alarmStatusObj.getStatus()) || "A".equals(alarmStatusObj.getStatus())) {
						// red icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						table.setWidget(i, 4, ledRed);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().alarm());
					} else if ("0".equals(alarmStatusObj.getStatus()) || "OK".equals(alarmStatusObj.getStatus())) {
						// green icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						table.setWidget(i, 4, ledGreen);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().ok());
					} else if ("W".equals(alarmStatusObj.getStatus())) {
						// yellow icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image ledYellow = new Image();
						ledYellow.setResource(iconImageBundle.ledYellow());
						table.setWidget(i, 4, ledYellow);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().warning());
					} else if ("WH".equals(alarmStatusObj.getStatus())) {
						// warning high
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image warningHigh = new Image();
						warningHigh.setResource(iconImageBundle.warningHigh());
						table.setWidget(i, 4, warningHigh);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().warning_high());
					} else if ("WL".equals(alarmStatusObj.getStatus())) {
						// warning low
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image warningLow = new Image();
						warningLow.setResource(iconImageBundle.warningLow());
						table.setWidget(i, 4, warningLow);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().warning_low());
					} else if ("AH".equals(alarmStatusObj.getStatus())) {
						// alarm high
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image alarmHigh = new Image();
						alarmHigh.setResource(iconImageBundle.alarmHigh());
						table.setWidget(i, 4, alarmHigh);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().alarm_high());
					} else if ("AL".equals(alarmStatusObj.getStatus())) {
						// alarm low
						IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
						Image alarmLow = new Image();
						alarmLow.setResource(iconImageBundle.alarmLow());
						table.setWidget(i, 4, alarmLow);
						table.getWidget(i, 4).setTitle(CentraleUI.getMessages().alarm_low());
					}
					table.getCellFormatter().setAlignment(i, 4, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);

					if (alarmStatusObj.getTimestamp() != null)
						table.setText(i, 5, df.format(alarmStatusObj.getTimestamp()));
					else
						table.setText(i, 5, " ");

					for (int j = 1; j < 6; j++)
						table.getCellFormatter().setStyleName(i, j, "gwt-table-data");
					map.put(historyButton, buttonAccessories);
				} // end for i
			}

		};

		centraleService.getStationAlarmStatus(stationId, callback);
	}

	@Override
	protected void reset() {
		// clear table
		Utils.clearTable(table);
		table.getCellFormatter().setWidth(0, 0, "80px");
		table.getCellFormatter().setWidth(0, 1, "100px");
		table.getCellFormatter().setWidth(0, 2, "250px");
		table.getCellFormatter().setWidth(0, 3, "250px");
		table.getCellFormatter().setWidth(0, 4, "100px");
		table.getCellFormatter().setWidth(0, 5, "200px");
	}

	@Override
	protected void loadContent() {
		setFields();
	}

}
