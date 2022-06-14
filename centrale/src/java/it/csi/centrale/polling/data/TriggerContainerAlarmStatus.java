/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for trigger of container alarm status
// Change log:
//   2014-04-14: initial version
// ----------------------------------------------------------------------------
// $Id: TriggerContainerAlarmStatus.java,v 1.2 2014/09/24 15:48:23 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.UUID;

/**
 * Class for trigger of container alarm status
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class TriggerContainerAlarmStatus extends ContainerAlarmStatus {

	public enum Status {
		OK, ALARM, WARNING, ALARM_HIGH, ALARM_LOW, WARNING_HIGH, WARNING_LOW
	}

	private Status status;

	TriggerContainerAlarmStatus(UUID id, Status status) {
		super(id);
		this.status = status;
	}

	TriggerContainerAlarmStatus(UUID id, String statusAsString) throws IllegalArgumentException {
		this(id, statusFromString(statusAsString));
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "TriggerContainerAlarmStatus [id=" + getId() + ", status=" + status + "]";
	}

	public static Status statusFromString(String value) throws IllegalArgumentException {
		if (value == null)
			return null;
		value = value.trim();
		if (value.isEmpty())
			return null;
		if ("OK".equalsIgnoreCase(value))
			return Status.OK;
		if ("A".equalsIgnoreCase(value))
			return Status.ALARM;
		if ("AH".equalsIgnoreCase(value))
			return Status.ALARM_HIGH;
		if ("AL".equalsIgnoreCase(value))
			return Status.ALARM_LOW;
		if ("W".equalsIgnoreCase(value))
			return Status.WARNING;
		if ("WH".equalsIgnoreCase(value))
			return Status.WARNING_HIGH;
		if ("WL".equalsIgnoreCase(value))
			return Status.WARNING_LOW;
		throw new IllegalArgumentException("Unparsable value for " + "trigger status field: '" + value + "'");
	}

	@Override
	public boolean isActive() {
		return status == Status.ALARM || status == Status.ALARM_HIGH || status == Status.ALARM_LOW;
	}

}
