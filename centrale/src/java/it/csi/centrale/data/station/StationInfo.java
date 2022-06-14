/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for storing anagraphic information about a station
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: StationInfo.java,v 1.38 2014/09/19 14:32:09 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import it.csi.centrale.polling.data.StationConfig;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for storing anagraphic information about a station
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationInfo implements Serializable {

	private static final long serialVersionUID = -789479339925600166L;

	private String shortStationName;

	// TODO: questo campo è un duplicato di stationUUID contenuto in Station,
	// sarebbe da eliminare però ci sono delle difficoltà dovute
	// all'architettura delle varie classi implicate.
	private String uuid;

	private Integer copId;

	private String copName;

	private String longStationName;

	private String address;

	private String city;

	private String province;

	private String location; // explain where station is

	private boolean enabled;

	private boolean gps;

	private String notes;

	private Integer map_x;

	private Integer map_y;

	private Date minTimestampForPolling;

	private String configUUID;

	private CommDeviceInfo commDeviceInfo;

	private boolean deleted;

	private boolean sampleDataDownloadEnable; // is true if when connect we

	// download also sample data for
	// current station.

	private Integer forcePollingTime; // is time between two polling for current

	// station if not null

	private Date deletionDate;

	private String configUserName;

	private Date configDate;

	private String configComment;

	private Date updateDate;

	private Date commonConfigStationUpdateDate; // common config date present on
	// remote station

	private boolean commonConfigSendingEnable;

	private Integer generic_map_x;

	private Integer generic_map_y;

	public StationInfo() {
		enabled = gps = false;
		deletionDate = null;
		forcePollingTime = null;
		commonConfigStationUpdateDate = null;
		commonConfigSendingEnable = true;
	}

	public StationInfo(StationConfig config) {
		this();
		update(config);
	}

	public void update(StationConfig config) {
		uuid = config.getStationId().toString();
		configUUID = config.getConfigId().toString();
		shortStationName = config.getShortName();
		longStationName = config.getName();
		province = config.getProvince();
		city = config.getCity();
		address = config.getAddress();
		location = config.getLocation();
		notes = config.getNotes();
		gps = config.isGpsInstalled();
		configDate = config.getCfgDate();
		configUserName = config.getCfgAuthor();
		configComment = config.getCfgComment();
	}

	public String getShortStationName() {
		return shortStationName;
	}

	public void setShortStationName(String shortStationName) {
		this.shortStationName = shortStationName;
	}

	public String getCopName() {
		return copName;
	}

	public void setCopName(String copName) {
		this.copName = copName;
	}

	public String getLongStationName() {
		return longStationName;
	}

	public void setLongStationName(String longStationName) {
		this.longStationName = longStationName;
	}

	public Integer getCopId() {
		return copId;
	}

	public void setCopId(Integer copId) {
		this.copId = copId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean hasGps() {
		return gps;
	}

	public void setGps(boolean gps) {
		this.gps = gps;
	}

	public Integer getMap_x() {
		return map_x;
	}

	public void setMap_x(Integer map_x) {
		this.map_x = map_x;
	}

	public Integer getMap_y() {
		return map_y;
	}

	public void setMap_y(Integer map_y) {
		this.map_y = map_y;
	}

	public Integer getGenericMap_x() {
		return generic_map_x;
	}

	public void setGenericMap_x(Integer genericMapX) {
		generic_map_x = genericMapX;
	}

	public Integer getGenericMap_y() {
		return generic_map_y;
	}

	public void setGenericMap_y(Integer genericMapY) {
		generic_map_y = genericMapY;
	}

	public String getConfigUUID() {
		return configUUID;
	}

	public void setConfigUUID(String configUUID) {
		this.configUUID = configUUID;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public CommDeviceInfo getCommDevice() {
		return commDeviceInfo;
	}

	public void setCommDevice(CommDeviceInfo commDeviceInfo) {
		this.commDeviceInfo = commDeviceInfo;
	}

	public Integer getForcePollingTime() {
		return forcePollingTime;
	}

	public void setForcePollingTime(Integer forcePollingTime) {
		this.forcePollingTime = forcePollingTime;
	}

	public Date getMinTimestampForPolling() {
		return minTimestampForPolling;
	}

	public void setMinTimestampForPolling(Date minTimestampForPolling) {
		this.minTimestampForPolling = minTimestampForPolling;
	}

	public String getConfigUserName() {
		return configUserName;
	}

	public void setConfigUserName(String configUserName) {
		this.configUserName = configUserName;
	}

	public Date getConfigDate() {
		return configDate;
	}

	public void setConfigDate(Date configDate) {
		this.configDate = configDate;
	}

	public String getConfigComment() {
		return configComment;
	}

	public void setConfigComment(String configComment) {
		this.configComment = configComment;
	}

	public boolean isSampleDataDownloadEnable() {
		return sampleDataDownloadEnable;
	}

	public void setSampleDataDownloadEnable(boolean sampleDataDownloadEnable) {
		this.sampleDataDownloadEnable = sampleDataDownloadEnable;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getCommonConfigStationUpdateDate() {
		return commonConfigStationUpdateDate;
	}

	public void setCommonConfigStationUpdateDate(
			Date commonConfigStationUpdateDate) {
		this.commonConfigStationUpdateDate = commonConfigStationUpdateDate;
	}

	public boolean isCommonConfigSendingEnable() {
		return commonConfigSendingEnable;
	}

	public void setCommonConfigSendingEnable(boolean commonConfigSendingEnable) {
		this.commonConfigSendingEnable = commonConfigSendingEnable;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

}
