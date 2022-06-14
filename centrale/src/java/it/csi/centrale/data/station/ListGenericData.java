/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Marco Puccio
 * Purpose of file: class for storing information about a list of generic data
 * Change log:
 *   2008-11-08: initial version
 * ----------------------------------------------------------------------------
 * $Id: ListGenericData.java,v 1.10 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.data.station;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Class for storing information about a list of generic data
 * 
 * @author Marco Puccio - CSI Piemonte (marco.puccio@consulenti.csi.it)
 * 
 */
public class ListGenericData implements Serializable {

	private static final long serialVersionUID = 1778419206011017385L;

	LinkedList<GenericData> lstData;

	public ListGenericData() {
	}

	public LinkedList<GenericData> getLstData() {
		return lstData;
	}

	public void setLstData(LinkedList<GenericData> lstData) {
		this.lstData = lstData;
	}

	public void addToExistList(LinkedList<GenericData> lstToAdd) {
		this.lstData.addAll(lstToAdd);
	}

	public void addElement(GenericData el) {
		if (lstData == null)
			this.lstData = new LinkedList<GenericData>();
		this.lstData.add(el);
	}

	public void clearData() {
		lstData = null;
	}

}
