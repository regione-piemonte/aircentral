/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Isabella Vespa
// Purpose of file: provides FatalException for fatal error.
// Change log:
//   2009-03-19: initial version
// ----------------------------------------------------------------------------
// $Id: FatalException.java,v 1.6 2009/05/05 10:02:10 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client;

/**
 * Provides FatalException for fatal error.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class FatalException extends Exception {

	private static final long serialVersionUID = 770942724097137001L;

	public FatalException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public FatalException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FatalException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FatalException(String message, Throwable cause) {
		super(message, cause);
	}

}
