/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class for digital container alarm status
// Change log:
//   2014-04-14: initial version
// ----------------------------------------------------------------------------
// $Id: DigitalContainerAlarmStatus.java,v 1.2 2014/09/24 15:48:23 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling.data;

import java.util.UUID;
/**
 * Class for digital container alarm status
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class DigitalContainerAlarmStatus extends ContainerAlarmStatus {

	private Boolean status;

	DigitalContainerAlarmStatus(UUID id, Boolean status) {
		super(id);
		this.status = status;
	}

	@Override
	public Boolean getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "DigitalContainerAlarmStatus [id=" + getId() + ", status="
				+ status + "]";
	}

	@Override
	public boolean isActive() {
		return Boolean.TRUE.equals(status);
	}

}
