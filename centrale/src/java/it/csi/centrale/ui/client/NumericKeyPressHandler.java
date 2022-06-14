/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Pierfrancesco Vallosio
* Purpose of file: key handler for numeric text boxes
* Change log:
*   2011-11-22: initial version
* ----------------------------------------------------------------------------
* $Id: NumericKeyPressHandler.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * key handler for numeric text boxes
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class NumericKeyPressHandler implements KeyPressHandler {

	private boolean onlyPositive;
	private boolean onlyInteger;
	private char separator = '.';

	public NumericKeyPressHandler(boolean onlyPositive) {
		this(onlyPositive, false);
	}

	public NumericKeyPressHandler(boolean onlyPositive, boolean onlyInteger) {
		this.onlyPositive = onlyPositive;
		this.onlyInteger = onlyInteger;
	}

	public NumericKeyPressHandler(boolean onlyPositive, char separator) {
		this(onlyPositive, false);
		this.separator = separator;
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		int nativeCode = event.getNativeEvent().getKeyCode();
		if (nativeCode == KeyCodes.KEY_TAB || nativeCode == KeyCodes.KEY_BACKSPACE || nativeCode == KeyCodes.KEY_DELETE
				|| nativeCode == KeyCodes.KEY_ENTER || nativeCode == KeyCodes.KEY_LEFT
				|| nativeCode == KeyCodes.KEY_RIGHT || nativeCode == KeyCodes.KEY_HOME
				|| nativeCode == KeyCodes.KEY_END)
			return;
		char keyCode = event.getCharCode();
		boolean acceptChar = Character.isDigit(keyCode) || (keyCode == separator && !onlyInteger)
				|| (keyCode == '-' && !onlyPositive);
		if (!acceptChar)
			((TextBox) event.getSource()).cancelKey();
	}

}
