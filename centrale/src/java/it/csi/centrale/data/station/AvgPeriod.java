/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a period of average 
 * of an element
 * Change log:
 *   2008-11-24: initial version
 * ----------------------------------------------------------------------------
 * $Id: AvgPeriod.java,v 1.16 2014/10/15 16:15:27 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Class for storing information about a period of average of an element
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class AvgPeriod implements DataNode, Serializable {

	private static final long serialVersionUID = 2303334956505340867L;

	private int avgPeriodVal;

	private Date deletionDate;

	private Date updateDate;

	private boolean allDataDownloaded;

	private DataNode parent;

	static Logger logger = Logger.getLogger("centrale."
			+ AvgPeriod.class.getSimpleName());

	public AvgPeriod(int value, DataNode parent) {
		avgPeriodVal = value;
		this.parent = parent;
		allDataDownloaded = false;
		update();
	}

	public int getAvgPeriodVal() {
		return avgPeriodVal;
	}

	public Date getDeletionDate() {
		return deletionDate;
	}

	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}

	public boolean isAllDataDownloaded() {
		return allDataDownloaded;
	}

	public void setAllDataDownloaded(boolean allDataDownloaded) {
		this.allDataDownloaded = allDataDownloaded;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void update() {
		if (parent.isDeleted()) {
			setDeleted();
		} else {
			deletionDate = null;
			if (isActive())
				allDataDownloaded = false;
		}
	}

	public void setDeleted() {
		if (isDeleted())
			return;
		deletionDate = new Date();
	}

	@Override
	public boolean isDeleted() {
		return deletionDate != null;
	}

	@Override
	public boolean isActive() {
		return !isDeleted() && parent.isActive();
	}

	public boolean onDownloadCompleted() {
		if (isActive())
			return false;
		if (allDataDownloaded)
			return false;
		allDataDownloaded = true;
		return true;
	}

}
