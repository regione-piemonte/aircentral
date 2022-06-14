/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: page for configuring anagraphic station informations
* Change log:
*   2008-09-16: initial version
*   2014-05-23: remodeled by Pierfrancesco Vallosio
* ----------------------------------------------------------------------------
* $Id: StationWidget.java,v 1.3 2014/09/23 10:26:18 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.stationconfig;

import it.csi.centrale.ui.client.CentraleUI;
import it.csi.centrale.ui.client.CentraleUIConstants;
import it.csi.centrale.ui.client.CentraleUIService;
import it.csi.centrale.ui.client.CentraleUIServiceAsync;
import it.csi.centrale.ui.client.PanelButtonWidget;
import it.csi.centrale.ui.client.UIAsyncCallback;
import it.csi.centrale.ui.client.Utils;
import it.csi.centrale.ui.client.data.StInfoObject;
import it.csi.centrale.ui.client.pagecontrol.AsyncPageOperation;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for configuring anagraphic station informations
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class StationWidget extends UIPage {
	private PanelButtonWidget panelButton;
	private ConnectionPanel connectionPanel;
	private AnagraphicPanel anagraphicPanel;
	private PollingPanel pollingPanel;
	private Integer stationId;
	private Integer virtualCopId;
	private StInfoObject stationInfo = null;
	private StInfoObject downloadedStationInfo = null;

	public StationWidget() {
		panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().station_title(),
				panelButton, false);

		// Connection panel actions
		ClickHandler connectionCancelHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (stationInfo == null) {
					anagraphicPanel.setVisible(false);
					anagraphicPanel.clearStationInfo();
					if (connectionPanel.isInputEnabled())
						CentraleUI.goToPreviousPage();
				} else {
					connectionPanel.setConnectionInfo(stationInfo);
					anagraphicPanel.setStationInfo(stationInfo);
					downloadedStationInfo = null;
				}
				connectionPanel.showSaveButton(false);
				connectionPanel.enableInput(true);
			}
		};
		ClickHandler connectionDownloadHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AsyncCallback<StInfoObject> callback = new UIAsyncCallback<StInfoObject>() {
					@Override
					public void onSuccess(StInfoObject stInfo) {
						Utils.unlockForPopup("loading");
						anagraphicPanel.setStationInfo(stInfo);
						anagraphicPanel.setVisible(true);
						if (stationInfo != null && stationInfo.getUuid() != null && stInfo.getUuid() != null
								&& !stationInfo.getUuid().equals(stInfo.getUuid())) {
							downloadedStationInfo = null;
							Window.alert(CentraleUI.getMessages().station_uuid_error());
						} else {
							downloadedStationInfo = stInfo;
							connectionPanel.showSaveButton(true);
							connectionPanel.enableInput(false);
						}
					}// end onSuccess

					@Override
					public void onFailure(Throwable caught) {
						Utils.unlockForPopup("loading");
						super.onFailure(caught);
						downloadedStationInfo = null;
						connectionPanel.showSaveButton(true);
						connectionPanel.enableInput(false);
					}
				};
				connectionPanel.showSaveButton(false);
				connectionPanel.enableInput(true);
				StInfoObject connInfo = getConnectionInfo(true);
				if (connInfo != null) {
					Utils.blockForPopup("loading");
					if (stationId != null)
						connInfo.setStationId(stationId);
					connInfo.getVirtualCopInfo().setVirtualCopId(virtualCopId);
					getUIService().downloadStationInfo(connInfo, callback);
				}
			}// end onClick
		};
		ClickHandler connectionSaveHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final StInfoObject connInfo = getConnectionInfo(true);
				connectionPanel.showSaveButton(false);
				connectionPanel.enableInput(true);
				if (connInfo == null)
					return;
				if (stationId != null)
					connInfo.setStationId(stationId);
				connInfo.getVirtualCopInfo().setVirtualCopId(virtualCopId);
				final AsyncCallback<Integer> callback = new UIAsyncCallback<Integer>() {
					public void onSuccess(Integer newStationId) {
						Utils.unlockForPopup("loading");
						if (stationInfo == null) {
							stationId = newStationId;
							stationInfo = new StInfoObject();
							stationInfo.setStationId(newStationId);
							stationInfo.getVirtualCopInfo().setVirtualCopId(virtualCopId);
							stationInfo.setEnabled(true);
							if (downloadedStationInfo == null)
								stationInfo.setShortName(connInfo.getShortName());
						}
						stationInfo.setIpAddress(connInfo.getIpAddress());
						stationInfo.setIpPort(connInfo.getIpPort());
						stationInfo.setPhoneNumber(connInfo.getPhoneNumber());
						stationInfo.setUseModem(connInfo.isUseModem());
						stationInfo.setLan(connInfo.isLan());
						stationInfo.setProxy(connInfo.isProxy());
						stationInfo.setRouterIpAddress(connInfo.getRouterIpAddress());
						if (downloadedStationInfo != null) {
							stationInfo.setUuid(downloadedStationInfo.getUuid());
							stationInfo.setShortName(downloadedStationInfo.getShortName());
							stationInfo.setName(downloadedStationInfo.getName());
							stationInfo.setLocation(downloadedStationInfo.getLocation());
							stationInfo.setAddress(downloadedStationInfo.getAddress());
							stationInfo.setCity(downloadedStationInfo.getCity());
							stationInfo.setProvince(downloadedStationInfo.getProvince());
							stationInfo.setNotes(downloadedStationInfo.getNotes());
							stationInfo.setUseGps(downloadedStationInfo.isUseGps());
						}
						downloadedStationInfo = null;
						connectionPanel.setConnectionInfo(stationInfo);
						anagraphicPanel.setStationInfo(stationInfo);
						pollingPanel.setPollingInfo(stationInfo);
						pollingPanel.setVisible(true);
						connectionPanel.setConnectionInfo(stationInfo);
						Utils.operationOk();
					}

					@Override
					public void onFailure(Throwable caught) {
						Utils.unlockForPopup("loading");
						super.onFailure(caught);
					}
				};
				if (stationInfo == null && downloadedStationInfo == null) {
					new AskStationNamePopup(new SaveAction() {
						@Override
						public void execute(String name) {
							Utils.blockForPopup("loading");
							connInfo.setShortName(name);
							getUIService().saveStationInfo(true, connInfo, null, callback);
						}
					});
				} else {
					Utils.blockForPopup("loading");
					getUIService().saveStationInfo(stationInfo == null, connInfo, downloadedStationInfo, callback);
				}
			}
		};
		connectionPanel = new ConnectionPanel(connectionCancelHandler, connectionSaveHandler,
				connectionDownloadHandler);
		connectionPanel.showSaveButton(false);
		connectionPanel.enableInput(true);
		externalPanel.add(connectionPanel);

		/*
		 * Anagraphic info
		 */
		anagraphicPanel = new AnagraphicPanel();
		anagraphicPanel.setVisible(false);
		externalPanel.add(anagraphicPanel);

		/*
		 * Polling info
		 */
		ClickHandler pollingUndoHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				pollingPanel.setPollingInfo(stationInfo);
			}
		};
		ClickHandler pollingSaveHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final StInfoObject pollingInfo = new StInfoObject();
				AsyncCallback<Object> callback = new UIAsyncCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						Utils.unlockForPopup("loading");
						stationInfo.setEnabled(pollingInfo.isEnabled());
						stationInfo.setForcePollingTime(pollingInfo.getForcePollingTime());
						stationInfo.setMinTimestampForPolling(pollingInfo.getMinTimestampForPolling());
						stationInfo.setDownloadSampleDataEnabled(pollingInfo.isDownloadSampleDataEnabled());
						Utils.operationOk();
					}

					@Override
					public void onFailure(Throwable caught) {
						Utils.unlockForPopup("loading");
						super.onFailure(caught);
					}
				};
				pollingInfo.setEnabled(pollingPanel.isPollingEnabled());
				pollingInfo.setDownloadSampleDataEnabled(pollingPanel.isSampleDataDownloadEnabled());
				pollingInfo.getVirtualCopInfo().setVirtualCopId(stationInfo.getVirtualCopInfo().getVirtualCopId());
				try {
					pollingInfo.setForcePollingTime(pollingPanel.getCustomPollingPeriod());
					pollingInfo.setMinTimestampForPolling(pollingPanel.getMinimumDateForDownload());
					Utils.blockForPopup("loading");
					getUIService().savePollingStationInfo(stationInfo.getStationId(), pollingInfo, callback);
				} catch (NumberFormatException nfEx) {
					Window.alert(CentraleUI.getMessages().error_parsing_integer());
				} catch (IllegalArgumentException e) {
					Window.alert(CentraleUI.getMessages().error_parsing_date());
				}
			}// end onClick
		};
		pollingPanel = new PollingPanel(pollingUndoHandler, pollingSaveHandler);
		pollingPanel.setVisible(false);
		externalPanel.add(pollingPanel);

		initWidget(externalPanel);
	}// end constructor

	public void setStation(int stationId, Integer virtualCopId) {
		this.stationId = stationId;
		this.virtualCopId = virtualCopId;
	}

	public void setNewStation(Integer virtualCopId) {
		this.stationId = null;
		this.virtualCopId = virtualCopId;
	}

	@Override
	protected void loadContent() {
		stationInfo = downloadedStationInfo = null;
		panelButton.clearButton();
		connectionPanel.setConnectionDefaults();
		connectionPanel.showSaveButton(false);
		connectionPanel.enableInput(true);
		anagraphicPanel.clearStationInfo();
		pollingPanel.setPollingDefaults();
		if (stationId == null) { // New station
			setPanelButton(null);
			anagraphicPanel.setVisible(false);
			pollingPanel.setVisible(false);
		} else { // Existing station
			anagraphicPanel.setVisible(true);
			pollingPanel.setVisible(true);
			AsyncCallback<StInfoObject> callback = new UIAsyncCallback<StInfoObject>() {
				public void onSuccess(StInfoObject stInfo) {
					if (stInfo == null) {
						Window.alert(CentraleUI.getMessages().error_station_not_found());
						CentraleUI.goToPreviousPage();
						return;
					}
					stationInfo = stInfo;
					setPanelButton(stationInfo.getShortName());
					connectionPanel.setConnectionInfo(stationInfo);
					anagraphicPanel.setStationInfo(stationInfo);
					pollingPanel.setPollingInfo(stationInfo);
					virtualCopId = stationInfo.getVirtualCopInfo().getVirtualCopId();
				}
			};
			getUIService().getStationInfo(stationId, callback);
		}
	}

	@Override
	protected void dismissContent(final AsyncPageOperation asyncPageOperation) {
		StInfoObject stInfo = getConnectionInfo(false);
		stInfo.setEnabled(pollingPanel.isPollingEnabled());
		stInfo.setDownloadSampleDataEnabled(pollingPanel.isSampleDataDownloadEnabled());
		try {
			stInfo.setForcePollingTime(pollingPanel.getCustomPollingPeriod());
			stInfo.setMinTimestampForPolling(pollingPanel.getMinimumDateForDownload());
		} catch (NumberFormatException nfEx) {
			Window.alert(CentraleUI.getMessages().error_parsing_integer());
			return;
		} catch (IllegalArgumentException e) {
			Window.alert(CentraleUI.getMessages().error_parsing_date());
			return;
		}
		if (stationId != null)
			stInfo.setStationId(stationId);
		stInfo.getVirtualCopInfo().setVirtualCopId(virtualCopId);
		getUIService().verifySameStationFields(stInfo, asyncPageOperation);
	}

	private StInfoObject getConnectionInfo(boolean checkFields) {
		String commDevice = connectionPanel.getCommunicationDevice();
		StInfoObject connInfo = new StInfoObject();
		connInfo.setIpAddress(connectionPanel.getStationHost());
		connInfo.setPhoneNumber(connectionPanel.getStationPhoneNumber());
		connInfo.setUseModem(commDevice.equals(CentraleUIConstants.MODEM));
		connInfo.setLan(commDevice.equals(CentraleUIConstants.LAN));
		connInfo.setProxy(commDevice.equals(CentraleUIConstants.PROXY));
		connInfo.setRouterIpAddress(connectionPanel.getRouterIP());
		boolean useRouter = !connInfo.isUseModem() && !connInfo.isLan() && !connInfo.isProxy();
		if (checkFields) {
			if (connInfo.getIpAddress().isEmpty() || (connInfo.isUseModem() && connInfo.getPhoneNumber().isEmpty())
					|| (useRouter && connInfo.getRouterIpAddress().isEmpty())) {
				Window.alert(CentraleUI.getMessages().conn_fields_empty());
				return null;
			}
		}
		try {
			connInfo.setIpPort(connectionPanel.getStationPort());
			return connInfo;
		} catch (NumberFormatException ex) {
			if (checkFields) {
				Window.alert(CentraleUI.getMessages().error_parsing_integer());
				return null;
			} else {
				return connInfo;
			}
		}
	}

	private void setPanelButton(final String stationShortName) {
		if (stationShortName != null)
			panelButton.addButton(CentraleUIConstants.XML, new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					AsyncCallback<Long> callback = new UIAsyncCallback<Long>() {
						public void onSuccess(Long lastConfigTime) {
							if (lastConfigTime != null && lastConfigTime != 0) {
								Window.open("./perifservice?function=" + "getStationConfig&xml=true&station="
										+ stationShortName + "&config=" + lastConfigTime, "", "");
							} else
								Window.alert(CentraleUI.getMessages().no_station_conf());
						}// end onSuccess
					};
					getUIService().getLastStationModifiedConfDate(stationId, callback);
				}
			});

		ClickHandler backClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.goToPreviousPage();
			}
		};
		panelButton.addButton(CentraleUIConstants.BACK, backClickHandler);

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale() + "/index.html#conf_stationWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
	}

	private CentraleUIServiceAsync getUIService() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		return centraleService;
	}

}// end class
