/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: defines the menu bar.
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: NavBar.java,v 1.47 2014/09/18 09:46:58 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Defines the menu bar.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
class NavBar extends Composite {

	private final DockPanel bar = new DockPanel();

	// Create Anchor to english language
	private final Anchor linkToEnglish = new Anchor();

	// Create Anchor to italian language
	private final Anchor linkToItalian = new Anchor();

	// Create hyperlink to french language
	/*
	 * private final ImageHyperlink linkToFrench = new ImageHyperlink(new Image(
	 * "images/flag_fr.png"));
	 * 
	 * // Create hyperlink to german language private final ImageHyperlink
	 * linkToGerman = new ImageHyperlink(new Image( "images/flag_de.png"));
	 */

	// Create Anchor to configuration view
	private final Anchor linkToConfiguration = new Anchor();

	// Create Anchor to visualization view
	private final Anchor linkToView = new Anchor();

	// Create Anchor to map view
	private final Anchor linkToMapView = new Anchor();

	// Create Anchor to data view
	private final Anchor linkToDataView = new Anchor();

	// Create Anchor to station's configuration
	private final Anchor linkToStationCfg = new Anchor();

	// Create Anchor to map configuration
	private final Anchor linkToMapCfg = new Anchor();

	// Create Anchor to cop configuration
	private final Anchor linkToCopCfg = new Anchor();

	// Create Anchor to common config
	private final Anchor linkToLoadCommonCfg = new Anchor();

	// Create Anchor to modem configuration
	private final Anchor linkToModemCfg = new Anchor();

	private final HTML status = new HTML();

