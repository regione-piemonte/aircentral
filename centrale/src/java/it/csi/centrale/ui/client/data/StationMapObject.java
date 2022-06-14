/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: object with information about a station to be showed on the
* map
* Change log:
*   2008-09-25: initial version
* ----------------------------------------------------------------------------
* $Id: StationMapObject.java,v 1.8 2014/02/14 10:37:21 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Object with information about a station to be showed on the map
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationMapObject implements Serializable {

	private static final long serialVersionUID = 7086325406874182200L;

	private int stationId;
	private String shortName;
	private String longName;
	private boolean enabled;
	private Integer map_x;
	private Integer map_y;
	private String stationUuid;
	private String ip;
	private Integer virtualCopId;
	private Boolean moved = null;

	public StationMapObject() {
		this.setMoved(null);
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
	 * @return the longName
	 */
	public String getLongName() {
		return longName;
	}

	/**
	 * @param longName the longName to set
	 */
	public void setLongName(String longName) {
		this.longName = longName;
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
	 * @return the map_x
	 */
	public Integer getMap_x() {
		return map_x;
	}

	/**
	 * @param map_x the map_x to set
	 */
	public void setMap_x(Integer map_x) {
		this.map_x = map_x;
	}

	/**
	 * @return the map_y
	 */
	public Integer getMap_y() {
		return map_y;
	}

	/**
	 * @param map_y the map_y to set
	 */
	public void setMap_y(Integer map_y) {
		this.map_y = map_y;
	}

	/**
	 * @return the stationUuid
	 */
	public String getStationUuid() {
		return stationUuid;
	}

	/**
	 * @param stationUuid the stationUuid to set
	 */
	public void setStationUuid(String stationUuid) {
		this.stationUuid = stationUuid;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the virtualCopId
	 */
	public Integer getVirtualCopId() {
		return virtualCopId;
	}

	/**
	 * @param virtualCopId the virtualCopId to set
	 */
	public void setVirtualCopId(Integer virtualCopId) {
		this.virtualCopId = virtualCopId;
	}

	/**
	 * @return the moved
	 */
	public Boolean getMoved() {
		return moved;
	}

	/**
	 * @param moved the moved to set
	 */
	public void setMoved(Boolean moved) {
		this.moved = moved;
	};

}
