/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: this class shows history data table (alarm, sample data, 
 * wind data).
 * Change log:
 *   2008-12-05: initial version
 * ----------------------------------------------------------------------------
 * $Id: HistoryTableWidget.java,v 1.25 2015/01/19 12:01:05 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.AlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusValuesObject;
import it.csi.centrale.ui.client.data.common.DataObject;
import it.csi.centrale.ui.client.data.common.WindDataObject;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This class shows history data table (alarm, sample data, wind data).
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class HistoryTableWidget extends UIPage {

	private static final int MAX_TABLE_DATA = 700;

	private static final String OK = "OK";

	private static final String ALARM = "A";

	private static final String WARNING = "W";

	private static final String ALARM_HIGH = "AH";

	private static final String ALARM_LOW = "AL";

	private static final String WARNING_HIGH = "WH";

	private static final String WARNING_LOW = "WL";

	private static final String TRUE = "1";

	private static final String FALSE = "0";

	private static final int BUTTON_HOUR = 0;

	private static final int BUTTON_MINUTES = 1;

	private static final int SAMPLE = 1;
	private static final int MEANS = 2;

	private Label title = new Label();

	private Hidden functionHidden;

	private Hidden startDateHidden;

	private Hidden endDateHidden;

	private Hidden numDec;
	private Hidden dirNumDec;
	private Hidden avgPeriod;

	private final FlexTable table;

	private FlexTable headerDataTable;

	private FlexTable headerAlarmTable;

	private FlexTable headerWindDataTable;

	private int type;

	private boolean isSampleData;
	private String startDate;
	private String endDate;

	private String lblTitle;

	private List<DataObject> dataList;

	private String dateString;

	private Boolean callFromChoosePeriodWidget = null;

	public Boolean getCallFromChoosePeriodWidget() {
		return callFromChoosePeriodWidget;
	}

	public void setCallFromChoosePeriodWidget(Boolean callFromChoosePeriodWidget) {
		this.callFromChoosePeriodWidget = callFromChoosePeriodWidget;
	}

	private Boolean buttonAlreadyShown = null;

	public Boolean isButtonAlreadyShown() {
		return buttonAlreadyShown;
	}

	public void setButtonAlreadyShown(Boolean buttonAlreadyShown) {
		this.buttonAlreadyShown = buttonAlreadyShown;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	private AnalyzerAlarmStatusObject analyzerAlarmStatusObj;

	private List<AlarmStatusObject> alarmStatusList;

	private Label lblNoData;

	private HorizontalPanel minutes10ChoicePanel;

	private VerticalPanel hourPanel = new VerticalPanel();

	private VerticalPanel minutesPanel = new VerticalPanel();

	private String hourSelectedStr = null;

	private FormPanel formPanel = new FormPanel();

	// private Hidden analyzerIdHidden;

	private Hidden elementIdHidden;

	private Hidden elementName;

	private Hidden analyzerName;

	private Hidden measureUnit;

	private Hidden stationName;

	private Hidden isWind;

	private ButtonAccessories accessories;

	private VerticalPanel externalPanel;

	String startDateText;

	String endDateText;
	PanelButtonWidget panelButton;

	public HistoryTableWidget() {
		panelButton = new PanelButtonWidget(PanelButtonWidget.BLUE);

		ClickHandler backClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (type == ChoosePeriodWidget.ANALYZER_ALARM
						|| type == ChoosePeriodWidget.STATION_ALARM)
					CentraleUI.goToPreviousPage();
				else {
					CentraleUI.viewStationDataWidget.setStationInfos(
							accessories.stationId, accessories.stationName);
					CentraleUI.setCurrentPage(CentraleUI.viewStationDataWidget);
				}
			}
		};
		panelButton.addButton(CentraleUIConstants.BACK, backClickHandler);

		externalPanel = CentraleUI.getTitledExternalPanel("", panelButton,
				false);
		// Label and panel for sub device info
		title.setStyleName("gwt-Label-title-blue");

		// panel that contains sub device info and buttons
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed-blue");

		// horizontal panel for table and hour buttons
		HorizontalPanel hPanel = new HorizontalPanel();
		panel.add(hPanel);

		// Vertical panel containing header table and table
		VerticalPanel vPanel = new VerticalPanel();

		headerDataTable = new FlexTable();
		headerAlarmTable = new FlexTable();
		headerWindDataTable = new FlexTable();
		// Prepare info table's title for history data
		headerDataTable.setText(0, 0, CentraleUI.getMessages().data());
		headerDataTable.setText(0, 1, CentraleUI.getMessages().value());
		headerDataTable.setText(0, 2, CentraleUI.getMessages().flag());
		headerDataTable.setText(0, 3, CentraleUI.getMessages().multiple_flag());

		headerDataTable.setStyleName("gwt-table-header");
		headerDataTable.setWidth("100%");
		headerDataTable.getCellFormatter().setWidth(0, 0, "356px");
		headerDataTable.getCellFormatter().setWidth(0, 1, "178px");
		headerDataTable.getCellFormatter().setWidth(0, 2, "178px");
		headerDataTable.getCellFormatter().setWidth(0, 3, "193px");
		for (int j = 0; j < headerDataTable.getCellCount(0); j++) {
			headerDataTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}
		vPanel.add(headerDataTable);
		headerDataTable.setVisible(false);

		// Prepare info table's title for history alarm
		headerAlarmTable.setText(0, 0, CentraleUI.getMessages().data());
		headerAlarmTable.setText(0, 1, CentraleUI.getMessages().lbl_status());
		headerAlarmTable.setStyleName("gwt-table-header");
		headerAlarmTable.setWidth("100%");
		headerAlarmTable.getCellFormatter().setWidth(0, 0, "200px");
		headerAlarmTable.getCellFormatter().setWidth(0, 1, "200px");
		for (int j = 0; j < headerAlarmTable.getCellCount(0); j++) {
			headerAlarmTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}
		vPanel.add(headerAlarmTable);
		headerAlarmTable.setVisible(false);

		// Prepare info table's title for wind data
		headerWindDataTable.setText(0, 0, CentraleUI.getMessages().data());
		headerWindDataTable
				.setText(0, 1, CentraleUI.getMessages().vectorial_speed());
		headerWindDataTable.setText(0, 2,
				CentraleUI.getMessages().vectorial_direction());
		headerWindDataTable.setText(0, 3,
				CentraleUI.getMessages().standard_deviation());
		headerWindDataTable.setText(0, 4, CentraleUI.getMessages().scalar_speed());
		headerWindDataTable.setText(0, 5, CentraleUI.getMessages().gust_speed());
		headerWindDataTable.setText(0, 6, CentraleUI.getMessages().gust_direction());
		headerWindDataTable.setText(0, 7,
				CentraleUI.getMessages().calm_number_percent());
		headerWindDataTable.setText(0, 8, CentraleUI.getMessages().calm());
		headerWindDataTable.setText(0, 9, CentraleUI.getMessages().multiple_flag());
		headerWindDataTable.setStyleName("gwt-table-header");
		headerWindDataTable.setWidth("100%");
		headerWindDataTable.getCellFormatter().setWidth(0, 0, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 1, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 2, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 3, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 4, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 5, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 6, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 7, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 8, "90px");
		headerWindDataTable.getCellFormatter().setWidth(0, 9, "90px");
		for (int j = 0; j < headerWindDataTable.getCellCount(0); j++) {
			headerWindDataTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}
		vPanel.add(headerWindDataTable);
		headerWindDataTable.setVisible(false);

		// form panel that contains hidden fields for csv
		formPanel.setAction("./exportService");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_GET);
		VerticalPanel vPanelForForm = new VerticalPanel();
		formPanel.setWidget(vPanelForForm);
		Hidden localeHidden = new Hidden();
		localeHidden.setName("locale");
		localeHidden.setValue(CentraleUI.getLocale());
		vPanelForForm.add(localeHidden);

		isWind = new Hidden();
		isWind.setName("iswind");
		vPanelForForm.add(isWind);

		functionHidden = new Hidden();
		functionHidden.setName("function");
		functionHidden.setValue((isSampleData ? "getSampleData"
				: "getMeansData"));
		vPanelForForm.add(functionHidden);

		elementName = new Hidden();
		elementName.setName("elementName");
		vPanelForForm.add(elementName);

		analyzerName = new Hidden();
		analyzerName.setName("analyzerName");
		vPanelForForm.add(analyzerName);

		measureUnit = new Hidden();
		measureUnit.setName("measureUnitStr");
		vPanelForForm.add(measureUnit);

		stationName = new Hidden();
		stationName.setName("stationName");
		vPanelForForm.add(stationName);

		elementIdHidden = new Hidden();
		elementIdHidden.setName("elementIdStr");
		vPanelForForm.add(elementIdHidden);

		startDateHidden = new Hidden();
		startDateHidden.setName("startDate");
		vPanelForForm.add(startDateHidden);

		endDateHidden = new Hidden();
		endDateHidden.setName("endDate");
		vPanelForForm.add(endDateHidden);

		numDec = new Hidden();
		numDec.setName("numDec");
		vPanelForForm.add(numDec);

		dirNumDec = new Hidden();
		dirNumDec.setName("dirNumDec");
		vPanelForForm.add(dirNumDec);

		avgPeriod = new Hidden();
		avgPeriod.setName("avgPeriod");
		vPanelForForm.add(avgPeriod);

		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String msg = event.getResults().substring("<pre>".length(),
						event.getResults().length() - "</pre>".length());
				Window.alert(msg);
			}
		});
		panel.add(formPanel);

		// Prepare hour and 10 minutes choice buttons
		minutes10ChoicePanel = new HorizontalPanel();
		minutes10ChoicePanel.setVisible(false);

		minutes10ChoicePanel.add(hourPanel);
		minutes10ChoicePanel.add(minutesPanel);
		for (int i = 0; i < 24; i++) {
			StringBuffer buttonValue = new StringBuffer(
					new Integer(i).toString());
			if (i < 10)
				buttonValue.insert(0, "0");
			Button hourButton = new Button(buttonValue.toString());
			hourButton.setStyleName("gwt-button-hour");
			hourPanel.add(hourButton);
			hourButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Button pressedButton = (Button) event.getSource();
					clearButtons(BUTTON_HOUR);
					pressedButton.setStyleName("gwt-button-hour-selected");
					// TODO: capire se va bene
					hourSelectedStr = pressedButton.getText();
					startDateText = dateString + " " + pressedButton.getText()
							+ ":00:00";
					endDateText = dateString + " " + pressedButton.getText()
							+ ":59:59";
					getData();

				}

			});
		}
		for (int i = 0; i < 60; i += 10) {
			StringBuffer buttonValue = new StringBuffer(
					new Integer(i).toString());
			if (i < 10)
				buttonValue.insert(0, "0");
			Button minutesButton = new Button(buttonValue.toString());
			minutesButton.setStyleName("gwt-button-hour");
			minutesPanel.add(minutesButton);
			minutesButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Button pressedButton = (Button) event.getSource();
					clearButtons(BUTTON_MINUTES);
					pressedButton.setStyleName("gwt-button-hour-selected");
					// TODO: capire se va bene cosi' per capire che bottone e'
					if (hourSelectedStr != null) {
						startDateText = dateString + " " + hourSelectedStr
								+ ":" + pressedButton.getText() + ":00";
						endDateText = dateString + " " + hourSelectedStr + ":"
								+ pressedButton.getText().charAt(0) + "9:59";
						getData();
					}

				}
			});
		}
		hPanel.add(minutes10ChoicePanel);

		panelButton.addButton(CentraleUIConstants.CHART, new ClickHandler() {
			public void onClick(ClickEvent event) {

				CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
						.create(CentraleUIService.class);
				ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
				endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
						+ "uiservice");
				AsyncCallback<String> callback = new UIAsyncCallback<String>() {
					public void onSuccess(String result) {
						Utils.unlockForPopup("loading");
						String title = CentraleUI.getMessages().means_data() + " "
								+ accessories.stationName;
						title = CentraleUI.getMessages().sample_data() + " "
								+ accessories.stationName;
						CentraleUI.showChartWidget
								.setButtonAlreadyShown(buttonAlreadyShown);
						CentraleUI.showChartWidget.setChartNameAndTitle(result,
								title);
						CentraleUI.setCurrentPage(CentraleUI.showChartWidget);
					}// end onSuccess

					@Override
					public void onFailure(Throwable caught) {
						Utils.unlockForPopup("loading");
						super.onFailure(caught);
					}
				};
				Utils.blockForPopup("loading");
				String elementType = null;
				String elementId = null;
				String measureUnit = null;
				if (accessories.dirMeasureUnit != null) {
					// wind case
					elementType = ChoosePeriodWidget.WIND;
					// TODO gestire il vento
					// String[] idMeasure = separateItem(windList
					// .getValue(windList.getSelectedIndex()));
					// elementId = idMeasure[0];
					// accessories.elementName = windList
					// .getItemText(windList.getSelectedIndex());
					// measureUnit= idMeasure[1];
				} else {
					elementType = ChoosePeriodWidget.SCALAR;
					elementId = accessories.elementId;
					measureUnit = accessories.measureUnit;
				}// end else

				centraleService.createAndGetChartName((isSampleData ? SAMPLE
						: MEANS), elementId, startDate, endDate, new Integer(
						ChoosePeriodWidget.MAX_CHART_DATA), new Integer(
						accessories.avgPeriod).toString(),
						accessories.stationName, accessories.elementName,
						measureUnit, false, elementType, accessories.numDec,
						accessories.dirNumDec, callback);
			}// end onClick
		});

		panelButton.addButton(CentraleUIConstants.CSV, new ClickHandler() {
			public void onClick(ClickEvent event) { // TODO capire come va
				if (isSampleData)
					functionHidden.setValue("getSampleData");
				else
					functionHidden.setValue("getMeansData");
				formPanel.submit();
			}// end onClick
		});

		// Prepare table for history real time in a ScrollPanel
		ScrollPanel scrollPanel = new ScrollPanel();
		table = new FlexTable();
		table.setStyleName("gwt-table-data");
		table.setWidth("100%");
		scrollPanel.add(table);
		scrollPanel.setHeight("400px");
		vPanel.add(scrollPanel);
		hPanel.add(vPanel);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		externalPanel.add(title);
		externalPanel.add(panel);

		lblNoData = new Label();
		lblNoData.setText(CentraleUI.getMessages().no_data());
		lblNoData.setVisible(false);
		externalPanel.add(lblNoData);
		initWidget(externalPanel);
	}// end constructor

	private void clearButtons(int buttonPressed) {
		// resetto sempre lo stile dei minuti
		for (int j = 0; j < 6; j++) {
			Button button = (Button) minutesPanel.getWidget(j);
			button.removeStyleName("gwt-button-hour-selected");
			button.setStyleName("gwt-button-hour");
		}

		if (buttonPressed == BUTTON_HOUR) {
			// ho schiacciato il bottone delle ore quindi devo resettare anche
			// le ore
			for (int i = 0; i < 24; i++) {
				Button button = (Button) hourPanel.getWidget(i);
				button.removeStyleName("gwt-button-hour-selected");
				button.setStyleName("gwt-button-hour");
			}
			hourSelectedStr = null;
		}

	}

	private void writeData() {
		lblNoData.setVisible(false);
		showData();
	}

	private void showData() {
		// show data in table
		Iterator<DataObject> it = dataList.iterator();
		int row = 0;
		StringBuffer strBuf = null;
		NumberFormat nf = null;
		NumberFormat dirNf = null;
		while (it.hasNext()) {
			DataObject dataObj = it.next();
			int nCol = 0;
			strBuf = new StringBuffer();
			for (int z = 0; z < dataObj.getNumDec(); z++)
				strBuf.append("#");
			nf = NumberFormat.getFormat("#." + strBuf.toString());
			if (strBuf.length() > 0)
				nf = NumberFormat.getFormat("#." + strBuf.toString());
			else
				nf = NumberFormat.getFormat("#");

			if (dataObj instanceof WindDataObject) {
				// Wind data
				strBuf = new StringBuffer();
				for (int z = 0; z < ((WindDataObject) (dataObj)).getDirNumDec(); z++)
					strBuf.append("#");
				if (strBuf.length() > 0)
					dirNf = NumberFormat.getFormat("#." + strBuf.toString());
				else
					dirNf = NumberFormat.getFormat("#");
				nCol = 11;
				WindDataObject windData = (WindDataObject) dataObj;
				if (row == 0) {
					startDate = windData.getDateString();
					startDateHidden.setValue(startDate);
				} else {
					endDate = windData.getDateString();
					endDateHidden.setValue(endDate);
				}
				table.setText(row, 0, windData.getDateString());
				table.setText(
						row,
						1,
						(windData.getVectorialSpeed() != null ? nf
								.format(windData.getVectorialSpeed()) : " "));
				table.setText(
						row,
						2,
						(windData.getVectorialDirection() != null ? dirNf
								.format(windData.getVectorialDirection()) : " "));
				table.setText(
						row,
						3,
						(windData.getStandardDeviation() != null ? windData.getStandardDeviation()
								.toString() : " "));
				table.setText(
						row,
						4,
						(windData.getScalarSpeed() != null ? windData.getScalarSpeed()
								.toString() : " "));
				table.setText(
						row,
						5,
						(windData.getGustSpeed() != null ? windData.getGustSpeed()
								.toString() : " "));
				table.setText(
						row,
						6,
						(windData.getGustDirection() != null ? windData.getGustDirection()
								.toString() : " "));
				table.setText(
						row,
						7,
						(windData.getCalmsNumberPercent() != null ? windData.getCalmsNumberPercent()
								.toString() : " "));
				table.setText(
						row,
						8,
						(windData.getCalm() != null ? windData.getCalm().toString() : " "));
				ClickWidget clickWidget = new ClickWidget(windData.getTitle(),
						Integer.toHexString(windData.getMultipleFlag()));
				table.setWidget(row, 9, clickWidget);

			} else {
				// sample and mean data
				nCol = 4;
				if (row == 0) {
					if (isSampleData) {
						startDate = dataObj.getDateString();
					} else {
						startDate = dataObj.getDateString() + ":00";
					}
					startDateHidden.setValue(startDate);
				} else {
					endDate = dataObj.getDateString();
					if (isSampleData) {
						endDate = dataObj.getDateString();
					} else {
						endDate = dataObj.getDateString() + ":00";
					}
					endDateHidden.setValue(endDate);
				}
				table.setText(row, 0, dataObj.getDateString());
				table.setText(
						row,
						1,
						(dataObj.getValue() == null ? " " : nf.format(dataObj.getValue())));
				table.setText(row, 2,
						(dataObj.isFlag() ? CentraleUI.getMessages().not_valid()
								: CentraleUI.getMessages().valid()));
				ClickWidget clickWidget = new ClickWidget(dataObj.getTitle(),
						Integer.toHexString(dataObj.getMultipleFlag()));
				table.setWidget(row, 3, clickWidget);
			}// end else
			for (int col = 0; col < nCol; col++) {
				table.getCellFormatter().setAlignment(row, col,
						HasHorizontalAlignment.ALIGN_CENTER,
						HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setStyleName(row, col,
						"gwt-table-data");
			}// end for
			row++;
		}// end while
	}

	private void setFields() {
		reset();
		this.title.setText(lblTitle);
		if (type == ChoosePeriodWidget.DATA) {

			headerAlarmTable.setVisible(false);
			if (accessories.dirMeasureUnit != null) {
				// wind case
				if (accessories.windSample) {
					headerWindDataTable.setVisible(false);
					headerDataTable.setVisible(true);
					table.getCellFormatter().setWidth(0, 0, "356px");
					table.getCellFormatter().setWidth(0, 1, "178px");
					table.getCellFormatter().setWidth(0, 2, "178px");
					table.getCellFormatter().setWidth(0, 3, "193px");
				} else {
					headerDataTable.setVisible(false);
					headerWindDataTable.setVisible(true);
					table.getCellFormatter().setWidth(0, 0, "90px");
					table.getCellFormatter().setWidth(0, 1, "90px");
					table.getCellFormatter().setWidth(0, 2, "90px");
					table.getCellFormatter().setWidth(0, 3, "90px");
					table.getCellFormatter().setWidth(0, 4, "90px");
					table.getCellFormatter().setWidth(0, 5, "90px");
					table.getCellFormatter().setWidth(0, 6, "90px");
					table.getCellFormatter().setWidth(0, 7, "90px");
					table.getCellFormatter().setWidth(0, 8, "90px");
					table.getCellFormatter().setWidth(0, 9, "90px");
				}
			} else {
				// sample case
				headerWindDataTable.setVisible(false);
				headerDataTable.setVisible(true);
				table.getCellFormatter().setWidth(0, 0, "356px");
				table.getCellFormatter().setWidth(0, 1, "178px");
				table.getCellFormatter().setWidth(0, 2, "178px");
				table.getCellFormatter().setWidth(0, 3, "193px");
			}

			if (dataList == null) {
				// troppi dati da visualizzare
				minutes10ChoicePanel.setVisible(true);
				buttonAlreadyShown = new Boolean(true);
				lblNoData.setVisible(false);
				panelButton.setVisibleButton(CentraleUIConstants.CHART, false);
				panelButton.setVisibleButton(CentraleUIConstants.CSV, false);

			} else if (dataList.size() == 0) {
				// There are no data
				lblNoData.setVisible(true);
				minutes10ChoicePanel.setVisible(false);
				panelButton.setVisibleButton(CentraleUIConstants.CHART, false);
				panelButton.setVisibleButton(CentraleUIConstants.CSV, false);
			} else {
				// i dati possono essere visualizzati
				panelButton.setVisibleButton(CentraleUIConstants.CHART, true);
				panelButton.setVisibleButton(CentraleUIConstants.CSV, true);
				if (buttonAlreadyShown)
					minutes10ChoicePanel.setVisible(true);
				else
					minutes10ChoicePanel.setVisible(false);
				lblNoData.setVisible(false);
				writeData();

			}
		}// end if DATA

		if (type == ChoosePeriodWidget.ANALYZER_ALARM) {
			panelButton.setVisibleButton(CentraleUIConstants.CHART, false);
			panelButton.setVisibleButton(CentraleUIConstants.CSV, false);
			minutes10ChoicePanel.setVisible(false);

			headerDataTable.setVisible(false);
			headerWindDataTable.setVisible(false);
			headerAlarmTable.setVisible(true);

			table.getCellFormatter().setWidth(0, 0, "200px");
			table.getCellFormatter().setWidth(0, 1, "200px");

			if (analyzerAlarmStatusObj.getAnAlarmValues().size() > 0) {
				// show data in table
				Iterator<AnalyzerAlarmStatusValuesObject> it = analyzerAlarmStatusObj.getAnAlarmValues()
						.iterator();
				int row = 0;
				while (it.hasNext()) {
					AnalyzerAlarmStatusValuesObject anAlarmValue = it.next();
					table.setText(row, 0, anAlarmValue.getTimestamp().toString());
					if (anAlarmValue.getType().compareTo("fault") == 0) {
						// set fault icon
						if (anAlarmValue.getValue() == null) {
							// gray icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							table.setWidget(row, 1, ledGray);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().not_enabled());
						} else if (anAlarmValue.getValue().booleanValue()) {
							// red icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							table.setWidget(row, 1, ledRed);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().alarm());
						} else {
							// green icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							table.setWidget(row, 1, ledGreen);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().ok());
						}
					} else if (anAlarmValue.getType().compareTo("maintenance") == 0) {
						// set status icons for maintenance and manual
						// calibration
						if (anAlarmValue.getValue() != null
								&& anAlarmValue.getValue().booleanValue()) {
							// yellow icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledYellow = new Image();
							ledYellow.setResource(iconImageBundle.ledYellow());
							table.setWidget(row, 1, ledYellow);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().alarm());
						} else if (anAlarmValue.getValue() != null
								&& !anAlarmValue.getValue().booleanValue()) {
							// gray icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							table.setWidget(row, 1, ledGray);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().not_enabled());
						}
					} else if (anAlarmValue.getType().compareTo("man_calib") == 0) {
						// set status icons for manual calibration
						if (anAlarmValue.getValue() != null
								&& anAlarmValue.getValue().booleanValue()) {
							// yellow icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledYellow = new Image();
							ledYellow.setResource(iconImageBundle.ledYellow());
							table.setWidget(row, 1, ledYellow);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().alarm());
						} else if (anAlarmValue.getValue() != null
								&& !anAlarmValue.getValue().booleanValue()) {
							// gray icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							table.setWidget(row, 1, ledGray);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().not_enabled());
						}
					} else if (anAlarmValue.getType().compareTo("achk_run") == 0) {
						// set status icons for running calibration
						if (anAlarmValue.getValue() != null
								&& anAlarmValue.getValue().booleanValue()) {
							// green icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							table.setWidget(row, 1, ledGreen);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().alarm());
						} else if (anAlarmValue.getValue() != null
								&& !anAlarmValue.getValue().booleanValue()) {
							// gray icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							table.setWidget(row, 1, ledGray);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().ok());
						}
					} else if (anAlarmValue.getType().compareTo("achk_fail") == 0) {
						// set status icons for failed auto calibration
						if (anAlarmValue.getValue() != null
								&& anAlarmValue.getValue().booleanValue()) {
							// red icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							table.setWidget(row, 1, ledRed);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().alarm());
						} else if (anAlarmValue.getValue() != null
								&& !anAlarmValue.getValue().booleanValue()) {
							// gray icon
							IconImageBundle iconImageBundle = (IconImageBundle) GWT
									.create(IconImageBundle.class);
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							table.setWidget(row, 1, ledGray);
							table.getWidget(row, 1).setTitle(
									CentraleUI.getMessages().ok());
						}
					}

					for (int col = 0; col < 2; col++) {
						table.getCellFormatter().setAlignment(row, col,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						table.getCellFormatter().setStyleName(row, col,
								"gwt-table-data");
					}// end for
					row++;

				}// end while
			} else
				// There are no data
				lblNoData.setVisible(true);

		}// end if ANALYZER_ALARM

		if (type == ChoosePeriodWidget.STATION_ALARM) {
			panelButton.setVisibleButton(CentraleUIConstants.CHART, false);
			panelButton.setVisibleButton(CentraleUIConstants.CSV, false);
			minutes10ChoicePanel.setVisible(false);

			headerDataTable.setVisible(false);
			headerWindDataTable.setVisible(false);
			headerAlarmTable.setVisible(true);

			table.getCellFormatter().setWidth(0, 0, "200px");
			table.getCellFormatter().setWidth(0, 1, "200px");

			if (alarmStatusList.size() > 0) {
				lblNoData.setVisible(false);
				// show data in table
				Iterator<AlarmStatusObject> it = alarmStatusList.iterator();
				int row = 0;
				while (it.hasNext()) {
					AlarmStatusObject alarmValue = it.next();
					table.setText(row, 0, alarmValue.getTimestamp().toString());
					if (alarmValue.getStatus().compareTo(OK) == 0) {
						// green icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						table.setWidget(row, 1, ledGreen);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().ok());
					} else if (alarmValue.getStatus().compareTo(ALARM) == 0) {
						// red icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						table.setWidget(row, 1, ledRed);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().alarm());
					} else if (alarmValue.getStatus().compareTo(WARNING) == 0) {
						// yellow icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image ledYellow = new Image();
						ledYellow.setResource(iconImageBundle.ledYellow());
						table.setWidget(row, 1, ledYellow);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().warning());
					} else if (alarmValue.getStatus().compareTo(WARNING_HIGH) == 0) {
						// warning_hingh icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image warningHigh = new Image();
						warningHigh.setResource(iconImageBundle.warningHigh());
						table.setWidget(row, 1, warningHigh);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().warning_high());
					} else if (alarmValue.getStatus().compareTo(WARNING_LOW) == 0) {
						// warning_low icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image warningLow = new Image();
						warningLow.setResource(iconImageBundle.warningLow());
						table.setWidget(row, 1, warningLow);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().warning_low());
					} else if (alarmValue.getStatus().compareTo(ALARM_HIGH) == 0) {
						// alarm_high icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image alarmHigh = new Image();
						alarmHigh.setResource(iconImageBundle.alarmHigh());
						table.setWidget(row, 1, alarmHigh);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().alarm_high());
					} else if (alarmValue.getStatus().compareTo(ALARM_LOW) == 0) {
						// alarm_low icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image alarmLow = new Image();
						alarmLow.setResource(iconImageBundle.alarmLow());
						table.setWidget(row, 1, alarmLow);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().alarm_low());
					} else if (alarmValue.getStatus().compareTo(TRUE) == 0) {
						// red icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						table.setWidget(row, 1, ledRed);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().alarm());
					} else if (alarmValue.getStatus().compareTo(FALSE) == 0) {
						// green icon
						IconImageBundle iconImageBundle = (IconImageBundle) GWT
								.create(IconImageBundle.class);
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						table.setWidget(row, 1, ledGreen);
						table.getWidget(row, 1).setTitle(
								CentraleUI.getMessages().ok());
					}

					for (int col = 0; col < 2; col++) {
						table.getCellFormatter().setAlignment(row, col,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						table.getCellFormatter().setStyleName(row, col,
								"gwt-table-data");
					}// end for
					row++;

				}// end while
			} else
				// There are no data
				lblNoData.setVisible(true);

		}// end if STATION_ALARM

	}// end setFields

	@Override
	protected void reset() {
		Utils.clearTable(table);
		startDateText = null;
		endDateText = null;
		startDateHidden.setValue(null);
		endDateHidden.setValue(null);
	}

	@Override
	protected void loadContent() {
		clearButtons(BUTTON_HOUR);
		if (buttonAlreadyShown == null)
			buttonAlreadyShown = new Boolean(false);
		elementIdHidden.setValue(accessories.elementId);
		if (this.accessories.dirMeasureUnit != null)
			isWind.setValue(ChoosePeriodWidget.WIND);
		else
			isWind.setValue(ChoosePeriodWidget.SCALAR);

		numDec.setValue(new Integer(accessories.numDec).toString());
		dirNumDec.setValue(new Integer(accessories.numDec).toString());
		avgPeriod.setValue((accessories.avgPeriod == null ? ""
				: accessories.avgPeriod.toString()));
		elementName.setValue(accessories.elementName);
		analyzerName.setValue(accessories.analyzerName);
		measureUnit.setValue(accessories.measureUnit);
		stationName.setValue(accessories.stationName);
		setFields();
	}

	public void setTitle(String title) {
		CentraleUI.setTitle(externalPanel, title);
	}

	public void setLblTitle() {
		this.lblTitle = this.accessories.stationName;
		if (type == ChoosePeriodWidget.DATA
				&& this.accessories.elementName != null)
			this.lblTitle += " - " + this.accessories.elementName;
		else if (type == ChoosePeriodWidget.ANALYZER_ALARM
				&& this.accessories.analyzerName != null)
			this.lblTitle += " - " + this.accessories.analyzerName;
		else if (type == ChoosePeriodWidget.STATION_ALARM
				&& this.accessories.alarmName != null)
			this.lblTitle += " - " + this.accessories.alarmName;
	}

	public void setDataList(List<DataObject> dataList) {
		this.dataList = dataList;
	}

	public void setAnalyzerAlarmStatusList(
			AnalyzerAlarmStatusObject analyzerAlarmStatusList) {
		this.analyzerAlarmStatusObj = analyzerAlarmStatusList;
	}

	public void setAlarmStatusList(List<AlarmStatusObject> alarmStatusList) {
		this.alarmStatusList = alarmStatusList;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isSampleData() {
		return isSampleData;
	}

	public void setSampleData(boolean isSampleData) {
		this.isSampleData = isSampleData;
	}

	public void setAccessories(ButtonAccessories buttonAccessories) {
		this.accessories = buttonAccessories;

	}

	private void getData() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<List<DataObject>> callback = new UIAsyncCallback<List<DataObject>>() {
			public void onSuccess(List<DataObject> dataList) {
				Utils.unlockForPopup("loading");
				CentraleUI.historyTableWidget.setDataList(dataList);
				CentraleUI.historyTableWidget
						.setCallFromChoosePeriodWidget(false);
				CentraleUI.historyTableWidget.setType(ChoosePeriodWidget.DATA);
				setFields();

			}// end onSuccess

			@Override
			public void onFailure(Throwable caught) {
				Utils.unlockForPopup("loading");
				super.onFailure(caught);
			}

		};
		String elementType = null;
		if (accessories.dirMeasureUnit != null) {
			// wind case
			elementType = ChoosePeriodWidget.WIND;

		} else {
			elementType = ChoosePeriodWidget.SCALAR;

		}// end else

		// CentraleUI.historyTableWidget.setLblTitle();
		// CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
		int dataType = 1; // (SAMPLE = 1)
		centraleService.getHistoryData(dataType, accessories.elementId,
				startDateText, endDateText, new Integer(MAX_TABLE_DATA),
				accessories.avgPeriod.toString(), elementType,
				accessories.numDec, accessories.dirNumDec, callback);

	}
}// end class
