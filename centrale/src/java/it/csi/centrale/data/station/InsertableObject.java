/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about an object which must
 * be written to Db
 * Change log:
 *   2009-01-13: initial version
 * ----------------------------------------------------------------------------
 * $Id: InsertableObject.java,v 1.11 2014/09/22 09:07:57 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

/**
 * Class for storing information about an object which must be written to Db
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class InsertableObject implements Serializable {

	private static final long serialVersionUID = -4426411317224158723L;

	private Integer[] key = new Integer[2];

	// element case
	// if MEAN first element is elementId second AvgPeriod Id
	// if SAMPLE first is element id second is null
	// if analyzerAlarm first element analyzerId second alarmId
	// if stationAlarm first element stationId second alarmId
	// if AllDataDownload first element is elementId second
	// avgPeriodId(avgPeriodVal)

	private LinkedList<Object> dataList = null;

	public InsertableObject() {
	}

	public InsertableObject(Integer[] key, Collection<? extends Object> c) {
		this.key = key;
		dataList = new LinkedList<Object>();
		dataList.addAll(c);
	}

	public InsertableObject(Integer[] key, Object object) {
		this.key = key;
		dataList = new LinkedList<Object>();
		dataList.add(object);
	}

	public Integer[] getKey() {
		return key;
	}

	public void setKey(Integer[] key) {
		this.key = key;
	}

	public LinkedList<Object> getDataList() {
		return dataList;
	}

	public void setDataList(LinkedList<Object> dataList) {
		this.dataList = dataList;
	}

	public InsertableObject makeDbAriaLoaderTrigger() {
		return new InsertableObject(key, new Date());
	}

	@Override
	public String toString() {
		return "InsertableObject [key=" + Arrays.toString(key) + ", dataSize="
				+ (dataList == null ? null : dataList.size()) + "]";
	}

}
