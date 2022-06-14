/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class of exception for incompatible device
// Change log:
//   2014-03-17: initial version
// ----------------------------------------------------------------------------
// $Id: IncompatibleDeviceException.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm;

/**
 * Class of exception for incompatible device
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class IncompatibleDeviceException extends CommManagerException {

	private static final long serialVersionUID = -7785062977979700975L;

	public IncompatibleDeviceException(String message) {
		super(message);
	}

}
