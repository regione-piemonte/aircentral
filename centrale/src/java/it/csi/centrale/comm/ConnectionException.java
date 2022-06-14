/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: Class of exception for connection
// Change log:
//   2014-03-30: initial version
// ----------------------------------------------------------------------------
// $Id: ConnectionException.java,v 1.3 2014/10/10 15:57:35 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.comm;

/**
 * Class of exception for connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class ConnectionException extends CommManagerException {

	private Integer code = null;
	private String description = null;

	public enum Failure {
		LOCAL_DEVICE, REMOTE_DEVICE, REMOTE_HOST;

		@Override
		public String toString() {
			switch (this) {
			case LOCAL_DEVICE:
				return "Local device not responding";
			case REMOTE_DEVICE:
				return "Remote device not responding";
			case REMOTE_HOST:
				return "Remote host not responding";
			default:
				return "Unknown error";
			}
		}
	}

	private static final long serialVersionUID = 8310418903764715986L;
	private Failure failure;

	public ConnectionException(Failure failure) {
		super(failure.toString());
		this.failure = failure;
	}

	public ConnectionException(Failure failure, Throwable cause) {
		super(failure.toString(), cause);
		this.failure = failure;
	}

	public ConnectionException(Failure failure, Integer code, String description) {
		super(failure.toString() + (code != null ? ", code " + code : "")
				+ (description != null ? ", description '" + description + "'" : ""));
		this.failure = failure;
	}

	public Failure getFailure() {
		return failure;
	}

	public Integer getFailureCode() {
		return code;
	}

	public String getFailureDescription() {
		return description;
	}

}
