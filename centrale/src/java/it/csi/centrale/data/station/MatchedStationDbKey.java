/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: Class for storing information about a station matched in
 * matched Db
 * Change log:
 *   2009-03-25: initial version
 * ----------------------------------------------------------------------------
 * $Id: MatchedStationDbKey.java,v 1.3 2009/04/08 15:52:46 vergnano Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.data.station;

import java.util.Date;

/**
 * Class for storing information about a station matched in matched Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class MatchedStationDbKey {

	private String idReteMonit;

	private String codiceIstatComune;

	private int progrPuntoCom;

	private Date updateDate;
	
	private boolean enabled;

	public String getCodiceIstatComune() {
		return codiceIstatComune;
	}

	public void setCodiceIstatComune(String codiceIstatComune) {
		this.codiceIstatComune = codiceIstatComune;
	}

	public int getProgrPuntoCom() {
		return progrPuntoCom;
	}

	public void setProgrPuntoCom(int progrPuntoCom) {
		this.progrPuntoCom = progrPuntoCom;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public String getIdReteMonit() {
		return idReteMonit;
	}

	public void setIdReteMonit(String idReteMonit) {
		this.idReteMonit = idReteMonit;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "MatchedStationDbKey [idReteMonit=" + idReteMonit + ", codiceIstatComune=" + codiceIstatComune
				+ ", progrPuntoCom=" + progrPuntoCom + ", updateDate=" + updateDate + ", enabled=" + enabled + "]";
	}
	
	public String toStationKey() {
		return idReteMonit + "." + codiceIstatComune + "." + progrPuntoCom;
	}

}
