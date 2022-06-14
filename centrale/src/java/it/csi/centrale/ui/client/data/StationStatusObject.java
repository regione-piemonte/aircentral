/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with information about the status of a station
* Change log:
*   2008-09-25: initial version
* ----------------------------------------------------------------------------
* $Id: StationStatusObject.java,v 1.7 2014/09/01 10:46:49 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

import it.csi.centrale.ui.client.CentraleUIConstants;

/**
 * Object with information about the status of a station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationStatusObject implements Serializable {

	private static final long serialVersionUID = 7086325406874182200L;

	private int stationId;
	private String shortName;
	private Date lastCorrectPollingDate;
	private Date lastPollingDate;
	private boolean isDuringPolling;
	private Integer lastTemperatureValue;
	private int communicationStatus;
	private int informaticStatus;
	private int stationStatus;
	private int clockStatus;
	private int doorStatus;
	private boolean enabled;

	public StationStatusObject() {
		setStationId(-1);
		setShortName(null);
		setLastCorrectPollingDate(null);
		setLastPollingDate(null);
		setDuringPolling(false);
		setLastTemperatureValue(null);
		setCommunicationStatus(CentraleUIConstants.UNKNOWN);
		setInformaticStatus(CentraleUIConstants.UNKNOWN);
		setStationStatus(CentraleUIConstants.UNKNOWN);
		setClockStatus(CentraleUIConstants.UNKNOWN);
		setDoorStatus(CentraleUIConstants.UNKNOWN);
		setEnabled(true);
	}

	/**
	 * @return the stationId
	 */
	public int getStationId() {
		return stationId;
	}

	/**
	 * @param stationId the stationId to set
	 */
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the lastCorrectPollingDate
	 */
	public Date getLastCorrectPollingDate() {
		return lastCorrectPollingDate;
	}

	/**
	 * @param lastCorrectPollingDate the lastCorrectPollingDate to set
	 */
	public void setLastCorrectPollingDate(Date lastCorrectPollingDate) {
		this.lastCorrectPollingDate = lastCorrectPollingDate;
	}

	/**
	 * @return the lastPollingDate
	 */
	public Date getLastPollingDate() {
		return lastPollingDate;
	}

	/**
	 * @param lastPollingDate the lastPollingDate to set
	 */
	public void setLastPollingDate(Date lastPollingDate) {
		this.lastPollingDate = lastPollingDate;
	}

	/**
	 * @return the isDuringPolling
	 */
	public boolean isDuringPolling() {
		return isDuringPolling;
	}

	/**
	 * @param isDuringPolling the isDuringPolling to set
	 */
	public void setDuringPolling(boolean isDuringPolling) {
		this.isDuringPolling = isDuringPolling;
	}

	/**
	 * @return the lastTemperatureValue
	 */
	public Integer getLastTemperatureValue() {
		return lastTemperatureValue;
	}

	/**
	 * @param lastTemperatureValue the lastTemperatureValue to set
	 */
	public void setLastTemperatureValue(Integer lastTemperatureValue) {
		this.lastTemperatureValue = lastTemperatureValue;
	}

	/**
	 * @return the communicationStatus
	 */
	public int getCommunicationStatus() {
		return communicationStatus;
	}

	/**
	 * @param communicationStatus the communicationStatus to set
	 */
	public void setCommunicationStatus(int communicationStatus) {
		this.communicationStatus = communicationStatus;
	}

	/**
	 * @return the informaticStatus
	 */
	public int getInformaticStatus() {
		return informaticStatus;
	}

	/**
	 * @param informaticStatus the informaticStatus to set
	 */
	public void setInformaticStatus(int informaticStatus) {
		this.informaticStatus = informaticStatus;
	}

	/**
	 * @return the stationStatus
	 */
	public int getStationStatus() {
		return stationStatus;
	}

	/**
	 * @param stationStatus the stationStatus to set
	 */
	public void setStationStatus(int stationStatus) {
		this.stationStatus = stationStatus;
	}

	/**
	 * @return the clockStatus
	 */
	public int getClockStatus() {
		return clockStatus;
	}

	/**
	 * @param clockStatus the clockStatus to set
	 */
	public void setClockStatus(int clockStatus) {
		this.clockStatus = clockStatus;
	}

	/**
	 * @return the doorStatus
	 */
	public int getDoorStatus() {
		return doorStatus;
	}

	/**
	 * @param doorStatus the doorStatus to set
	 */
	public void setDoorStatus(int doorStatus) {
		this.doorStatus = doorStatus;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void printObjToStdOut() {
		System.out.println("stationId:" + this.getStationId());
		System.out.println("shortName:" + this.getShortName());
		System.out.println("lastCorrectPollingDate:" + this.getLastCorrectPollingDate());
		System.out.println("lastPollingDate:" + this.getLastPollingDate());
		System.out.println("isDuringPolling:" + this.isDuringPolling());
		System.out.println("lastTemperatureValue:" + this.getLastTemperatureValue());
		System.out.println("communicationStatus:" + this.getCommunicationStatus());
		System.out.println("informaticStatus:" + this.getInformaticStatus());
		System.out.println("stationStatus:" + this.getStationStatus());
		System.out.println("clockStatus:" + this.getClockStatus());
		System.out.println("doorStatus:" + this.getDoorStatus());
		System.out.println("enabled:" + this.isEnabled());
	}

}
