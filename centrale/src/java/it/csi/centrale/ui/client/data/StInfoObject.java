/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: stores informations about the station to be configured
* Change log:
*   2008-10-07: initial version
* ----------------------------------------------------------------------------
* $Id: StInfoObject.java,v 1.9 2014/09/19 08:41:11 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Stores informations about the station to be configured
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StInfoObject implements Serializable {

	private static final long serialVersionUID = 4096447021010709745L;

	private int stationId;
	private String uuid;
	private String shortName;
	private String name;
	private String address;
	private String city;
	private String province;
	private String location; // explain where station is
	private boolean enabled;
	private boolean useGps;
	private String notes;
	private String configUUID;
	private String ipAddress;
	private Integer ipPort;
	private boolean useModem;
	private String phoneNumber;
	private String routerIpAddress;
	private boolean deleted;
	private Integer forcePollingTime;
	private Date minTimestampForPolling;
	private boolean downloadSampleDataEnabled;
	private boolean lan;
	private boolean proxy;
	private VirtualCopInfo virtualCopInfo;

	public StInfoObject() {
		setVirtualCopInfo(new VirtualCopInfo());
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
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
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

	/**
	 * @return the useGps
	 */
	public boolean isUseGps() {
		return useGps;
	}

	/**
	 * @param useGps the useGps to set
	 */
	public void setUseGps(boolean useGps) {
		this.useGps = useGps;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the configUUID
	 */
	public String getConfigUUID() {
		return configUUID;
	}

	/**
	 * @param configUUID the configUUID to set
	 */
	public void setConfigUUID(String configUUID) {
		this.configUUID = configUUID;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the ipPort
	 */
	public Integer getIpPort() {
		return ipPort;
	}

	/**
	 * @param ipPort the ipPort to set
	 */
	public void setIpPort(Integer ipPort) {
		this.ipPort = ipPort;
	}

	/**
	 * @return the useModem
	 */
	public boolean isUseModem() {
		return useModem;
	}

	/**
	 * @param useModem the useModem to set
	 */
	public void setUseModem(boolean useModem) {
		this.useModem = useModem;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the routerIpAddress
	 */
	public String getRouterIpAddress() {
		return routerIpAddress;
	}

	/**
	 * @param routerIpAddress the routerIpAddress to set
	 */
	public void setRouterIpAddress(String routerIpAddress) {
		this.routerIpAddress = routerIpAddress;
	}

	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the forcePollingTime
	 */
	public Integer getForcePollingTime() {
		return forcePollingTime;
	}

	/**
	 * @param forcePollingTime the forcePollingTime to set
	 */
	public void setForcePollingTime(Integer forcePollingTime) {
		this.forcePollingTime = forcePollingTime;
	}

	/**
	 * @return the minTimestampForPolling
	 */
	public Date getMinTimestampForPolling() {
		return minTimestampForPolling;
	}

	/**
	 * @param minTimestampForPolling the minTimestampForPolling to set
	 */
	public void setMinTimestampForPolling(Date minTimestampForPolling) {
		this.minTimestampForPolling = minTimestampForPolling;
	}

	/**
	 * @return the downloadSampleDataEnabled
	 */
	public boolean isDownloadSampleDataEnabled() {
		return downloadSampleDataEnabled;
	}

	/**
	 * @param downloadSampleDataEnabled the downloadSampleDataEnabled to set
	 */
	public void setDownloadSampleDataEnabled(boolean downloadSampleDataEnabled) {
		this.downloadSampleDataEnabled = downloadSampleDataEnabled;
	}

	/**
	 * @return the lan
	 */
	public boolean isLan() {
		return lan;
	}

	/**
	 * @param lan the lan to set
	 */
	public void setLan(boolean lan) {
		this.lan = lan;
	}

	/**
	 * @return the proxy
	 */
	public boolean isProxy() {
		return proxy;
	}

	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	/**
	 * @return the virtualCopInfo
	 */
	public VirtualCopInfo getVirtualCopInfo() {
		return virtualCopInfo;
	}

	/**
	 * @param virtualCopInfo the virtualCopInfo to set
	 */
	public void setVirtualCopInfo(VirtualCopInfo virtualCopInfo) {
		this.virtualCopInfo = virtualCopInfo;
	}

}
