/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class of exception for device not found
// Change log:
//   2014-03-17: initial version
// ----------------------------------------------------------------------------
// $Id: DeviceNotFoundException.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm;

/**
 * Class of exception for device not found
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class DeviceNotFoundException extends CommManagerException {

	private static final long serialVersionUID = -1013375461467068850L;

	public DeviceNotFoundException(String message) {
		super(message);
	}

}
