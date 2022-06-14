/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: class for manage an operation about a page
// Change log:
//   2009-02-16: initial version
// ----------------------------------------------------------------------------
// $Id: AsyncPageOperation.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.pagecontrol;

import it.csi.centrale.ui.client.UIAsyncCallback;

/**
 * Class for manage an operation about a page
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public abstract class AsyncPageOperation extends UIAsyncCallback<Boolean> {

	public abstract void complete();

}
