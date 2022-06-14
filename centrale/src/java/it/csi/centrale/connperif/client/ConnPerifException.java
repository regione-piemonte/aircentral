/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: exception for user params
// Change log:
//   2009-02-24: initial version
// ----------------------------------------------------------------------------
// $Id: ConnPerifException.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.connperif.client;

/**
 * Exception for Connperif UI
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class ConnPerifException extends Exception {

	private static final long serialVersionUID = 770942724097137001L;

	public ConnPerifException() {
	}

	/**
	 * @param message
	 */
	public ConnPerifException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConnPerifException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConnPerifException(String message, Throwable cause) {
		super(message, cause);
	}

}
