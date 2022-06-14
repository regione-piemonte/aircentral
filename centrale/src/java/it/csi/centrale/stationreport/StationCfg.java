/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.stationreport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class for station configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 */
public class StationCfg {

	private UUID id;
	private String shortName;
	private String name;
	private String location;
	private String address;
	private String city;
	private String province;
	private Boolean gpsInstalled;
	private String userNotes;
	private List<AnalyzerCfg> listAnalyzer = new ArrayList<AnalyzerCfg>();

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getIdAsString() {
		if (id == null)
			return "";
		return id.toString();
	}

	public void setIdAsString(String strId) {
		this.id = UUID.fromString(strId);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUserNotes() {
		return userNotes;
	}

	public void setUserNotes(String userNotes) {
		this.userNotes = userNotes;
	}

	public Boolean isGpsInstalled() {
		return gpsInstalled;
	}

	public void setGpsInstalled(Boolean gpsInstalled) {
		this.gpsInstalled = gpsInstalled;
	}

	public List<AnalyzerCfg> getListAnalyzer() {
		return listAnalyzer;
	}

	public void setListAnalyzer(List<AnalyzerCfg> listAnalyzer) {
		this.listAnalyzer = listAnalyzer;
	}

}
