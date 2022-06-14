/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: timer for update station status
* Change log:
*   2008-11-19: initial version
* ----------------------------------------------------------------------------
* $Id: StationsStatusTimer.java,v 1.12 2014/08/29 10:27:24 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import it.csi.centrale.ui.client.data.StationStatusObject;

/**
 * Timer for update station status
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationsStatusTimer extends Timer {

	private HashMap<Integer, Date> hm_stStatus = new HashMap<Integer, Date>();

	private ViewMapWidget viewMapWidget;

	private final StationsStatusTimer stStatusTimer;

	private int actualSchedulingTime;

	public StationsStatusTimer(ViewMapWidget viewMapWidget) {
		this.viewMapWidget = viewMapWidget;
		stStatusTimer = this;
		actualSchedulingTime = CentraleUIConstants.TIMER_SCHEDULING;
	}

	boolean pollingFinished = true;

	public void run() {
		// get list of station status
		CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT.create(CentraleUIService.class);
		ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
		endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<List<StationStatusObject>> callback2 = new AsyncCallback<List<StationStatusObject>>() {
			public void onSuccess(List<StationStatusObject> stStatusObjList) {
				boolean isAtLeastOnePollingNow = false;
				for (int i = 0; i < stStatusObjList.size(); i++) {
					// verify if there is stationStatusObject in internal
					// HashMap
					StationStatusObject stStatusObj = stStatusObjList.get(i);
					Date lastUpdatedImages = null;
					if (!hm_stStatus.containsKey(new Integer(stStatusObj.getStationId()))) {
						// create new HashMap entry
						hm_stStatus.put(new Integer(stStatusObj.getStationId()), null);
					} else
						lastUpdatedImages = hm_stStatus.get(new Integer(stStatusObj.getStationId()));

					boolean updateNecessary = false;
					// Window.alert("stazione: " + stStatusObj.stationId
					// + " isduringpolling: "
					// + stStatusObj.isDuringPolling);
					if (stStatusObj.isDuringPolling()) {
						pollingFinished = false;

						// Window.alert("station " + stStatusObj.stationId
						// + " is during polling");

						isAtLeastOnePollingNow = true;
						// update button for is during polling
						viewMapWidget.updatePollingButton(true);
						// scheduling the timer more frequently
						stStatusTimer.scheduleRepeating(CentraleUIConstants.TIMER_SCHEDULING_FREQUENT);
						actualSchedulingTime = CentraleUIConstants.TIMER_SCHEDULING_FREQUENT;
						updateNecessary = true;
					} else {
						// Window.alert("polling finito");
						// Window.alert("lastUpdatedImages: " +
						// lastUpdatedImages + " stStatusObj.lastPollingDate: "
						// + stStatusObj.lastPollingDate);
						if (stStatusObj.getLastPollingDate() != null) {
							if (lastUpdatedImages == null)
								updateNecessary = true;
							else if (lastUpdatedImages.before(stStatusObj.getLastPollingDate()) || !pollingFinished) {
								updateNecessary = true;
								pollingFinished = true;
							}
						}
						// Window.alert("updatenecessary vale: " +
						// updateNecessary);
					}
					if (updateNecessary) {

						// Window.alert("station " + stStatusObj.stationId
						// + " needs images updated");

						viewMapWidget.updateStationPanel(stStatusObj);
						// update existing HashMap entry
						hm_stStatus.remove(new Integer(stStatusObj.getStationId()));
						hm_stStatus.put(new Integer(stStatusObj.getStationId()), new Date());
					}

				}
				if (!isAtLeastOnePollingNow) {
					// update button for is during polling
					// Window.alert("cambio in finito polling");
					viewMapWidget.updatePollingButton(false);
				}
				if (!isAtLeastOnePollingNow && actualSchedulingTime == CentraleUIConstants.TIMER_SCHEDULING_FREQUENT) {
					// Window.alert("Adesso rallento il timer");
					// scheduling the timer less frequently
					stStatusTimer.scheduleRepeating(CentraleUIConstants.TIMER_SCHEDULING);
					actualSchedulingTime = CentraleUIConstants.TIMER_SCHEDULING;
				}

			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		};

		centraleService2.getStationStatusList(callback2);

	}
}
