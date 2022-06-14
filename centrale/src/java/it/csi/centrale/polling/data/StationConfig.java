/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.polling.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Class for station confgiuration
 * 
 * @author isabella.vespa@csi.it
 * 
 */
public class StationConfig {

	private static final String DATA_DATE_FMT_STR = "yyyyMMdd HHmmss";

	private UUID stationId;
	private UUID configId;
	private String shortName;
	private String name;
	private String province;
	private String city;
	private String address;
	private String location;
	private String notes;
	private boolean gpsInstalled;
	private Date cfgDate;
	private String cfgAuthor;
	private String cfgComment;

	public StationConfig(UUID stationId, UUID configId, Map<String, String> attributes) throws ParseException {
		this.stationId = stationId;
		this.configId = configId;
		shortName = attributes.get("shortName");
		name = attributes.get("name");
		province = attributes.get("province");
		city = attributes.get("city");
		address = attributes.get("address");
		location = attributes.get("location");
		notes = attributes.get("notes");
		gpsInstalled = new Boolean(attributes.get("gpsInstalled"));
		cfgDate = new SimpleDateFormat(DATA_DATE_FMT_STR).parse(attributes.get("cfgDate"));
		cfgAuthor = attributes.get("cfgAuthor");
		cfgComment = attributes.get("cfgComment");
	}

	public UUID getStationId() {
		return stationId;
	}

	public UUID getConfigId() {
		return configId;
	}

	public String getShortName() {
		return shortName;
	}

	public String getName() {
		return name;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getLocation() {
		return location;
	}

	public String getNotes() {
		return notes;
	}

	public boolean isGpsInstalled() {
		return gpsInstalled;
	}

	public Date getCfgDate() {
		return cfgDate;
	}

	public String getCfgAuthor() {
		return cfgAuthor;
	}

	public String getCfgComment() {
		return cfgComment;
	}

	@Override
	public String toString() {
		return "StationConfig [stationId=" + stationId + ", configId=" + configId + ", shortName=" + shortName
				+ ", name=" + name + ", province=" + province + ", city=" + city + ", address=" + address
				+ ", location=" + location + ", notes=" + notes + ", gpsInstalled=" + gpsInstalled + ", cfgDate="
				+ cfgDate + ", cfgAuthor=" + cfgAuthor + ", cfgComment=" + cfgComment + "]";
	}

}
