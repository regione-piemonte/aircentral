/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: page for choose a station to configure or to delete one
 * station from configuration
 * Change log:
 *   2008-10-29: initial version
 * ----------------------------------------------------------------------------
 * $Id: ListStationWidget.java,v 1.27 2015/10/15 12:10:23 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.StationMapObject;
import it.csi.centrale.ui.client.data.VirtualCopInfo;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.ArrayList;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for choose a station to configure or to delete one station from
 * configuration
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class ListStationWidget extends UIPage {

	private int[] stationsId;

	private Integer[] virtualCopsId;

	// Table for station information.
	private final FlexTable table;

	private Button senderButtonModify;

	private Button senderButtonDelete;

	private ListBox listBoxVirtualCop;

	private List<VirtualCopInfo> virtualCopList = new ArrayList<VirtualCopInfo>();

	public ListStationWidget() {

		// panel that represents the page
		PanelButtonWidget panelButton = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);
		panelButton.addButton(CentraleUIConstants.CSV, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./exportService?function=getStationReport", "", "");
			}
		});
		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#conf_listStationWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().list_station_title(), panelButton, false);

		/*
		 * New station panel
		 */
		// Prepare panel for new station
		Label titleNew = new Label();
		titleNew.setText(CentraleUI.getMessages().lbl_station_new());
		titleNew.setStyleName("gwt-Label-title");

		HorizontalPanel newStPanel = new HorizontalPanel();
		newStPanel.setStyleName("gwt-post-boxed");
		newStPanel.setSpacing(10);

		HorizontalPanel newStationPanel = new HorizontalPanel();
		// newStationPanel.setStyleName("gwt-post-boxed");
		newStationPanel.setSpacing(5);
		newStationPanel.setWidth("500");
		// newStationPanel.setBorderWidth(3);
		newStPanel.add(newStationPanel);
		newStationPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);

		Label virtualCopLabel = new Label();
		virtualCopLabel.setText(CentraleUI.getMessages().lbl_virtual_cop());
		virtualCopLabel.setStyleName("gwt-cop-title");
		newStationPanel.add(virtualCopLabel);

		listBoxVirtualCop = new ListBox();
		listBoxVirtualCop.setSize("150px", "30px");
		newStationPanel.add(listBoxVirtualCop);

		Button newStationButton = new Button();
		newStationButton.setStyleName("gwt-button-new-orange");
		newStationButton.setTitle(CentraleUI.getMessages().station_button_new());
		newStationButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (listBoxVirtualCop.getSelectedIndex() == -1) {
					Window.alert(CentraleUI.getMessages().not_create_station());
				} else {
					CentraleUI.stationWidget.setNewStation(new Integer(
							listBoxVirtualCop.getValue(listBoxVirtualCop
									.getSelectedIndex())));
					CentraleUI.setCurrentPage(CentraleUI.stationWidget);
				}
			}
		});

		newStationPanel.add(newStationButton);

		// setting the alignment
		newStationPanel.setCellHorizontalAlignment(newStationButton,
				HasHorizontalAlignment.ALIGN_LEFT);
		newStationPanel.setCellVerticalAlignment(newStationButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		newStationPanel.setCellHorizontalAlignment(listBoxVirtualCop,
				HasHorizontalAlignment.ALIGN_LEFT);
		newStationPanel.setCellVerticalAlignment(listBoxVirtualCop,
				HasVerticalAlignment.ALIGN_MIDDLE);

		externalPanel.add(titleNew);
		externalPanel.add(newStPanel);

		/*
		 * Station list table
		 */
		// Prepare panel for station list
		Label title = new Label();
		title.setText(CentraleUI.getMessages().station_list_title());
		title.setStyleName("gwt-Label-title");

		// panel that contains the grid station list
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		// Prepare table of titles
		FlexTable headerTable = new FlexTable();
		headerTable.setText(0, 0, CentraleUI.getMessages().lbl_status());
		headerTable.setText(0, 1, CentraleUI.getMessages().lbl_virtual_cop());
		headerTable.setText(0, 2, CentraleUI.getMessages().lbl_name());
		headerTable.setText(0, 3, CentraleUI.getMessages().lbl_modify());
		headerTable.setText(0, 4, CentraleUI.getMessages().lbl_cancel());
		headerTable.setStyleName("gwt-table-header");
		headerTable.setWidth("100%");
		headerTable.getCellFormatter().setWidth(0, 0, "100px");
		headerTable.getCellFormatter().setWidth(0, 1, "100px");
		headerTable.getCellFormatter().setWidth(0, 2, "380px");
		headerTable.getCellFormatter().setWidth(0, 3, "200px");
		headerTable.getCellFormatter().setWidth(0, 4, "200px");
		for (int j = 0; j < 5; j++) {
			headerTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}
		panel.add(headerTable);

		// Prepare table for analyzers info in a ScrollPanel
		ScrollPanel scrollPanel = new ScrollPanel();
		table = new FlexTable();
		table.setStyleName("gwt-table-data");
		table.setWidth("100%");
		scrollPanel.add(table);
		scrollPanel.setHeight("300px");
		panel.add(scrollPanel);

		externalPanel.add(title);
		externalPanel.add(panel);

		initWidget(externalPanel);

	}// end constructor

	private void getVirtualCop() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<List<VirtualCopInfo>> callback = new UIAsyncCallback<List<VirtualCopInfo>>() {
			public void onSuccess(List<VirtualCopInfo> virtualCopList) {
				listBoxVirtualCop.clear();
				setVirtualCopList(virtualCopList);
				for (int i = 0; i < virtualCopList.size(); i++) {
					VirtualCopInfo vCop = virtualCopList.get(i);
					listBoxVirtualCop.addItem(vCop.getCopName(),
							vCop.getVirtualCopId().toString());
				}
			}
		};
		centraleService.getEnabledVirtualCop(callback);

	}

	private List<VirtualCopInfo> getVirtualCopList() {
		return virtualCopList;
	}

	private void setVirtualCopList(List<VirtualCopInfo> virtualCopList) {
		this.virtualCopList = virtualCopList;
	}

	List<StationMapObject> stationsList = null;

	private void getStationList() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<List<StationMapObject>> callback = new UIAsyncCallback<List<StationMapObject>>() {
			public void onSuccess(List<StationMapObject> stations) {

				stationsId = new int[stations.size()];
				virtualCopsId = new Integer[stations.size()];
				stationsList = stations;
				// clear table
				Utils.clearTable(table);
				table.getCellFormatter().setWidth(0, 0, "100px");
				table.getCellFormatter().setWidth(0, 1, "100px");
				table.getCellFormatter().setWidth(0, 2, "380px");
				table.getCellFormatter().setWidth(0, 3, "200px");
				table.getCellFormatter().setWidth(0, 4, "200px");
				for (int i = 0; i < stations.size(); i++) {
					StationMapObject station = stations.get(i);
					IconImageBundle iconImageBundle = (IconImageBundle) GWT
							.create(IconImageBundle.class);
					if (station.isEnabled()) {
						Image ledGreen = new Image();
						ledGreen.setResource(iconImageBundle.ledGreen());
						table.setWidget(i, 0, ledGreen);
						table.getWidget(i, 0).setTitle(
								CentraleUI.getMessages().enabled());
					} else {
						Image ledGray = new Image();
						ledGray.setResource(iconImageBundle.ledGray());
						table.setWidget(i, 0, ledGray);
						table.getWidget(i, 0).setTitle(
								CentraleUI.getMessages().disabled());
					}
					List<VirtualCopInfo> virtualCop = getVirtualCopList();
					for (int l = 0; l < virtualCop.size(); l++) {
						VirtualCopInfo vCopInfo = virtualCop.get(l);
						if (vCopInfo.getVirtualCopId().equals(station.getVirtualCopId())) {
							table.setText(i, 1, vCopInfo.getCopName());
						}
					}// end for

					if (station.getLongName() != null
							&& !station.getLongName().trim().equals(""))
						table.setText(i, 2, station.getLongName());
					else
						table.setText(i, 2, station.getShortName());

					stationsId[i] = station.getStationId();
					virtualCopsId[i] = station.getVirtualCopId();
					Button modifyButton = new Button();
					modifyButton.setStyleName("gwt-button-modify");
					modifyButton.setTitle(CentraleUI.getMessages()
							.station_button_modify());
					table.setWidget(i, 3, modifyButton);
					modifyButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							senderButtonModify = (Button) event.getSource();
							for (int k = 0; k < table.getRowCount(); k++) {
								if (((Button) (table.getWidget(k, 3)))
										.equals(senderButtonModify)) {
									// load anagraphic info
									CentraleUI.stationWidget.setStation(
											stationsId[k], virtualCopsId[k]);
									CentraleUI
											.setCurrentPage(CentraleUI.stationWidget);
								}
							}

						}
					});
					Button deleteButton = new Button();
					deleteButton.setStyleName("gwt-button-delete");
					deleteButton.setTitle(CentraleUI.getMessages()
							.station_button_delete());
					table.setWidget(i, 4, deleteButton);
					deleteButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {

							senderButtonDelete = (Button) event.getSource();
							for (int k = 0; k < table.getRowCount(); k++) {
								if (((Button) (table.getWidget(k, 4)))
										.equals(senderButtonDelete)) {
									if ((stationsList.get(k)).isEnabled())
										Window.alert(CentraleUI.getMessages()
												.disabled_station());
									else {
										boolean mustDelete = Window
												.confirm(CentraleUI.getMessages()
														.sure_delete_station());
										if (mustDelete) {
											CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
													.create(CentraleUIService.class);
											ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
											endpoint2.setServiceEntryPoint(GWT
													.getModuleBaseURL()
													+ "uiservice");

											AsyncCallback<String> callback2 = new UIAsyncCallback<String>() {
												public void onSuccess(
														String errStr) {
													if (errStr != null)
														Window.alert(errStr);
													else {
														CentraleUI.listStationWidget
																.getStationList();
														Utils.operationOk();
													}
												}
											};
											centraleService2
													.setStationDeleted(
															new Integer(
																	stationsId[k]),
															virtualCopsId[k],
															callback2);
										}
									}// end else
								}
							}// end for
						}// end onClick
					});

					for (int j = 0; j < 5; j++) {
						table.getCellFormatter().setAlignment(i, j,
								HasHorizontalAlignment.ALIGN_CENTER,
								HasVerticalAlignment.ALIGN_MIDDLE);
						table.getCellFormatter().setStyleName(i, j,
								"gwt-table-data");
					}
				}// end for stations
			}

		};
		centraleService.getStMapObjList(false, true, callback);
	}// end getStationList

	@Override
	protected void loadContent() {
		getVirtualCop();
		getStationList();
	}

}// end class
