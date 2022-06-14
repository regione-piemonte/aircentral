/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file:   This class implements Comparator of elements of class 
* StationMapObject.
* Change log:
*   2011-07-22: initial version
* ----------------------------------------------------------------------------
* $Id: StationMapObjComparator.java,v 1.2 2011/08/19 15:52:57 vergnano Exp $
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.server;

import java.util.Comparator;

import it.csi.centrale.ui.client.data.StationMapObject;

/**
 * This class implements the comparator of elements of class StationMapObject.
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class StationMapObjComparator implements Comparator<StationMapObject> {
	public int compare(StationMapObject stObj1, StationMapObject stObj2) {

		String stName1 = stObj1.getShortName();
		if (stObj1.getLongName() != null && !stObj1.getLongName().trim().equals(""))
			stName1 = stObj1.getLongName();
		String stName2 = stObj2.getShortName();
		if (stObj2.getLongName() != null && !stObj2.getLongName().trim().equals(""))
			stName2 = stObj2.getLongName();

		return (stName1.compareTo(stName2));
	}

}
