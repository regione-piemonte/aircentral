/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: 
// Change log:
//   2014-03-17: initial version
// ----------------------------------------------------------------------------
// $Id: LineBasedDevice.java,v 1.2 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm.device;

import it.csi.centrale.comm.CommDevice;

public abstract class LineBasedDevice extends CommDevice {

	private String lineName;

	public LineBasedDevice(String name, String lineName) {
		super(name);
		setLineName(lineName);
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		if (lineName == null || (lineName = lineName.trim()).isEmpty())
			this.lineName = null;
		else
			this.lineName = lineName;
	}

}
