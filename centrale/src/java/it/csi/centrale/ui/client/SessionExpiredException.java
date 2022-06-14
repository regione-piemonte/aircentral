/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: exception for session expired
// Change log:
//   2009-02-24: initial version
// ----------------------------------------------------------------------------
// $Id: SessionExpiredException.java,v 1.4 2009/04/07 11:23:28 vespa Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client;

/**
 * Exception for session expired
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class SessionExpiredException extends Exception {

	private static final long serialVersionUID = 3326171908773689416L;

	public SessionExpiredException() {
	}

	/**
	 * @param message
	 */
	public SessionExpiredException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SessionExpiredException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SessionExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

}
