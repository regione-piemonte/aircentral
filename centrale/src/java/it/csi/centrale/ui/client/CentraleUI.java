/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: entry point for user interface part of application
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: CentraleUI.java,v 1.65 2015/01/19 12:01:05 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.pagecontrol.PageController;
import it.csi.centrale.ui.client.pagecontrol.PageUpdateAction;
import it.csi.centrale.ui.client.pagecontrol.UIPage;
import it.csi.centrale.ui.client.stationconfig.StationWidget;

/**
 * Entry point for user interface part of application
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class CentraleUI implements EntryPoint {

	private static String locale;

	protected static NavBar navBar;

	protected static LoginWidget loginWidget;

	protected static CfgMapWidget cfgMapWidget;

	protected static ListStationWidget listStationWidget;

	protected static StationWidget stationWidget;

	protected static ViewMapWidget viewMapWidget;

	protected static ViewDataWidget viewDataWidget;

	protected static ViewStationDataWidget viewStationDataWidget;

	protected static CopWidget copWidget;

	protected static InformaticStatusWidget informaticStatusWidget;

	protected static StationAlarmStatusWidget stationAlarmStatusWidget;

	protected static ShowChartWidget showChartWidget;

	protected static AnalyzersStatusWidget analyzersStatusWidget;

	protected static HistoryTableWidget historyTableWidget;

	protected static PhysicalDimensionWidget physicalDimensionWidget;

	protected static CommonCfgWidget commonCfgWidget;

	protected static OtherCommonCfgWidget otherCommonCfgWidget;

	protected static ParameterWidget parameterWidget;

	protected static MeasureUnitWidget measureUnitWidget;

	protected static AlarmNameWidget alarmNameWidget;

	protected static ListModemWidget listModemWidget;

	protected static ModemWidget modemWidget;

	protected static AbsolutePanel slotConfig = RootPanel.get("config-box");

	protected static AbsolutePanel slotView = RootPanel.get("view-box");

	protected static RootPanel slotPage = RootPanel.get("login");

	protected static AbsolutePanel menuBar = RootPanel.get("menu");

	private static MessageBundleClient messages = null;

	private static PageController pageController = new PageController();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Load internationalization
		messages = (MessageBundleClient) GWT.create(MessageBundleClient.class);
		locale = messages.locale();

		// Define menu' bar
		navBar = new NavBar();
		navBar.setVisible(true);

		// Create loginWidget
		loginWidget = new LoginWidget();
		loginWidget.setVisible(true);
		navBar.setBarForLogin();
		slotPage.add(loginWidget);

		// Create listStationWidget and set it to invisible
		listStationWidget = new ListStationWidget();
		listStationWidget.setVisible(false);
		slotConfig.add(listStationWidget);

		// Create stationWidget and set it to invisible
		stationWidget = new StationWidget();
		stationWidget.setVisible(false);
		slotConfig.add(stationWidget);

		// Create physicalDimensionWidget and set it to invisible
		physicalDimensionWidget = new PhysicalDimensionWidget();
		physicalDimensionWidget.setVisible(false);
		slotConfig.add(physicalDimensionWidget);

		// Create commonCfgWidget and set it to invisible
		commonCfgWidget = new CommonCfgWidget();
		commonCfgWidget.setVisible(false);
		slotConfig.add(commonCfgWidget);

		// Create otherCommonCfgWidget and set it to invisible
		otherCommonCfgWidget = new OtherCommonCfgWidget();
		otherCommonCfgWidget.setVisible(false);
		slotConfig.add(otherCommonCfgWidget);

		// Create parameterWidget and set it to invisible
		parameterWidget = new ParameterWidget();
		parameterWidget.setVisible(false);
		slotConfig.add(parameterWidget);

		// Create alarmNameWidget and set it to invisible
		alarmNameWidget = new AlarmNameWidget();
		alarmNameWidget.setVisible(false);
		slotConfig.add(alarmNameWidget);

		// Create alarmNameWidget and set it to invisible
		listModemWidget = new ListModemWidget();
		listModemWidget.setVisible(false);
		slotConfig.add(listModemWidget);

		// Create alarmNameWidget and set it to invisible
		modemWidget = new ModemWidget();
		modemWidget.setVisible(false);
		slotConfig.add(modemWidget);

		// Create measureUnitWidget and set it to invisible
		measureUnitWidget = new MeasureUnitWidget();
		measureUnitWidget.setVisible(false);
		slotConfig.add(measureUnitWidget);

		// Create cfgMapWidget
		cfgMapWidget = new CfgMapWidget();
		Window.addResizeHandler(cfgMapWidget);
		cfgMapWidget.setVisible(false);
		slotConfig.add(cfgMapWidget);

		// Create copWidget and set it to invisible
		copWidget = new CopWidget();
		copWidget.setVisible(false);
		slotConfig.add(copWidget);

		// Create viewMapWidget
		viewMapWidget = new ViewMapWidget();
		Window.addResizeHandler(viewMapWidget);
		viewMapWidget.setVisible(false);
		slotView.add(viewMapWidget);

		// Create viewDataWidget
		viewDataWidget = new ViewDataWidget();
		viewDataWidget.setVisible(false);
		slotView.add(viewDataWidget);

		// Create viewStationDataWidget
		viewStationDataWidget = new ViewStationDataWidget();
		viewStationDataWidget.setVisible(false);
		slotView.add(viewStationDataWidget);

		// Create informaticStatusWidget
		informaticStatusWidget = new InformaticStatusWidget();
		informaticStatusWidget.setVisible(false);
		slotView.add(informaticStatusWidget);

		// Create analyzersStatusWidget
		analyzersStatusWidget = new AnalyzersStatusWidget();
		analyzersStatusWidget.setVisible(false);
		slotView.add(analyzersStatusWidget);

		// Create stationStatusWidget
		stationAlarmStatusWidget = new StationAlarmStatusWidget();
		stationAlarmStatusWidget.setVisible(false);
		slotView.add(stationAlarmStatusWidget);

		// Create showChartWidget and set it to invisible
		showChartWidget = new ShowChartWidget();
		showChartWidget.setVisible(false);
		slotView.add(showChartWidget);

		// Create showChartWidget and set it to invisible
		historyTableWidget = new HistoryTableWidget();
		historyTableWidget.setVisible(false);
		slotView.add(historyTableWidget);

		/*
		 * WARNING! when create a new widget you must set visible/invisible widget and
		 * then add it to panel
		 */

		stationWidget.setUpperLevelPage(listStationWidget);
		viewStationDataWidget.setUpperLevelPage(viewDataWidget);
		parameterWidget.setUpperLevelPage(commonCfgWidget);
		measureUnitWidget.setUpperLevelPage(commonCfgWidget);
		alarmNameWidget.setUpperLevelPage(commonCfgWidget);
		modemWidget.setUpperLevelPage(listModemWidget);

		navBar.setPageTabs();

		menuBar.add(navBar);

	}// end onModuleLoad

	/**
	 * Create and prepare title for the external panel page
	 * 
	 * @param title    the title of the page
	 * @param messages the messageBundle
	 * @return a verticaPanel that can contains element for the page
	 */
	public static VerticalPanel getTitledExternalPanel(String title, PanelButtonWidget panelButton,
			boolean pageForMap) {
		// panel that represents the page
		VerticalPanel externalPanel = new VerticalPanel();
		if (pageForMap)
			externalPanel.setStyleName("gwt-page-for-map");
		else
			externalPanel.setStyleName("gwt-page");

		// page title and application button
		Label pageTitle = new Label(title);
		pageTitle.setStyleName("gwt-title-widget");
		HorizontalPanel topPanel = new HorizontalPanel();
		topPanel.add(pageTitle);
		if (pageForMap)
			topPanel.setCellWidth(pageTitle, "100%");
		else
			topPanel.setCellWidth(pageTitle, "600px");
		topPanel.setCellHorizontalAlignment(pageTitle, HasHorizontalAlignment.ALIGN_LEFT);
		topPanel.setCellVerticalAlignment(pageTitle, HasVerticalAlignment.ALIGN_MIDDLE);
		if (panelButton != null) {
			topPanel.add(panelButton);
			topPanel.setCellWidth(panelButton, "400px");
			topPanel.setCellHorizontalAlignment(panelButton, HasHorizontalAlignment.ALIGN_RIGHT);
		}
		externalPanel.add(topPanel);
		return externalPanel;
	}

	public static void setTitle(VerticalPanel externalPanel, String title) {
		HorizontalPanel topPanel = (HorizontalPanel) externalPanel.getWidget(0);
		Label pageTitle = (Label) topPanel.getWidget(0);
		pageTitle.setText(title);
	}

	// Deselect rows of visible tables
	public static void clearGridRowFormatter() {

	}

	// Deselect rows of all tables
	public static void clearAllGridRowFormatter() {

	}

	public static void sessionEnded() {
		boolean confirmResult = Window.confirm(messages.session_ended());
		if (confirmResult) {
			pageController.setCurrentPage(null, true);
			navBar.showLoginWidget();
		}
	}

	public static void unexpectedError(Throwable caught) {
		caught.printStackTrace();
		Window.alert(CentraleUI.messages.unexpected_server_error() + " (" + caught.toString() + ")");
		navBar.setBarForLogin();
		CentraleUI.slotPage.setVisible(true);
		CentraleUI.setCurrentPage(CentraleUI.loginWidget);
	}

	public static void goToUpperLevelPage() {
		pageController.goToUpperLevelPage();
	}

	public static void goToPreviousPage() {
		pageController.goToPreviousPage();
	}

	public static void setCurrentPage(UIPage page) {
		pageController.setCurrentPage(page);
	}

	public static void setPreviousPage(UIPage page) {
		pageController.setPreviousPage(page);
	}

	public static void updateCurrentPage(PageUpdateAction pageUpdateAction) {
		pageController.updateCurrentPage(pageUpdateAction);
	}

	public static void shutdown() {
		// TODO: capire se e' necessario fare altro
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<Object> callback = new UIAsyncCallback<Object>() {
			public void onSuccess(Object obj) {
			}
		};
		centraleService.stopVerifyInternalDb(callback);

	}

	public static String getLocale() {
		return locale;
	}

	public static void setLocale(String locale) {
		CentraleUI.locale = locale;
	}

	public static MessageBundleClient getMessages() {
		return messages;
	}

}// end class
