/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents storage manager.
* Change log:
*   2009-04-21: initial version
* ----------------------------------------------------------------------------
* $Id: StorageManagerInfo.java,v 1.1 2009/04/21 09:49:34 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;

/**
 * Class that represents storage manager.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class StorageManagerInfo implements Serializable {

	private static final long serialVersionUID = -2280398898809881241L;

	private Integer maxDaysOfData;
	private Integer maxDaysOfAggregateData;
	private Integer diskFullWarningThresholdPercent;
	private Integer diskFullAlarmThresholdPercent;

	/**
	 * @return the maxDaysOfData
	 */
	public Integer getMaxDaysOfData() {
		return maxDaysOfData;
	}

	/**
	 * @param maxDaysOfData the maxDaysOfData to set
	 */

	public void setMaxDaysOfData(Integer maxDaysOfData) {
		this.maxDaysOfData = maxDaysOfData;
	}

	/**
	 * @return the maxDaysOfAggregateData
	 */

	public Integer getMaxDaysOfAggregateData() {
		return maxDaysOfAggregateData;
	}

	/**
	 * @param maxDaysOfAggregateData the maxDaysOfAggregateData to set
	 */

	public void setMaxDaysOfAggregateData(Integer maxDaysOfAggregateData) {
		this.maxDaysOfAggregateData = maxDaysOfAggregateData;
	}

	/**
	 * @return the diskFullWarningThresholdPercent
	 */

	public Integer getDiskFullWarningThresholdPercent() {
		return diskFullWarningThresholdPercent;
	}

	/**
	 * @param diskFullWarningThresholdPercent the diskFullWarningThresholdPercent to
	 *                                        set
	 */

	public void setDiskFullWarningThresholdPercent(Integer diskFullWarningThresholdPercent) {
		this.diskFullWarningThresholdPercent = diskFullWarningThresholdPercent;
	}

	/**
	 * @return the diskFullAlarmThresholdPercent
	 */

	public Integer getDiskFullAlarmThresholdPercent() {
		return diskFullAlarmThresholdPercent;
	}

	/**
	 * @param diskFullAlarmThresholdPercent the diskFullAlarmThresholdPercent to set
	 */

	public void setDiskFullAlarmThresholdPercent(Integer diskFullAlarmThresholdPercent) {
		this.diskFullAlarmThresholdPercent = diskFullAlarmThresholdPercent;
	}

}
