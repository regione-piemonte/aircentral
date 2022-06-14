/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents average period.
* Change log:
*   2009-04-17: initial version
* ----------------------------------------------------------------------------
* $Id: AvgPeriodInfo.java,v 1.1 2009/04/20 07:44:55 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that represents average period.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class AvgPeriodInfo implements Serializable {

	private static final long serialVersionUID = -1506154284275189655L;

	private int avgPeriodId;
	private boolean defaultAvgPeriod;
	private Date updateDate;

	/**
	 * @return the avgPeriodId
	 */
	public int getAvgPeriodId() {
		return avgPeriodId;
	}

	/**
	 * @param avgPeriodId the avgPeriodId to set
	 */
	public void setAvgPeriodId(int avgPeriodId) {
		this.avgPeriodId = avgPeriodId;
	}

	/**
	 * @return the defaultAvgPeriod
	 */
	public boolean isDefaultAvgPeriod() {
		return defaultAvgPeriod;
	}

	/**
	 * @param defaultAvgPeriod the defaultAvgPeriod to set
	 */
	public void setDefaultAvgPeriod(boolean defaultAvgPeriod) {
		this.defaultAvgPeriod = defaultAvgPeriod;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
