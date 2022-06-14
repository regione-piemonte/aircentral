/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: utility class for graphic interface
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: Utils.java,v 1.4 2009/04/07 11:23:28 vespa Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Utility class for graphic interface
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class Utils {

	public final static String ANALOG_INPUT = "ANALOG_INPUT";

	public final static String DIGITAL_INPUT = "DIGITAL_INPUT";

	public final static String DIGITAL_INPUT_OUTPUT = "DIGITAL_INPUT_OUTPUT";

	public final static String DIGITAL_OUTPUT = "DIGITAL_OUTPUT";

	public static final String API = "API";

	public static final String SAMPLE = "SAMPLE";

	public static final String AVG = "AVG";

	public static final String RAIN = "RAIN";

	public static final String WIND = "WIND";

	public static native void changeLocale(String locale) /*-{
	 $wnd.self.location.href = "CentraleUI.html?locale=" + locale;
	 }-*/;

	public static native void blockForPopup(String id) /*-{
	 $doc.getElementById(id).style.display='block';
	 
	 }-*/;

	public static native void unlockForPopup(String id) /*-{
	 $doc.getElementById(id).style.display='none';
	 
	 }-*/;

	public static FlexTable clearTable(FlexTable table) {
		int dim = table.getRowCount();
		for (int i = 0; i < dim; i++)
			table.removeRow(0);
		return table;
	} // end clearTable

	public static void operationOk() {
		Window.alert(CentraleUI.getMessages().operation_ok());
	}

}// end class
