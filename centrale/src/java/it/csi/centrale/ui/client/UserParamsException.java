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
// $Id: UserParamsException.java,v 1.4 2009/04/07 11:23:28 vespa Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client;

/**
 * Exception for user params
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class UserParamsException extends Exception {

	private static final long serialVersionUID = 770942724097137001L;

	public UserParamsException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UserParamsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UserParamsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserParamsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