	public NavBar() {

		initWidget(bar);

		// Anchor to italian language
		linkToItalian.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToItalian.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {
				if (CentraleUI.slotConfig != null) {

					// Call changeLocale
					Utils.changeLocale("it");
					CentraleUI.setLocale("it");
					setLocale(CentraleUI.getLocale());
					// Set visible/invisible widget
					showLoginWidget();
				}
			}
		});
		Image imgItalian = new Image("images/flag_it.png");
		linkToItalian.getElement().appendChild(imgItalian.getElement());
		linkToItalian.setTitle(CentraleUI.getMessages().lbl_language_italian());

		// Anchor to english language
		linkToEnglish.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToEnglish.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {

				if (CentraleUI.slotConfig != null) {
					// Call changeLocale
					Utils.changeLocale("en");
					CentraleUI.setLocale("en");
					setLocale(CentraleUI.getLocale());
					// Set visible/invisible widget
					showLoginWidget();
				}
			}
		});
		Image imgEnglish = new Image("images/flag_en.png");
		linkToEnglish.getElement().appendChild(imgEnglish.getElement());
		linkToEnglish.setTitle(CentraleUI.getMessages().lbl_language_english());

		// hyperlink to french language
		/*
		 * linkToFrench.setTitle(CentraleUI.messages.lbl_language_french());
		 * linkToFrench.addClickListener(new ClickListener() { public void
		 * onClick(ClickEvent event) { if (CentraleUI.slotConfig != null) {
		 * 
		 * // Call changeLocale Utils.changeLocale("fr"); CentraleUI.setLocale("fr");
		 * setLocale(CentraleUI.getLocale()); // Set visible/invisible widget
		 * showLoginWidget(); } } });
		 */

		// hyperlink to german language
		/*
		 * linkToGerman.setTitle(CentraleUI.messages.lbl_language_german());
		 * linkToGerman.addClickListener(new ClickListener() { public void
		 * onClick(ClickEvent event) { if (CentraleUI.slotConfig != null) {
		 * 
		 * // Call changeLocale Utils.changeLocale("de"); CentraleUI.setLocale("de");
		 * setLocale(CentraleUI.getLocale()); // Set visible/invisible widget
		 * showLoginWidget(); } } });
		 */

		linkToView.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {
				setBarForView();
				if (CentraleUI.slotView == null)
					return;
				CentraleUI.slotConfig.setVisible(false);
				CentraleUI.slotView.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.viewMapWidget);
			}
		});
		Image imgView = new Image("images/kview.png");
		linkToView.getElement().appendChild(imgView.getElement());
		linkToView.setTitle(CentraleUI.getMessages().link_view());
		linkToView.setStyleName("right");

		linkToConfiguration.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToConfiguration.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {
				setBarForConfiguration();
				if (CentraleUI.slotConfig == null)
					return;
				loadConfig(CentraleUI.getLocale());
				CentraleUI.slotConfig.setVisible(true);
				CentraleUI.slotView.setVisible(false);
				CentraleUI.setCurrentPage(CentraleUI.listStationWidget);
			}
		});
		Image imgConf = new Image("images/kcontrol.png");
		linkToConfiguration.getElement().appendChild(imgConf.getElement());
		linkToConfiguration.setTitle(CentraleUI.getMessages().link_conf());
		linkToConfiguration.setStyleName("right");

		// Anchor to map view
		linkToMapView.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToMapView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {
				if (CentraleUI.slotView == null)
					return;
				CentraleUI.slotView.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.viewMapWidget);
			}
		});
		Image imgMapView = new Image("images/kview.png");
		linkToMapView.getElement().appendChild(imgMapView.getElement());
		linkToMapView.setTitle(CentraleUI.getMessages().link_map());

		// Anchor to data view
		linkToDataView.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToDataView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {
				if (CentraleUI.slotView == null)
					return;
				CentraleUI.slotView.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.viewDataWidget);
			}
		});
		Image imgDataView = new Image("images/dati.png");
		linkToDataView.getElement().appendChild(imgDataView.getElement());
		linkToDataView.setTitle(CentraleUI.getMessages().link_view_data());

		// Anchor to station's configuration
		linkToStationCfg.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToStationCfg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent ev) {
				if (CentraleUI.slotConfig == null)
					return;
				CentraleUI.slotConfig.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.listStationWidget);
			}
		});
		Image imgStationCfg = new Image("images/conf_station.png");
		linkToStationCfg.getElement().appendChild(imgStationCfg.getElement());
		linkToStationCfg.setTitle(CentraleUI.getMessages().link_station_cfg());


		// Anchor to map configuration
		linkToMapCfg.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToMapCfg.setTitle(CentraleUI.getMessages().link_map_cfg());
		Image imgMapCfg = new Image("images/conf_mappa.png");
		linkToMapCfg.getElement().appendChild(imgMapCfg.getElement());
		linkToMapCfg.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (CentraleUI.slotConfig == null)
					return;
				CentraleUI.slotConfig.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.cfgMapWidget);
			}

		});

		// Anchor to cop configuration
		linkToCopCfg.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToCopCfg.setTitle(CentraleUI.getMessages().link_cop_cfg());
		Image imgCopCfg = new Image("images/package_system.png");
		linkToCopCfg.getElement().appendChild(imgCopCfg.getElement());
		linkToCopCfg.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (CentraleUI.slotConfig == null)
					return;
				CentraleUI.slotConfig.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.copWidget);
			}

		});

		// Create Anchor to load common config
		linkToLoadCommonCfg.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToLoadCommonCfg.setTitle(CentraleUI.getMessages().link_load_commoncfg());
		Image imgLoadCommonCfg = new Image("images/network.png");
		linkToLoadCommonCfg.getElement().appendChild(
				imgLoadCommonCfg.getElement());
		linkToLoadCommonCfg.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (CentraleUI.slotConfig == null)
					return;
				CentraleUI.slotConfig.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.physicalDimensionWidget);
			}
		});

		// Create Anchor to modem configuration
		linkToModemCfg.getElement().getStyle().setCursor(Cursor.POINTER);
		linkToModemCfg.setTitle(CentraleUI.getMessages().link_modem_conf());
		Image imgModemCfg = new Image("images/modem.png");
		linkToModemCfg.getElement().appendChild(imgModemCfg.getElement());
		linkToModemCfg.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (CentraleUI.slotConfig == null)
					return;
				CentraleUI.slotConfig.setVisible(true);
				CentraleUI.setCurrentPage(CentraleUI.listModemWidget);
			}
		});

		HorizontalPanel links = new HorizontalPanel();
		links.setSpacing(10);

		links.add(linkToItalian);
		links.add(linkToEnglish);
		// links.add(linkToFrench);
		// links.add(linkToGerman);
		links.add(linkToMapView);
		links.add(linkToDataView);
		links.add(linkToStationCfg);
		links.add(linkToMapCfg);
		links.add(linkToCopCfg);
		links.add(linkToLoadCommonCfg);
		links.add(linkToModemCfg);

		bar.add(links, DockPanel.WEST);
		bar.setCellHorizontalAlignment(links, DockPanel.ALIGN_RIGHT);

		HorizontalPanel changeView = new HorizontalPanel();
		changeView.setSpacing(10);
		changeView.add(linkToConfiguration);
		changeView.add(linkToView);

		bar.add(changeView, DockPanel.EAST);
		bar.add(status, DockPanel.CENTER);
		bar.setVerticalAlignment(DockPanel.ALIGN_MIDDLE);
		bar.setCellHorizontalAlignment(status, HasAlignment.ALIGN_RIGHT);
		bar.setCellVerticalAlignment(status, HasAlignment.ALIGN_MIDDLE);
		bar.setCellWidth(status, "100%");

	}

	void setPageTabs() {
		CentraleUI.viewMapWidget.setPageTab(linkToMapView);
		CentraleUI.viewDataWidget.setPageTab(linkToDataView);
		CentraleUI.listStationWidget.setPageTab(linkToStationCfg);
		CentraleUI.cfgMapWidget.setPageTab(linkToMapCfg);
		CentraleUI.copWidget.setPageTab(linkToCopCfg);
		CentraleUI.physicalDimensionWidget.setPageTab(linkToLoadCommonCfg);
		CentraleUI.commonCfgWidget.setPageTab(linkToLoadCommonCfg);
		CentraleUI.otherCommonCfgWidget.setPageTab(linkToLoadCommonCfg);
		CentraleUI.parameterWidget.setPageTab(linkToLoadCommonCfg);
		CentraleUI.alarmNameWidget.setPageTab(linkToLoadCommonCfg);
		CentraleUI.measureUnitWidget.setPageTab(linkToLoadCommonCfg);
		CentraleUI.listModemWidget.setPageTab(linkToModemCfg);
		CentraleUI.modemWidget.setPageTab(linkToModemCfg);
		CentraleUI.showChartWidget.setPageTab(linkToDataView);
	}

	protected void loadConfig(String locale) {

	}

	protected void setLocale(String locale) {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<Object> callback = new UIAsyncCallback<Object>() {
			public void onSuccess(Object result) {
			}
		};
		centraleService.setLocale(locale, callback);

	}

	void showLoginWidget() {
		if (CentraleUI.slotPage != null) {
			setBarForLogin();
			CentraleUI.slotPage.setVisible(true);
			CentraleUI.setCurrentPage(CentraleUI.loginWidget);
		}
	}

	void setBarForLogin() {
		if (CentraleUI.getLocale().equals("en")) {
			linkToEnglish.setVisible(false);
			linkToItalian.setVisible(true);
			// linkToFrench.setVisible(true);
			// linkToGerman.setVisible(true);
		}
		if (CentraleUI.getLocale().equals("it")) {
			linkToEnglish.setVisible(true);
			linkToItalian.setVisible(false);
			// linkToFrench.setVisible(true);
			// linkToGerman.setVisible(true);
		}
		if (CentraleUI.getLocale().equals("fr")) {
			linkToEnglish.setVisible(true);
			linkToItalian.setVisible(true);
			// linkToFrench.setVisible(false);
			// linkToGerman.setVisible(true);
		}
		if (CentraleUI.getLocale().equals("de")) {
			linkToEnglish.setVisible(true);
			linkToItalian.setVisible(true);
			// linkToFrench.setVisible(true);
			// linkToGerman.setVisible(false);
		}

		linkToConfiguration.setVisible(false);
		linkToView.setVisible(false);
		linkToMapView.setVisible(false);
		linkToDataView.setVisible(false);
		linkToStationCfg.setVisible(false);
		linkToLoadCommonCfg.setVisible(false);
		linkToModemCfg.setVisible(false);
		linkToMapCfg.setVisible(false);
		linkToCopCfg.setVisible(false);
	}

	void setBarAfterLogin() {
		linkToEnglish.setVisible(false);
		linkToItalian.setVisible(false);
		// linkToFrench.setVisible(false);
		// linkToGerman.setVisible(false);

		linkToConfiguration.setVisible(true);
		linkToView.setVisible(true);
		linkToStationCfg.setVisible(false);
		linkToLoadCommonCfg.setVisible(false);
		linkToModemCfg.setVisible(false);
		linkToMapCfg.setVisible(false);
		linkToCopCfg.setVisible(false);
	}

	void setBarForConfiguration() {
		linkToEnglish.setVisible(false);
		linkToItalian.setVisible(false);
		// linkToFrench.setVisible(false);
		// linkToGerman.setVisible(false);

		linkToConfiguration.setVisible(false);
		linkToView.setVisible(true);
		linkToMapView.setVisible(false);
		linkToDataView.setVisible(false);
		linkToStationCfg.setVisible(true);
		linkToLoadCommonCfg.setVisible(true);
		linkToModemCfg.setVisible(true);
		linkToMapCfg.setVisible(true);
		linkToCopCfg.setVisible(true);
	}

	void setBarForView() {
		linkToEnglish.setVisible(false);
		linkToItalian.setVisible(false);
		// linkToFrench.setVisible(false);
		// linkToGerman.setVisible(false);

		linkToConfiguration.setVisible(true);
		linkToView.setVisible(false);
		linkToMapView.setVisible(true);
		linkToDataView.setVisible(true);
		linkToStationCfg.setVisible(false);
		linkToLoadCommonCfg.setVisible(false);
		linkToModemCfg.setVisible(false);
		linkToMapCfg.setVisible(false);
		linkToCopCfg.setVisible(false);
	}

}// end class
