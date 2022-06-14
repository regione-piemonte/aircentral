/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents other information.
* Change log:
*   2009-04-21: initial version
* ----------------------------------------------------------------------------
* $Id: OtherInfo.java,v 1.2 2009/04/22 11:17:04 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Class that represents other information.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class OtherInfo implements Serializable {

	private static final long serialVersionUID = -655249678263532510L;

	private Integer manualOperationsAutoResetPeriod;
	private Integer dataWriteToDiskPeriod;
	private String doorAlarmId;
	private Integer copServicePort;
	private String mapsSiteURLFormatter;

	/**
	 * @return the manualOperationsAutoResetPeriod
	 */
	public Integer getManualOperationsAutoResetPeriod() {
		return manualOperationsAutoResetPeriod;
	}

	/**
	 * @param manualOperationsAutoResetPeriod the manualOperationsAutoResetPeriod to
	 *                                        set
	 */
	public void setManualOperationsAutoResetPeriod(Integer manualOperationsAutoResetPeriod) {
		this.manualOperationsAutoResetPeriod = manualOperationsAutoResetPeriod;
	}

	/**
	 * @return the dataWriteToDiskPeriod
	 */
	public Integer getDataWriteToDiskPeriod() {
		return dataWriteToDiskPeriod;
	}

	/**
	 * @param dataWriteToDiskPeriod the dataWriteToDiskPeriod to set
	 */
	public void setDataWriteToDiskPeriod(Integer dataWriteToDiskPeriod) {
		this.dataWriteToDiskPeriod = dataWriteToDiskPeriod;
	}

	/**
	 * @return the doorAlarmId
	 */
	public String getDoorAlarmId() {
		return doorAlarmId;
	}

	/**
	 * @param doorAlarmId the doorAlarmId to set
	 */
	public void setDoorAlarmId(String doorAlarmId) {
		this.doorAlarmId = doorAlarmId;
	}

	/**
	 * @return the copServicePort
	 */
	public Integer getCopServicePort() {
		return copServicePort;
	}

	/**
	 * @param copServicePort the copServicePort to set
	 */
	public void setCopServicePort(Integer copServicePort) {
		this.copServicePort = copServicePort;
	}

	/**
	 * @return the mapsSiteURLFormatter
	 */
	public String getMapsSiteURLFormatter() {
		return mapsSiteURLFormatter;
	}

	/**
	 * @param mapsSiteURLFormatter the mapsSiteURLFormatter to set
	 */
	public void setMapsSiteURLFormatter(String mapsSiteURLFormatter) {
		this.mapsSiteURLFormatter = mapsSiteURLFormatter;
	}

}
