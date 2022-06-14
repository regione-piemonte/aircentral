/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Exception for polling
// Change log:
//   2014-06-04: initial version
// ----------------------------------------------------------------------------
// $Id: PollingException.java,v 1.1 2014/06/26 07:46:51 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;
/**
 * Exception for polling
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class PollingException extends Exception {

	private static final long serialVersionUID = 7726836727576154661L;

	public PollingException() {
	}

	public PollingException(String message) {
		super(message);
	}

	public PollingException(Throwable cause) {
		super(cause);
	}

	public PollingException(String message, Throwable cause) {
		super(message, cause);
	}

}
