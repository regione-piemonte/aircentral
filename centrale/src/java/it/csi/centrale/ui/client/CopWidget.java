/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: page for configuration of informations about a Cop
* Change log:
*   2008-11-26: initial version
* ----------------------------------------------------------------------------
* $Id: CopWidget.java,v 1.26 2014/09/22 11:01:43 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.ConfigInfoObject;
import it.csi.centrale.ui.client.pagecontrol.AsyncPageOperation;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/**
 * Page for configuration of informations about a Cop
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class CopWidget extends UIPage {

	// Grids for cop information.
	public final FlexTable copGrid = new FlexTable();

	public final FlexTable pollingGrid = new FlexTable();

	public final FlexTable extraPollingGrid = new FlexTable();

	public final Label extraPollingTitle = new Label();

	public final FlexTable thresholdsGrid = new FlexTable();

	private TextBox textPollingTime;

	private CheckBox checkExtraPolling;

	private ListBox sampleDataDownloadList;

	private CheckBox checkDownloadAlarm;

	private TextBox textOpeningTime;

	private TextBox textCloseTime;

	private CheckBox checkCloseSat;

	private CheckBox checkCloseSun;

	private TextBox textExtraPollingTime;

	private TextBox textMinTemperatureThreshold;

	private TextBox textMaxTemperatureThreshold;

	private TextBox textAlarmMaxTemperatureThreshold;

	private TextBox textMaxNumAvailableLines;

	private TextBox textTotalModemNumber;

	private TextBox textNumSharedLines;

	private TextBox textNumReservedLinesUi;

	private TextBox textRouterTimeout;

	private TextBox textRouterTryTimeout;

	private TextBox textCopIp;

	private TextBox textTimeHostRouter;

	private TextBox textTimeHostLan;

	private TextBox textTimeHostModem;

	private TextBox textTimeHostProxy;

	private TextBox textCopRouterIp;

	private CheckBox checkReservedLine;

	private TextBox nameTextBox;

	private TextBox mapTextBox;

	private TextBox proxyHostTextBox;

	private TextBox proxyPortTextBox;

	private TextBox proxyExclusionTextBox;

	private TextBoxKeyPressHandler textBoxKeyPressHandler = new TextBoxKeyPressHandler(true);

	private TextBoxKeyPressHandler negativeTextBoxKeyPressHandler = new TextBoxKeyPressHandler(false);

	private TextBoxKeyPressHandler positiveKeyPressHandler = new TextBoxKeyPressHandler(true);

	private boolean isNewCop = false;

	public CopWidget() {
		// panel that represents the page
		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_copWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().lbl_cop_info(),
				panelButton, false);

		/*
		 * Cop info
		 */

		// Label and panel for cop info
		Label copTitle = new Label();
		copTitle.setStyleName("gwt-Label-title");

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");
		panel.add(copGrid);

		// title for cop name
		Label nameTitle = new Label();
		nameTitle.setText(CentraleUI.getMessages().cop_name());
		nameTitle.setStyleName("gwt-cop-title");
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.setSpacing(5);
		namePanel.add(nameTitle);
		nameTextBox = new TextBox();
		nameTextBox.setStyleName("gwt-bg-text-orange");
		nameTextBox.setHeight("30px");
		namePanel.add(nameTextBox);
		copGrid.setWidget(0, 0, namePanel);

		// info per cartina
		Label mapTitle = new Label();
		mapTitle.setText(CentraleUI.getMessages().map_name());
		mapTitle.setStyleName("gwt-cop-title");
		namePanel.add(mapTitle);
		mapTextBox = new TextBox();
		mapTextBox.setStyleName("gwt-bg-text-orange");
		mapTextBox.setHeight("30px");
		namePanel.add(mapTextBox);

		// title for polling info
		Label pollingTitle = new Label();
		pollingTitle.setText(CentraleUI.getMessages().polling_title());
		pollingTitle.setStyleName("gwt-cop-title");
		copGrid.setWidget(1, 0, pollingTitle);
		copGrid.setWidget(2, 0, pollingGrid);
		copGrid.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);

		// title for extra polling info
		extraPollingTitle.setText(CentraleUI.getMessages().extra_polling_title());
		extraPollingTitle.setStyleName("gwt-cop-title");
		copGrid.setWidget(3, 0, extraPollingTitle);
		copGrid.setWidget(4, 0, extraPollingGrid);
		copGrid.getCellFormatter().setVerticalAlignment(4, 0, HasVerticalAlignment.ALIGN_TOP);
		copGrid.setCellSpacing(10);

		// title for thresholds info
		Label thresholdsTitle = new Label();
		thresholdsTitle.setText(CentraleUI.getMessages().thresholds_temperature_title());
		thresholdsTitle.setStyleName("gwt-cop-title");
		copGrid.setWidget(5, 0, thresholdsTitle);
		copGrid.setWidget(6, 0, thresholdsGrid);
		copGrid.getCellFormatter().setVerticalAlignment(6, 0, HasVerticalAlignment.ALIGN_TOP);
		copGrid.setCellSpacing(10);

		// fill polling grid fields
		pollingGrid.setStyleName("gwt-Grid");
		pollingGrid.setWidth("100%");
		pollingGrid.setCellSpacing(3);
		pollingGrid.setText(0, 0, CentraleUI.getMessages().polling_time());
		textPollingTime = new TextBox();
		textPollingTime.setStyleName("gwt-bg-text-orange");
		textPollingTime.setMaxLength(4);
		textPollingTime.setWidth("100px");
		textPollingTime.addKeyPressHandler(textBoxKeyPressHandler);
		pollingGrid.setWidget(0, 1, textPollingTime);

		pollingGrid.setText(0, 2, CentraleUI.getMessages().use_polling_extra());
		checkExtraPolling = new CheckBox();
		checkExtraPolling.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (checkExtraPolling.getValue()) {
					extraPollingTitle.setVisible(true);
					extraPollingGrid.setVisible(true);
				} else {
					extraPollingTitle.setVisible(false);
					extraPollingGrid.setVisible(false);
				}
			}

		});
		pollingGrid.setWidget(0, 3, checkExtraPolling);

		pollingGrid.setText(1, 0, CentraleUI.getMessages().download_sample_data_option());
		sampleDataDownloadList = new ListBox();
		// sampleDataDownloadList.setWidth("100px");
		sampleDataDownloadList.setVisibleItemCount(1);
		sampleDataDownloadList.addItem(CentraleUI.getMessages().disabled(),
				new Integer(CentraleUIConstants.DISABLED).toString());
		sampleDataDownloadList.addItem(CentraleUI.getMessages().calibration_data(),
				new Integer(CentraleUIConstants.CALIBRATION_DATA).toString());
		sampleDataDownloadList.addItem(CentraleUI.getMessages().all_data(),
				new Integer(CentraleUIConstants.ALL_DATA).toString());
		pollingGrid.setWidget(1, 1, sampleDataDownloadList);

		pollingGrid.setText(1, 2, CentraleUI.getMessages().note_for_download_option());
		pollingGrid.getFlexCellFormatter().setColSpan(1, 2, 2);

		pollingGrid.setText(2, 0, CentraleUI.getMessages().download_alarm());
		checkDownloadAlarm = new CheckBox();
		pollingGrid.setWidget(2, 1, checkDownloadAlarm);

		pollingGrid.setText(3, 0, CentraleUI.getMessages().max_available_lines());
		textMaxNumAvailableLines = new TextBox();
		textMaxNumAvailableLines.setStyleName("gwt-bg-text-orange");
		textMaxNumAvailableLines.setMaxLength(3);
		textMaxNumAvailableLines.setWidth("100px");
		textMaxNumAvailableLines.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				// TODO Auto-generated method stub

			}
		});
		pollingGrid.setWidget(3, 1, textMaxNumAvailableLines);

		pollingGrid.setText(4, 0, CentraleUI.getMessages().total_num_modem());
		textTotalModemNumber = new TextBox();
		textTotalModemNumber.setStyleName("gwt-bg-text-orange");
		textTotalModemNumber.setMaxLength(3);
		textTotalModemNumber.setWidth("100px");
		textTotalModemNumber.addKeyPressHandler(textBoxKeyPressHandler);
		pollingGrid.setWidget(4, 1, textTotalModemNumber);

		pollingGrid.setText(4, 2, CentraleUI.getMessages().num_shared_lines());
		textNumSharedLines = new TextBox();
		textNumSharedLines.setStyleName("gwt-bg-text-orange");
		textNumSharedLines.setMaxLength(3);
		textNumSharedLines.setWidth("100px");
		textNumSharedLines.addKeyPressHandler(textBoxKeyPressHandler);
		pollingGrid.setWidget(4, 3, textNumSharedLines);

		pollingGrid.setText(5, 0, CentraleUI.getMessages().num_reserved_lines_ui());
		textNumReservedLinesUi = new TextBox();
		textNumReservedLinesUi.setStyleName("gwt-bg-text-orange");
		textNumReservedLinesUi.setMaxLength(3);
		textNumReservedLinesUi.setWidth("100px");
		textNumReservedLinesUi.addKeyPressHandler(textBoxKeyPressHandler);
		pollingGrid.setWidget(5, 1, textNumReservedLinesUi);

		pollingGrid.setText(5, 2, CentraleUI.getMessages().reserved_line());
		checkReservedLine = new CheckBox();
		pollingGrid.setWidget(5, 3, checkReservedLine);

		pollingGrid.setText(6, 0, CentraleUI.getMessages().router_timeout());
		textRouterTimeout = new TextBox();
		textRouterTimeout.setStyleName("gwt-bg-text-orange");
		textRouterTimeout.setMaxLength(4);
		textRouterTimeout.setWidth("100px");
		textRouterTimeout.addKeyPressHandler(textBoxKeyPressHandler);
		pollingGrid.setWidget(6, 1, textRouterTimeout);

		pollingGrid.setText(6, 2, CentraleUI.getMessages().router_try_timeout());
		textRouterTryTimeout = new TextBox();
		textRouterTryTimeout.setStyleName("gwt-bg-text-orange");
		textRouterTryTimeout.setMaxLength(4);
		textRouterTryTimeout.setWidth("100px");
		textRouterTryTimeout.addKeyPressHandler(textBoxKeyPressHandler);
		pollingGrid.setWidget(6, 3, textRouterTryTimeout);

		pollingGrid.setText(7, 0, CentraleUI.getMessages().cop_ip());
		textCopIp = new TextBox();
		textCopIp.setStyleName("gwt-bg-text-orange");
		textCopIp.setMaxLength(15);
		textCopIp.setWidth("100px");
		textCopIp.addKeyPressHandler(positiveKeyPressHandler);
		pollingGrid.setWidget(7, 1, textCopIp);

		pollingGrid.setText(7, 2, CentraleUI.getMessages().cop_router_ip());
		textCopRouterIp = new TextBox();
		textCopRouterIp.setStyleName("gwt-bg-text-orange");
		textCopRouterIp.setMaxLength(15);
		textCopRouterIp.setWidth("100px");
		textCopRouterIp.addKeyPressHandler(positiveKeyPressHandler);
		pollingGrid.setWidget(7, 3, textCopRouterIp);

		pollingGrid.setText(8, 0, CentraleUI.getMessages().time_host_lan());
		textTimeHostLan = new TextBox();
		textTimeHostLan.setStyleName("gwt-bg-text-orange");
		textTimeHostLan.setMaxLength(200);
		textTimeHostLan.setWidth("100px");
		pollingGrid.setWidget(8, 1, textTimeHostLan);

		pollingGrid.setText(8, 2, CentraleUI.getMessages().time_host_modem());
		textTimeHostModem = new TextBox();
		textTimeHostModem.setStyleName("gwt-bg-text-orange");
		textTimeHostModem.setMaxLength(200);
		textTimeHostModem.setWidth("100px");
		pollingGrid.setWidget(8, 3, textTimeHostModem);

		pollingGrid.setText(9, 0, CentraleUI.getMessages().time_host_proxy());
		textTimeHostProxy = new TextBox();
		textTimeHostProxy.setStyleName("gwt-bg-text-orange");
		textTimeHostProxy.setMaxLength(200);
		textTimeHostProxy.setWidth("100px");
		pollingGrid.setWidget(9, 1, textTimeHostProxy);

		pollingGrid.setText(9, 2, CentraleUI.getMessages().time_host_router());
		textTimeHostRouter = new TextBox();
		textTimeHostRouter.setStyleName("gwt-bg-text-orange");
		textTimeHostRouter.setMaxLength(200);
		textTimeHostRouter.setWidth("100px");
		pollingGrid.setWidget(9, 3, textTimeHostRouter);

		pollingGrid.setText(10, 0, CentraleUI.getMessages().proxy_host());
		proxyHostTextBox = new TextBox();
		proxyHostTextBox.setStyleName("gwt-bg-text-orange");
		proxyHostTextBox.setMaxLength(200);
		proxyHostTextBox.setWidth("100px");
		pollingGrid.setWidget(10, 1, proxyHostTextBox);

		pollingGrid.setText(10, 2, CentraleUI.getMessages().proxy_port());
		proxyPortTextBox = new TextBox();
		proxyPortTextBox.setStyleName("gwt-bg-text-orange");
		proxyPortTextBox.setMaxLength(200);
		proxyPortTextBox.setWidth("100px");
		pollingGrid.setWidget(10, 3, proxyPortTextBox);

		pollingGrid.setText(11, 0, CentraleUI.getMessages().proxy_exclusion());
		pollingGrid.getFlexCellFormatter().setColSpan(11, 0, 2);
		proxyExclusionTextBox = new TextBox();
		proxyExclusionTextBox.setStyleName("gwt-bg-text-orange");
		proxyExclusionTextBox.setMaxLength(200);
		proxyExclusionTextBox.setWidth("200px");
		pollingGrid.setWidget(11, 1, proxyExclusionTextBox);

		for (int i = 0; i < pollingGrid.getRowCount(); i++)
			for (int j = 0; j < pollingGrid.getCellCount(i); j++) {
				// pollingGrid.getCellFormatter().setWidth(i, j, "227px");
				pollingGrid.getCellFormatter().setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_LEFT);
			}

		// fill extrapolling grid fields
		extraPollingGrid.setStyleName("gwt-Grid");
		extraPollingGrid.setWidth("100%");
		extraPollingGrid.setCellSpacing(3);
		extraPollingTitle.setVisible(false);
		extraPollingGrid.setVisible(false);
		extraPollingGrid.setText(0, 0, CentraleUI.getMessages().office_opening_time());
		textOpeningTime = new TextBox();
		textOpeningTime.setStyleName("gwt-bg-text-orange");
		textOpeningTime.setMaxLength(2);
		textOpeningTime.addKeyPressHandler(textBoxKeyPressHandler);
		textOpeningTime.setWidth("100px");
		extraPollingGrid.setWidget(0, 1, textOpeningTime);

		extraPollingGrid.setText(0, 2, CentraleUI.getMessages().office_close_time());
		textCloseTime = new TextBox();
		textCloseTime.setStyleName("gwt-bg-text-orange");
		textCloseTime.setMaxLength(2);
		textCloseTime.addKeyPressHandler(textBoxKeyPressHandler);
		textCloseTime.setWidth("100px");
		extraPollingGrid.setWidget(0, 3, textCloseTime);

		extraPollingGrid.setText(1, 0, CentraleUI.getMessages().close_saturday());
		checkCloseSat = new CheckBox();
		extraPollingGrid.setWidget(1, 1, checkCloseSat);

		extraPollingGrid.setText(1, 2, CentraleUI.getMessages().close_sunday());
		checkCloseSun = new CheckBox();
		extraPollingGrid.setWidget(1, 3, checkCloseSun);

		extraPollingGrid.setText(2, 0, CentraleUI.getMessages().extra_office_time());
		textExtraPollingTime = new TextBox();
		textExtraPollingTime.setStyleName("gwt-bg-text-orange");
		textExtraPollingTime.setMaxLength(4);
		textExtraPollingTime.setWidth("100px");
		textExtraPollingTime.addKeyPressHandler(textBoxKeyPressHandler);
		extraPollingGrid.setWidget(2, 1, textExtraPollingTime);

		for (int i = 0; i < extraPollingGrid.getRowCount(); i++)
			for (int j = 0; j < extraPollingGrid.getCellCount(i); j++) {
				extraPollingGrid.getCellFormatter().setWidth(i, j, "227px");
				extraPollingGrid.getCellFormatter().setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_LEFT);
			}

		// fill thresholds grid fields
		thresholdsGrid.setStyleName("gwt-Grid");
		thresholdsGrid.setWidth("100%");
		thresholdsGrid.setCellSpacing(3);
		thresholdsGrid.setText(0, 0, CentraleUI.getMessages().min_threshold_temperature());
		textMinTemperatureThreshold = new TextBox();
		textMinTemperatureThreshold.setStyleName("gwt-bg-text-orange");
		textMinTemperatureThreshold.setMaxLength(4);
		textMinTemperatureThreshold.setWidth("100px");
		textMinTemperatureThreshold.addKeyPressHandler(negativeTextBoxKeyPressHandler);
		thresholdsGrid.setWidget(0, 1, textMinTemperatureThreshold);

		thresholdsGrid.setText(0, 2, CentraleUI.getMessages().max_threshold_temperature());
		textMaxTemperatureThreshold = new TextBox();
		textMaxTemperatureThreshold.setStyleName("gwt-bg-text-orange");
		textMaxTemperatureThreshold.setMaxLength(4);
		textMaxTemperatureThreshold.setWidth("100px");
		textMaxTemperatureThreshold.addKeyPressHandler(negativeTextBoxKeyPressHandler);
		thresholdsGrid.setWidget(0, 3, textMaxTemperatureThreshold);

		thresholdsGrid.setText(1, 0, CentraleUI.getMessages().max_alarm_threshold_temperature());
		textAlarmMaxTemperatureThreshold = new TextBox();
		textAlarmMaxTemperatureThreshold.setStyleName("gwt-bg-text-orange");
		textAlarmMaxTemperatureThreshold.setMaxLength(4);
		textAlarmMaxTemperatureThreshold.setWidth("100px");
		textAlarmMaxTemperatureThreshold.addKeyPressHandler(negativeTextBoxKeyPressHandler);
		thresholdsGrid.setWidget(1, 1, textAlarmMaxTemperatureThreshold);

		for (int i = 0; i < thresholdsGrid.getRowCount(); i++)
			for (int j = 0; j < thresholdsGrid.getCellCount(i); j++) {
				thresholdsGrid.getCellFormatter().setWidth(i, j, "227px");
				thresholdsGrid.getCellFormatter().setHorizontalAlignment(i, j, HasHorizontalAlignment.ALIGN_LEFT);
			}

		// button for cancel and save
		PanelButtonWidget panelButtonForUndoSave = new PanelButtonWidget(PanelButtonWidget.ORANGE);

		// button cancel for connection info
		ClickHandler cancelClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getConfigInfo();
			}
		};
		panelButtonForUndoSave.addButton(CentraleUIConstants.UNDO, cancelClickHandler);

		// button send/verify
		ClickHandler sendVerifyClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ConfigInfoObject configInfo = prepareConfigInfoObj(true);
				CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
				ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
				endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
				AsyncCallback<String> callback = new UIAsyncCallback<String>() {
					public void onSuccess(String errStr) {
						if (errStr != null)
							Window.alert(errStr);
						else {
							Utils.operationOk();
							if (isNewCop)
								isNewCop = false;

						}
					}
				};

				if (configInfo != null)
					centraleService.saveConfigInfo(configInfo, isNewCop, callback);

			}
		};
		panelButtonForUndoSave.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler);

		panel.add(panelButtonForUndoSave);
		panel.setSpacing(10);
		panel.setCellHorizontalAlignment(panelButtonForUndoSave, HasHorizontalAlignment.ALIGN_CENTER);

		externalPanel.add(copTitle);
		externalPanel.add(panel);

		initWidget(externalPanel);

	} // end constructor

	private void getConfigInfo() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<ConfigInfoObject> callback = new UIAsyncCallback<ConfigInfoObject>() {
			public void onSuccess(ConfigInfoObject configInfoObj) {
				// case of existing configInfo
				if (configInfoObj != null) {
					isNewCop = false;
					nameTextBox.setText(configInfoObj.getName());
					mapTextBox.setText(configInfoObj.getGenericMapName());
					textPollingTime.setText(new Integer(configInfoObj.getPollingOfficeTime()).toString());
					checkExtraPolling.setValue(configInfoObj.isUsePollingExtra());
					if (checkExtraPolling.getValue()) {
						extraPollingTitle.setVisible(true);
						extraPollingGrid.setVisible(true);
					} else {
						extraPollingTitle.setVisible(false);
						extraPollingGrid.setVisible(false);
					}
					if (configInfoObj.getSampleDataTypeToDownload() == CentraleUIConstants.DISABLED)
						sampleDataDownloadList.setSelectedIndex(0);
					else if (configInfoObj.getSampleDataTypeToDownload() == CentraleUIConstants.CALIBRATION_DATA)
						sampleDataDownloadList.setSelectedIndex(1);
					else if (configInfoObj.getSampleDataTypeToDownload() == CentraleUIConstants.ALL_DATA)
						sampleDataDownloadList.setSelectedIndex(2);
					checkDownloadAlarm.setValue(configInfoObj.isDownloadAlarm());
					textMaxNumAvailableLines.setText(new Integer(configInfoObj.getMaxNumLines()).toString());
					textTotalModemNumber.setText(new Integer(configInfoObj.getTotalNumModem()).toString());
					textNumSharedLines.setText(new Integer(configInfoObj.getNumModemSharedLines()).toString());
					textRouterTimeout.setText(new Integer(configInfoObj.getRouterTimeout()).toString());
					textRouterTryTimeout.setText(new Integer(configInfoObj.getRouterTryTimeout()).toString());
					textCopIp.setText(configInfoObj.getCopIp());
					textCopRouterIp.setText(configInfoObj.getCopRouterIp());
					textTimeHostLan.setText(configInfoObj.getTimeHostLan());
					textTimeHostModem.setText(configInfoObj.getTimeHostModem());
					textTimeHostProxy.setText(configInfoObj.getTimeHostProxy());
					textTimeHostRouter.setText(configInfoObj.getTimeHostRouter());
					checkReservedLine.setValue(configInfoObj.isReservedLine());
					proxyHostTextBox.setText(configInfoObj.getProxyHost());
					if (configInfoObj.getProxyPort() != null)
						proxyPortTextBox.setText(
								(configInfoObj.getProxyPort() != null ? configInfoObj.getProxyPort().toString() : ""));
					else
						proxyPortTextBox.setText("");
					proxyExclusionTextBox.setText(configInfoObj.getProxyExclusion());
					textNumReservedLinesUi.setText(new Integer(configInfoObj.getNumReservedSharedLinesUi()).toString());
					if (configInfoObj.getOpenAt() != null)
						textOpeningTime.setText(configInfoObj.getOpenAt().toString());
					else
						textOpeningTime.setText("");
					if (configInfoObj.getCloseAt() != null)
						textCloseTime.setText(configInfoObj.getCloseAt().toString());
					else
						textCloseTime.setText("");
					checkCloseSat.setValue(configInfoObj.isCloseSat());
					checkCloseSun.setValue(configInfoObj.isCloseSun());
					if (configInfoObj.getPollingExtraOffice() != null)
						textExtraPollingTime.setText(configInfoObj.getPollingExtraOffice().toString());
					else
						textExtraPollingTime.setText("");
					if (configInfoObj.getMinThreshold() != null)
						textMinTemperatureThreshold.setText(configInfoObj.getMinThreshold().toString());
					else
						textMinTemperatureThreshold.setText("");
					if (configInfoObj.getMaxThreshold() != null)
						textMaxTemperatureThreshold.setText(configInfoObj.getMaxThreshold().toString());
					else
						textMaxTemperatureThreshold.setText("");
					if (configInfoObj.getAlarmMaxThreshold() != null)
						textAlarmMaxTemperatureThreshold.setText(configInfoObj.getAlarmMaxThreshold().toString());
					else
						textAlarmMaxTemperatureThreshold.setText("");
				} else {
					isNewCop = true;
					nameTextBox.setText("");
					mapTextBox.setText("");
					textPollingTime.setText("");
					checkExtraPolling.setValue(false);
					extraPollingTitle.setVisible(false);
					extraPollingGrid.setVisible(false);
					sampleDataDownloadList.setSelectedIndex(0);
					checkDownloadAlarm.setValue(false);
					textMaxNumAvailableLines.setText("");
					textTotalModemNumber.setText("");
					textNumSharedLines.setText("");
					textRouterTimeout.setText("");
					textRouterTryTimeout.setText("");
					textCopIp.setText("");
					textCopRouterIp.setText("");
					textTimeHostLan.setText("");
					textTimeHostModem.setText("");
					textTimeHostProxy.setText("");
					textTimeHostRouter.setText("");
					checkReservedLine.setValue(false);
					textOpeningTime.setText("");
					textCloseTime.setText("");
					checkCloseSat.setValue(false);
					checkCloseSun.setValue(false);
					textExtraPollingTime.setText("");
					textMinTemperatureThreshold.setText("");
					textMaxTemperatureThreshold.setText("");
					textAlarmMaxTemperatureThreshold.setText("");
					textNumReservedLinesUi.setText("");
					proxyPortTextBox.setText("");
					proxyPortTextBox.setText("");
					proxyExclusionTextBox.setText("");
				}
			}
		};
		centraleService.getConfigInfo(callback);
	}// end getConfigInfo

	private ConfigInfoObject prepareConfigInfoObj(boolean verifyCorrectFields) {
		ConfigInfoObject configInfo = new ConfigInfoObject();
		boolean parsingError = false;
		if (textPollingTime.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().polling_time_empty());
		} else if (textMaxNumAvailableLines.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().max_num_lines_empty());
		} else if (textTotalModemNumber.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().total_num_modem_empty());
		} else if (textNumSharedLines.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().num_modem_shared_lines_empty());
		} else if (textRouterTimeout.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().router_timeout_empty());
		} else if (textRouterTryTimeout.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().router_try_timeout_empty());
		} else if (textCopIp.getText().trim().equals("")) {
			parsingError = true;
			Window.alert(CentraleUI.getMessages().cop_ip_empty());
		} else {
			try {
				configInfo.setPollingOfficeTime(new Integer(textPollingTime.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			configInfo.setUsePollingExtra(checkExtraPolling.getValue());
			int selectedIndex = sampleDataDownloadList.getSelectedIndex();
			configInfo.setSampleDataTypeToDownload(
					new Integer(sampleDataDownloadList.getValue(selectedIndex)).intValue());
			configInfo.setDownloadAlarm(checkDownloadAlarm.getValue());
			try {
				configInfo.setMaxNumLines(new Integer(textMaxNumAvailableLines.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			try {
				configInfo.setTotalNumModem(new Integer(textTotalModemNumber.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			try {
				configInfo.setNumModemSharedLines(new Integer(textNumSharedLines.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			try {
				configInfo.setNumReservedSharedLinesUi(new Integer(textNumReservedLinesUi.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			try {
				configInfo.setRouterTimeout(new Integer(textRouterTimeout.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			try {
				configInfo.setRouterTryTimeout(new Integer(textRouterTryTimeout.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			if (nameTextBox.getText().equals(""))
				configInfo.setName(null);
			else
				configInfo.setName(nameTextBox.getText());
			if (mapTextBox.getText().equals(""))
				configInfo.setGenericMapName(CentraleUIConstants.DEFAULT_MAP_NAME);
			else
				configInfo.setGenericMapName(mapTextBox.getText());
			if (!textCopIp.getText().equals(""))
				configInfo.setCopIp(textCopIp.getText());
			else
				configInfo.setCopIp(null);
			if (!textCopRouterIp.getText().equals(""))
				configInfo.setCopRouterIp(textCopRouterIp.getText());
			else
				configInfo.setCopRouterIp(null);
			if (!textTimeHostLan.getText().equals(""))
				configInfo.setTimeHostLan(textTimeHostLan.getText());
			else
				configInfo.setTimeHostLan(null);
			if (!textTimeHostModem.getText().equals(""))
				configInfo.setTimeHostModem(textTimeHostModem.getText());
			else
				configInfo.setTimeHostModem(null);
			if (!textTimeHostProxy.getText().equals(""))
				configInfo.setTimeHostProxy(textTimeHostProxy.getText());
			else
				configInfo.setTimeHostProxy(null);
			if (!textTimeHostRouter.getText().equals(""))
				configInfo.setTimeHostRouter(textTimeHostRouter.getText());
			else
				configInfo.setTimeHostRouter(null);
			if (!proxyHostTextBox.getText().equals(""))
				configInfo.setProxyHost(proxyHostTextBox.getText());
			else
				configInfo.setProxyHost(null);
			try {
				if (proxyPortTextBox.getText().equals("") && proxyHostTextBox.getText().equals(""))
					configInfo.setProxyPort(null);
				else if (!proxyHostTextBox.getText().equals("") && proxyPortTextBox.getText().equals("")
						|| proxyHostTextBox.getText().equals("") && !proxyPortTextBox.getText().equals("")) {
					parsingError = true;
					Window.alert(CentraleUI.getMessages().mandatory_proxy());
				} else
					configInfo.setProxyPort(new Integer(proxyPortTextBox.getText()).intValue());
			} catch (NumberFormatException nfEx) {
				parsingError = true;
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
			}
			if (!proxyExclusionTextBox.getText().equals(""))
				configInfo.setProxyExclusion(proxyExclusionTextBox.getText());
			else
				configInfo.setProxyExclusion(null);
			configInfo.setReservedLine(checkReservedLine.getValue());
			if (checkExtraPolling.getValue()) {
				if (!textOpeningTime.getText().equals("")) {
					try {
						configInfo.setOpenAt(new Integer(textOpeningTime.getText()));
					} catch (NumberFormatException nfEx) {
						parsingError = true;
						Window.alert(CentraleUI.getMessages().error_parsing_integer());
					}
					if (verifyCorrectFields && configInfo.getOpenAt().intValue() > 23) {
						parsingError = true;
						Window.alert(CentraleUI.getMessages().hour_value_incorrect());
					}
				} else
					configInfo.setOpenAt(null);
				if (!textCloseTime.getText().equals("")) {
					try {
						configInfo.setCloseAt(new Integer(textCloseTime.getText()));
					} catch (NumberFormatException nfEx) {
						parsingError = true;
						Window.alert(CentraleUI.getMessages().error_parsing_integer());
					}
					if (verifyCorrectFields && configInfo.getCloseAt().intValue() > 23) {
						parsingError = true;
						Window.alert(CentraleUI.getMessages().hour_value_incorrect());
					}
				} else
					configInfo.setCloseAt(null);
				if (!textExtraPollingTime.getText().equals("")) {
					try {
						configInfo.setPollingExtraOffice(new Integer(textExtraPollingTime.getText()));
					} catch (NumberFormatException nfEx) {
						parsingError = true;
						Window.alert(CentraleUI.getMessages().error_parsing_integer());
					}
				} else
					configInfo.setPollingExtraOffice(null);
				configInfo.setCloseSat(checkCloseSat.getValue());
				configInfo.setCloseSun(checkCloseSun.getValue());
			} // end if checkExtraPolling.isChecked()
			else {
				configInfo.setOpenAt(null);
				configInfo.setCloseAt(null);
				configInfo.setPollingExtraOffice(null);
				configInfo.setCloseSat(false);
				configInfo.setCloseSun(false);
			} // end else checkExtraPolling.isChecked()
			if (!textMinTemperatureThreshold.getText().equals("")) {
				try {
					configInfo.setMinThreshold(new Integer(textMinTemperatureThreshold.getText()));
				} catch (NumberFormatException nfEx) {
					parsingError = true;
					Window.alert(CentraleUI.getMessages().error_parsing_integer());
				}
			} else
				configInfo.setMinThreshold(null);
			if (!textMaxTemperatureThreshold.getText().equals("")) {
				try {
					configInfo.setMaxThreshold(new Integer(textMaxTemperatureThreshold.getText()));
				} catch (NumberFormatException nfEx) {
					parsingError = true;
					Window.alert(CentraleUI.getMessages().error_parsing_integer());
				}
			} else
				configInfo.setMaxThreshold(null);
			if (!textAlarmMaxTemperatureThreshold.getText().equals("")) {
				try {
					configInfo.setAlarmMaxThreshold(new Integer(textAlarmMaxTemperatureThreshold.getText()));
				} catch (NumberFormatException nfEx) {
					parsingError = true;
					Window.alert(CentraleUI.getMessages().error_parsing_integer());
				}
			} else
				configInfo.setAlarmMaxThreshold(null);
		} // end else all needed fields
		if (!parsingError)
			return configInfo;
		else
			return null;
	}

	@Override
	protected void dismissContent(final AsyncPageOperation asyncPageOperation) {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		ConfigInfoObject configInfo = prepareConfigInfoObj(false);
		if (configInfo != null)
			centraleService.verifySameCopFields(configInfo, asyncPageOperation);
	}

	@Override
	protected void loadContent() {
		getConfigInfo();
	}
}
