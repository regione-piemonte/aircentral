/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manage the update operation on a page
// Change log:
//   2009-02-16: initial version
// ----------------------------------------------------------------------------
// $Id: AsyncPageUpdate.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.pagecontrol;

import it.csi.centrale.ui.client.CentraleUI;

import com.google.gwt.user.client.Window;

/**
 * Class for manage the update operation on a page
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class AsyncPageUpdate extends AsyncPageOperation {

	private PageController pageController;

	private PageUpdateAction pageUpdateAction;

	public AsyncPageUpdate(PageController pageController,
			PageUpdateAction pageUpdateAction) {
		this.pageController = pageController;
		this.pageUpdateAction = pageUpdateAction;
		if (pageUpdateAction != null)
			pageUpdateAction.setAsyncPageUpdate(this);
	}

	@Override
	public void complete() {
		if (pageUpdateAction != null) {
			pageUpdateAction.action();
		} else
			pageController.updateCurrentPageImpl();
	}

	void updatePage() {
		pageController.updateCurrentPageImpl();
	}

	public void onSuccess(Boolean notModified) {
		if (notModified)
			complete();
		else if (Window.confirm(CentraleUI.getMessages().confirm_reload_page()))
			complete();
	}

}