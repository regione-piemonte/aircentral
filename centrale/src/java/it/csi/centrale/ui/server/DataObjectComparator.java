/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.ui.server;

import it.csi.centrale.ui.client.data.common.DataObject;

import java.util.Comparator;
/**
 * Class implemant a comparator
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class DataObjectComparator implements Comparator<DataObject> {
	public int compare(DataObject dataObj1, DataObject dataObj2) {

		String dataObject1 = dataObj1.getAnalyzerName();
		String dataObject2 = dataObj2.getAnalyzerName();
		return (dataObject1.compareTo(dataObject2));
	}

}
