/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manage page check
// Change log:
//   2009-03-12: initial version
// ----------------------------------------------------------------------------
// $Id: AsyncModifiedPageCheck.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.pagecontrol;

import com.google.gwt.user.client.Window;

/**
 * Class for manage page check
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class AsyncModifiedPageCheck extends AsyncPageOperation {

	private String modifiedWarning;

	private UserAction userAction;

	public AsyncModifiedPageCheck(String modifiedWarning, UserAction userAction) {
		this.modifiedWarning = modifiedWarning;
		this.userAction = userAction;
	}

	@Override
	public void complete() {
		if (userAction != null) {
			userAction.action();
		}
	}

	public void onSuccess(Boolean notModified) {
		if (notModified)
			complete();
		else if (modifiedWarning != null && Window.confirm(modifiedWarning))
			complete();
	}

}