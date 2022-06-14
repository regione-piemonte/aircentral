/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: page for viewing status of the alarms of all the analyzers 
* of a selected station
* Change log:
*   2009-01-15: initial version
* ----------------------------------------------------------------------------
* $Id: AnalyzersStatusWidget.java,v 1.23 2014/09/18 09:46:58 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import java.util.HashMap;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusValuesObject;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/**
 * Page for viewing status of the alarms of all the analyzers of a selected
 * station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class AnalyzersStatusWidget extends UIPage {

	private VerticalPanel externalPanel;

	private final FlexTable table;

	private int stationId;

	private String stationName;

	private HashMap<Button, ButtonAccessories> map = new HashMap<Button, ButtonAccessories>();

	public AnalyzersStatusWidget() {
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
		panelButton.setEnabledButton(CentraleUIConstants.ANALYZER_STATUS, false);

		panelButton.addButton(CentraleUIConstants.STATION_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.stationAlarmStatusWidget.setStNameAndId(stationName, stationId);
				CentraleUI.setCurrentPage(CentraleUI.stationAlarmStatusWidget);
			}
		});

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
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#view_analyzerStatusWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().analyzers_status_title(),
				panelButton, false);

		// Label and panel for analyzers info
		Label title = new Label();
		title.setStyleName("gwt-Label-title-blue");
		title.setText(CentraleUI.getMessages().analyzers_status_fault_title());

		// panel that contains analyzers info and buttons
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed-blue");

		// Prepare info table's title
		FlexTable headerTable = new FlexTable();
		headerTable.setText(0, 0, CentraleUI.getMessages().analyzer_status_link_history());
		headerTable.setText(0, 1, CentraleUI.getMessages().analyzers_status_id());
		headerTable.setText(0, 2, CentraleUI.getMessages().analyzers_status_brand_model());
		headerTable.setText(0, 3, CentraleUI.getMessages().analyzers_status_fault());
		headerTable.setText(0, 4, CentraleUI.getMessages().analyzers_status_maintenance());
		headerTable.setText(0, 5, CentraleUI.getMessages().analyzers_status_calibration_manual());
		// TODO inserire queste colonne solo per gli strumenti che ce l'hanno
		headerTable.setText(0, 6, CentraleUI.getMessages().analyzers_status_autocalibration());
		headerTable.setText(0, 7, CentraleUI.getMessages().analyzers_status_autocalibration_failure());

		headerTable.setStyleName("gwt-table-header");
		headerTable.setWidth("100%");
		headerTable.getCellFormatter().setWidth(0, 0, "80px");
		headerTable.getCellFormatter().setWidth(0, 1, "100px");
		headerTable.getCellFormatter().setWidth(0, 2, "200px");
		headerTable.getCellFormatter().setWidth(0, 3, "80px");
		headerTable.getCellFormatter().setWidth(0, 4, "80px");
		headerTable.getCellFormatter().setWidth(0, 5, "80px");
		// TODO inserire queste colonne solo per gli strumenti che ce l'hanno
		headerTable.getCellFormatter().setWidth(0, 6, "80px");
		headerTable.getCellFormatter().setWidth(0, 7, "80px");
		for (int j = 0; j < 8; j++) {
			headerTable.getCellFormatter().setStyleName(0, j, "gwt-table-header");
		}

		panel.add(headerTable);

		// Prepare table for channel info in a ScrollPanel
		ScrollPanel scrollPanel = new ScrollPanel();
		table = new FlexTable();
		table.setStyleName("gwt-table-data");
		table.setWidth("100%");
		scrollPanel.add(table);
		scrollPanel.setHeight("400px");
		panel.add(scrollPanel);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		externalPanel.add(title);
		externalPanel.add(panel);

		initWidget(externalPanel);
	}

	public void setStNameAndId(String name, int stationId) {
		this.stationId = stationId;
		this.stationName = name;
	}

	private void setFields() {
		Label pageTitle = (Label) (((HorizontalPanel) (externalPanel.getWidget(0))).getWidget(0));
		pageTitle.setText(stationName);
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<List<AnalyzerAlarmStatusObject>> callback = new UIAsyncCallback<List<AnalyzerAlarmStatusObject>>() {
			public void onSuccess(List<AnalyzerAlarmStatusObject> analyzerAlarmsList) {
				for (int i = 0; i < analyzerAlarmsList.size(); i++) {
					AnalyzerAlarmStatusObject analyzerAlarmObj = analyzerAlarmsList.get(i);

					// prepare buttonAccessories
					ButtonAccessories buttonAccessories = new ButtonAccessories();
					buttonAccessories.stationName = stationName;
					buttonAccessories.analyzerId = new Integer(analyzerAlarmObj.getAnalyzerId()).toString();
					buttonAccessories.analyzerName = analyzerAlarmObj.getAnalyzerName();

					// create button for link to history page
					Button historyButton = new Button();
					historyButton.setStyleName("gwt-button-history");
					historyButton.setTitle(CentraleUI.getMessages().analyzer_status_link_history());
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
							new ChoosePeriodWidget(false, true, accessories, ChoosePeriodWidget.ANALYZER_ALARM_HISTORY);
						}
					});

					table.setText(i, 1, analyzerAlarmObj.getAnalyzerName());
					table.setText(i, 2, analyzerAlarmObj.getBrandModel());

					for (int j = 0; j < analyzerAlarmObj.getAnAlarmValues().size(); j++) {
						if (j == 0) {
							// set fault status icons
							AnalyzerAlarmStatusValuesObject anAlarmValue = analyzerAlarmObj.getAnAlarmValues().get(0);
							if (anAlarmValue.getValue() == null) {
								// gray icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGray = new Image();
								ledGray.setResource(iconImageBundle.ledGray());
								table.setWidget(i, 3, ledGray);
								table.getWidget(i, 3).setTitle(CentraleUI.getMessages().not_enabled());
							} else if (anAlarmValue.getValue().booleanValue()) {
								// red icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledRed = new Image();
								ledRed.setResource(iconImageBundle.ledRed());
								table.setWidget(i, 3, ledRed);
								table.getWidget(i, 3).setTitle(CentraleUI.getMessages().alarm());
							} else {
								// green icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGreen = new Image();
								ledGreen.setResource(iconImageBundle.ledGreen());
								table.setWidget(i, 3, ledGreen);
								table.getWidget(i, 3).setTitle(CentraleUI.getMessages().ok());
							}
						} else if (j == 1) {
							// set status icons for manteinance and manual
							// calibration
							AnalyzerAlarmStatusValuesObject anAlarmValue = analyzerAlarmObj.getAnAlarmValues().get(1);
							if (anAlarmValue.getValue() != null && anAlarmValue.getValue().booleanValue()) {
								// yellow icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledYellow = new Image();
								ledYellow.setResource(iconImageBundle.ledYellow());
								table.setWidget(i, 4, ledYellow);
								table.getWidget(i, 4).setTitle(CentraleUI.getMessages().alarm());
							} else if (anAlarmValue.getValue() != null && !anAlarmValue.getValue().booleanValue()) {
								// gray icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGray = new Image();
								ledGray.setResource(iconImageBundle.ledGray());
								table.setWidget(i, 4, ledGray);
								table.getWidget(i, 4).setTitle(CentraleUI.getMessages().not_enabled());
							} else
								table.setText(i, 4, " ");
						} else if (j == 2) {
							// set status icons for manual calibration
							AnalyzerAlarmStatusValuesObject anAlarmValue = analyzerAlarmObj.getAnAlarmValues().get(2);
							if (anAlarmValue.getValue() != null && anAlarmValue.getValue().booleanValue()) {
								// yellow icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledYellow = new Image();
								ledYellow.setResource(iconImageBundle.ledYellow());
								table.setWidget(i, 5, ledYellow);
								table.getWidget(i, 5).setTitle(CentraleUI.getMessages().alarm());
							} else if (anAlarmValue.getValue() != null && !anAlarmValue.getValue().booleanValue()) {
								// gray icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGray = new Image();
								ledGray.setResource(iconImageBundle.ledGray());
								table.setWidget(i, 5, ledGray);
								table.getWidget(i, 5).setTitle(CentraleUI.getMessages().not_enabled());
							} else
								table.setText(i, 5, " ");
						} else if (j == 3) {
							// set status icons for running calibration
							AnalyzerAlarmStatusValuesObject anAlarmValue = analyzerAlarmObj.getAnAlarmValues().get(3);
							if (anAlarmValue.getValue() != null && anAlarmValue.getValue().booleanValue()) {
								// green icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGreen = new Image();
								ledGreen.setResource(iconImageBundle.ledGreen());
								table.setWidget(i, 6, ledGreen);
								table.getWidget(i, 6).setTitle(CentraleUI.getMessages().alarm());
							} else if (anAlarmValue.getValue() != null && !anAlarmValue.getValue().booleanValue()) {
								// gray icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGray = new Image();
								ledGray.setResource(iconImageBundle.ledGray());
								table.setWidget(i, 6, ledGray);
								table.getWidget(i, 6).setTitle(CentraleUI.getMessages().ok());
							} else
								table.setText(i, 6, " ");
						} else if (j == 4) {
							// set status icons for failed auto calibration
							AnalyzerAlarmStatusValuesObject anAlarmValue = analyzerAlarmObj.getAnAlarmValues().get(4);
							if (anAlarmValue.getValue() != null && anAlarmValue.getValue().booleanValue()) {
								// red icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledRed = new Image();
								ledRed.setResource(iconImageBundle.ledRed());
								table.setWidget(i, 7, ledRed);
								table.getWidget(i, 7).setTitle(CentraleUI.getMessages().alarm());
							} else if (anAlarmValue.getValue() != null && !anAlarmValue.getValue().booleanValue()) {
								// gray icon
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								Image ledGray = new Image();
								ledGray.setResource(iconImageBundle.ledGray());
								table.setWidget(i, 7, ledGray);
								table.getWidget(i, 7).setTitle(CentraleUI.getMessages().ok());
							} else
								table.setText(i, 7, " ");
						}
					} // end for j

					for (int j = 1; j < 8; j++) {
						if (j > 2)
							table.getCellFormatter().setAlignment(i, j, HasHorizontalAlignment.ALIGN_CENTER,
									HasVerticalAlignment.ALIGN_MIDDLE);
						table.getCellFormatter().setStyleName(i, j, "gwt-table-data");
					}
					map.put(historyButton, buttonAccessories);
				} // end for i
			}

		};

		centraleService.getAnalyzersStatusFields(stationId, callback);
	}

	@Override
	protected void reset() {
		// clear table
		Utils.clearTable(table);
		table.getCellFormatter().setWidth(0, 0, "80px");
		table.getCellFormatter().setWidth(0, 1, "100px");
		table.getCellFormatter().setWidth(0, 2, "200px");
		table.getCellFormatter().setWidth(0, 3, "80px");
		table.getCellFormatter().setWidth(0, 4, "80px");
		table.getCellFormatter().setWidth(0, 5, "80px");
		table.getCellFormatter().setWidth(0, 6, "80px");
		table.getCellFormatter().setWidth(0, 7, "80px");
	}

	@Override
	protected void loadContent() {
		setFields();
	}
}
