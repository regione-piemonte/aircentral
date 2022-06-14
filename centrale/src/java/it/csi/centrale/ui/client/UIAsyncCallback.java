/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class that implements AsyncCallback to manage onFailure
// Change log:
//   2009-02-24: initial version
// ----------------------------------------------------------------------------
// $Id: UIAsyncCallback.java,v 1.4 2009/04/07 11:23:28 vespa Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class that implements AsyncCallback to manage onFailure
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public abstract class UIAsyncCallback<T> implements AsyncCallback<T> {

	public void onFailure(Throwable caught) {
		if (caught instanceof SessionExpiredException)
			CentraleUI.sessionEnded();
		else if (caught instanceof UserParamsException)
			Window.alert(caught.getMessage());
		else if (caught instanceof FatalException) {
			Window.alert(caught.getMessage());
			CentraleUI.shutdown();
		} else
			CentraleUI.unexpectedError(caught);
	}

}
