/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.stationreport;

import java.util.Date;
import java.util.UUID;

/**
 * Class for configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 */
public class Config {

	private UUID id;
	private StationCfg station;
	private Date date;
	private String author;
	private String comment;

	public Config() {
	}

	public UUID getId() {
		return id;
	}

	public String getIdAsString() {
		if (id == null)
			return "";
		return id.toString();
	}

	public void setIdAsString(String strId) {
		this.id = UUID.fromString(strId);
	}

	public StationCfg getStation() {
		return station;
	}

	public void setStation(StationCfg station) {
		this.station = station;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	void setNewDate() {
		this.date = new Date();
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
