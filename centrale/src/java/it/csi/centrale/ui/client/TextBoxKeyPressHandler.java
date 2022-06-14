/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: listener for verifying text inserted in TextBox
* Change log:
*   2008-01-10: initial version
* ----------------------------------------------------------------------------
* $Id: TextBoxKeyPressHandler.java,v 1.2 2015/10/15 12:10:23 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Listener for verifying text inserted in TextBox
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class TextBoxKeyPressHandler implements KeyPressHandler {

	boolean onlyPositive;

	char separatorAllowed = '.';

	public TextBoxKeyPressHandler(boolean onlyPositive) {
		this.onlyPositive = onlyPositive;
	}

	public TextBoxKeyPressHandler(boolean onlyPositive, char separatorAllowed) {
		this.onlyPositive = onlyPositive;
		this.separatorAllowed = separatorAllowed;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		// Let's disallow non-numeric entry in the normal text box.
		// Allow number and separatorAllowed

		// ATTENZIONE: KEY_DELETE per gwt ha lo stesso keyCode di .
		// quindi se il separatore permesso e' . si puo' usare da tastiera il
		// tasto canc. altrimenti e' vietato
		char keyCode = event.getCharCode();
		if ((!Character.isDigit(keyCode)) && (keyCode != (char) KeyCodes.KEY_TAB)
				&& (keyCode != (char) KeyCodes.KEY_BACKSPACE) && (keyCode != (char) KeyCodes.KEY_ENTER)
				&& (keyCode != (char) KeyCodes.KEY_LEFT) && (keyCode != (char) KeyCodes.KEY_RIGHT)
				&& (keyCode != (char) KeyCodes.KEY_HOME) && (keyCode != (char) KeyCodes.KEY_END)
				&& (keyCode != separatorAllowed) && (keyCode != '-')) {
			((TextBox) event.getSource()).cancelKey();
		}
		if (onlyPositive && (keyCode == '-'))
			((TextBox) event.getSource()).cancelKey();

	}

}
