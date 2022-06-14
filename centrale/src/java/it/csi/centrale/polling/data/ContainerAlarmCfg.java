/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for container alarm configuration
// Change log:
//   2014-04-15: initial version
// ----------------------------------------------------------------------------
// $Id: ContainerAlarmCfg.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.Map;
import java.util.UUID;
/**
 * Class for container alarm configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class ContainerAlarmCfg {

	private UUID id;
	private String alarmNameId;
	private String notes;

	public ContainerAlarmCfg(UUID id, Map<String, String> attributes)
			throws IllegalArgumentException {
		this.id = id;
		this.alarmNameId = attributes.get("alarmId");
		this.notes = attributes.get("notes");
		if (alarmNameId == null || alarmNameId.isEmpty())
			throw new IllegalArgumentException(
					"Missing or empty attribute 'Alarm name id'");
	}

	public UUID getId() {
		return id;
	}

	public String getAlarmNameId() {
		return alarmNameId;
	}

	public String getNotes() {
		return notes;
	}

	@Override
	public String toString() {
		return "ContainerAlarmCfg [id=" + id + ", alarmNameId=" + alarmNameId
				+ ", notes=" + notes + "]";
	}

}
