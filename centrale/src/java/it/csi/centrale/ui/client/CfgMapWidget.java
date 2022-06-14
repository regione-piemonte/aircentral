/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: widget for station position on map configuration
* Change log:
*   2008-09-15: initial version
* ----------------------------------------------------------------------------
* $Id: CfgMapWidget.java,v 1.31 2015/10/15 12:10:23 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.csi.centrale.ui.client.data.ConfigInfoObject;
import it.csi.centrale.ui.client.data.StationMapObject;
import it.csi.centrale.ui.client.pagecontrol.AsyncPageOperation;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

/**
 * Widget for station position on map configuration
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */

public class CfgMapWidget extends UIPage implements ResizeHandler {

	private VerticalPanel externalPanel;

	private Image image;

	private List<StationMapObject> stMapObjList;

	private boolean useSyntheticIcon;

	public CfgMapWidget() {
		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		// button save for map_x and map_y of the stations
		ClickHandler saveClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveStationsMapCoords();
			}
		};

		panelButton.addButton(CentraleUIConstants.SAVE, saveClickHandler);

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_cfgMapWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);

		externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().view_map_title(), panelButton, true);

		externalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		image = new Image();
		externalPanel.add(image);

		initWidget(externalPanel);

	}

	private void loadImage() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<String> callback = new UIAsyncCallback<String>() {
			public void onSuccess(String imageStr) {
				image.setUrl(CentraleUIConstants.URL_FOR_IMAGE + "&imagename=" + imageStr);
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
				if (configInfoObj != null)
					useSyntheticIcon = configInfoObj.isSyntheticIcon();

				// get the list of station objects for map
				CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
				ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
				endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
				AsyncCallback<List<StationMapObject>> callback2 = new UIAsyncCallback<List<StationMapObject>>() {
					public void onSuccess(List<StationMapObject> stMapList) {
						stMapObjList = stMapList;
						for (int i = 0; i < stMapObjList.size(); i++) {
							StationMapObject stMapObj = stMapObjList.get(i);
							// verify if the station is to be added to
							// externalPanel
							if (isStationToBeAdded(new Integer(stMapObj.getStationId()).toString())) {
								Panel stPanel = new VerticalPanel();
								stPanel.setStyleName("gwt-station");
								Label stLabel = new Label(stMapObj.getShortName());
								Hidden stIdHidden = new Hidden();
								stIdHidden.setValue(new Integer(stMapObj.getStationId()).toString());
								stPanel.add(stIdHidden);
								Hidden stVirtualCopIdHidden = new Hidden();
								stVirtualCopIdHidden.setValue(stMapObj.getVirtualCopId().toString());
								stPanel.add(stVirtualCopIdHidden);
								stPanel.add(stLabel);

								HorizontalPanel hPanelImg = new HorizontalPanel();
								hPanelImg.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
								IconImageBundle iconImageBundle = (IconImageBundle) GWT.create(IconImageBundle.class);
								// case of synthetic icons
								if (useSyntheticIcon) {
									Image informaticStatusGray = new Image();
									informaticStatusGray.setResource(iconImageBundle.informaticStatusGray());
									hPanelImg.add(informaticStatusGray);
									Image stationStatusGray = new Image();
									stationStatusGray.setResource(iconImageBundle.stationStatusGray());
									hPanelImg.add(stationStatusGray);
									stPanel.add(hPanelImg);
								}
								// case of detailed icons
								else {
									HorizontalPanel hPanelDate = new HorizontalPanel();
									Label dateLabel = new Label("--/--/-- --:--");
									hPanelDate.add(dateLabel);
									stPanel.add(hPanelDate);
									Image communicationGray = new Image();
									communicationGray.setResource(iconImageBundle.communicationGray());
									hPanelImg.add(communicationGray);
									Image informaticStatusGray = new Image();
									informaticStatusGray.setResource(iconImageBundle.informaticStatusGray());
									hPanelImg.add(informaticStatusGray);
									Image stationStatusGray = new Image();
									stationStatusGray.setResource(iconImageBundle.stationStatusGray());
									hPanelImg.add(stationStatusGray);
									Image clockGray = new Image();
									clockGray.setResource(iconImageBundle.clockGray());
									hPanelImg.add(clockGray);
									Image doorGray = new Image();
									doorGray.setResource(iconImageBundle.doorGray());
									hPanelImg.add(doorGray);
									stPanel.add(hPanelImg);
								}
								SimplePanel panel = null;
								if (stMapObj.getMap_x() != null && stMapObj.getMap_y() != null) {
									int objX = image.getAbsoluteLeft() + stMapObj.getMap_x().intValue();
									int objY = image.getAbsoluteTop() + stMapObj.getMap_y().intValue();
									panel = new DraggableWidgetWrapper(stPanel, new Integer(objX), new Integer(objY),
											image.getAbsoluteLeft(), image.getAbsoluteTop(), image.getOffsetWidth(),
											image.getOffsetHeight());
								} else {
									panel = new DraggableWidgetWrapper(stPanel, image.getAbsoluteLeft(),
											image.getAbsoluteTop(), image.getAbsoluteLeft(), image.getAbsoluteTop(),
											image.getOffsetWidth(), image.getOffsetHeight());
								}

								externalPanel.add(panel);
							}
						}
					}
				};
				// il true serve per dire che le stazioni da estrarre sono
				// quelle con i privilegi di scrittura
				centraleService2.getStMapObjList(false, true, callback2);
			}
		};

		centraleService.getConfigInfo(callback);

	}

	public void setNewXY(Widget w, Integer x, Integer y) {
		for (int i = 0; i < externalPanel.getWidgetCount() - 2; i++) {
			SimplePanel sPanel = (SimplePanel) externalPanel.getWidget(i + 2);
			if (w.equals(sPanel)) {
				StationMapObject stMapObj = stMapObjList.get(i);
				stMapObj.setMap_x((x.intValue() - image.getAbsoluteLeft()));
				stMapObj.setMap_y((y.intValue() - image.getAbsoluteTop()));
				stMapObj.setMoved(true);
			}
		}
	}

	private boolean isStationToBeAdded(String stId) {
		boolean retValue = true;
		for (int i = 2; i < externalPanel.getWidgetCount(); i++) {
			SimplePanel sPanel = (SimplePanel) externalPanel.getWidget(i);
			VerticalPanel vPanel = (VerticalPanel) sPanel.getWidget();
			Hidden stIdHidden = (Hidden) vPanel.getWidget(0);
			if (stId.equals(stIdHidden.getValue()))
				return false;
		}

		return retValue;
	}

	private void saveStationsMapCoords() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<String> callback = new UIAsyncCallback<String>() {
			public void onSuccess(String errStr) {
				if (errStr != null)
					Window.alert(errStr);
				else
					Utils.operationOk();
			}
		};

		centraleService.saveStMapObjList(stMapObjList, callback);
	}

	@Override
	protected void dismissContent(final AsyncPageOperation asyncPageOperation) {
		for (int i = externalPanel.getWidgetCount() - 1; i >= 2; i--) {
			externalPanel.remove(i);
		}
		super.dismissContent(asyncPageOperation);
	}

	@Override
	protected void loadContent() {
		loadImage();
		getAndShowStation();
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (CentraleUI.cfgMapWidget.isVisible()) {

			for (int i = 0; i < stMapObjList.size(); i++) {
				StationMapObject stMapObj = stMapObjList.get(i);
				SimplePanel sPanel = (SimplePanel) externalPanel.getWidget(i + 2);
				((DraggableWidgetWrapper) (sPanel)).setImageDimensions(image.getAbsoluteLeft(), image.getAbsoluteTop(),
						image.getOffsetWidth(), image.getOffsetHeight());
				if (stMapObj.getMap_x() != null && stMapObj.getMap_y() != null) {
					int objX = image.getAbsoluteLeft() + stMapObj.getMap_x().intValue();
					int objY = image.getAbsoluteTop() + stMapObj.getMap_y().intValue();
					// correct values for avoiding widget from going out of the
					// map
					if (objX < image.getAbsoluteLeft())
						objX = image.getAbsoluteLeft();
					if ((objX + sPanel.getOffsetWidth()) > (image.getOffsetWidth() + image.getAbsoluteLeft()))
						objX = (image.getOffsetWidth() + image.getAbsoluteLeft() - sPanel.getOffsetWidth());
					if (objY < image.getAbsoluteTop())
						objY = image.getAbsoluteTop();
					if ((objY + sPanel.getOffsetHeight()) > (image.getOffsetHeight() + image.getAbsoluteTop()))
						objY = (image.getOffsetHeight() + image.getAbsoluteTop() - sPanel.getOffsetHeight());
					sPanel.getElement().getStyle().setProperty("left", new Integer(objX).toString() + "px");
					sPanel.getElement().getStyle().setProperty("top", new Integer(objY).toString() + "px");
				}
			}
		}

	}
}
