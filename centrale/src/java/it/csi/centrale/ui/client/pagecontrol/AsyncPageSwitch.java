/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manage the switch of a page
// Change log:
//   2009-02-16: initial version
// ----------------------------------------------------------------------------
// $Id: AsyncPageSwitch.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.pagecontrol;

import it.csi.centrale.ui.client.CentraleUI;

import com.google.gwt.user.client.Window;

/**
 * Class for manage the switch of a page
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class AsyncPageSwitch extends AsyncPageOperation {

	private PageController pageController;

	private UIPage newPage;

	public AsyncPageSwitch(PageController pageController, UIPage newPage) {
		this.pageController = pageController;
		this.newPage = newPage;
	}

	@Override
	public void complete() {
		pageController.setCurrentPageImpl(newPage);
	}

	public void onSuccess(Boolean notModified) {
		if (notModified)
			complete();
		else if (Window.confirm(CentraleUI.getMessages().confirm_abandon_page()))
			complete();
	}

}
