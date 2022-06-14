/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.ui.server;

import it.csi.centrale.ui.client.data.VirtualCopInfo;

import java.util.Comparator;

/**
 * Class that implements a comparator
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class VirtualCopComparator implements Comparator<VirtualCopInfo> {
	public int compare(VirtualCopInfo vCopInfo1, VirtualCopInfo vCopInfo2) {

		String copName1 = vCopInfo1.getCopName();
		String copName2 = vCopInfo2.getCopName();
		return (copName1.compareTo(copName2));
	}

}
