/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: commands from menu bar of station panel
* Change log:
*   2008-09-16: initial version
* ----------------------------------------------------------------------------
* $Id: StationMenuBarCommand.java,v 1.33 2015/10/22 13:54:23 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Commands from menu bar of station panel
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationMenuBarCommand implements Command {
	public final static int BUTTON_CFG = 0;

	public final static int BUTTON_POLLING = 1;

	public final static int BUTTON_CALL_UI = 2;

	private int stationId;

	private String stationName;

	private Integer virtualCopId;

	private int buttonType;

	public StationMenuBarCommand(int stationId, Integer virtualCopId, int buttonType, String stationName, String uuid,
			String ip) {
		super();
		this.stationId = stationId;
		this.virtualCopId = virtualCopId;
		this.buttonType = buttonType;
		this.stationName = stationName;
	}

	public void execute() {
		if (buttonType == BUTTON_CFG) {
			CentraleUI.navBar.setBarForConfiguration();
			CentraleUI.navBar.loadConfig(CentraleUI.getLocale());
			CentraleUI.slotView.setVisible(false);
			CentraleUI.slotConfig.setVisible(true);
			CentraleUI.stationWidget.setStation(stationId, virtualCopId);
			CentraleUI.setCurrentPage(CentraleUI.stationWidget);
			CentraleUI.setPreviousPage(CentraleUI.listStationWidget);
		} else if (buttonType == BUTTON_POLLING) {
			CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
			AsyncCallback<Boolean> callback = new UIAsyncCallback<Boolean>() {
				public void onSuccess(Boolean resultValue) {
					if (resultValue != null) {
						/*
						 * if (resultValue) Window.alert(CentraleUI.messages.operation_ok()); else
						 */
						if (!resultValue)
							Window.alert(CentraleUI.getMessages().operation_error());
					}
				}
			};

			centraleService.callSingleStation(stationId, callback);
		} else if (buttonType == BUTTON_CALL_UI) {
			Utils.blockForPopup("loading");
			CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
			AsyncCallback<Boolean> callback = new UIAsyncCallback<Boolean>() {
				public void onSuccess(Boolean useModem) {
					Utils.unlockForPopup("loading");
					// if (useModem == null)
					// Window.alert("ERRORE");
					// if (useModem)
					Window.open(
							"ConnPerif.html?stationId=" + stationId + "&stationName=" + stationName + "&locale="
									+ CentraleUI.getLocale(),
							"", "directories=no,toolbar=no," + "menubar=no,location=no,status=no,"
									+ "scrollbars=yes,left=0,top=0,width=1033," + "height=760");
					// else
					// Window
					// .open(
					// "./proxy/station_" + stationId
					// + "/PerifericoUI.html?"
					// + "locale=" + CentraleUI.getLocale(),
					// "",
					// "directories=no,toolbar=no,"
					// + "menubar=no,location=no,status=no,"
					// + "scrollbars=yes,left=0,top=0,width=1033,"
					// + "height=760");
				}
			};

			centraleService.useModem(stationId, callback);

		}
	}// end execute
}
