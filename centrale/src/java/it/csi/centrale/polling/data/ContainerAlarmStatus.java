/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for container alarm status
// Change log:
//   2014-04-14: initial version
// ----------------------------------------------------------------------------
// $Id: ContainerAlarmStatus.java,v 1.2 2014/09/24 15:48:23 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.UUID;
/**
 * Class for container alarm status
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public abstract class ContainerAlarmStatus {

	private UUID id;

	public UUID getId() {
		return id;
	}

	public ContainerAlarmStatus(UUID id) {
		this.id = id;
	}

	public abstract Object getStatus();

	public abstract boolean isActive();

	public static ContainerAlarmStatus valueOf(String value)
			throws IllegalArgumentException {
		if (value == null)
			throw new IllegalArgumentException("Null argument not allowed");
		String[] fields = value.split(",", 3);
		if (fields.length != 3)
			throw new IllegalArgumentException("3 fields expected, found "
					+ fields.length);
		UUID id = UUID.fromString(fields[0].trim());
		String type = fields[1].trim();
		String strStatus = fields[2].trim();
		if ("DIGITAL".equalsIgnoreCase(type)) {
			Boolean status;
			if (strStatus.isEmpty())
				status = null;
			else if ("1".equals(strStatus))
				status = true;
			else if ("0".equals(strStatus))
				status = false;
			else
				throw new IllegalArgumentException("Unparsable value for "
						+ type + " status field: '" + strStatus + "'");
			return new DigitalContainerAlarmStatus(id, status);
		} else if ("TRIGGER".equalsIgnoreCase(type)) {
			return new TriggerContainerAlarmStatus(id, strStatus);
		} else {
			throw new IllegalArgumentException("Unknown container alarm type: "
					+ type);
		}
	}

}
