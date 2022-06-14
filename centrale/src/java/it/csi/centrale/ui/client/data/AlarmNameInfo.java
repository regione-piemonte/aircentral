/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents alam name.
* Change log:
*   2009-04-15: initial version
* ----------------------------------------------------------------------------
* $Id: AlarmNameInfo.java,v 1.1 2009/04/15 11:16:04 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that represents alarm name.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class AlarmNameInfo implements Serializable {

	private static final long serialVersionUID = -1177052744974864857L;
	private String alarmId;
	private String alarmName;
	private String type;
	private boolean dataQualityRelevant;
	private Date updateDate;

	/**
	 * @return the alarmId
	 */
	public String getAlarmId() {
		return alarmId;
	}

	/**
	 * @param alarmId the alarmId to set
	 */
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	/**
	 * @return the alarmName
	 */
	public String getAlarmName() {
		return alarmName;
	}

	/**
	 * @param alarmName the alarmName to set
	 */
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the dataQualityRelevant
	 */
	public boolean isDataQualityRelevant() {
		return dataQualityRelevant;
	}

	/**
	 * @param dataQualityRelevant the dataQualityRelevant to set
	 */
	public void setDataQualityRelevant(boolean dataQualityRelevant) {
		this.dataQualityRelevant = dataQualityRelevant;
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

}// end class
