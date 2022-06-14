/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: class for storing information about a key of match_dbaria 
 * table of Db
 * Change log:
 *   2009-02-20: initial version
 * ----------------------------------------------------------------------------
 * $Id: MatchedDbKey.java,v 1.4 2009/06/10 09:52:36 vespa Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.data.station;

import java.util.Date;

/**
 * Class for storing information about a key of match_dbaria table of Db
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class MatchedDbKey {
	
	private Integer elementId;
	
	private Integer avgPeriodId;
	
	public Integer getElementId() {
		return elementId;
	}

	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}

	public Integer getAvgPeriodId() {
		return avgPeriodId;
	}

	public void setAvgPeriodId(Integer avgPeriodId) {
		this.avgPeriodId = avgPeriodId;
	}

	private String idReteMonit;

	private String codiceIstatComune;

	private int progrPuntoCom;

	private String idParametro;

	private String component;

	private Date updateDate;
	
	private boolean enabled;

	public String getIdReteMonit() {
		return idReteMonit;
	}

	public void setIdReteMonit(String idReteMonit) {
		this.idReteMonit = idReteMonit;
	}

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

	public String getIdParametro() {
		return idParametro;
	}

	public void setIdParametro(String idParametro) {
		this.idParametro = idParametro;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "MatchedDbKey [elementId=" + elementId + ", avgPeriodId=" + avgPeriodId + ", idReteMonit=" + idReteMonit
				+ ", codiceIstatComune=" + codiceIstatComune + ", progrPuntoCom=" + progrPuntoCom + ", idParametro="
				+ idParametro + ", component=" + component + ", updateDate=" + updateDate + ", enabled=" + enabled
				+ "]";
	}
	
	public String toStationKey() {
		return idReteMonit + "." + codiceIstatComune + "." + progrPuntoCom;
	}
	
	public String toSensorKey() {
		return idReteMonit + "." + codiceIstatComune + "." + progrPuntoCom + "." + idParametro;
	}
	
}
