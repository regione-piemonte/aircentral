/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manages exception of the application
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: CentraleException.java,v 1.3 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale;

import java.text.MessageFormat;
import java.util.MissingResourceException;

/**
 * Class for manages exception of the application
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class CentraleException extends Exception {

	private static final long serialVersionUID = -6292288720142066744L;

	private Object[] args;

	private String msg;

	private PropertyUtil propertyUtil;

	/**
	 * 
	 */
	public CentraleException() {
	}

	/**
	 * @param message
	 */
	public CentraleException(String message) {
		super(message);
		this.msg = message;
		try {
			propertyUtil = new PropertyUtil("it/csi/centrale/MessageBundleCore");
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param cause
	 */
	public CentraleException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CentraleException(String message, Throwable cause) {
		super(message, cause);
		this.msg = message;
		try {
			propertyUtil = new PropertyUtil("it/csi/centrale/MessageBundleCore");
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param msg
	 * @param args
	 */
	public CentraleException(String msg, Object... args) {
		this.msg = msg;
		this.args = args;
		try {
			propertyUtil = new PropertyUtil("it/csi/centrale/MessageBundleCore");
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toString() {
		if (msg == null)
			return super.toString();
		return MessageFormat.format(msg, args);
	}

	public String getMessage() {
		if (msg == null)
			return super.getMessage();
		return MessageFormat.format(msg, args);
	}

	public String getLocalizedMessage() {
		if (msg == null)
			return super.getLocalizedMessage();
		return MessageFormat.format(msg, args);
	}

	public String getLocalizedMessage(String locale) {
		if (msg == null)
			return getMessage();
		String msgBundle = null;
		try {
			msgBundle = propertyUtil.getProperty(locale, msg);
		} catch (MissingResourceException mrex) {
			return getMessage();
		}
		if (args == null)
			return msgBundle;
		Object[] translatedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			try {
				String resultString = propertyUtil.getProperty(locale,
						(String) args[i]);
				translatedArgs[i] = resultString;
			} catch (MissingResourceException mrex) {
				translatedArgs[i] = args[i];
			}
		}
		return MessageFormat.format(msgBundle, translatedArgs);
	}

}
