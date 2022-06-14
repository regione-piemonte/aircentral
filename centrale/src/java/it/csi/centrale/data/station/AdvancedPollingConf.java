/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about polling configuration
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: AdvancedPollingConf.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.Serializable;

/**
 * Class for storing information about polling configuration
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class AdvancedPollingConf implements Serializable {

	private static final long serialVersionUID = 5300337450021638261L;

	private int pollingOfficeTime; // cadency in minute

	private Integer pollingExtraOffice; // cadency in minute

	private boolean usePollingExtra;

	private boolean closeSat;

	private boolean closeSun;

	private Integer openAt;

	private Integer closeAt;

	public AdvancedPollingConf() {
		usePollingExtra = false;
		closeSat = false;
		closeSun = false;
	}

	public int getPollingOfficeTime() {
		return pollingOfficeTime;
	}

	public void setPollingOfficeTime(int pollingOfficeTime) {
		this.pollingOfficeTime = pollingOfficeTime;
	}

	public Integer getPollingExtraOffice() {
		return pollingExtraOffice;
	}

	public void setPollingExtraOffice(Integer pollingExtraOffice) {
		this.pollingExtraOffice = pollingExtraOffice;
	}

	public boolean isUsePollingExtra() {
		return usePollingExtra;
	}

	public void setUsePollingExtra(boolean usePollingExtra) {
		this.usePollingExtra = usePollingExtra;
	}

	public boolean isCloseSat() {
		return closeSat;
	}

	public void setCloseSat(boolean closeSat) {
		this.closeSat = closeSat;
	}

	public boolean isCloseSun() {
		return closeSun;
	}

	public void setCloseSun(boolean closeSun) {
		this.closeSun = closeSun;
	}

	public Integer getOpenAt() {
		return openAt;
	}

	public void setOpenAt(Integer openAt) {
		this.openAt = openAt;
	}

	public Integer getCloseAt() {
		return closeAt;
	}

	public void setCloseAt(Integer closeAt) {
		this.closeAt = closeAt;
	}

}
