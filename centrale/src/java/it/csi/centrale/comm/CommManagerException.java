/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class of exception for communication
// Change log:
//   2014-03-17: initial version
// ----------------------------------------------------------------------------
// $Id: CommManagerException.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.comm;

/**
 * Class of exception for communication
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class CommManagerException extends Exception {

	private static final long serialVersionUID = 583838448894600299L;

	public CommManagerException() {
	}

	public CommManagerException(String message) {
		super(message);
	}

	public CommManagerException(Throwable cause) {
		super(cause);
	}

	public CommManagerException(String message, Throwable cause) {
		super(message, cause);
	}

}
