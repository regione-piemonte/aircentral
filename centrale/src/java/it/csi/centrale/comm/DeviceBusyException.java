/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Device exception
// Change log:
//   2014-03-17: initial version
// ----------------------------------------------------------------------------
// $Id: DeviceBusyException.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm;

/**
 * Device exception
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class DeviceBusyException extends CommManagerException {

	private static final long serialVersionUID = -6971475134796679930L;

	public DeviceBusyException(String message) {
		super(message);
	}

}
