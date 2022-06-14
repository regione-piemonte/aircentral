/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.stationreport;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

/**
 * Class for station configuration holder
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class StationConfigHolder {

	private static final String STATION_MAPPING = "station_mapping.xml";
	private static Logger logger = Logger.getLogger("centrale." + StationConfigHolder.class.getSimpleName());
	private static Mapping xmlMapping;

	private int copId;
	private int stationId;
	private Date configTimestamp;
	private Date configUploadDate;
	private Config stationConfig;

	static {
		xmlMapping = new Mapping();
		try {
			xmlMapping.loadMapping(StationConfigHolder.class.getClassLoader().getResource(STATION_MAPPING));
		} catch (Exception e) {
			logger.error("Cannot load mapping for station configuration", e);
		}
	}

	public StationConfigHolder(int copId, int stationId, long configTimestamp, Date configUploadDate,
			byte[] configData) {
		this.copId = copId;
		this.stationId = stationId;
		this.configTimestamp = new Date(configTimestamp);
		this.configUploadDate = configUploadDate;
		this.stationConfig = unmarshalConfig(configData);
	}

	public int getCopId() {
		return copId;
	}

	public int getStationId() {
		return stationId;
	}

	public Date getConfigTimestamp() {
		return configTimestamp;
	}

	public Date getConfigUploadDate() {
		return configUploadDate;
	}

	public Config getStationConfig() {
		return stationConfig;
	}

	private Config unmarshalConfig(byte[] configData) {
		try {
			logger.debug("Parsing configuration for station with id: " + stationId);
			Unmarshaller unmar = new Unmarshaller(xmlMapping);
			ByteArrayInputStream bis = new ByteArrayInputStream(configData);
			InputSource is = new InputSource(bis);
			Config cfg = (Config) unmar.unmarshal(is);
			bis.close();
			return cfg;
		} catch (Exception e) {
			logger.error("Error parsing xml configuration for station with id " + stationId + ": '"
					+ new String(configData) + "'", e);
			return null;
		}
	}

}
