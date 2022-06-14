/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa 
 * Purpose of file:   Page for manage other element of common configuration.
 * Change log:
 *   2009-04-06: initial version
 * ----------------------------------------------------------------------------
 * $Id: OtherCommonCfgWidget.java,v 1.15 2014/09/18 09:46:57 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.AvgPeriodInfo;
import it.csi.centrale.ui.client.data.OtherInfo;
import it.csi.centrale.ui.client.data.StandardInfo;
import it.csi.centrale.ui.client.data.StorageManagerInfo;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for manage other element of common configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class OtherCommonCfgWidget extends UIPage {

	private VerticalPanel externalPanel;

	private FlexTable table = new FlexTable();

	private TextBox newAvgPeriodTextBox;

	private TextBox diskFullAlarmThresholdPercentTextBox;

	private TextBox diskFullWarningThresholdPercentTextBox;

	private TextBox maxDaysOfAggregateDataTextBox;

	private TextBox maxDaysOfDataTextBox;

	private TextBox referenceTemperatureK;

	private TextBox referencePressurekPa;

	private TextBox dataWriteToDiskPeriod;

	private TextBox manualOperationsAutoResetPeriod;

	private TextBox doorAlarmId;

	private TextBox copServicePort;

	private TextBox mapsSiteUrlFormatter;

	private HashMap<Button, Integer> buttonMap = new HashMap<Button, Integer>();

	private HashMap<RadioButton, Integer> radioButtonMap = new HashMap<RadioButton, Integer>();

	public OtherCommonCfgWidget() {

		PanelButtonWidget panelButton = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		panelButton.addButton(CentraleUIConstants.XML, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./exportService?function=getCommonConfig", "", "");
			}
		});

		panelButton.addButton(CentraleUIConstants.PARAMETER, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.commonCfgWidget.setType(CentraleUIConstants.PARAMETER);
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.MEASURE_UNIT, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.commonCfgWidget.setType(CentraleUIConstants.MEASURE_UNIT);
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.ALARM_NAME, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.commonCfgWidget.setType(CentraleUIConstants.ALARM_NAME);
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.PHYSICAL_DIMENSION,
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						CentraleUI
								.setCurrentPage(CentraleUI.physicalDimensionWidget);
					}
				});

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#conf_otherCommonCfgWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);

		externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().common_cfg_title(), panelButton, false);

		Label avgPeriodLabel = new Label();
		avgPeriodLabel.setText(CentraleUI.getMessages().lbl_avg_period());
		avgPeriodLabel.setStyleName("gwt-Label-title");

		// panel that contains avg period
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		FlexTable newTable = new FlexTable();
		newTable.getCellFormatter().setWidth(0, 0, "200");
		newTable.getCellFormatter().setWidth(0, 1, "200");
		newTable.getCellFormatter().setWidth(0, 2, "200");
		newTable.getCellFormatter().setWidth(0, 3, "100");
		newTable.getCellFormatter().setWidth(0, 4, "100");
		newTable.setCellPadding(5);
		newTable.setCellSpacing(5);

		panel.add(newTable);

		// avg period info
		Label newAvgPeriodLabel = new Label();
		newAvgPeriodLabel.setText(CentraleUI.getMessages().new_avg_period());
		newTable.setWidget(0, 0, newAvgPeriodLabel);
		newAvgPeriodTextBox = new TextBox();
		TextBoxKeyPressHandler positiveKeyPressHandler = new TextBoxKeyPressHandler(
				true);
		newAvgPeriodTextBox.addKeyPressHandler(positiveKeyPressHandler);
		newAvgPeriodTextBox.setStyleName("gwt-bg-text-orange");
		newAvgPeriodTextBox.setWidth("100px");
		newTable.setWidget(0, 1, newAvgPeriodTextBox);

		Button newAvgPeriodButton = new Button();
		newAvgPeriodButton.setStyleName("gwt-button-new-orange");
		newAvgPeriodButton.setTitle(CentraleUI.getMessages().lbl_new());
		newAvgPeriodButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (newAvgPeriodTextBox.getText().equals(""))
					Window.alert(CentraleUI.getMessages().write_avg_period());
				else {
					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
							+ "uiservice");

					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String errorStr) {
							if (errorStr == null)
								Window.alert(CentraleUI.getMessages()
										.added_avg_period());
							else
								Window.alert(errorStr);
							resetAvgPeriod();
							setAvgField();
						}// end onSuccess
					};
					centraleService.addAvgPeriod(newAvgPeriodTextBox.getText(),
							callback);
				}// end else
			}// end onClick

		});
		newTable.setWidget(0, 2, newAvgPeriodButton);

		externalPanel.add(avgPeriodLabel);
		externalPanel.add(panel);

		FlexTable headerTable = new FlexTable();
		headerTable.setText(0, 0, CentraleUI.getMessages().lbl_avg_period());
		headerTable.setText(0, 1, CentraleUI.getMessages().lbl_default_avg_period());
		headerTable.setText(0, 2, CentraleUI.getMessages().lbl_cancel());
		headerTable.setStyleName("gwt-table-header");
		headerTable.getCellFormatter().setWidth(0, 0, "200px");
		headerTable.getCellFormatter().setWidth(0, 1, "200px");
		headerTable.getCellFormatter().setWidth(0, 2, "50px");
		for (int j = 0; j < 3; j++) {
			headerTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}
		panel.add(headerTable);
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("150px");
		scrollPanel.add(table);
		panel.add(scrollPanel);

		// storage manager info
		Label storageManagerLabel = new Label();
		storageManagerLabel.setText(CentraleUI.getMessages().lbl_storage_manager());
		storageManagerLabel.setStyleName("gwt-Label-title");

		// panel that contains storage manager
		VerticalPanel panel2 = new VerticalPanel();
		panel2.setStyleName("gwt-post-boxed");

		FlexTable storageTable = new FlexTable();
		// format the grid
		storageTable.getCellFormatter().setWidth(0, 0, "250px");
		storageTable.getCellFormatter().setWidth(0, 1, "220px");
		storageTable.getCellFormatter().setWidth(0, 2, "250px");
		storageTable.getCellFormatter().setWidth(0, 3, "220px");
		storageTable.setCellSpacing(5);
		storageTable.setCellPadding(5);

		storageTable.setText(0, 0, CentraleUI.getMessages().max_days_of_data());
		maxDaysOfDataTextBox = new TextBox();
		maxDaysOfDataTextBox.setStyleName("gwt-bg-text-orange");
		maxDaysOfDataTextBox.setWidth("100px");
		maxDaysOfDataTextBox.addKeyPressHandler(positiveKeyPressHandler);
		storageTable.setWidget(0, 1, maxDaysOfDataTextBox);
		storageTable.setText(0, 2,
				CentraleUI.getMessages().max_days_of_aggregate_data());
		maxDaysOfAggregateDataTextBox = new TextBox();
		maxDaysOfAggregateDataTextBox.setStyleName("gwt-bg-text-orange");
		maxDaysOfAggregateDataTextBox.setWidth("100px");
		maxDaysOfAggregateDataTextBox
				.addKeyPressHandler(positiveKeyPressHandler);
		storageTable.setWidget(0, 3, maxDaysOfAggregateDataTextBox);
		storageTable.setText(1, 0,
				CentraleUI.getMessages().disk_full_warning_threshold_percent());
		diskFullWarningThresholdPercentTextBox = new TextBox();
		diskFullWarningThresholdPercentTextBox
				.setStyleName("gwt-bg-text-orange");
		diskFullWarningThresholdPercentTextBox.setWidth("100px");
		diskFullWarningThresholdPercentTextBox
				.addKeyPressHandler(positiveKeyPressHandler);
		storageTable.setWidget(1, 1, diskFullWarningThresholdPercentTextBox);
		storageTable.setText(1, 2,
				CentraleUI.getMessages().disk_full_alarm_threshold_percent());
		diskFullAlarmThresholdPercentTextBox = new TextBox();
		diskFullAlarmThresholdPercentTextBox.setStyleName("gwt-bg-text-orange");
		diskFullAlarmThresholdPercentTextBox.setWidth("100px");
		diskFullAlarmThresholdPercentTextBox
				.addKeyPressHandler(positiveKeyPressHandler);
		storageTable.setWidget(1, 3, diskFullAlarmThresholdPercentTextBox);

		panel2.add(storageTable);

		// create buttons for undo and save for storage manager
		PanelButtonWidget panelButton2 = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		// button cancel for storage manager
		ClickHandler cancelClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetStorageManager();
				setStorageManagerField();
			}
		};
		panelButton2.addButton(CentraleUIConstants.UNDO, cancelClickHandler);

		// button send/verify for storage manager
		ClickHandler sendVerifyClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StorageManagerInfo storageManager = new StorageManagerInfo();
				try {
					Integer dfatp = null;
					Integer dfwtp = null;
					Integer mdoad = null;
					Integer mdod = null;
					if (!diskFullAlarmThresholdPercentTextBox.getText().equals(
							""))
						dfatp = new Integer(
								diskFullAlarmThresholdPercentTextBox.getText());
					if (!diskFullWarningThresholdPercentTextBox.getText()
							.equals(""))
						dfwtp = new Integer(
								diskFullWarningThresholdPercentTextBox
										.getText());
					if (!maxDaysOfAggregateDataTextBox.getText().equals(""))
						mdoad = new Integer(
								maxDaysOfAggregateDataTextBox.getText());
					if (!maxDaysOfDataTextBox.getText().equals(""))
						mdod = new Integer(maxDaysOfDataTextBox.getText());
					storageManager.setDiskFullAlarmThresholdPercent(dfatp);
					storageManager.setDiskFullWarningThresholdPercent(dfwtp);
					storageManager.setMaxDaysOfAggregateData(mdoad);
					storageManager.setMaxDaysOfData(mdod);
					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
							+ "uiservice");

					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String errStr) {
							if (errStr != null) {
								Window.alert(errStr);
								resetStorageManager();
								setStorageManagerField();
							} else
								Window.alert(CentraleUI.getMessages()
										.storage_manager_updated());

						}// end onSuccess
					};
					centraleService.updateStorageManager(storageManager,
							callback);
				} catch (NumberFormatException e) {
					Window.alert(CentraleUI.getMessages().only_positive());
					resetStorageManager();
					setStorageManagerField();
				}

			}// end onClick
		};
		panelButton2.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler);

		// panel that contains button for storage manager
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("gwt-button-panel");
		hPanel2.add(panelButton2);
		hPanel2.setSpacing(10);
		hPanel2.setCellHorizontalAlignment(panelButton2,
				HasHorizontalAlignment.ALIGN_CENTER);
		panel2.add(hPanel2);

		externalPanel.add(storageManagerLabel);
		externalPanel.add(panel2);

		// standard info
		Label standardLabel = new Label();
		standardLabel.setText(CentraleUI.getMessages().lbl_standard());
		standardLabel.setStyleName("gwt-Label-title");

		// panel that contains standard
		VerticalPanel panel3 = new VerticalPanel();
		panel3.setStyleName("gwt-post-boxed");

		FlexTable standardTable = new FlexTable();
		// format the grid
		standardTable.getCellFormatter().setWidth(0, 0, "250px");
		standardTable.getCellFormatter().setWidth(0, 1, "220px");
		standardTable.getCellFormatter().setWidth(0, 2, "250px");
		standardTable.getCellFormatter().setWidth(0, 3, "220px");
		standardTable.setCellSpacing(5);
		standardTable.setCellPadding(5);

		standardTable.setText(0, 0,
				CentraleUI.getMessages().reference_temperature_K());
		referenceTemperatureK = new TextBox();
		referenceTemperatureK.setStyleName("gwt-bg-text-orange");
		referenceTemperatureK.setWidth("100px");
		TextBoxKeyPressHandler negativeKeyBoardHandler = new TextBoxKeyPressHandler(
				false);
		referenceTemperatureK.addKeyPressHandler(negativeKeyBoardHandler);
		standardTable.setWidget(0, 1, referenceTemperatureK);
		standardTable
				.setText(0, 2, CentraleUI.getMessages().referencePressure_kPa());
		referencePressurekPa = new TextBox();
		referencePressurekPa.setStyleName("gwt-bg-text-orange");
		referencePressurekPa.setWidth("100px");
		referencePressurekPa.addKeyPressHandler(negativeKeyBoardHandler);
		standardTable.setWidget(0, 3, referencePressurekPa);

		panel3.add(standardTable);

		// create buttons for undo and save for standard
		PanelButtonWidget panelButton3 = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		// button cancel for standard
		ClickHandler cancelClickHandler2 = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetStandard();
				setStandardField();
			}
		};
		panelButton3.addButton(CentraleUIConstants.UNDO, cancelClickHandler2);

		// button send/verify for standard
		ClickHandler sendVerifyClickHandler2 = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StandardInfo standard = new StandardInfo();
				try {
					Double refTemp = null;
					Double refPress = null;
					if (!referenceTemperatureK.getText().equals(""))
						refTemp = new Double(referenceTemperatureK.getText());
					if (!referencePressurekPa.getText().equals(""))
						refPress = new Double(referencePressurekPa.getText());
					standard.setReferencePressureKPa(refPress);
					standard.setReferenceTemperatureK(refTemp);

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
							+ "uiservice");

					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String errStr) {
							if (errStr != null) {
								Window.alert(errStr);
								resetStandard();
								setStandardField();
							} else
								Window.alert(CentraleUI.getMessages()
										.standard_updated());

						}// end onSuccess
					};
					centraleService.updateStandard(standard, callback);
				} catch (NumberFormatException e) {
					Window.alert(CentraleUI.getMessages().only_positive());
					resetStandard();
					setStandardField();
				}

			}// end onClick
		};
		panelButton3.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler2);

		// panel that contains button for standard
		HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setStyleName("gwt-button-panel");
		hPanel3.add(panelButton3);
		hPanel3.setSpacing(10);
		hPanel3.setCellHorizontalAlignment(panelButton3,
				HasHorizontalAlignment.ALIGN_CENTER);
		panel3.add(hPanel3);

		externalPanel.add(standardLabel);
		externalPanel.add(panel3);

		// other info
		Label otherLabel = new Label();
		otherLabel.setText(CentraleUI.getMessages().lbl_other());
		otherLabel.setStyleName("gwt-Label-title");

		// panel that contains other info
		VerticalPanel panel4 = new VerticalPanel();
		panel4.setStyleName("gwt-post-boxed");

		FlexTable otherTable = new FlexTable();
		// format the grid
		otherTable.getCellFormatter().setWidth(0, 0, "250px");
		otherTable.getCellFormatter().setWidth(0, 1, "220px");
		otherTable.getCellFormatter().setWidth(0, 2, "250px");
		otherTable.getCellFormatter().setWidth(0, 3, "220px");
		otherTable.setCellSpacing(5);
		otherTable.setCellPadding(5);

		otherTable.setText(0, 0,
				CentraleUI.getMessages().manual_operations_auto_reset_period());
		manualOperationsAutoResetPeriod = new TextBox();
		manualOperationsAutoResetPeriod.setStyleName("gwt-bg-text-orange");
		manualOperationsAutoResetPeriod.setWidth("220px");

		manualOperationsAutoResetPeriod
				.addKeyPressHandler(positiveKeyPressHandler);
		otherTable.setWidget(0, 1, manualOperationsAutoResetPeriod);
		otherTable.setText(0, 2,
				CentraleUI.getMessages().data_write_to_disk_period());
		dataWriteToDiskPeriod = new TextBox();
		dataWriteToDiskPeriod.setStyleName("gwt-bg-text-orange");
		dataWriteToDiskPeriod.setWidth("100px");
		dataWriteToDiskPeriod.addKeyPressHandler(positiveKeyPressHandler);
		otherTable.setWidget(0, 3, dataWriteToDiskPeriod);
		otherTable.setText(1, 0, CentraleUI.getMessages().door_alarm_id());
		doorAlarmId = new TextBox();
		doorAlarmId.setStyleName("gwt-bg-text-orange");
		doorAlarmId.setWidth("220px");
		otherTable.setWidget(1, 1, doorAlarmId);

		otherTable.setText(1, 2, CentraleUI.getMessages().cop_service_port());
		copServicePort = new TextBox();
		copServicePort.setStyleName("gwt-bg-text-orange");
		copServicePort.setWidth("100px");
		copServicePort.addKeyPressHandler(positiveKeyPressHandler);
		otherTable.setWidget(1, 3, copServicePort);

		otherTable.setText(2, 0, CentraleUI.getMessages().maps_site_url_formatter());
		mapsSiteUrlFormatter = new TextBox();
		mapsSiteUrlFormatter.setStyleName("gwt-bg-text-orange");
		mapsSiteUrlFormatter.setWidth("220px");
		otherTable.setWidget(2, 1, mapsSiteUrlFormatter);

		panel4.add(otherTable);

		// create buttons for undo and save for other info
		PanelButtonWidget panelButton4 = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		// button cancel for other info
		ClickHandler cancelClickHandler3 = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				resetOther();
				setOtherField();
			}
		};
		panelButton4.addButton(CentraleUIConstants.UNDO, cancelClickHandler3);

		// button send/verify for other info
		ClickHandler sendVerifyClickHandler3 = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				OtherInfo other = new OtherInfo();
				try {
					Integer dwtdp = null;
					Integer moarp = null;
					Integer port = null;
					if (!dataWriteToDiskPeriod.getText().equals(""))
						dwtdp = new Integer(dataWriteToDiskPeriod.getText());
					if (!manualOperationsAutoResetPeriod.getText().equals(""))
						moarp = new Integer(
								manualOperationsAutoResetPeriod.getText());
					if (!copServicePort.getText().equals(""))
						port = new Integer(copServicePort.getText());
					other.setDoorAlarmId(doorAlarmId.getText());
					other.setManualOperationsAutoResetPeriod(moarp);
					other.setDataWriteToDiskPeriod(dwtdp);
					other.setCopServicePort(port);
					other.setMapsSiteURLFormatter(mapsSiteUrlFormatter.getText());

					CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
							.create(CentraleUIService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
					endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
							+ "uiservice");

					AsyncCallback<String> callback = new UIAsyncCallback<String>() {
						public void onSuccess(String errStr) {
							if (errStr != null) {
								Window.alert(errStr);
								resetStandard();
								setStandardField();
							} else
								Window.alert(CentraleUI.getMessages()
										.other_updated());

						}// end onSuccess
					};
					centraleService.updateOther(other, callback);
				} catch (NumberFormatException e) {
					Window.alert(CentraleUI.getMessages().only_positive());
					resetOther();
					setOtherField();
				}

			}// end onClick
		};
		panelButton4.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler3);

		// panel that contains button for other info
		HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setStyleName("gwt-button-panel");
		hPanel4.add(panelButton4);
		hPanel4.setSpacing(10);
		hPanel4.setCellHorizontalAlignment(panelButton4,
				HasHorizontalAlignment.ALIGN_CENTER);
		panel4.add(hPanel4);

		externalPanel.add(otherLabel);
		externalPanel.add(panel4);

		initWidget(externalPanel);

	}// end constructor

	private void setAvgField() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<List<AvgPeriodInfo>> callback = new UIAsyncCallback<List<AvgPeriodInfo>>() {
			public void onSuccess(List<AvgPeriodInfo> avgPeriodList) {
				Iterator<AvgPeriodInfo> it = avgPeriodList.iterator();
				int i = 0;
				while (it.hasNext()) {
					AvgPeriodInfo avgPeriod = it.next();
					table.setText(i, 0,
							new Integer(avgPeriod.getAvgPeriodId()).toString());
					RadioButton defaultRadioButton = new RadioButton("default");
					defaultRadioButton.setValue(avgPeriod.isDefaultAvgPeriod());
					defaultRadioButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							boolean confirm = Window
									.confirm(CentraleUI.getMessages()
											.change_default_avg_period());
							if (confirm) {
								// save in db
								Integer avgPeriodId = radioButtonMap.get(event
										.getSource());
								CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
										.create(CentraleUIService.class);
								ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
								endpoint2.setServiceEntryPoint(GWT
										.getModuleBaseURL() + "uiservice");

								AsyncCallback<String> callback2 = new UIAsyncCallback<String>() {
									public void onSuccess(String errorMsg) {
										if (errorMsg == null)
											Window.alert(CentraleUI.getMessages()
													.updated_default_avg_period());
										else
											Window.alert(errorMsg);
										resetAvgPeriod();
										setAvgField();
									}// end onSuccess

								};
								centraleService2.updateDefaultAvgPeriod(
										avgPeriodId, ((RadioButton) event
												.getSource()).getValue(),
										callback2);
							}// end if confirm
							else {
								resetAvgPeriod();
								setAvgField();
							}
						}// end onClick

					});
					radioButtonMap.put(defaultRadioButton,
							avgPeriod.getAvgPeriodId());
					table.setWidget(i, 1, defaultRadioButton);
					table.getCellFormatter().setHorizontalAlignment(i, 1,
							HasHorizontalAlignment.ALIGN_CENTER);

					Button buttonCancel = new Button();
					buttonCancel.setStyleName("gwt-button-delete");
					buttonCancel.setTitle(CentraleUI.getMessages().delete());
					buttonCancel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							Integer avgPeriod = buttonMap.get(event.getSource());
							CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
									.create(CentraleUIService.class);
							ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
							endpoint2.setServiceEntryPoint(GWT
									.getModuleBaseURL() + "uiservice");
							AsyncCallback<String> callback2 = new UIAsyncCallback<String>() {
								public void onSuccess(String errorMsg) {
									if (errorMsg == null)
										Window.alert(CentraleUI.getMessages()
												.deleted_avg_period());
									else
										Window.alert(errorMsg);
									resetAvgPeriod();
									setAvgField();
								}// end onSuccess
							};
							centraleService2.deleteAvgPeriod(
									avgPeriod.intValue(), callback2);

						}// end onClick
					});
					table.setWidget(i, 2, buttonCancel);
					table.getCellFormatter().setHorizontalAlignment(i, 2,
							HasHorizontalAlignment.ALIGN_CENTER);
					buttonMap.put(buttonCancel, new Integer(
							avgPeriod.getAvgPeriodId()));
					for (int j = 0; j < 3; j++) {
						table.getCellFormatter().setStyleName(i, j,
								"gwt-table-data");
					}// end for
					i++;
				}// end while

			}// end onSuccess
		};
		centraleService.readAvgPeriod(callback);
	}// end setAvgField

	private void setStandardField() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<StandardInfo> callback3 = new UIAsyncCallback<StandardInfo>() {
			public void onSuccess(StandardInfo standrad) {
				referencePressurekPa
						.setText(standrad.getReferencePressureKPa() != null ? standrad.getReferencePressureKPa()
								.toString() : "");
				referenceTemperatureK
						.setText(standrad.getReferenceTemperatureK() != null ? standrad.getReferenceTemperatureK()
								.toString() : "");
			}// end onSuccess
		};
		centraleService.readStandard(callback3);
	}// end setStandardField

	private void setStorageManagerField() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<StorageManagerInfo> callback2 = new UIAsyncCallback<StorageManagerInfo>() {
			public void onSuccess(StorageManagerInfo storageManager) {
				maxDaysOfDataTextBox
						.setText(storageManager.getMaxDaysOfData() != null ? storageManager.getMaxDaysOfData()
								.toString() : "");
				maxDaysOfAggregateDataTextBox
						.setText(storageManager.getMaxDaysOfAggregateData() != null ? storageManager.getMaxDaysOfAggregateData()
								.toString() : "");
				diskFullWarningThresholdPercentTextBox
						.setText(storageManager.getDiskFullWarningThresholdPercent() != null ? storageManager.getDiskFullWarningThresholdPercent()
								.toString() : "");
				diskFullAlarmThresholdPercentTextBox
						.setText(storageManager.getDiskFullAlarmThresholdPercent() != null ? storageManager.getDiskFullAlarmThresholdPercent()
								.toString() : "");
			}// end onSuccess
		};
		centraleService.readStorageManager(callback2);
	}// end setStorageManagerField

	private void setOtherField() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<OtherInfo> callback4 = new UIAsyncCallback<OtherInfo>() {
			public void onSuccess(OtherInfo other) {
				manualOperationsAutoResetPeriod
						.setText((other.getManualOperationsAutoResetPeriod() != null ? other.getManualOperationsAutoResetPeriod()
								.toString() : ""));
				dataWriteToDiskPeriod
						.setText(other.getDataWriteToDiskPeriod() != null ? other.getDataWriteToDiskPeriod()
								.toString() : "");
				doorAlarmId.setText(other.getDoorAlarmId());
				copServicePort
						.setText(other.getCopServicePort() != null ? other.getCopServicePort()
								.toString() : "");
				mapsSiteUrlFormatter.setText(other.getMapsSiteURLFormatter());
			}// end onSuccess
		};
		centraleService.readOther(callback4);

	}// end setOtherField

	@Override
	protected void reset() {
		resetAvgPeriod();
		resetStorageManager();
		resetStandard();
		resetOther();

	}// end reset

	private void resetAvgPeriod() {
		// clear avg period fields
		newAvgPeriodTextBox.setText("");
		Utils.clearTable(table);
		table.getCellFormatter().setWidth(0, 0, "200px");
		table.getCellFormatter().setWidth(0, 1, "200px");
		table.getCellFormatter().setWidth(0, 2, "50px");
	}

	private void resetStorageManager() {
		// clear storage manager fields
		maxDaysOfAggregateDataTextBox.setText("");
		maxDaysOfDataTextBox.setText("");
		diskFullAlarmThresholdPercentTextBox.setText("");
		diskFullWarningThresholdPercentTextBox.setText("");
	}

	private void resetStandard() {
		// clear standard fields
		referencePressurekPa.setText("");
		referenceTemperatureK.setText("");
	}

	private void resetOther() {
		// clear other fields
		manualOperationsAutoResetPeriod.setText("");
		dataWriteToDiskPeriod.setText("");
		doorAlarmId.setText("");
		copServicePort.setText("");
		mapsSiteUrlFormatter.setText("");
	}

	@Override
	protected void loadContent() {

		setAvgField();

		setStorageManagerField();

		setStandardField();

		setOtherField();

	}

}// end class
