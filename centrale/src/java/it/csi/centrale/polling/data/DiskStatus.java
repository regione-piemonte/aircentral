/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for disk status
// Change log:
//   2014-04-11: initial version
// ----------------------------------------------------------------------------
// $Id: DiskStatus.java,v 1.2 2014/09/03 13:08:52 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.HashMap;
import java.util.Map;
/**
 * Class for disk status
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class DiskStatus {

	public enum Status {
		UNAVAILABLE, OK, WARNING, ERROR
	}

	private Status raidStatus;
	private Status smartStatus;
	private int spaceWarnThreshold;
	private int spaceAlarmThreshold;
	private Integer rootFsUsedSpacePercent;
	private Integer tmpFsUsedSpacePercent;
	private Integer dataFsUsedSpacePercent;

	public DiskStatus(Status raidStatus, Status smartStatus,
			int spaceWarnThreshold, int spaceAlarmThreshold,
			Integer rootFsUsedSpacePercent, Integer tmpFsUsedSpacePercent,
			Integer dataFsUsedSpacePercent) {
		this.raidStatus = raidStatus;
		this.smartStatus = smartStatus;
		this.spaceWarnThreshold = spaceWarnThreshold;
		this.spaceAlarmThreshold = spaceAlarmThreshold;
		this.rootFsUsedSpacePercent = rootFsUsedSpacePercent;
		this.tmpFsUsedSpacePercent = tmpFsUsedSpacePercent;
		this.dataFsUsedSpacePercent = dataFsUsedSpacePercent;
	}

	public DiskStatus(Map<String, String> mapParams, Map<String, Integer> mapFS)
			throws IllegalArgumentException {
		smartStatus = parseStatus(mapParams.get("SMART"));
		raidStatus = parseStatus(mapParams.get("RAID"));
		spaceWarnThreshold = Integer.parseInt(mapParams.get("warn"));
		spaceAlarmThreshold = Integer.parseInt(mapParams.get("alarm"));
		rootFsUsedSpacePercent = mapFS.get("root");
		tmpFsUsedSpacePercent = mapFS.get("temp");
		dataFsUsedSpacePercent = mapFS.get("data");
	}

	private Status parseStatus(String value) {
		if (value == null || "NULL".equalsIgnoreCase(value))
			return Status.UNAVAILABLE;
		return Status.valueOf(value.toUpperCase());
	}

	public Status getRaidStatus() {
		return raidStatus;
	}

	public Status getSmartStatus() {
		return smartStatus;
	}

	public int getSpaceWarnThreshold() {
		return spaceWarnThreshold;
	}

	public int getSpaceAlarmThreshold() {
		return spaceAlarmThreshold;
	}

	public Integer getRootFsUsedSpacePercent() {
		return rootFsUsedSpacePercent;
	}

	public Integer getTmpFsUsedSpacePercent() {
		return tmpFsUsedSpacePercent;
	}

	public Integer getDataFsUsedSpacePercent() {
		return dataFsUsedSpacePercent;
	}

	public HashMap<String, Integer> getFsSpaceMap() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("root", rootFsUsedSpacePercent);
		map.put("temp", tmpFsUsedSpacePercent);
		map.put("data", dataFsUsedSpacePercent);
		return map;
	}

	@Override
	public String toString() {
		return "DiskStatus [raidStatus=" + raidStatus + ", smartStatus="
				+ smartStatus + ", spaceWarnThreshold=" + spaceWarnThreshold
				+ ", spaceAlarmThreshold=" + spaceAlarmThreshold
				+ ", rootFsUsedSpacePercent=" + rootFsUsedSpacePercent
				+ ", tmpFsUsedSpacePercent=" + tmpFsUsedSpacePercent
				+ ", dataFsUsedSpacePercent=" + dataFsUsedSpacePercent + "]";
	}

	public Status getGlobalStatus() {
		int status = 0x00;
		for (Status s : new Status[] { raidStatus, smartStatus }) {
			if (s == Status.OK)
				status |= 0x01;
			else if (s == Status.WARNING)
				status |= 0x02;
			else if (s == Status.ERROR)
				status |= 0x04;
		}
		for (Integer usage : new Integer[] { rootFsUsedSpacePercent,
				tmpFsUsedSpacePercent, dataFsUsedSpacePercent }) {
			if (usage == null)
				continue;
			if (usage <= spaceWarnThreshold)
				status |= 0x01;
			else if (usage <= spaceAlarmThreshold)
				status |= 0x02;
			else
				status |= 0x04;
		}
		if ((status & 0x04) != 0)
			return Status.ERROR;
		if ((status & 0x02) != 0)
			return Status.WARNING;
		if ((status & 0x01) != 0)
			return Status.OK;
		return Status.UNAVAILABLE;
	}

}
