/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: this class provides method for display chart.
* Change log:
*   2008-01-10: initial version
* ----------------------------------------------------------------------------
* $Id: ChartImage.java,v 1.4 2009/04/07 11:23:28 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import com.google.gwt.user.client.ui.Image;

/**
 * This class provides method for display chart
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ChartImage extends Image {

	public ChartImage() {
		super();
	}// end constructor

	public ChartImage displayChart(String chartName) {
		String imageUrl = "./displayChart?filename=" + chartName;
		setUrl(imageUrl);
		return this;
	}
}// end class
