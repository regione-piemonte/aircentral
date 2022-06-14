/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: class for manage information about login 
* Change log:
*   2008-09-11: initial version
* ----------------------------------------------------------------------------
* $Id: LoginCfg.java,v 1.4 2009/04/08 15:52:46 vergnano Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for manage information about login
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class LoginCfg {

	private List<Password> passwordList = new ArrayList<Password>();

	public LoginCfg() {
		super();
	}

	public LoginCfg(List<Password> passwordList) {
		super();
		this.passwordList = passwordList;
	}

	/**
	 * @return the passwordList
	 */
	public List<Password> getPasswordList() {
		return passwordList;
	}

	/**
	 * @param passwordList the passwordList to set
	 */
	public void setPasswordList(List<Password> passwordList) {
		this.passwordList = passwordList;
	}

	/**
	 * Print login configuration
	 */
	public void printLoginCfg() {
		for (int i = 0; i < passwordList.size(); i++) {
			Password pwd = passwordList.get(i);
			pwd.printPassword();
		}
	}

}
