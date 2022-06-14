/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manage an update operation on a page
// Change log:
//   2009-02-17: initial version
// ----------------------------------------------------------------------------
// $Id: PageUpdateAction.java,v 1.3 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.pagecontrol;

/**
 * Class for manage an update operation on a page
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public abstract class PageUpdateAction extends UserAction {

	private AsyncPageUpdate asyncPageUpdate;

	void setAsyncPageUpdate(AsyncPageUpdate asyncPageUpdate) {
		this.asyncPageUpdate = asyncPageUpdate;
	}

	public void updatePage() {
		asyncPageUpdate.updatePage();
	}

}
