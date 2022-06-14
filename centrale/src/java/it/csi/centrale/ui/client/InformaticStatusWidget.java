/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: page for viewing informatic status of a selected station
 * Change log:
 *   2009-01-15: initial version
 * ----------------------------------------------------------------------------
 * $Id: InformaticStatusWidget.java,v 1.31 2015/10/19 13:22:22 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.InformaticStatusObject;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for viewing informatic status of a selected station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class InformaticStatusWidget extends UIPage {

	private static final String GPS_APP_ERROR = "GPS_APP_ERROR";

	private static final String GPS_READ_ERROR = "GPS_READ_ERROR";

	private static final String FIX_2D = "FIX_2D";

	private static final String FIX_3D = "FIX_3D";

	private static final int COMMONCFGSENTOK = 0;

	private static final int COMMONCFGNOTSENTFORCFGERR = 1;

	private static final int COMMONCFGNOTSENTFORPERIFERICOERR = 2;

	private static final int COMMONCFGNOTSENTFORPERIFERICOCRASH = 3;

	private VerticalPanel externalPanel;

	// Grids for global information.
	private final FlexTable globalStatusGrid = new FlexTable();

	// Grids for confBoard information.
	private final FlexTable confBoardGrid = new FlexTable();

	// Grids for boardInitialize information.
	private final FlexTable boardInitializeStatusGrid = new FlexTable();

	// Grids for dpa analyzer status information.
	private final FlexTable dpaStatusGrid = new FlexTable();

	// Grids for configuration information.
	private final FlexTable configurationStatusGrid = new FlexTable();
	private FlexTable configurationStatusSubGrid = new FlexTable();

	// Grids for filesystem information.
	private final FlexTable fileSystemStatusGrid = new FlexTable();

	// Grids for gps information.
	private final FlexTable gpsInfoGrid = new FlexTable();

	// Grids for gps status information.
	private final FlexTable gpsStatusGrid = new FlexTable();

	// Grids for gps position information.
	private final FlexTable gpsPositionGrid = new FlexTable();

	// Grids for communication information.
	private final FlexTable communicationStatusGrid = new FlexTable();

	private int stationId;

	private String stationName;

	private DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

	public InformaticStatusWidget() {
		PanelButtonWidget panelButton = new PanelButtonWidget(
				PanelButtonWidget.BLUE);

		panelButton.addButton(CentraleUIConstants.INFORMATIC_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

			}
		});
		panelButton.setEnabledButton(CentraleUIConstants.INFORMATIC_STATUS, false);

		panelButton.addButton(CentraleUIConstants.ANALYZER_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.analyzersStatusWidget.setStNameAndId(stationName,
						stationId);
				CentraleUI.setCurrentPage(CentraleUI.analyzersStatusWidget);
			}
		});

		panelButton.addButton(CentraleUIConstants.STATION_STATUS, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.stationAlarmStatusWidget.setStNameAndId(stationName,
						stationId);
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
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#view_informaticStatusWidget", "",
						"");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().informatic_status_title(), panelButton,
				false);

		// Label and panel for application status info
		Label title = new Label();
		title.setStyleName("gwt-Label-title-blue");
		title.setText(CentraleUI.getMessages().informatic_status_application());

		// panel that contains application info
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed-blue");

		panel.add(globalStatusGrid);

		panel.add(confBoardGrid);

		// title for initialize boards
		Label boardTitle = new Label();
		boardTitle.setText(CentraleUI.getMessages().informatic_board_title());
		boardTitle.setStyleName("gwt-informatic-title");
		confBoardGrid.setWidget(0, 0, boardTitle);
		confBoardGrid.setWidget(1, 0, boardInitializeStatusGrid);
		confBoardGrid.getCellFormatter().setVerticalAlignment(1, 0,
				HasVerticalAlignment.ALIGN_TOP);

		// title for initialize boards
		Label confTitle = new Label();
		confTitle.setText(CentraleUI.getMessages().informatic_configuration_title());
		confTitle.setStyleName("gwt-informatic-title");
		confBoardGrid.setWidget(0, 1, confTitle);
		confBoardGrid.setWidget(1, 1, configurationStatusGrid);
		confBoardGrid.getCellFormatter().setVerticalAlignment(1, 1,
				HasVerticalAlignment.ALIGN_TOP);

		Label dpaStatusTitle = new Label();
		dpaStatusTitle.setText(CentraleUI.getMessages().informatic_dpa_status());
		dpaStatusTitle.setStyleName("gwt-informatic-title");
		confBoardGrid.setWidget(2, 0, dpaStatusTitle);
		// dpaStatusGrid.getCellFormatter().setWidth(0, 0, "100%");
		confBoardGrid.setWidget(3, 0, dpaStatusGrid);
		confBoardGrid.getCellFormatter().setVerticalAlignment(3, 0,
				HasVerticalAlignment.ALIGN_TOP);
		confBoardGrid.getFlexCellFormatter().setColSpan(3, 0, 2);

		confBoardGrid.setCellSpacing(10);

		externalPanel.add(title);
		externalPanel.add(panel);

		// Label and panel for file system info
		Label title2 = new Label();
		title2.setStyleName("gwt-Label-title-blue");
		title2.setText(CentraleUI.getMessages().informatic_file_system_status());

		// panel that contains file system info
		VerticalPanel panel2 = new VerticalPanel();
		panel2.setStyleName("gwt-post-boxed-blue");
		panel2.add(fileSystemStatusGrid);

		externalPanel.add(title2);
		externalPanel.add(panel2);

		// Label and panel for gps info
		Label title3 = new Label();
		title3.setStyleName("gwt-Label-title-blue");
		title3.setText(CentraleUI.getMessages().informatic_gps_status());

		// panel that contains gps info
		VerticalPanel panel3 = new VerticalPanel();
		panel3.setStyleName("gwt-post-boxed-blue");
		panel3.add(gpsInfoGrid);

		gpsInfoGrid.setWidget(0, 0, gpsStatusGrid);
		gpsInfoGrid.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_TOP);

		gpsInfoGrid.setWidget(0, 1, gpsPositionGrid);
		gpsInfoGrid.getCellFormatter().setVerticalAlignment(0, 1,
				HasVerticalAlignment.ALIGN_TOP);

		externalPanel.add(title3);
		externalPanel.add(panel3);

		// Label and panel for file communication info
		Label title4 = new Label();
		title4.setStyleName("gwt-Label-title-blue");
		title4.setText(CentraleUI.getMessages().informatic_communication_status());

		// panel that contains file communication info
		VerticalPanel panel4 = new VerticalPanel();
		panel4.setStyleName("gwt-post-boxed-blue");
		panel4.add(communicationStatusGrid);

		externalPanel.add(title4);
		externalPanel.add(panel4);

		// Label and panel for extra info
		Label title5 = new Label();
		title5.setStyleName("gwt-Label-title-blue");
		title5.setText(CentraleUI.getMessages().extra_info());

		initWidget(externalPanel);
	}

	public void setStNameAndId(String stationName, int stationId) {
		this.stationId = stationId;
		this.stationName = stationName;
	}

	private void setFields() {
		Label pageTitle = (Label) (((HorizontalPanel) (externalPanel
				.getWidget(0))).getWidget(0));
		pageTitle.setText(stationName);

		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<InformaticStatusObject> callback = new UIAsyncCallback<InformaticStatusObject>() {
			public void onSuccess(InformaticStatusObject informaticStatusObj) {
				IconImageBundle iconImageBundle = (IconImageBundle) GWT
						.create(IconImageBundle.class);

				if (informaticStatusObj != null) {
					// isOk
					if (informaticStatusObj.isOK()) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						globalStatusGrid.setWidget(0, 0, ledGreen);
						globalStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().ok());
					} else {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						globalStatusGrid.setWidget(0, 0, ledRed);
						globalStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().alarm());
					}
					globalStatusGrid.setText(0, 1,
							CentraleUI.getMessages().informatic_global_status());
					globalStatusGrid.getCellFormatter().setWidth(0, 0, "100px");
					globalStatusGrid.getCellFormatter().setAlignment(0, 0,
							HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					globalStatusGrid.getCellFormatter().setAlignment(0, 1,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					globalStatusGrid.setWidth("990px");
					globalStatusGrid.getCellFormatter().setWidth(0, 0, "62px");
					globalStatusGrid.setCellPadding(5);
					globalStatusGrid.setCellSpacing(5);

					// boardManagerInitStatus
					if (informaticStatusObj.getBoardManInitStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						boardInitializeStatusGrid.setWidget(0, 0, ledGray);
						boardInitializeStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().no_info());
					} else if (informaticStatusObj.getBoardManInitStatus()
							.booleanValue()) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						boardInitializeStatusGrid.setWidget(0, 0, ledGreen);
						boardInitializeStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().ok());
					} else {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						boardInitializeStatusGrid.setWidget(0, 0, ledRed);
						boardInitializeStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().alarm());
					}
					boardInitializeStatusGrid.setText(0, 1, CentraleUI.getMessages()
							.informatic_boardManagerInitStatus());
					boardInitializeStatusGrid.getCellFormatter().setAlignment(
							0, 0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					boardInitializeStatusGrid.getCellFormatter().setAlignment(
							0, 1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					// configuredBoardsNumber and initializedBoardsNumber
					if (informaticStatusObj.getInitBoardsNumber() == null
							|| informaticStatusObj.getCfgBoardsNumber() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						boardInitializeStatusGrid.setWidget(1, 0, ledGray);
						boardInitializeStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().no_info());
						boardInitializeStatusGrid
								.setText(
										1,
										1,
										CentraleUI.getMessages()
												.informatic_configured_initializedBoardsNumber()
												+ CentraleUI.getMessages().no_info());
					} else {
						if (informaticStatusObj.getCfgBoardsNumber().intValue() != informaticStatusObj.getInitBoardsNumber()
								.intValue()) {
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							boardInitializeStatusGrid.setWidget(1, 0, ledRed);
							boardInitializeStatusGrid.getWidget(1, 0).setTitle(
									CentraleUI.getMessages().alarm());
						} else {
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							boardInitializeStatusGrid.setWidget(1, 0, ledGreen);
							boardInitializeStatusGrid.getWidget(1, 0).setTitle(
									CentraleUI.getMessages().ok());
						}
						boardInitializeStatusGrid
								.setText(
										1,
										1,
										CentraleUI.getMessages()
												.informatic_configured_initializedBoardsNumber()
												+ " "
												+ informaticStatusObj.getCfgBoardsNumber()
														.toString()
												+ "/"
												+ informaticStatusObj.getInitBoardsNumber()
														.toString());
					}
					boardInitializeStatusGrid.getCellFormatter().setAlignment(
							1, 0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					boardInitializeStatusGrid.getCellFormatter().setAlignment(
							1, 1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					// failed initialized board
					String number = "";
					if (informaticStatusObj.getFailedBoardBindingsNumber() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						boardInitializeStatusGrid.setWidget(2, 0, ledGray);
						boardInitializeStatusGrid.getWidget(2, 0).setTitle(
								CentraleUI.getMessages().no_info());
					} else {
						number = informaticStatusObj.getFailedBoardBindingsNumber()
								.toString();
						if (informaticStatusObj.getFailedBoardBindingsNumber()
								.intValue() == 0) {
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							boardInitializeStatusGrid.setWidget(2, 0, ledGreen);
							boardInitializeStatusGrid.getWidget(2, 0).setTitle(
									CentraleUI.getMessages().ok());
						} else {
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							boardInitializeStatusGrid.setWidget(2, 0, ledRed);
							boardInitializeStatusGrid.getWidget(2, 0).setTitle(
									CentraleUI.getMessages().alarm());
						}
					}// end else
					boardInitializeStatusGrid.setText(
							2,
							1,
							CentraleUI.getMessages()
									.informatic_board_failed_initialize()
									+ " "
									+ number);
					boardInitializeStatusGrid.getCellFormatter().setAlignment(
							2, 0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					boardInitializeStatusGrid.getCellFormatter().setAlignment(
							2, 1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					boardInitializeStatusGrid.setStyleName("gwt-Grid-blue");
					boardInitializeStatusGrid.getCellFormatter().setWidth(0, 0,
							"40px");
					boardInitializeStatusGrid.getCellFormatter().setWidth(0, 1,
							"430px");
					boardInitializeStatusGrid.setWidth("480px");
					boardInitializeStatusGrid.setHeight("85px");
					boardInitializeStatusGrid.setCellPadding(4);
					boardInitializeStatusGrid.setCellSpacing(4);

					// loadConfigurationStatus
					String loadCfgStatus = "";
					if (informaticStatusObj.getLoadCfgStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						configurationStatusGrid.setWidget(0, 0, ledGray);
						configurationStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().no_info());
					} else {
						loadCfgStatus = informaticStatusObj.getLoadCfgStatus();
						if (informaticStatusObj.getLoadCfgStatus().equals("OK")) {
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							configurationStatusGrid.setWidget(0, 0, ledGreen);
							configurationStatusGrid.getWidget(0, 0).setTitle(
									CentraleUI.getMessages().ok());
						} else {
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							configurationStatusGrid.setWidget(0, 0, ledRed);
							configurationStatusGrid.getWidget(0, 0).setTitle(
									CentraleUI.getMessages().alarm());
						}
					}

					configurationStatusGrid.getCellFormatter().setAlignment(0,
							0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);

					configurationStatusGrid.setWidget(0, 1,
							configurationStatusSubGrid);
					configurationStatusSubGrid.setText(
							0,
							0,
							CentraleUI.getMessages()
									.informatic_loadConfigurationStatus()
									+ " "
									+ loadCfgStatus);
					configurationStatusSubGrid.getCellFormatter().setAlignment(
							0, 1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					// saveNewConfigurationStatus
					if (informaticStatusObj.getSaveCfgStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						configurationStatusGrid.setWidget(1, 0, ledGray);
						configurationStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().no_info());
					} else if (informaticStatusObj.getSaveCfgStatus().booleanValue()) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						configurationStatusGrid.setWidget(1, 0, ledGreen);
						configurationStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().ok());
					} else {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						configurationStatusGrid.setWidget(1, 0, ledRed);
						configurationStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().alarm());
					}
					configurationStatusGrid.setText(1, 1, CentraleUI.getMessages()
							.informatic_saveNewConfigurationStatus());
					configurationStatusGrid.getCellFormatter().setWidth(0, 0,
							"100px");
					configurationStatusGrid.getCellFormatter().setAlignment(1,
							0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					configurationStatusGrid.getCellFormatter().setAlignment(1,
							1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					// commoncfgfromcopstatus
					if (informaticStatusObj.getCommonCfgFromCopStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						configurationStatusGrid.setWidget(2, 0, ledGray);
					} else if (informaticStatusObj.getCommonCfgFromCopStatus()
							.toUpperCase().equals("OK")) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						configurationStatusGrid.setWidget(2, 0, ledGreen);
					} else {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						configurationStatusGrid.setWidget(2, 0, ledRed);
					}
					String problem = "";
					if (informaticStatusObj.getCommonCfgFromCopStatus() != null) {
						if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("CONSISTENCY_ERROR")) {
							problem = CentraleUI.getMessages()
									.consistency_error_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("UNPARSABLE")) {
							problem = CentraleUI.getMessages().unparsable_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("SAVE_ERROR")) {
							problem = CentraleUI.getMessages().save_error_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("LOAD_ERROR")) {
							problem = CentraleUI.getMessages().load_error_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("INCOMPATIBLE")) {
							problem = CentraleUI.getMessages()
									.incompatible_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("CONFIG_START_ERROR")) {
							problem = CentraleUI.getMessages()
									.config_start_error_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("CONFIG_LOAD_ERROR")) {
							problem = CentraleUI.getMessages()
									.config_load_error_from_cop();
						} else if (informaticStatusObj.getCommonCfgFromCopStatus()
								.toUpperCase().equals("OK")) {
							problem = CentraleUI.getMessages().config_ok_from_cop();
						}
						configurationStatusGrid.setText(2, 1, problem);
					} else {
						configurationStatusGrid.setText(2, 1,
								CentraleUI.getMessages().no_cmm_cfg_from_cop());
					}

					configurationStatusGrid.getCellFormatter().setAlignment(2,
							0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					configurationStatusGrid.getCellFormatter().setAlignment(2,
							1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					// acquisitionStarted
					String msg = "";
					if (informaticStatusObj.getCfgActivationStatus() == null) {
						Image ledYellow = new Image();
						ledYellow.setResource(iconImageBundle.ledYellow());
						configurationStatusSubGrid.setWidget(0, 1, ledYellow);
						msg = CentraleUI.getMessages().activation();
						configurationStatusSubGrid.getWidget(0, 1)
								.setTitle(msg);
					} else if (informaticStatusObj.getCfgActivationStatus()
							.booleanValue()) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						configurationStatusSubGrid.setWidget(0, 1, ledGreen);
						msg = CentraleUI.getMessages().active();
						configurationStatusSubGrid.getWidget(0, 1)
								.setTitle(msg);
					} else {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						configurationStatusSubGrid.setWidget(0, 1, ledRed);
						msg = CentraleUI.getMessages().not_active();
						configurationStatusSubGrid.getWidget(0, 1)
								.setTitle(msg);
					}
					configurationStatusSubGrid.setText(0, 2,
							CentraleUI.getMessages().informatic_acquisitionStarted()
									+ " " + msg);
					configurationStatusSubGrid.getCellFormatter().setAlignment(
							0, 1, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					configurationStatusSubGrid.getCellFormatter().setAlignment(
							0, 2, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					configurationStatusGrid.setStyleName("gwt-Grid-blue");
					configurationStatusGrid.getCellFormatter().setWidth(0, 0,
							"40px");
					configurationStatusGrid.getCellFormatter().setWidth(0, 1,
							"430px");
					configurationStatusGrid.setWidth("480px");
					configurationStatusGrid.setHeight("85px");
					configurationStatusGrid.setCellPadding(4);
					configurationStatusGrid.setCellSpacing(4);

					configurationStatusSubGrid.setCellPadding(4);
					configurationStatusSubGrid.setCellSpacing(0);
					configurationStatusSubGrid.setWidth("455px");
					configurationStatusSubGrid.getCellFormatter().setWidth(0,
							0, "240px");
					configurationStatusSubGrid.getCellFormatter().setWidth(0,
							1, "40px");
					configurationStatusSubGrid.getCellFormatter().setWidth(0,
							2, "175px");
					configurationStatusSubGrid.getCellFormatter().setAlignment(
							0, 0, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					configurationStatusSubGrid.getCellFormatter().setAlignment(
							0, 1, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					configurationStatusSubGrid.getCellFormatter().setAlignment(
							0, 2, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					// application services status
					if (informaticStatusObj.getDpaOk() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						dpaStatusGrid.setWidget(0, 0, ledGray);
						dpaStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().no_info());
					} else if (informaticStatusObj.getDpaOk().booleanValue()) {
						boolean dpaWarn = informaticStatusObj.getDriverConfigsOk() != null
								&& !informaticStatusObj.getDriverConfigsOk()
										.booleanValue();
						if (dpaWarn) {
							Image ledYellow = new Image();
							ledYellow.setResource(iconImageBundle.ledYellow());
							dpaStatusGrid.setWidget(0, 0, ledYellow);
							dpaStatusGrid.getWidget(0, 0).setTitle(
									CentraleUI.getMessages()
											.drv_configs_locally_changed());
						} else {
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							dpaStatusGrid.setWidget(0, 0, ledGreen);
							dpaStatusGrid.getWidget(0, 0).setTitle(
									CentraleUI.getMessages().ok());
						}
					} else {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						dpaStatusGrid.setWidget(0, 0, ledRed);
						dpaStatusGrid.getWidget(0, 0).setTitle(
								CentraleUI.getMessages().alarm());
					}
					String enabled = "-";
					String initialized = "-";
					String failed = "-";
					if (informaticStatusObj.getEnabledDPANumber() != null)
						enabled = informaticStatusObj.getEnabledDPANumber()
								.toString();
					if (informaticStatusObj.getInitDPADriversNumber() != null)
						initialized = informaticStatusObj.getInitDPADriversNumber()
								.toString();
					if (informaticStatusObj.getFailedDPAThreadsNumber() != null)
						failed = informaticStatusObj.getFailedDPAThreadsNumber()
								.toString();
					dpaStatusGrid.setText(0, 1,
							CentraleUI.getMessages().informatic_dpa_number() + " "
									+ enabled + "/" + initialized + "/"
									+ failed);

					dpaStatusGrid.getCellFormatter().setAlignment(0, 0,
							HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.getCellFormatter().setAlignment(0, 1,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.setStyleName("gwt-Grid-blue");
					dpaStatusGrid.getCellFormatter().setWidth(0, 0, "40px");
					dpaStatusGrid.getCellFormatter().setWidth(0, 1, "430px");

					if (informaticStatusObj.getDataWriteErrorCount() > 0) {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						dpaStatusGrid.setWidget(0, 2, ledRed);
						dpaStatusGrid.getWidget(0, 2).setTitle(
								CentraleUI.getMessages().alarm());
					} else {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						dpaStatusGrid.setWidget(0, 2, ledGreen);
						dpaStatusGrid.getWidget(0, 2).setTitle(
								CentraleUI.getMessages().ok());
					}
					dpaStatusGrid
							.setText(
									0,
									3,
									CentraleUI.getMessages()
											.informatic_error_number()
											+ " "
											+ new Integer(
													informaticStatusObj.getDataWriteErrorCount())
													.toString());

					dpaStatusGrid.getCellFormatter().setAlignment(0, 2,
							HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.getCellFormatter().setAlignment(0, 3,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.setStyleName("gwt-Grid-blue");
					dpaStatusGrid.getCellFormatter().setWidth(0, 2, "40px");
					dpaStatusGrid.getCellFormatter().setWidth(0, 3, "430px");

					if (informaticStatusObj.getCurrentThreadFailures() > 0) {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						dpaStatusGrid.setWidget(1, 0, ledRed);
						dpaStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().alarm());
					} else if (informaticStatusObj.getTotalThreadFailures() > 0) {
						Image ledYellow = new Image();
						ledYellow.setResource(iconImageBundle.ledYellow());
						dpaStatusGrid.setWidget(1, 0, ledYellow);
						dpaStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().warning());
					} else {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						dpaStatusGrid.setWidget(1, 0, ledGreen);
						dpaStatusGrid.getWidget(1, 0).setTitle(
								CentraleUI.getMessages().ok());
					}
					dpaStatusGrid
							.setText(
									1,
									1,
									CentraleUI.getMessages()
											.informatic_error_number_application()
											+ " "
											+ new Integer(
													informaticStatusObj.getTotalThreadFailures())
													.toString()
											+ "/"
											+ new Integer(
													informaticStatusObj.getCurrentThreadFailures())
													.toString());
					dpaStatusGrid.getCellFormatter().setAlignment(1, 0,
							HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.getCellFormatter().setAlignment(1, 1,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.setStyleName("gwt-Grid-blue");
					dpaStatusGrid.getCellFormatter().setWidth(1, 0, "40px");
					dpaStatusGrid.getCellFormatter().setWidth(1, 1, "430px");

					if (informaticStatusObj.getDateInFuture() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						dpaStatusGrid.setWidget(1, 2, ledGray);
						dpaStatusGrid.setText(1, 3,
								CentraleUI.getMessages().data_future_no_info());
					} else if (informaticStatusObj.getDateInFuture().booleanValue()) {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						dpaStatusGrid.setWidget(1, 2, ledRed);
						dpaStatusGrid.setText(1, 3,
								CentraleUI.getMessages().data_in_future());
					} else {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						dpaStatusGrid.setWidget(1, 2, ledGreen);
						dpaStatusGrid.setText(1, 3,
								CentraleUI.getMessages().data_not_future());
					}

					dpaStatusGrid.getCellFormatter().setWidth(1, 2, "40px");
					dpaStatusGrid.getCellFormatter().setWidth(1, 3, "430px");

					dpaStatusGrid.getCellFormatter().setAlignment(1, 2,
							HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					dpaStatusGrid.getCellFormatter().setAlignment(1, 3,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					dpaStatusGrid.setWidth("980px");
					dpaStatusGrid.setHeight("60px");
					dpaStatusGrid.setCellPadding(4);
					dpaStatusGrid.setCellSpacing(4);

					// Disk usage percent
					Iterator<String> it_filesystem = informaticStatusObj.getFilesystem()
							.keySet().iterator();
					int columnNumber = 0;
					while (it_filesystem.hasNext()) {
						String diskName = it_filesystem.next();
						Integer diskUsage = informaticStatusObj.getFilesystem()
								.get(diskName);
						if (diskUsage == null) {
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledGray);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().no_info());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().disk() + " " + diskName
											+ CentraleUI.getMessages().no_info());
						} else {
							int thresholdIcon = getThresholdIcon(
									diskUsage.intValue(),
									informaticStatusObj.getWarningLevel(),
									informaticStatusObj.getAlarmLevel());
							if (thresholdIcon == OK) {
								Image ledGreen = new Image();
								ledGreen.setResource(iconImageBundle.ledGreen());
								fileSystemStatusGrid.setWidget(0, columnNumber,
										ledGreen);
								fileSystemStatusGrid.getWidget(0, columnNumber)
										.setTitle(CentraleUI.getMessages().ok());
							} else if (thresholdIcon == WARNING) {
								Image ledYellow = new Image();
								ledYellow.setResource(iconImageBundle
										.ledYellow());
								fileSystemStatusGrid.setWidget(0, columnNumber,
										ledYellow);
								fileSystemStatusGrid.getWidget(0, columnNumber)
										.setTitle(CentraleUI.getMessages().alarm());
							} else {
								Image ledRed = new Image();
								ledRed.setResource(iconImageBundle.ledRed());
								fileSystemStatusGrid.setWidget(0, columnNumber,
										ledRed);
								fileSystemStatusGrid.getWidget(0, columnNumber)
										.setTitle(CentraleUI.getMessages().alarm());
							}
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().disk() + " " + diskName
											+ ": " + diskUsage + "%");
						}// end else
						fileSystemStatusGrid.getCellFormatter().setWidth(0,
								columnNumber, "100px");
						fileSystemStatusGrid.getCellFormatter().setWidth(0,
								columnNumber, "62px");
						fileSystemStatusGrid.getCellFormatter().setAlignment(0,
								columnNumber,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						fileSystemStatusGrid.getCellFormatter().setAlignment(0,
								columnNumber + 1,
								HasHorizontalAlignment.ALIGN_LEFT,
								HasVerticalAlignment.ALIGN_MIDDLE);
						columnNumber = columnNumber + 2;
					}

					// SMART
					if (informaticStatusObj.getSmartStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						fileSystemStatusGrid
								.setWidget(0, columnNumber, ledGray);
						fileSystemStatusGrid.getWidget(0, columnNumber)
								.setTitle(CentraleUI.getMessages().no_info());
						fileSystemStatusGrid.setText(0, columnNumber + 1,
								CentraleUI.getMessages().smart() + ": "
										+ CentraleUI.getMessages().no_info());
					} else {
						if ("UNAVAILABLE"
								.equals(informaticStatusObj.getSmartStatus())) {
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledGray);
							fileSystemStatusGrid
									.getWidget(0, columnNumber)
									.setTitle(
											CentraleUI.getMessages().not_available());
							fileSystemStatusGrid.setText(
									0,
									columnNumber + 1,
									CentraleUI.getMessages().smart()
											+ ": "
											+ CentraleUI.getMessages()
													.not_available());
						} else if ("OK".equals(informaticStatusObj.getSmartStatus())) {
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledGreen);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().ok());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().smart() + ": "
											+ CentraleUI.getMessages().ok());
						} else if ("WARNING"
								.equals(informaticStatusObj.getSmartStatus())) {
							Image ledYellow = new Image();
							ledYellow.setResource(iconImageBundle.ledYellow());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledYellow);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().warning());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().smart() + ": "
											+ CentraleUI.getMessages().warning());
						} else {
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledRed);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().alarm());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().smart() + ": "
											+ CentraleUI.getMessages().alarm());
						}
					}
					fileSystemStatusGrid.getCellFormatter().setAlignment(0,
							columnNumber, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					fileSystemStatusGrid.getCellFormatter().setAlignment(0,
							columnNumber + 1,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					columnNumber = columnNumber + 2;

					// RAID
					if (informaticStatusObj.getRaidStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						fileSystemStatusGrid
								.setWidget(0, columnNumber, ledGray);
						fileSystemStatusGrid.getWidget(0, columnNumber)
								.setTitle(CentraleUI.getMessages().no_info());
						fileSystemStatusGrid.setText(0, columnNumber + 1,
								CentraleUI.getMessages().raid() + ": "
										+ CentraleUI.getMessages().no_info());
					} else {
						if ("UNAVAILABLE"
								.equals(informaticStatusObj.getRaidStatus())) {
							Image ledGray = new Image();
							ledGray.setResource(iconImageBundle.ledGray());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledGray);
							fileSystemStatusGrid
									.getWidget(0, columnNumber)
									.setTitle(
											CentraleUI.getMessages().not_available());
							fileSystemStatusGrid.setText(
									0,
									columnNumber + 1,
									CentraleUI.getMessages().raid()
											+ ": "
											+ CentraleUI.getMessages()
													.not_available());
						} else if ("OK".equals(informaticStatusObj.getRaidStatus())) {
							Image ledGreen = new Image();
							ledGreen.setResource(iconImageBundle.ledGreen());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledGreen);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().ok());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().raid() + ": "
											+ CentraleUI.getMessages().ok());
						} else if ("WARNING"
								.equals(informaticStatusObj.getRaidStatus())) {
							Image ledYellow = new Image();
							ledYellow.setResource(iconImageBundle.ledYellow());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledYellow);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().warning());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().raid() + ": "
											+ CentraleUI.getMessages().warning());
						} else {
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							fileSystemStatusGrid.setWidget(0, columnNumber,
									ledRed);
							fileSystemStatusGrid.getWidget(0, columnNumber)
									.setTitle(CentraleUI.getMessages().alarm());
							fileSystemStatusGrid.setText(0, columnNumber + 1,
									CentraleUI.getMessages().raid() + ": "
											+ CentraleUI.getMessages().alarm());
						}
					}
					fileSystemStatusGrid.getCellFormatter().setAlignment(0,
							columnNumber, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					fileSystemStatusGrid.getCellFormatter().setAlignment(0,
							columnNumber + 1,
							HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);

					fileSystemStatusGrid.setWidth("990px");
					fileSystemStatusGrid.setCellPadding(5);
					fileSystemStatusGrid.setCellSpacing(5);

					// load gps information
					if (!informaticStatusObj.isUseGps()
							|| informaticStatusObj.getGpsTimestamp() == null) {
						// gps not installed
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						gpsStatusGrid.setWidget(0, 0, ledGray);
						gpsStatusGrid.setText(0, 1,
								CentraleUI.getMessages().gps_not_installed());
						gpsStatusGrid.getCellFormatter().setWidth(0, 0, "62px");
						gpsStatusGrid.getCellFormatter().setAlignment(0, 0,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						gpsStatusGrid.setCellPadding(5);
						gpsStatusGrid.setCellSpacing(5);
					} else {
						// gps is installed
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						gpsStatusGrid.setWidget(0, 0, ledGreen);
						gpsStatusGrid.setText(0, 1,
								CentraleUI.getMessages().gps_installed());
						gpsStatusGrid.getCellFormatter().setWidth(0, 0, "62px");
						gpsStatusGrid.getCellFormatter().setAlignment(0, 0,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						gpsStatusGrid.getCellFormatter().setAlignment(0, 1,
								HasHorizontalAlignment.ALIGN_LEFT,
								HasVerticalAlignment.ALIGN_MIDDLE);
						String fix = null;
						if (informaticStatusObj.getGpsFix().equals(FIX_2D)
								|| informaticStatusObj.getGpsFix().equals(FIX_3D)) {
							gpsStatusGrid.setWidget(1, 0, ledGreen);
							if (informaticStatusObj.getGpsFix().equals(FIX_2D))
								fix = CentraleUI.getMessages().gps_2d_fix();
							else
								fix = CentraleUI.getMessages().gps_3d_fix();
						} else {
							Image ledRed = new Image();
							ledRed.setResource(iconImageBundle.ledRed());
							gpsStatusGrid.setWidget(1, 0, ledRed);
							if (informaticStatusObj.getGpsFix()
									.equals(GPS_APP_ERROR))
								fix = CentraleUI.getMessages().gps_app_error();
							else if (informaticStatusObj.getGpsFix()
									.equals(GPS_READ_ERROR))
								fix = CentraleUI.getMessages().gps_read_error();
							else
								fix = CentraleUI.getMessages().gps_no_fix();
						}
						gpsStatusGrid.setText(1, 1,
								CentraleUI.getMessages().gps_fix() + " " + fix);
						gpsStatusGrid.getCellFormatter().setAlignment(1, 0,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						gpsStatusGrid.getCellFormatter().setAlignment(1, 1,
								HasHorizontalAlignment.ALIGN_LEFT,
								HasVerticalAlignment.ALIGN_MIDDLE);

						gpsStatusGrid.setWidth("480px");
						gpsStatusGrid.setHeight("86x");
						gpsStatusGrid.setCellPadding(4);
						gpsStatusGrid.setCellSpacing(4);

						gpsPositionGrid.setText(0, 0,
								CentraleUI.getMessages().gps_data());
						DateTimeFormat df = DateTimeFormat
								.getFormat("dd/MM/yyyy HH:mm");
						gpsPositionGrid.setText(0, 1,
								df.format(informaticStatusObj.getGpsTimestamp()));
						gpsPositionGrid.setText(1, 0,
								CentraleUI.getMessages().gps_altitude());
						String gpsAltitude = informaticStatusObj.getGpsAltitude() != null ? informaticStatusObj.getGpsAltitude()
								.toString() : "";
						gpsPositionGrid.setText(1, 1, gpsAltitude);
						gpsPositionGrid.setText(2, 0,
								CentraleUI.getMessages().gps_latitude());
						gpsPositionGrid.setText(3, 0,
								CentraleUI.getMessages().gps_longitude());
						if (informaticStatusObj.getLinkToGoogleMaps() == null) {
							// no link
							String gpsLatitude = informaticStatusObj.getGpsLatitude() != null ? informaticStatusObj.getGpsLatitude()
									.toString() : "";
							gpsPositionGrid.setText(2, 1, gpsLatitude);
							String gpsLongitude = informaticStatusObj.getGpsLongitude() != null ? informaticStatusObj.getGpsLongitude()
									.toString() : "";
							gpsPositionGrid.setText(3, 1, gpsLongitude);
						} else {
							// link to Google maps
							gpsPositionGrid
									.setWidget(
											2,
											1,
											new HTML(
													"<a href='javascript:urlWin=window.open(\""
															+ informaticStatusObj.getLinkToGoogleMaps()
															+ "\"); window[\"urlWin\"].focus()' title='"
															+ CentraleUI.getMessages()
																	.url()
															+ "'>"
															+ informaticStatusObj.getGpsLatitude()
															+ "</a>"));
							gpsPositionGrid
									.setWidget(
											3,
											1,
											new HTML(
													"<a href='javascript:urlWin=window.open(\""
															+ informaticStatusObj.getLinkToGoogleMaps()
															+ "\"); window[\"urlWin\"].focus()' title='"
															+ CentraleUI.getMessages()
																	.url()
															+ "'>"
															+ informaticStatusObj.getGpsLongitude()
															+ "</a>"));
						}
						gpsPositionGrid.getCellFormatter().setWidth(0, 0,
								"62px");
						gpsPositionGrid.getCellFormatter().setAlignment(0, 0,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						gpsPositionGrid.setWidth("480px");
						gpsPositionGrid.setHeight("86px");
						gpsPositionGrid.setCellPadding(4);
						gpsPositionGrid.setCellSpacing(4);

					}// end else

					// loads communication information
					StringBuffer commTextBuf = new StringBuffer();
					if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.UNKNOWN) {
						Image communicationGray = new Image();
						communicationGray.setResource(iconImageBundle
								.communicationGray());
						communicationStatusGrid.setWidget(0, 0,
								communicationGray);
						commTextBuf.append(CentraleUI.getMessages().no_diagnostic());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.SW_NOT_RESPONDING) {
						Image swNotResponding = new Image();
						swNotResponding.setResource(iconImageBundle
								.swNotResponding());
						communicationStatusGrid
								.setWidget(0, 0, swNotResponding);
						commTextBuf.append(CentraleUI.getMessages().sw_crashed());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.PROTOCOL_ERROR) {
						Image swNotResponding = new Image();
						swNotResponding.setResource(iconImageBundle
								.swNotResponding());
						communicationStatusGrid
								.setWidget(0, 0, swNotResponding);
						commTextBuf
								.append(CentraleUI.getMessages().protocol_error());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.POLLING_ERROR) {
						Image swNotResponding = new Image();
						swNotResponding.setResource(iconImageBundle
								.swNotResponding());
						communicationStatusGrid
								.setWidget(0, 0, swNotResponding);
						commTextBuf.append(CentraleUI.getMessages().polling_error());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.UNEXPECTED_ERROR) {
						Image swNotResponding = new Image();
						swNotResponding.setResource(iconImageBundle
								.swNotResponding());
						communicationStatusGrid
								.setWidget(0, 0, swNotResponding);
						commTextBuf.append(CentraleUI.getMessages()
								.unexpected_Error());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.CONNECT_ERROR) {
						Image swNotResponding = new Image();
						swNotResponding.setResource(iconImageBundle
								.swNotResponding());
						communicationStatusGrid
								.setWidget(0, 0, swNotResponding);
						commTextBuf.append(CentraleUI.getMessages().sw_crashed());
					}

					else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.PC_NOT_RESPONDING) {
						Image pcNotResponding = new Image();
						pcNotResponding.setResource(iconImageBundle
								.pcNotResponding());
						communicationStatusGrid
								.setWidget(0, 0, pcNotResponding);
						commTextBuf.append(CentraleUI.getMessages().hw_crashed());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.ROUTER_REMOTE_NOT_RESPONING) {
						Image routerNotResponding = new Image();
						routerNotResponding.setResource(iconImageBundle
								.routerNotResponding());
						communicationStatusGrid.setWidget(0, 0,
								routerNotResponding);
						commTextBuf.append(CentraleUI.getMessages()
								.local_router_ko());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.ROUTER_LOCALE_NOT_RESPONING) {
						Image routerLocaleNotResponding = new Image();
						routerLocaleNotResponding.setResource(iconImageBundle
								.routerLocaleNotResponding());
						communicationStatusGrid.setWidget(0, 0,
								routerLocaleNotResponding);
						commTextBuf.append(CentraleUI.getMessages()
								.local_router_ko());
					} else if (informaticStatusObj.getCommunicationStatus() == CentraleUIConstants.POLLING_FINISHED_OK) {
						Image pollingFinishedOk = new Image();
						pollingFinishedOk.setResource(iconImageBundle
								.pollingFinishedOk());
						communicationStatusGrid.setWidget(0, 0,
								pollingFinishedOk);
						commTextBuf.append(CentraleUI.getMessages().polling_ok());
					}
					if (informaticStatusObj.getCommunicationStatus() != CentraleUIConstants.UNKNOWN
							&& informaticStatusObj.getLastCorrectPollingTime() != null) {
						commTextBuf.append(" - ");
						commTextBuf.append(CentraleUI.getMessages().last_comm_ok());
						commTextBuf.append(" ");
						commTextBuf
								.append(df
										.format(informaticStatusObj.getLastCorrectPollingTime()));
					}
					communicationStatusGrid.setText(0, 1,
							commTextBuf.toString());
					communicationStatusGrid.getCellFormatter().setAlignment(0,
							0, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					communicationStatusGrid.getCellFormatter().setAlignment(0,
							1, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					if (informaticStatusObj.getCommonConfigLastUpdateStatus() == null) {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						communicationStatusGrid.setWidget(0, 2, ledGray);
					} else if (informaticStatusObj.getCommonConfigLastUpdateStatus() == COMMONCFGNOTSENTFORCFGERR
							|| informaticStatusObj.getCommonConfigLastUpdateStatus() == COMMONCFGNOTSENTFORPERIFERICOCRASH
							|| informaticStatusObj.getCommonConfigLastUpdateStatus() == COMMONCFGNOTSENTFORPERIFERICOERR) {
						Image ledRed = new Image();
						ledRed.setResource(iconImageBundle.ledRed());
						communicationStatusGrid.setWidget(0, 2, ledRed);
					} else if (informaticStatusObj.getCommonConfigLastUpdateStatus() == COMMONCFGSENTOK) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						communicationStatusGrid.setWidget(0, 2, ledGreen);
					}
					problem = "";
					if (informaticStatusObj.getCommonConfigProblem() != null) {
						if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("CONSISTENCY_ERROR")) {
							problem = CentraleUI.getMessages().consistency_error();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("UNPARSABLE")) {
							problem = CentraleUI.getMessages().unparsable();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("SAVE_ERROR")) {
							problem = CentraleUI.getMessages().save_error();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("LOAD_ERROR")) {
							problem = CentraleUI.getMessages().load_error();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("INCOMPATIBLE")) {
							problem = CentraleUI.getMessages().incompatible();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("CONFIG_START_ERROR")) {
							problem = CentraleUI.getMessages().config_start_error();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("CONFIG_LOAD_ERROR")) {
							problem = CentraleUI.getMessages().config_load_error();
						} else if (informaticStatusObj.getCommonConfigProblem()
								.toUpperCase().equals("OK")) {
							problem = CentraleUI.getMessages().config_ok();
						}
						communicationStatusGrid.setText(0, 3, problem);
					} else {
						communicationStatusGrid.setText(0, 3,
								CentraleUI.getMessages().no_common_config());
					}

					communicationStatusGrid.getCellFormatter().setAlignment(0,
							2, HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
					communicationStatusGrid.getCellFormatter().setAlignment(0,
							3, HasHorizontalAlignment.ALIGN_LEFT,
							HasVerticalAlignment.ALIGN_MIDDLE);
					communicationStatusGrid.getCellFormatter().setWidth(0, 0,
							"40px");
					communicationStatusGrid.getCellFormatter().setWidth(0, 1,
							"430px");
					communicationStatusGrid.getCellFormatter().setWidth(0, 2,
							"40px");
					communicationStatusGrid.getCellFormatter().setWidth(0, 3,
							"430px");
					communicationStatusGrid.setCellPadding(5);
					communicationStatusGrid.setCellSpacing(5);
					communicationStatusGrid.setWidth("980px");
					communicationStatusGrid.setHeight("60px");

				}
			}// end onSuccess

			private static final int OK = 0;

			private static final int WARNING = 1;

			private static final int ALARM = 2;

			private int getThresholdIcon(int value, int warning, int alarm) {
				if (value <= warning)
					return OK;
				if (value > warning && value <= alarm)
					return WARNING;
				else
					return ALARM;
			}
		};

		centraleService.getInformaticStatusObject(stationId, callback);

	}

	@Override
	protected void reset() {
		// clear table
		Utils.clearTable(globalStatusGrid);
		Utils.clearTable(boardInitializeStatusGrid);
		Utils.clearTable(configurationStatusGrid);
		Utils.clearTable(configurationStatusSubGrid);
		Utils.clearTable(dpaStatusGrid);
		Utils.clearTable(gpsPositionGrid);
		Utils.clearTable(gpsStatusGrid);
		Utils.clearTable(communicationStatusGrid);
		Utils.clearTable(fileSystemStatusGrid);
	}

	@Override
	protected void loadContent() {
		setFields();
	}
}// end class
