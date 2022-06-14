/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for utilities for manage window of graphic interface
 * of Periferico application
 * Change log:
 *   2008-10-27: initial version
 * ----------------------------------------------------------------------------
 * $Id: Utils.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.connperif.client;

/**
 * Class for utilities for manage window of graphic interface of Periferico
 * application
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class Utils {

	public static native void closeWindow() /*-{
	 $wnd.self.close();
	 }-*/;

	public static native void blockForPopup(String id) /*-{
	 $doc.getElementById(id).style.display='block';
	 
	 }-*/;

	public static native void unlockForPopup(String id) /*-{
	 $doc.getElementById(id).style.display='none';
	 
	 }-*/;

}// end class
