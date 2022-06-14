/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: abstract class for defining an action on a page
// Change log:
//   2014-05-23: initial version
// ----------------------------------------------------------------------------
// $Id: SaveAction.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.ui.client.stationconfig;
/**
 * abstract class for defining an action on a page
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public abstract class SaveAction {

	public abstract void execute(String name);

}
