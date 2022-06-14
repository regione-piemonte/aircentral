/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.polling;
/**
 * Exception for no replay
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class NoReplyException extends Exception {

	private static final long serialVersionUID = 1352594954784675726L;

	public NoReplyException() {
	}

	public NoReplyException(String message) {
		super(message);
	}

	public NoReplyException(Throwable cause) {
		super(cause);
	}

	public NoReplyException(String message, Throwable cause) {
		super(message, cause);
	}

}
