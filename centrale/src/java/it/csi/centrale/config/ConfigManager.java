/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
// ----------------------------------------------------------------------------
// Original Author of file: Pierfrancesco Vallosio
// Purpose of file: manager for configurations for the application
// Change log:
//   2008-09-11: initial version
// ----------------------------------------------------------------------------
// $Id: ConfigManager.java,v 1.15 2015/10/15 12:10:23 pfvallosio Exp $
// ----------------------------------------------------------------------------

package it.csi.centrale.config;

import it.csi.periferico.config.common.CommonCfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

/**
 * Manager for configurations for the application
 * 
 * @author Pierfrancesco Vallosio - CSI Piemonte
 *         (pierfrancesco.vallosio@consulenti.csi.it)
 * 
 */
public class ConfigManager {

	public static final String COMMON_MAPPING = "common_mapping.xml";

	private static final String LOGIN_MAPPING = "login_mapping.xml";

	private static final String CFG_DIR = "config";

	private static final String LOGIN_CFG = "login.xml";

	private static final String DBCONFIG_MAPPING = "dbconfig_mapping.xml";

	private static final String DB_CFG = "db_config.xml";

	private static final String HISTORIC_CFG_DIR = CFG_DIR + File.separator
			+ "historic";

	private static final String HISTORIC_FILE_PREFIX = "station_";

	private static final String HISTORIC_FILE_DATE_FMT_STR = "yyyyMMdd_HHmm";

	private static final String LOG4J_CONFIG = "log4j.properties";

	public enum LoadConfigResult {
		MISSING, PARSE_ERROR, ERROR, OK
	}

	static Logger logger = Logger.getLogger("centrale."
			+ ConfigManager.class.getSimpleName());

	private LoginCfg loginCfg = null;

	private DbConfig dbConfig = null;

	private DateFormat historicFileDateFormat = new SimpleDateFormat(
			HISTORIC_FILE_DATE_FMT_STR);

	public ConfigManager() {
	}

	public boolean init() {

		logger.info("Loading login configuration ...");
		loginCfg = (LoginCfg) loadXMLConfig(LOGIN_MAPPING, LOGIN_CFG);
		if (loginCfg == null) {
			logger.error("Login configuration load/parse error");
			return false;
		}
		logger.info("Login configuration loaded");

		logger.info("Loading database configuration ...");
		dbConfig = (DbConfig) loadXMLConfig(DBCONFIG_MAPPING, DB_CFG);
		if (dbConfig == null) {
			logger.error("Database configuration load/parse error");
			return false;
		}
		logger.info("Database configuration loaded");

		return true;
	}

	public LoginCfg getLoginCfg() {
		return loginCfg;
	}

	public DbConfig getDbConfig() {
		return dbConfig;
	}

	/**
	 * @param mappingFile
	 * @param configFile
	 * @return the configuration
	 */
	private Object loadXMLConfig(String mappingFile, String configFile) {

		try {
			// Load the mapping information from the mapping file
			logger.debug("Loading XML mapping file: " + mappingFile);
			Mapping mapping = new Mapping();
			mapping.loadMapping(getClass().getClassLoader().getResource(
					mappingFile));
			// Unmarshal the data
			logger.debug("Unmarshalling XML file: " + configFile);
			Unmarshaller unmar = new Unmarshaller(mapping);
			InputSource is = new InputSource(new FileReader(CFG_DIR
					+ File.separator + configFile));
			return (unmar.unmarshal(is));
		} catch (Exception e) {
			String msg = "Error loading and parsing: " + configFile;
			logger.error(msg, e);
			return null;
		}
	}

	public List<Date> readHistoricConfigsList() {
		return (readHistoricConfigsList(null, null));
	}

	public List<Date> readHistoricConfigsList(final Date startDate,
			final Date endDate) {

		List<Date> validConfigsList = new ArrayList<Date>();
		File historicDir = new File(HISTORIC_CFG_DIR);
		if (!historicDir.exists()) {
			logger.info("Historic config directory does not exist");
			return validConfigsList;
		}
		String[] historicFileNames = historicDir.list();
		if (historicFileNames == null)
			logger.error("Cannot read historic config directory"
					+ historicDir.getAbsolutePath());
		for (int i = 0; i < historicFileNames.length; i++) {
			Date date = parseHistoricCfgFileName(historicFileNames[i]);
			if (date == null)
				continue;
			if (startDate != null && date.before(startDate))
				continue;
			if (endDate != null && date.after(endDate))
				continue;
			validConfigsList.add(date);
		}
		if (validConfigsList.size() == 0)
			logger.info("Historic configs not available");
		return validConfigsList;
	}

	private Date parseHistoricCfgFileName(String name) {
		if (name == null)
			return null;
		if (!name.startsWith(HISTORIC_FILE_PREFIX))
			return null;
		name = name.substring(HISTORIC_FILE_PREFIX.length());
		if (!name.endsWith(".xml"))
			return null;
		name = name.substring(0, name.length() - 4);
		try {
			Date fileNameDate = historicFileDateFormat.parse(name);
			return fileNameDate;
		} catch (ParseException e) {
		}
		return null;
	}

	/**
	 * @return properties of logger
	 */
	public static Properties readLog4jProperties() {
		try {
			String fileName = CFG_DIR + File.separator + LOG4J_CONFIG;
			File file = new File(fileName);
			if (!file.exists() || !file.isFile() || !file.canRead())
				return null;
			Properties log4jProperties = new Properties();
			log4jProperties.load(new BufferedReader(new FileReader(file)));
			return log4jProperties;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Write configuration
	 * 
	 * @param commonCfg
	 * @return configuration written
	 * @throws IOException
	 * @throws MappingException
	 * @throws MarshalException
	 * @throws ValidationException
	 */
	public String writeCommonCfg(CommonCfg commonCfg) throws IOException,
			MappingException, MarshalException, ValidationException {
		// Load Mapping
		Mapping mapping = new Mapping();
		XMLContext context = new XMLContext();

		mapping.loadMapping(getClass().getClassLoader().getResource(
				ConfigManager.COMMON_MAPPING));
		// initialize and configure XMLContext
		context.addMapping(mapping);

		// create a new Marshaller
		Marshaller marshaller = context.createMarshaller();
		StringWriter stringWriter = new StringWriter();
		marshaller.setWriter(stringWriter);

		// marshal the commonCfg object

		marshaller.marshal(commonCfg);
		return stringWriter.toString();
	}
}
