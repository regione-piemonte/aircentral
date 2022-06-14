/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file:  Exception for communication protocol
// Change log:
//   2014-04-10: initial version
// ----------------------------------------------------------------------------
// $Id: ProtocolException.java,v 1.1 2014/04/22 09:46:13 pfvallosio Exp $
// ----------------------------------------------------------------------------
package it.csi.centrale.polling;
/**
 * Exception for communication protocol
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class ProtocolException extends Exception {

	public enum Failure {
		BAD_REQUEST("bad request"), //
		BAD_FUNCTION_PARAMS("bad function params"), //
		FUNCTION_EXEC_FAILURE("function execution failed"), //
		NO_REPLY("no replay"), //
		TRUNCATED_REPLAY("truncated reply"), //
		MALFORMED_REPLAY("malformed reply");

		private String name;

		private Failure(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	};

	private static final long serialVersionUID = 5768748957966944304L;
	private Failure failure;

	public ProtocolException(String request, Failure failure) {
		this(request, failure, null, null);
	}

	public ProtocolException(String request, Failure failure, String message) {
		this(request, failure, message, null);
	}

	public ProtocolException(String request, Failure failure, Throwable cause) {
		this(request, failure, null, cause);
	}

	public ProtocolException(String request, Failure failure, String message,
			Throwable cause) {
		super("Request '" + request + "' failed: " + failure
				+ (message == null ? "" : ". " + message), cause);
		this.failure = failure;
	}

	public Failure getFailure() {
		return failure;
	}

}
