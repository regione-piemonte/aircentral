/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: page for viewing map and station polling status
* Change log:
*   2008-10-02: initial version
* ----------------------------------------------------------------------------
* $Id: ViewMapWidget.java,v 1.67 2015/10/22 13:54:23 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import it.csi.centrale.ui.client.data.ConfigInfoObject;
import it.csi.centrale.ui.client.data.StationMapObject;
import it.csi.centrale.ui.client.data.StationStatusObject;
import it.csi.centrale.ui.client.pagecontrol.AsyncPageOperation;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/**
 * Page for viewing map and station polling status
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */

public class ViewMapWidget extends UIPage implements ResizeHandler {

	// TODO: capire se si può prendere dinamicamente
	private static final int STPANEL_WIDTH = 118;

	private VerticalPanel externalPanel;

	private Image image;

	private List<StationMapObject> stMapObjList;

	private ConfigInfoObject configInfo;

	private StationsStatusTimer stStatusTimer;

	private DateTimeFormat df;

	private final ViewMapWidget viewMapWidget;

	private PanelButtonWidget panelButton;

	public ViewMapWidget() {
		viewMapWidget = this;

		df = DateTimeFormat.getFormat("dd-MM-yy HH:mm");

		panelButton = new PanelButtonWidget(PanelButtonWidget.BLUE);
		panelButton.addButton(CentraleUIConstants.POLLING, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
				ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
				endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
				AsyncCallback<Boolean> callback = new UIAsyncCallback<Boolean>() {
					public void onSuccess(Boolean resultValue) {
						if (resultValue != null) {
							if (!resultValue)
								Window.alert(CentraleUI.getMessages().operation_error());
						} else
							Window.alert(CentraleUI.getMessages().operation_error());
					}
				};
				centraleService.callAllStations(stMapObjList, callback);
			}
		});

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#view_viewMapWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().view_map_title(), panelButton, true);

		externalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		image = new Image();
		externalPanel.add(image);

		initWidget(externalPanel);

	}

	private void writeTitle() {
		final CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		AsyncCallback<String> getCopName = new UIAsyncCallback<String>() {
			public void onSuccess(String name) {

				Window.setTitle("Centrale - " + (name == null ? "COP" : name));
			}
		};

		centraleService.getName(getCopName);
	}

	private void loadImage() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<String> callback = new UIAsyncCallback<String>() {
			public void onSuccess(String imageStr) {
				image.setUrl(CentraleUIConstants.URL_FOR_IMAGE + "&imagename=" + imageStr);
				getAndShowStation();
			}
		};
		centraleService.getMapName(callback);

	}

	private void getAndShowStation() {
		// get information about using or not synthetic icon
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<ConfigInfoObject> callback = new UIAsyncCallback<ConfigInfoObject>() {
			public void onSuccess(ConfigInfoObject configInfoObj) {
				// System.out.println("On success 1");
				configInfo = configInfoObj;
				// get the list of station objects for map
				CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
				ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
				endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
				AsyncCallback<List<StationMapObject>> callback2 = new UIAsyncCallback<List<StationMapObject>>() {
					public void onSuccess(List<StationMapObject> currentStMapObjList) {
						// System.out.println("On success 2");
						stMapObjList = currentStMapObjList;
						// scheduling timer for refresh stations status
						stStatusTimer = new StationsStatusTimer(viewMapWidget);
						stStatusTimer.scheduleRepeating(CentraleUIConstants.TIMER_SCHEDULING);
						// clear map from old stations
						int numWidgetToRemove = externalPanel.getWidgetCount() - 2;
						int removedWidget = 0;
						for (int j = 0; j < numWidgetToRemove; j++) {
							externalPanel.remove(externalPanel.getWidget(j + 2 - removedWidget));
							removedWidget++;
						}
						for (int i = 0; i < stMapObjList.size(); i++) {
							StationMapObject stMapObj = stMapObjList.get(i);

							Panel stPanel = new VerticalPanel();
							Label stLabel = new Label(stMapObj.getShortName());
							Hidden stIdHidden = new Hidden();
							stIdHidden.setValue(new Integer(stMapObj.getStationId()).toString());
							stPanel.add(stIdHidden);
							stPanel.add(stLabel);

							// WARNING: if necessary, manage here
							// useSyntheticIcon
							HorizontalPanel hPanelImg = new HorizontalPanel();
							hPanelImg.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
							HorizontalPanel hPanelDate = new HorizontalPanel();
							Label dateLabel = new Label("--/--/-- --:--");
							Label temperatureLabel = new Label();
							hPanelDate.add(dateLabel);
							hPanelDate.add(temperatureLabel);
							hPanelDate.setCellHorizontalAlignment(temperatureLabel, HasHorizontalAlignment.ALIGN_RIGHT);
							stPanel.add(hPanelDate);
							stPanel.add(hPanelImg);
							hPanelDate.setCellWidth(temperatureLabel, "24px");

							// set position of stPanel on the map
							stPanel.setStyleName("gwt-station");

							stPanel.getElement().getStyle().setProperty("position", "absolute");
							int objX = 0;
							int objY = 0;
							// Log.debug("stMapObj.map_x " + stMapObj.map_x);
							// Log.debug("stMapObj.map_y " + stMapObj.map_y);
							// Log.debug("image.getAbsoluteLeft() "
							// + image.getAbsoluteLeft());
							// Log.debug("image.getAbsoluteTop() "
							// + image.getAbsoluteTop());
							if (stMapObj.getMap_x() != null && stMapObj.getMap_y() != null) {
								objX = image.getAbsoluteLeft() + stMapObj.getMap_x().intValue();
								objY = image.getAbsoluteTop() + stMapObj.getMap_y().intValue();
							} else {
								objX = image.getAbsoluteLeft();
								objY = image.getAbsoluteTop();
							}

							stPanel.getElement().getStyle().setProperty("left", new Integer(objX).toString() + "px");
							stPanel.getElement().getStyle().setProperty("top", new Integer(objY).toString() + "px");
							// create MenuBar for station and set it on map
							MenuBar contextMenuBar = new MenuBar(true);
							// Add items
							Command cmd = null;
							if (stMapObj.isEnabled()) {
								cmd = new StationMenuBarCommand(stMapObj.getStationId(), stMapObj.getVirtualCopId(),
										StationMenuBarCommand.BUTTON_POLLING, stMapObj.getShortName(),
										stMapObj.getStationUuid(), stMapObj.getIp());
								contextMenuBar.addItem(new MenuItem(CentraleUI.getMessages().call_polling(), cmd));
							}
							cmd = new StationMenuBarCommand(stMapObj.getStationId(), stMapObj.getVirtualCopId(),
									StationMenuBarCommand.BUTTON_CFG, stMapObj.getShortName(),
									stMapObj.getStationUuid(), stMapObj.getIp());
							contextMenuBar.addItem(new MenuItem(CentraleUI.getMessages().call_cfg_station(), cmd));
							cmd = new StationMenuBarCommand(stMapObj.getStationId(), stMapObj.getVirtualCopId(),
									StationMenuBarCommand.BUTTON_CALL_UI, stMapObj.getShortName(),
									stMapObj.getStationUuid(), stMapObj.getIp());
							contextMenuBar.addItem(new MenuItem(CentraleUI.getMessages().call_ui_station(), cmd));

							contextMenuBar.getElement().getStyle().setProperty("position", "absolute");

							if (stMapObj.getMap_x() != null && stMapObj.getMap_y() != null) {

								int xMenu = image.getAbsoluteLeft() + stMapObj.getMap_x().intValue() + STPANEL_WIDTH;
								// Log.debug("xMenu " + xMenu);
								int clientWidht = Window.getClientWidth();
								// Log.debug("clientWidht " + clientWidht);
								if (xMenu >= clientWidht || xMenu >= (clientWidht - (2 * STPANEL_WIDTH))) {
									xMenu = xMenu - (2 * STPANEL_WIDTH);
									// Log.debug("x maggiore - > nuova x " +
									// xMenu);
								}
								objX = xMenu;

								if (objX >= image.getOffsetWidth() + image.getAbsoluteLeft()) {
									objX = image.getAbsoluteLeft() + stMapObj.getMap_x().intValue() - STPANEL_WIDTH;
								}

							} else {
								objX = image.getAbsoluteLeft() + STPANEL_WIDTH;
								if (objX >= image.getOffsetWidth() + image.getAbsoluteLeft()) {
									objX = image.getAbsoluteLeft() - STPANEL_WIDTH;
								}
							}

							contextMenuBar.getElement().getStyle().setProperty("left",
									new Integer(objX).toString() + "px");

							// Create a ContextMenuTrigger which wraps stPanel
							final ContextMenuTrigger widgetWithContextMenu = new ContextMenuTrigger(stPanel);
							widgetWithContextMenu.setMenuBar(contextMenuBar);
							externalPanel.add(widgetWithContextMenu);

							// get stationStatus
							CentraleUIServiceAsync centraleService3 = (CentraleUIServiceAsync) GWT
									.create(CentraleUIService.class);
							ServiceDefTarget endpoint3 = (ServiceDefTarget) centraleService3;
							endpoint3.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
							AsyncCallback<StationStatusObject> callback3 = new UIAsyncCallback<StationStatusObject>() {
								public void onSuccess(StationStatusObject stStatusObj) {
									updateStationPanel(stStatusObj);

								}
							};
							centraleService3.getStationStatus(stMapObj.getStationId(), callback3);
						}
						resize(0);
					}
				};
				centraleService2.getStMapObjList(false, false, callback2);

			}
		};
		centraleService.getConfigInfo(callback);
	}

	public void updatePollingButton(boolean isDuringPolling) {
		if (isDuringPolling && panelButton.containsButtonType(CentraleUIConstants.POLLING)) {
			panelButton.changeButtonType(CentraleUIConstants.POLLING, CentraleUIConstants.DURING_POLLING);
			// Window.alert("e' in poling e ho polling allora cambio in during polling");
		} else if (!isDuringPolling && panelButton.containsButtonType(CentraleUIConstants.DURING_POLLING)) {
			panelButton.changeButtonType(CentraleUIConstants.DURING_POLLING, CentraleUIConstants.POLLING);
			// Window.alert("non e' in poling e ho during polling allora cambio in
			// polling");
		}
	}

	public void updateStationPanel(StationStatusObject stStatusObj) {
		boolean founded = false;
		for (int i = 2; i < externalPanel.getWidgetCount() && !founded; i++) {
			ContextMenuTrigger widgetWithContextMenu = (ContextMenuTrigger) externalPanel.getWidget(i);
			VerticalPanel vPanel = (VerticalPanel) widgetWithContextMenu.getAttachedTo();
			Hidden stIdHidden = (Hidden) vPanel.getWidget(0);
			if (new Integer(stStatusObj.getStationId()).toString().equals(stIdHidden.getValue())) {
				founded = true;
				Label stLabel = (Label) vPanel.getWidget(1);
				stLabel.setText(stStatusObj.getShortName());
				HorizontalPanel hPanelDate = (HorizontalPanel) vPanel.getWidget(2);
				Label dateLabel = (Label) hPanelDate.getWidget(0);
				if (!stStatusObj.isEnabled()) {
					dateLabel.setText(CentraleUI.getMessages().disabled().toUpperCase());
				} else {
					if (stStatusObj.getLastCorrectPollingDate() != null)
						dateLabel.setText(df.format(stStatusObj.getLastCorrectPollingDate()));
					else
						dateLabel.setText("--/--/-- --:--");
				}

				Label temperatureLabel = (Label) hPanelDate.getWidget(1);
				if (stStatusObj.isEnabled()) {
					if (stStatusObj.getLastTemperatureValue() != null) {
						if (configInfo.getMinThreshold() != null
								&& stStatusObj.getLastTemperatureValue().intValue() < configInfo.getMinThreshold())
							temperatureLabel.setStyleName("gwt-temp-low");
						else if (configInfo.getAlarmMaxThreshold() != null
								&& stStatusObj.getLastTemperatureValue().intValue() > configInfo.getAlarmMaxThreshold())
							temperatureLabel.setStyleName("gwt-temp-alarm");
						else if (configInfo.getMaxThreshold() != null
								&& stStatusObj.getLastTemperatureValue().intValue() > configInfo.getMaxThreshold())
							temperatureLabel.setStyleName("gwt-temp-warning");
						else if (configInfo.getMinThreshold() != null && configInfo.getAlarmMaxThreshold() != null
								&& configInfo.getMaxThreshold() != null)
							temperatureLabel.setStyleName("gwt-temp-normal-green");
						else
							temperatureLabel.setStyleName("gwt-temp-normal");
						temperatureLabel.setText(stStatusObj.getLastTemperatureValue().toString() + "°");
					} else {
						temperatureLabel.setText("");
					}
				} else {
					temperatureLabel.setText("");
				}
				HorizontalPanel hPanelImg = (HorizontalPanel) vPanel.getWidget(3);
				hPanelImg.clear();
				IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);

				// set communication status
				if (stStatusObj.isEnabled()) {
					if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.IS_CALLING) {
						HTML html = new HTML("<img src='images/router_router_ok_animated.gif'/>");
						hPanelImg.add(html);
					} else if (stStatusObj
							.getCommunicationStatus() == CentraleUIConstants.ROUTER_REMOTE_NOT_RESPONING) {
						Image routerNotResponding = new Image();
						routerNotResponding.setResource(iconImageBundle.routerNotResponding());
						hPanelImg.add(routerNotResponding);
						/*
						 * Window.alert(
						 * "stStatusObj.communicationStatus == CentraleUI.ROUTER_NOT_RESPONDING: " +
						 * (stStatusObj.communicationStatus == CentraleUI.ROUTER_NOT_RESPONDING) +
						 * "\nstStatusObj.communicationStatus == CentraleUI.ROUTER_REMOTE_NOT_RESPONING: "
						 * + (stStatusObj.communicationStatus == CentraleUI.ROUTER_REMOTE_NOT_RESPONING)
						 * + " --> routerNotResponding");
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.SW_NOT_RESPONDING
							|| stStatusObj.getCommunicationStatus() == CentraleUIConstants.PROTOCOL_ERROR
							|| stStatusObj.getCommunicationStatus() == CentraleUIConstants.POLLING_ERROR
							|| stStatusObj.getCommunicationStatus() == CentraleUIConstants.UNEXPECTED_ERROR) {
						Image swNotResponding = new Image();
						swNotResponding.setResource(iconImageBundle.swNotResponding());
						hPanelImg.add(swNotResponding);
						/*
						 * Window.alert(
						 * "stStatusObj.communicationStatus == CentraleUI.SW_NOT_RESPONDING: " +
						 * (stStatusObj.communicationStatus == CentraleUI.SW_NOT_RESPONDING) +
						 * "\nstStatusObj.communicationStatus == CentraleUI.DIAGNOSTIC_SW_NOT_RESPONDING: "
						 * + (stStatusObj.communicationStatus ==
						 * CentraleUI.DIAGNOSTIC_SW_NOT_RESPONDING) + " --> swNotResponding");
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.SW_RESPONDING) {
						HTML html = new HTML("<img src='images/dialogo_in_corso.gif'/>");
						hPanelImg.add(html);
						/*
						 * Window.alert( "stStatusObj.communicationStatus == CentraleUI.SW_RESPONDING: "
						 * + (stStatusObj.communicationStatus == CentraleUI.SW_RESPONDING) +
						 * " --> dialogo_in_corso.gif");
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.WAIT) {
						HTML html = new HTML("<img src='images/clessidra.gif'/>");
						hPanelImg.add(html);
						/*
						 * Window.alert( "stStatusObj.communicationStatus == CentraleUI.WAIT: " +
						 * (stStatusObj.communicationStatus == CentraleUI.WAIT) + " --> clessidra.gif");
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.COMMUNICATION_LOST) {
						Image noLine = new Image();
						noLine.setResource(iconImageBundle.noLine());
						hPanelImg.add(noLine);
						/*
						 * Window.alert(
						 * "stStatusObj.communicationStatus == CentraleUI.COMMUNICATION_LOST: " +
						 * (stStatusObj.communicationStatus == CentraleUI.COMMUNICATION_LOST) +
						 * " --> noLine");
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.POLLING_FINISHED_OK) {
						Image pollingFinishedOk = new Image();
						pollingFinishedOk.setResource(iconImageBundle.pollingFinishedOk());
						hPanelImg.add(pollingFinishedOk);
						/*
						 * Window.alert(
						 * "(stStatusObj.communicationStatus == CentraleUI.POLLING_FINISHED_OK: " +
						 * ((stStatusObj.communicationStatus == CentraleUI.POLLING_FINISHED_OK) +
						 * " --> pollingFinishedOk"));
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.UNKNOWN) {
						Image communicationGray = new Image();
						communicationGray.setResource(iconImageBundle.communicationGray());
						hPanelImg.add(communicationGray);
						/*
						 * Window.alert( "(stStatusObj.communicationStatus == CentraleUI.UNKNOWN: " +
						 * ((stStatusObj.communicationStatus == CentraleUI.UNKNOWN) +
						 * " --> communicationGray"));
						 */
					} else if (stStatusObj
							.getCommunicationStatus() == CentraleUIConstants.ROUTER_LOCALE_NOT_RESPONING) {
						Image routerLocaleNotResponding = new Image();
						routerLocaleNotResponding.setResource(iconImageBundle.routerLocaleNotResponding());
						hPanelImg.add(routerLocaleNotResponding);
						/*
						 * Window.alert(
						 * "(stStatusObj.communicationStatus == CentraleUI.ROUTER_LOCALE_NOT_RESPONING: "
						 * + ((stStatusObj.communicationStatus ==
						 * CentraleUI.ROUTER_LOCALE_NOT_RESPONING) + " --> routerLocaleNotResponding"));
						 */
					} else if (stStatusObj.getCommunicationStatus() == CentraleUIConstants.PC_NOT_RESPONDING) {
						Image pcNotResponding = new Image();
						pcNotResponding.setResource(iconImageBundle.pcNotResponding());
						hPanelImg.add(pcNotResponding);
						/*
						 * Window.alert(
						 * "(stStatusObj.communicationStatus == CentraleUI.PC_NOT_RESPONDING: " +
						 * ((stStatusObj.communicationStatus == CentraleUI.PC_NOT_RESPONDING) +
						 * " --> pcNotResponding"));
						 */
					}
				} else {
					Image communicationGray = new Image();
					communicationGray.setResource(iconImageBundle.communicationGray());
					hPanelImg.add(communicationGray);
				}

				// set informatic status
				if (stStatusObj.isEnabled()) {
					if (stStatusObj.getInformaticStatus() == CentraleUIConstants.ALARM) {
						Image informaticStatusKo = new Image();
						informaticStatusKo.setResource(iconImageBundle.informaticStatusKo());
						hPanelImg.add(informaticStatusKo);
					}
					if (stStatusObj.getInformaticStatus() == CentraleUIConstants.WARNING) {
						Image informaticStatusWarning = new Image();
						informaticStatusWarning.setResource(iconImageBundle.informaticStatusWarning());
						hPanelImg.add(informaticStatusWarning);
					} else if (stStatusObj.getInformaticStatus() == CentraleUIConstants.OK) {
						Image informaticStatusOk = new Image();
						informaticStatusOk.setResource(iconImageBundle.informaticStatusOk());
						hPanelImg.add(informaticStatusOk);
					} else if (stStatusObj.getInformaticStatus() == CentraleUIConstants.UNKNOWN) {
						Image informaticStatusGray = new Image();
						informaticStatusGray.setResource(iconImageBundle.informaticStatusGray());
						hPanelImg.add(informaticStatusGray);
					}
				} else {
					Image informaticStatusGray = new Image();
					informaticStatusGray.setResource(iconImageBundle.informaticStatusGray());
					hPanelImg.add(informaticStatusGray);
				}

				// set station status
				if (stStatusObj.isEnabled()) {
					if (stStatusObj.getStationStatus() == CentraleUIConstants.ALARM) {
						Image stationStatusKo = new Image();
						stationStatusKo.setResource(iconImageBundle.stationStatusKo());
						hPanelImg.add(stationStatusKo);
					} else if (stStatusObj.getStationStatus() == CentraleUIConstants.OK) {
						Image stationStatusOk = new Image();
						stationStatusOk.setResource(iconImageBundle.stationStatusOk());
						hPanelImg.add(stationStatusOk);
					} else if (stStatusObj.getStationStatus() == CentraleUIConstants.UNKNOWN) {
						Image stationStatusGray = new Image();
						stationStatusGray.setResource(iconImageBundle.stationStatusGray());
						hPanelImg.add(stationStatusGray);
					}
				} else {
					Image stationStatusGray = new Image();
					stationStatusGray.setResource(iconImageBundle.stationStatusGray());
					hPanelImg.add(stationStatusGray);
				}

				// set clockstatus
				if (stStatusObj.isEnabled()) {
					if (stStatusObj.getClockStatus() == CentraleUIConstants.OK) {
						Image clock = new Image();
						clock.setResource(iconImageBundle.clock());
						hPanelImg.add(clock);
					} else if (stStatusObj.getClockStatus() == CentraleUIConstants.WARNING) {
						Image clockWarning = new Image();
						clockWarning.setResource(iconImageBundle.clockWarning());
						hPanelImg.add(clockWarning);
					} else if (stStatusObj.getClockStatus() == CentraleUIConstants.ALARM) {
						Image clockError = new Image();
						clockError.setResource(iconImageBundle.clockError());
						hPanelImg.add(clockError);
					} else if (stStatusObj.getClockStatus() == CentraleUIConstants.UNKNOWN) {
						Image clockGray = new Image();
						clockGray.setResource(iconImageBundle.clockGray());
						hPanelImg.add(clockGray);
					}
				} else {
					Image clockGray = new Image();
					clockGray.setResource(iconImageBundle.clockGray());
					hPanelImg.add(clockGray);
				}

				// set door status
				if (stStatusObj.isEnabled()) {
					if (stStatusObj.getDoorStatus() == CentraleUIConstants.OK) {
						Image doorClose = new Image();
						doorClose.setResource(iconImageBundle.doorClose());
						hPanelImg.add(doorClose);
					} else if (stStatusObj.getDoorStatus() == CentraleUIConstants.ALARM) {
						Image doorOpen = new Image();
						doorOpen.setResource(iconImageBundle.doorOpen());
						hPanelImg.add(doorOpen);
					} else if (stStatusObj.getDoorStatus() == CentraleUIConstants.UNKNOWN) {
						Image doorGray = new Image();
						doorGray.setResource(iconImageBundle.doorGray());
						hPanelImg.add(doorGray);
					}
				} else {
					Image doorGray = new Image();
					doorGray.setResource(iconImageBundle.doorGray());
					hPanelImg.add(doorGray);
				} // end else
			} // end if
		} // end for
	}// end updateStationPanel

	public void stopStatusTimer() {
		if (stStatusTimer != null)
			stStatusTimer.cancel();
	}

	@Override
	protected void dismissContent(final AsyncPageOperation asyncPageOperation) {
		stopStatusTimer();
		super.dismissContent(asyncPageOperation);
	}

	@Override
	protected void loadContent() {
		Window.addWindowScrollHandler(new Window.ScrollHandler() {

			@Override
			public void onWindowScroll(com.google.gwt.user.client.Window.ScrollEvent event) {
				resize(event.getScrollLeft());

			}
		});
		writeTitle();
		loadImage();
	}

	@Override
	public void onResize(ResizeEvent event) {
		resize(0);
	}

	private void resize(int scrollLeft) {
		if (CentraleUI.viewMapWidget.isVisible()) {

			for (int i = 0; i < stMapObjList.size(); i++) {
				StationMapObject stMapObj = stMapObjList.get(i);
				ContextMenuTrigger widgetWithContextMenu = (ContextMenuTrigger) externalPanel.getWidget(i + 2);
				if (stMapObj.getMap_x() != null && stMapObj.getMap_y() != null) {
					int objX = image.getAbsoluteLeft() + stMapObj.getMap_x().intValue();
					int objY = image.getAbsoluteTop() + stMapObj.getMap_y().intValue();
					// correct values for avoiding widget from going out of the
					// map
					// Log.debug("in resize: stazione: " + stMapObj.shortName);
					if (objX < image.getAbsoluteLeft())
						objX = image.getAbsoluteLeft();
					if ((objX + widgetWithContextMenu.getOffsetWidth()) > (image.getOffsetWidth()
							+ image.getAbsoluteLeft()))
						objX = (image.getOffsetWidth() + image.getAbsoluteLeft()
								- widgetWithContextMenu.getOffsetWidth());

					int menuObjX = objX + STPANEL_WIDTH;
					// Log.debug("in resize: menuObjX: " + menuObjX);
					int clientWidht = Window.getClientWidth();
					// Log.debug("in resize: clientWidht: " + clientWidht);
					if (menuObjX >= clientWidht || menuObjX >= (clientWidht - (2 * STPANEL_WIDTH))) {
						menuObjX = menuObjX - (2 * STPANEL_WIDTH) - scrollLeft;
						// Log.debug("in resize: dim maggiore quindi modificato in : "
						// + menuObjX);
					} else {
						menuObjX = menuObjX - scrollLeft;
					}

					if (objY < image.getAbsoluteTop())
						objY = image.getAbsoluteTop();
					if ((objY + widgetWithContextMenu.getOffsetHeight()) > (image.getOffsetHeight()
							+ image.getAbsoluteTop()))
						objY = (image.getOffsetHeight() + image.getAbsoluteTop()
								- widgetWithContextMenu.getOffsetHeight());
					widgetWithContextMenu.getElement().getStyle().setProperty("left",
							new Integer(objX).toString() + "px");
					widgetWithContextMenu.getElement().getStyle().setProperty("top",
							new Integer(objY).toString() + "px");

					widgetWithContextMenu.getMenuBar().getElement().getStyle().setProperty("left",
							new Integer(menuObjX).toString() + "px");
				}
			}
		}
	}

}
