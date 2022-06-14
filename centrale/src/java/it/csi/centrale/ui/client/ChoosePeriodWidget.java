/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: this class provides popup for choose date.
* Change log:
*   2009-02-23: initial version
* ----------------------------------------------------------------------------
* $Id: ChoosePeriodWidget.java,v 1.19 2015/10/13 11:32:28 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.AlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusObject;
import it.csi.centrale.ui.client.data.AnalyzerAlarmStatusValuesObject;
import it.csi.centrale.ui.client.data.ElementObject;
import it.csi.centrale.ui.client.data.common.DataObject;

/**
 * This class provides popup for choose date.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ChoosePeriodWidget extends PopupPanel {

	static final int SAMPLE_HISTORY = 3;

	static final int MEANS_HISTORY = 4;

	static final int SAMPLE_DOWNLOAD_CSV = 5;

	static final int SAMPLE_DOWNLOAD_CHART = 6;

	static final int MEANS_DOWNLOAD_CSV = 7;

	static final int MEANS_DOWNLOAD_CHART = 8;

	static final int WIND_HISTORY = 9;

	static final int ANALYZER_ALARM_HISTORY = 10;

	static final int STATION_ALARM_HISTORY = 11;

	static final int DATA = 11;

	static final int ANALYZER_ALARM = 12;

	static final int STATION_ALARM = 13;

	private static final String ITEM_SEPARATOR = ":";

	public static final int MAX_CHART_DATA = 86400;

	private static final int MAX_TABLE_DATA = 700;

	public static final String SCALAR = "scalar";

	public static final String WIND = "wind";

	private static final int SAMPLE = 1;

	private static final int MEANS = 2;

	private final ChoosePeriodWidget choosePeriodPopup;

	private VerticalPanel panel;

	private HorizontalPanel hPanel1;

	private HorizontalPanel hPanel2;

	private TextBox startDate = new TextBox();

	private TextBox endDate = new TextBox();

	private TextBox startHour = new TextBox();

	private TextBox endHour = new TextBox();

	private int pageType = 0;

	private final FormPanel formPanel = new FormPanel();

	private CheckBox showMinMax = new CheckBox();

	private ListBox fieldSeparator = new ListBox();

	private ListBox decimalSeparator = new ListBox();

	private ListBox alarmList = new ListBox();

	private ListBox windList = new ListBox();

	private Hidden functionHidden;

	private Hidden avgPeriod;

	private Hidden isWind;

	private Hidden analyzerId;

	private Hidden elementId;

	private Hidden locale;

	private Hidden stationName;

	private Hidden elementName;

	private Hidden analyzerName;

	private Hidden measureUnit;

	private Hidden numDec;

	private Hidden dirNumDec;

	private ButtonAccessories buttonAccessories;

	private VerticalPanel vPanelForForm;

	public ChoosePeriodWidget(boolean autoHide, boolean modal, ButtonAccessories accessories, int pageType) {

		super(autoHide, modal);
		choosePeriodPopup = this;
		this.pageType = pageType;
		this.buttonAccessories = accessories;

		avgPeriod = new Hidden();
		avgPeriod.setName("avgPeriod");
		avgPeriod.setValue((accessories.avgPeriod == null ? "" : accessories.avgPeriod.toString()));

		// panel that contains choose period and buttons
		panel = new VerticalPanel();
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		// panel for form
		vPanelForForm = new VerticalPanel();
		vPanelForForm.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanelForForm.setSpacing(10);
		formPanel.setWidget(vPanelForForm);
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String msg = event.getResults().substring("<pre>".length(),
						event.getResults().length() - "</pre>".length());
				Window.alert(msg);

			}
		});
		formPanel.setAction("./exportService");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_GET);

		hPanel1 = new HorizontalPanel();

		functionHidden = new Hidden();
		functionHidden.setName("function");
		hPanel1.add(functionHidden);

		analyzerId = new Hidden();
		analyzerId.setName("analyzerIdStr");
		analyzerId.setValue(buttonAccessories.analyzerId);
		hPanel1.add(analyzerId);

		elementId = new Hidden();
		elementId.setName("elementIdStr");
		elementId.setValue(buttonAccessories.elementId);
		hPanel1.add(elementId);

		locale = new Hidden();
		locale.setName("locale");
		locale.setValue(CentraleUI.getLocale());
		hPanel1.add(locale);

		elementName = new Hidden();
		elementName.setName("elementName");
		elementName.setValue(buttonAccessories.elementName);
		hPanel1.add(elementName);

		analyzerName = new Hidden();
		analyzerName.setName("analyzerName");
		analyzerName.setValue(buttonAccessories.analyzerName);
		hPanel1.add(analyzerName);

		measureUnit = new Hidden();
		measureUnit.setName("measureUnitStr");
		measureUnit.setValue(buttonAccessories.measureUnit);
		hPanel1.add(measureUnit);

		stationName = new Hidden();
		stationName.setName("stationName");
		stationName.setValue(buttonAccessories.stationName);
		hPanel1.add(stationName);

		isWind = new Hidden();
		isWind.setName("iswind");
		if (this.buttonAccessories.dirMeasureUnit != null)
			isWind.setValue(WIND);
		else
			isWind.setValue(SCALAR);
		hPanel1.add(isWind);

		numDec = new Hidden();
		numDec.setName("numDec");
		numDec.setValue(new Integer(buttonAccessories.numDec).toString());
		hPanel1.add(numDec);

		dirNumDec = new Hidden();
		dirNumDec.setName("dirNumDec");
		dirNumDec.setValue(new Integer(buttonAccessories.numDec).toString());
		hPanel1.add(dirNumDec);

		if (pageType == SAMPLE_DOWNLOAD_CSV || pageType == SAMPLE_DOWNLOAD_CHART || pageType == SAMPLE_HISTORY) {
			// metto solo la data
			Label startDateLabel = new Label(CentraleUI.getMessages().lbl_day());
			hPanel1.add(startDateLabel);
			startDate.setWidth("100px");
			startDate.setName("startDate");
			hPanel1.add(startDate);
			vPanelForForm.add(hPanel1);

		} else if (pageType == ANALYZER_ALARM_HISTORY || pageType == STATION_ALARM_HISTORY
				|| pageType == MEANS_DOWNLOAD_CSV || pageType == MEANS_DOWNLOAD_CHART || pageType == MEANS_HISTORY) {
			// metto data e ora inizio e data e ora fine
			// start date
			Label startDateLabel = new Label(CentraleUI.getMessages().lbl_day());
			hPanel1.add(startDateLabel);
			startDate.setWidth("100px");
			startDate.setName("startDate");
			hPanel1.add(startDate);

			// start hour
			Label startHourLabel = new Label(CentraleUI.getMessages().lbl_start_hour());
			hPanel1.add(startHourLabel);
			startHour.setWidth("60px");
			startHour.setName("startHour");
			hPanel1.add(startHour);
			startHour.setText("00:00");

			vPanelForForm.add(hPanel1);

			hPanel2 = new HorizontalPanel();

			// end date and hour
			Label endDateLabel = new Label(CentraleUI.getMessages().lbl_end_date());
			hPanel2.add(endDateLabel);
			endDate.setWidth("100px");
			endDate.setName("endDate");
			hPanel2.add(endDate);

			Label endHourLabel = new Label(CentraleUI.getMessages().lbl_end_hour());
			hPanel2.add(endHourLabel);
			endHour.setWidth("60px");
			endHour.setName("endHour");
			hPanel2.add(endHour);

			vPanelForForm.add(hPanel2);

		}

		alarmList.setWidth("40px");
		alarmList.setVisibleItemCount(1);
		alarmList.setName("alarmList");

		windList.setWidth("40px");
		windList.setVisibleItemCount(1);
		windList.setName("windList");

		if (pageType == SAMPLE_DOWNLOAD_CSV || pageType == MEANS_DOWNLOAD_CSV) {
			HorizontalPanel optPanel1 = new HorizontalPanel();
			optPanel1.setSpacing(10);
			Label fieldSeparatorLabel = new Label(CentraleUI.getMessages().lbl_field_separator());
			fieldSeparator.setWidth("40px");
			fieldSeparator.setVisibleItemCount(1);
			fieldSeparator.setName("fieldSeparator");
			fieldSeparator.addItem(",");
			fieldSeparator.addItem(";");
			optPanel1.add(fieldSeparatorLabel);
			optPanel1.add(fieldSeparator);
			vPanelForForm.add(optPanel1);
			HorizontalPanel optPanel2 = new HorizontalPanel();
			optPanel2.setSpacing(10);
			Label decimalSeparatorLabel = new Label(CentraleUI.getMessages().lbl_decimal_separator());
			optPanel2.add(decimalSeparatorLabel);
			decimalSeparator.setWidth("40px");
			decimalSeparator.setVisibleItemCount(1);
			decimalSeparator.setName("decimalSeparator");
			decimalSeparator.addItem(".");
			decimalSeparator.addItem(",");
			optPanel2.add(decimalSeparator);
			vPanelForForm.add(optPanel2);
		} // end if

		if (pageType == SAMPLE_DOWNLOAD_CHART || pageType == MEANS_DOWNLOAD_CHART) {
			// set chart options
			HorizontalPanel optPanel2 = new HorizontalPanel();
			Label showMinMaxLabel = new Label(CentraleUI.getMessages().lbl_show_minmax());
			optPanel2.add(showMinMaxLabel);
			optPanel2.add(showMinMax);
			vPanelForForm.add(optPanel2);
		}

		if (pageType == ANALYZER_ALARM_HISTORY) {
			// set choice for analyzer alarm
			HorizontalPanel optPanel1 = new HorizontalPanel();
			optPanel1.setSpacing(10);
			Label alarmLabel = new Label(CentraleUI.getMessages().lbl_alarm_type());
			alarmList.clear();
			alarmList.setWidth("150px");
			alarmList.setVisibleItemCount(1);
			optPanel1.add(alarmLabel);
			optPanel1.add(alarmList);
			vPanelForForm.add(optPanel1);

			CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
			AsyncCallback<List<String>> callback = new UIAsyncCallback<List<String>>() {
				public void onSuccess(List<String> listAlarm) {
					Iterator<String> itList = listAlarm.iterator();
					while (itList.hasNext()) {
						String value = itList.next();
						alarmList.addItem(value, value);
					} // end while
				}
			};
			centraleService.getAlarmType(callback);
		} // end if

		if ((pageType == SAMPLE_HISTORY && isWind.getValue().equals(WIND))
				|| (pageType == SAMPLE_DOWNLOAD_CSV && isWind.getValue().equals(WIND))
				|| (pageType == SAMPLE_DOWNLOAD_CHART && isWind.getValue().equals(WIND))) {
			// case of wind element
			HorizontalPanel optPanel2 = new HorizontalPanel();
			optPanel2.setSpacing(10);
			Label windLabel = new Label(CentraleUI.getMessages().lbl_wind_label());
			windList.clear();
			windList.setWidth("150px");
			windList.setVisibleItemCount(1);
			optPanel2.add(windLabel);
			optPanel2.add(windList);
			vPanelForForm.add(optPanel2);
			// set choice for wind element
			CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
			AsyncCallback<List<ElementObject>> callback = new UIAsyncCallback<List<ElementObject>>() {
				public void onSuccess(List<ElementObject> listElement) {
					Iterator<ElementObject> itList = listElement.iterator();
					while (itList.hasNext()) {
						ElementObject obj = itList.next();
						windList.addItem(obj.getElementName(),
								new Integer(obj.getElementId()).toString() + ITEM_SEPARATOR + obj.getMeasureUnit());
					} // end while
				}
			};
			centraleService.getWindComponents(new Integer(accessories.elementId).intValue(), callback);
		}

		panel.add(formPanel);

		// load current date
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<String[]> callback = new UIAsyncCallback<String[]>() {
			public void onSuccess(String[] resultString) {
				startDate.setText(resultString[0]);
				endDate.setText(resultString[0]);
				endHour.setText(resultString[1]);
			}
		};
		centraleService.getCurrentDate(callback);

		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.BLUE);

		// undo button
		panelButton.addButton(CentraleUIConstants.UNDO, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Utils.unlockForPopup("popup");
				choosePeriodPopup.hide();
			}
		});

		// send button
		panelButton.addButton(CentraleUIConstants.SEND_VERIFY, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				if (choosePeriodPopup.pageType == MEANS_DOWNLOAD_CSV) {
					functionHidden.setValue("getMeansData");
					vPanelForForm.add(avgPeriod);
					formPanel.submit();
				}
				if (choosePeriodPopup.pageType == SAMPLE_DOWNLOAD_CSV) {
					functionHidden.setValue("getSampleData");
					if (isWind.getValue().equals(WIND)) {
						// wind case
						String[] idMeasure = separateItem(windList.getValue(windList.getSelectedIndex()));
						elementId.setValue(idMeasure[0]);
						elementName.setValue(windList.getItemText(windList.getSelectedIndex()));
						measureUnit.setValue(idMeasure[1]);
					}
					formPanel.submit();
				}

				int type = 0;
				if (choosePeriodPopup.pageType == SAMPLE_DOWNLOAD_CHART || choosePeriodPopup.pageType == SAMPLE_HISTORY)
					type = SAMPLE;
				else if (choosePeriodPopup.pageType == MEANS_DOWNLOAD_CHART
						|| choosePeriodPopup.pageType == MEANS_HISTORY)
					type = MEANS;

				if (choosePeriodPopup.pageType == SAMPLE_DOWNLOAD_CHART
						|| choosePeriodPopup.pageType == MEANS_DOWNLOAD_CHART) {

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String result) {
							Utils.unlockForPopup("loading");
							String title = CentraleUI.getMessages().means_data() + " " + buttonAccessories.stationName;
							if (choosePeriodPopup.pageType == SAMPLE_DOWNLOAD_CHART)
								title = CentraleUI.getMessages().sample_data() + " " + buttonAccessories.stationName;
							CentraleUI.showChartWidget.setChartNameAndTitle(result, title);
							CentraleUI.setCurrentPage(CentraleUI.showChartWidget);
						}// end onSuccess

						@Override
						public void onFailure(Throwable caught) {
							Utils.unlockForPopup("loading");
							super.onFailure(caught);
						}
					};
					Utils.unlockForPopup("popup");
					choosePeriodPopup.hide();
					Utils.blockForPopup("loading");
					String elementType = null;
					if (buttonAccessories.dirMeasureUnit != null) {
						// wind case
						elementType = WIND;
						String[] idMeasure = separateItem(windList.getValue(windList.getSelectedIndex()));
						elementId.setValue(idMeasure[0]);
						buttonAccessories.elementName = windList.getItemText(windList.getSelectedIndex());
						measureUnit.setValue(idMeasure[1]);
					} else {
						elementType = SCALAR;
						elementId.setValue(buttonAccessories.elementId);
						measureUnit.setValue(buttonAccessories.measureUnit);
					} // end else
					centraleService.createAndGetChartName(type, elementId.getValue(),
							startDate.getText() + " "
									+ (choosePeriodPopup.pageType == SAMPLE_DOWNLOAD_CHART ? "00:00:00"
											: startHour.getText() + ":00"),
							(choosePeriodPopup.pageType == SAMPLE_DOWNLOAD_CHART ? startDate.getText() + " 23:59:59"
									: endDate.getText() + " " + endHour.getText() + ":59"),
							MAX_CHART_DATA, avgPeriod.getValue(), buttonAccessories.stationName,
							buttonAccessories.elementName, measureUnit.getValue(), showMinMax.getValue(), elementType,
							buttonAccessories.numDec, buttonAccessories.dirNumDec, callback);

				} // end if SAMPLE_DOWNLOAD_CHART and MEANS_DOWNLOAD_CHART

				if (choosePeriodPopup.pageType == SAMPLE_HISTORY) {

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					AsyncCallback<Integer> callback = new UIAsyncCallback<Integer>() {
						public void onSuccess(Integer numData) {
							CentraleUI.historyTableWidget.setType(DATA);
							Utils.unlockForPopup("loading");
							if (numData > MAX_TABLE_DATA) {
								// i dati sono troppi e bisogna mostrare i
								// pulsantini delle ore e dei minuti
								CentraleUI.historyTableWidget.setDataList(null);
								CentraleUI.historyTableWidget.setAccessories(buttonAccessories);
								CentraleUI.historyTableWidget.setSampleData(true);
								CentraleUI.historyTableWidget.setTitle(CentraleUI.getMessages().sample_data());
								CentraleUI.historyTableWidget.setDateString(startDate.getText());
								CentraleUI.historyTableWidget.setCallFromChoosePeriodWidget(true);
								CentraleUI.historyTableWidget.setLblTitle();
								CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
							} else if (numData.equals(0)) {
								// non ci sono dati per la data selezionata
								CentraleUI.historyTableWidget.setDataList(new ArrayList<DataObject>());
								CentraleUI.historyTableWidget.setAccessories(buttonAccessories);
								CentraleUI.historyTableWidget.setSampleData(true);
								CentraleUI.historyTableWidget.setTitle(CentraleUI.getMessages().sample_data());
								CentraleUI.historyTableWidget.setDateString(startDate.getText());
								CentraleUI.historyTableWidget.setCallFromChoosePeriodWidget(true);
								CentraleUI.historyTableWidget.setLblTitle();
								CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
							} else if (numData.intValue() > 0 && numData.intValue() <= MAX_TABLE_DATA) {
								// posso mostrare i dati (comunque sempre con i
								// pulsantini delle ore e minuti) e quindi devo
								// rifare la richiessta dei dati
								CentraleUIServiceAsync centraleService1 = (CentraleUIServiceAsync) GWT
										.create(CentraleUIService.class);
								ServiceDefTarget endpoint1 = (ServiceDefTarget) centraleService1;
								endpoint1.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
								AsyncCallback<List<DataObject>> callback1 = new UIAsyncCallback<List<DataObject>>() {
									public void onSuccess(List<DataObject> dataList) {
										Utils.unlockForPopup("loading");

										CentraleUI.historyTableWidget.setType(DATA);
										CentraleUI.historyTableWidget.setAccessories(buttonAccessories);
										CentraleUI.historyTableWidget.setSampleData(true);
										CentraleUI.historyTableWidget.setTitle(CentraleUI.getMessages().sample_data());
										CentraleUI.historyTableWidget.setDateString(startDate.getText());
										CentraleUI.historyTableWidget.setCallFromChoosePeriodWidget(true);

										CentraleUI.historyTableWidget.setDataList(dataList);

										CentraleUI.historyTableWidget.setLblTitle();
										CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
									}// end onSuccess

									@Override
									public void onFailure(Throwable caught) {
										Utils.unlockForPopup("loading");
										super.onFailure(caught);
									}

								};
								Utils.unlockForPopup("popup");
								choosePeriodPopup.hide();
								Utils.blockForPopup("loading");
								String elementType = null;
								String startDateText = "";
								String endDateText = "";

								if (choosePeriodPopup.pageType == SAMPLE_HISTORY) {
									if (buttonAccessories.dirMeasureUnit != null) {
										// wind case
										// elementType = WIND;
										String[] idMeasure = separateItem(
												windList.getValue(windList.getSelectedIndex()));
										// elementId.setValue(idMeasure[0]);
										buttonAccessories.elementId = idMeasure[0];
										buttonAccessories.elementName = windList
												.getItemText(windList.getSelectedIndex());

									} else {
										// elementType = SCALAR;
										elementId.setValue(buttonAccessories.elementId);
									} // end else
									startDateText = startDate.getText() + " 00:00:00";
									endDateText = startDate.getText() + " 23:59:59";
									centraleService1.getHistoryData(SAMPLE, elementId.getValue(), startDateText,
											endDateText, MAX_TABLE_DATA, avgPeriod.getValue(), elementType,
											buttonAccessories.numDec, buttonAccessories.dirNumDec, callback1);
								} // end if SAMPLE_HISTORY

							}
						}// end onSuccess

						@Override
						public void onFailure(Throwable caught) {
							Utils.unlockForPopup("loading");
							super.onFailure(caught);
						}

					};
					Utils.unlockForPopup("popup");
					choosePeriodPopup.hide();
					Utils.blockForPopup("loading");
					String startDateText = "";
					String endDateText = "";

					if (choosePeriodPopup.pageType == SAMPLE_HISTORY) {
						if (buttonAccessories.dirMeasureUnit != null) {
							// wind case
							// elementType = WIND;
							String[] idMeasure = separateItem(windList.getValue(windList.getSelectedIndex()));
							// elementId.setValue(idMeasure[0]);
							buttonAccessories.elementId = idMeasure[0];
							buttonAccessories.elementName = windList.getItemText(windList.getSelectedIndex());

						} else {
							// elementType = SCALAR;
							elementId.setValue(buttonAccessories.elementId);
						} // end else
						startDateText = startDate.getText() + " 00:00:00";
						endDateText = startDate.getText() + " 23:59:59";
						centraleService.countHistorySampleData(elementId.getValue(), startDateText, endDateText,
								callback);
					} // end if SAMPLE_HISTORY

				} else if (choosePeriodPopup.pageType == MEANS_HISTORY) {

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					AsyncCallback<List<DataObject>> callback = new UIAsyncCallback<List<DataObject>>() {
						public void onSuccess(List<DataObject> dataList) {
							Utils.unlockForPopup("loading");
							CentraleUI.historyTableWidget.setType(DATA);
							CentraleUI.historyTableWidget.setAccessories(buttonAccessories);
							CentraleUI.historyTableWidget.setButtonAlreadyShown(null);
							CentraleUI.historyTableWidget.setSampleData(false);
							CentraleUI.historyTableWidget.setDataList(dataList);
							CentraleUI.historyTableWidget.setTitle(CentraleUI.getMessages().means_data());

							CentraleUI.historyTableWidget.setLblTitle();
							CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
						}// end onSuccess

						@Override
						public void onFailure(Throwable caught) {
							Utils.unlockForPopup("loading");
							super.onFailure(caught);
						}

					};
					Utils.unlockForPopup("popup");
					choosePeriodPopup.hide();
					Utils.blockForPopup("loading");
					String elementType = null;
					String startDateText = "";
					String endDateText = "";
					if (choosePeriodPopup.pageType == MEANS_HISTORY) {
						Utils.blockForPopup("loading");
						if (isWind.getValue().equals(WIND))
							// wind case
							elementType = WIND;
						else
							elementType = SCALAR;
						elementId.setValue(buttonAccessories.elementId);
						startDateText = startDate.getText() + " " + startHour.getText() + ":00:00";
						endDateText = endDate.getText() + " " + endHour.getText() + ":59:59";
						centraleService.getHistoryData(type, elementId.getValue(), startDateText, endDateText,
								MAX_TABLE_DATA, avgPeriod.getValue(), elementType, buttonAccessories.numDec,
								buttonAccessories.dirNumDec, callback);
					} // end if MEANS_HISTORY

				} // end if SAMPLE_HISTORY and MEANS_HISTORY

				if (choosePeriodPopup.pageType == ANALYZER_ALARM_HISTORY) {

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					AsyncCallback<List<AnalyzerAlarmStatusValuesObject>> callback = new UIAsyncCallback<List<AnalyzerAlarmStatusValuesObject>>() {
						public void onSuccess(List<AnalyzerAlarmStatusValuesObject> statusList) {
							Utils.unlockForPopup("loading");
							AnalyzerAlarmStatusObject status = new AnalyzerAlarmStatusObject();
							status.setAnAlarmValues(statusList);
							status.setAnalyzerId(new Integer(buttonAccessories.analyzerId).intValue());
							status.setAnalyzerName(buttonAccessories.analyzerName);

							CentraleUI.historyTableWidget.setAnalyzerAlarmStatusList(status);
							CentraleUI.historyTableWidget.setType(ANALYZER_ALARM);
							CentraleUI.historyTableWidget.setAccessories(buttonAccessories);
							CentraleUI.historyTableWidget.setTitle(CentraleUI.getMessages().analyzer_alarm_data());
							CentraleUI.historyTableWidget.setButtonAlreadyShown(null);
							CentraleUI.historyTableWidget.setLblTitle();
							CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
						}// end onSuccess

						@Override
						public void onFailure(Throwable caught) {
							Utils.unlockForPopup("loading");
							super.onFailure(caught);
						}

					};
					Utils.unlockForPopup("popup");
					choosePeriodPopup.hide();
					Utils.blockForPopup("loading");
					centraleService.getHistoryAnalyzerAlarm(alarmList.getItemText(alarmList.getSelectedIndex()),
							buttonAccessories.analyzerId, startDate.getText() + " " + startHour.getText() + ":00",
							endDate.getText() + " " + endHour.getText() + ":59", MAX_TABLE_DATA, callback);

				} // end if ANALYZER_ALARM_HISTORY

				if (choosePeriodPopup.pageType == STATION_ALARM_HISTORY) {

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
					AsyncCallback<List<AlarmStatusObject>> callback = new UIAsyncCallback<List<AlarmStatusObject>>() {
						public void onSuccess(List<AlarmStatusObject> statusList) {
							Utils.unlockForPopup("loading");
							CentraleUI.historyTableWidget.setAlarmStatusList(statusList);
							CentraleUI.historyTableWidget.setType(STATION_ALARM);
							CentraleUI.historyTableWidget.setAccessories(buttonAccessories);
							CentraleUI.historyTableWidget.setTitle(CentraleUI.getMessages().station_alarm_data());
							CentraleUI.historyTableWidget.setButtonAlreadyShown(null);
							CentraleUI.historyTableWidget.setLblTitle();
							CentraleUI.setCurrentPage(CentraleUI.historyTableWidget);
						}// end onSuccess

						@Override
						public void onFailure(Throwable caught) {
							Utils.unlockForPopup("loading");
							super.onFailure(caught);
						}

					};
					Utils.unlockForPopup("popup");
					choosePeriodPopup.hide();
					Utils.blockForPopup("loading");
					centraleService.getHistoryStationAlarm(new Integer(buttonAccessories.alarmId).toString(),
							startDate.getText() + " " + startHour.getText() + ":00",
							endDate.getText() + " " + endHour.getText() + ":59", MAX_TABLE_DATA, callback);

				} // end if STATION_ALARM_HISTORY

			}// end onClick
		});

		panel.add(panelButton);

		this.add(panel);
		this.show();

	}// end constructor

	/**
	 * Separates item divided by ITEM_SEPARATOR
	 * 
	 * @param item to separate
	 * @return array string of 2 elements: element 0 is id; element 1 is measureUnit
	 */
	private String[] separateItem(String item) {
		String[] idMeasure = new String[2];
		int index = item.indexOf(ITEM_SEPARATOR);
		idMeasure[0] = item.substring(0, index);
		idMeasure[1] = item.substring(index, item.length());
		return idMeasure;
	}

	public void show() {

		super.show();

		int cWidth = Window.getClientWidth();
		int cHeight = Window.getClientHeight();
		setPopupPosition(((cWidth / 2) - 250), ((cHeight / 2) - 75));
		setWidth("500px");
		setHeight("150px");
		setStyleName("gwt-popup-panel-blue");

	}// end show

}// end class
