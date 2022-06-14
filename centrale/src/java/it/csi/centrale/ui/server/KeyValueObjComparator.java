/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file:   This class implements the comparator of elements of class
*  KeyValueObject.
* Change log:
*   2011-08-19: initial version
* ----------------------------------------------------------------------------
* $Id: KeyValueObjComparator.java,v 1.1 2011/08/19 15:55:29 vergnano Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.server;

import it.csi.centrale.ui.client.data.common.KeyValueObject;

import java.util.Comparator;

/**
 * This class implements the comparator of elements of class KeyValueObject.
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class KeyValueObjComparator implements Comparator<KeyValueObject> {

	@Override
	public int compare(KeyValueObject o1, KeyValueObject o2) {
		String keyValue1 = o1.getKey();
		if (o1.getValue() != null && !o1.getValue().trim().equals(""))
			keyValue1 = o1.getValue();
		String keyValue2 = o2.getKey();
		if (o2.getValue() != null && !o2.getValue().trim().equals(""))
			keyValue2 = o2.getValue();

		return (keyValue1.compareTo(keyValue2));
	}

}
