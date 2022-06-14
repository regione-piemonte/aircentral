/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for manage messages to client for connection with
 * Periferico application
 * Change log:
 *   2008-10-27: initial version
 * ----------------------------------------------------------------------------
 * $Id: MessageBundleClient.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.connperif.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Class for manage messages to client for connection with Periferico
 * application
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public interface MessageBundleClient extends Messages {

	String locale();

	String timeout();

	String connected();

	String loading();

	String close();

	String ok();

}
