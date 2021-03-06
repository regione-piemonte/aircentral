/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for controlling pages of user interface
// Change log:
//   2009-02-13: initial version
// ----------------------------------------------------------------------------
// $Id: PageController.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.pagecontrol;

/**
 * Class for controlling pages of user interface
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class PageController {

	private UIPage currentPage = null;

	private UIPage previousPage = null;

	public void goToUpperLevelPage() {
		if (currentPage == null)
			return;
		UIPage upperLevelPage = currentPage.getUpperLevelPage();
		if (upperLevelPage == null)
			return;
		setCurrentPage(upperLevelPage);
	}

	public void goToTopLevelPage() {
		goToTopLevelPage(false);
	}

	public void goToTopLevelPage(boolean force) {
		if (currentPage == null)
			return;
		UIPage page = currentPage;
		UIPage upperLevelPage = currentPage.getUpperLevelPage();
		while (upperLevelPage != null) {
			page = upperLevelPage;
			upperLevelPage = page.getUpperLevelPage();
		}
		setCurrentPage(page, force);
	}

	public void goToPreviousPage() {
		setCurrentPage(previousPage);
	}

	public void setCurrentPage(UIPage page) {
		setCurrentPage(page, false);
	}

	public void setPreviousPage(UIPage page) {
		previousPage = page;
	}

	public void setCurrentPage(UIPage page, boolean force) {
		if (currentPage == null || force) {
			setCurrentPageImpl(page);
			return;
		}
		AsyncPageOperation apo = new AsyncPageSwitch(this, page);
		currentPage.dismissContent(apo);
	}

	void setCurrentPageImpl(UIPage page) {
		if (page != null)
			page.reset();
		if (currentPage != null)
			currentPage.hide();
		if (page != null)
			page.show();
		if (page != null)
			page.loadContent();
		previousPage = currentPage;
		currentPage = page;
	}

	public void updateCurrentPage(PageUpdateAction pageUpdateAction) {
		if (currentPage == null)
			return;
		AsyncPageOperation apo = new AsyncPageUpdate(this, pageUpdateAction);
		currentPage.dismissContent(apo);
	}

	void updateCurrentPageImpl() {
		if (currentPage != null) {
			currentPage.reset();
			currentPage.loadContent();
		}
	}

	public void doActionOnUnmodifiedPage(String modifiedWarning,
			UserAction userAction) {
		AsyncPageOperation apo = new AsyncModifiedPageCheck(modifiedWarning,
				userAction);
		if (currentPage != null)
			currentPage.dismissContent(apo);
		else
			apo.complete();
	}
}
