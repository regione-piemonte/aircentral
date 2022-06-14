/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa 
* Purpose of file:   Class that represents parameter.
* Change log:
*   2009-04-08: initial version
* ----------------------------------------------------------------------------
* $Id: ParameterInfo.java,v 1.2 2009/04/10 11:10:48 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that represents parameter.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ParameterInfo implements Serializable {

	private static final long serialVersionUID = -4559966045893563030L;

	private String param_id;
	private String name;
	private String physicalDimension;
	private String type;
	private Double molecularWeight;
	private Date updateDate;

	/**
	 * @return the param_id
	 */
	public String getParam_id() {
		return param_id;
	}

	/**
	 * @param param_id the param_id to set
	 */
	public void setParam_id(String param_id) {
		this.param_id = param_id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the physicalDimension
	 */
	public String getPhysicalDimension() {
		return physicalDimension;
	}

	/**
	 * @param physicalDimension the physicalDimension to set
	 */
	public void setPhysicalDimension(String physicalDimension) {
		this.physicalDimension = physicalDimension;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the molecularWeight
	 */
	public Double getMolecularWeight() {
		return molecularWeight;
	}

	/**
	 * @param molecularWeight the molecularWeight to set
	 */
	public void setMolecularWeight(Double molecularWeight) {
		this.molecularWeight = molecularWeight;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
