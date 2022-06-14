/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Pierfrancesco Vallosio
* Purpose of file: constants for UI
* Change log:
*   2015-12-15: initial version
* ----------------------------------------------------------------------------
* $Id:$
* ----------------------------------------------------------------------------
*/

package it.csi.centrale.ui.client;

/**
 * constants for UI
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class CentraleUIConstants {

	public static final String CENTRALE_FUNCTION = "centrale";
	public static final String TIPO_OGGETTO_COP = "cop";
	public static final String TIPO_OGGETTO_RETE = "rete";
	public static final Integer SAVE = new Integer(0);
	public static final Integer RELOAD = new Integer(1);
	public static final Integer NEW = new Integer(2);
	public static final Integer BACK = new Integer(3);
	public static final Integer LIST_ELEMENT = new Integer(4);
	public static final Integer SEND_VERIFY = new Integer(5);
	public static final Integer UNDO = new Integer(6);
	public static final Integer DELETE = new Integer(7);
	public static final Integer CSV = new Integer(8);
	public static final Integer CHART = new Integer(9);
	public static final Integer CHART_SMALL = new Integer(10);
	public static final Integer CSV_SMALL = new Integer(11);
	public static final Integer TABLE_SMALL = new Integer(12);
	public static final Integer REFRESH = new Integer(13);
	public static final Integer HELP = new Integer(14);
	public static final Integer DOWNLOAD = new Integer(15);
	public static final Integer STATION_STATUS = new Integer(16);
	public static final Integer ANALYZER_STATUS = new Integer(17);
	public static final Integer INFORMATIC_STATUS = new Integer(18);
	public static final Integer POLLING = new Integer(19);
	public static final Integer UNLINK_DB = new Integer(20);
	public static final Integer LINK_DB = new Integer(21);
	public static final Integer PHYSICAL_DIMENSION = new Integer(22);
	public static final Integer PARAMETER = new Integer(23);
	public static final Integer MEASURE_UNIT = new Integer(24);
	public static final Integer ALARM_NAME = new Integer(25);
	public static final Integer OTHER_COMMON_CFG = new Integer(26);
	public static final Integer DURING_POLLING = new Integer(27);
	public static final Integer REFRESH_REMOVED = new Integer(29);
	public static final String SESSION_ENDED = "session_ended";
	public static final Integer VIEW_STATION_DATA_WIDGET = new Integer(99);
	public static final Integer NOT_FREE_LINE = new Integer(0);
	public static final Integer ERROR_GETTING_LINE = new Integer(1);
	public static final Integer NO_PORT_AVAILABLE = new Integer(2);
	public static final Integer CONNECTION_ERROR = new Integer(3);
	public static final Integer PRIVATE_LINE = new Integer(4);
	public static final Integer SHARED_LINE = new Integer(5);
	public static final Integer STATION_UUID_ERROR = new Integer(4);
	public static final Integer DOWNLOADING_OK = new Integer(5);
	public static final Integer VERSION_ERROR = new Integer(6);
	public static final int PC_NOT_RESPONDING = 2;
	public static final int ROUTER_REMOTE_NOT_RESPONING = 3;
	public static final int ROUTER_LOCALE_NOT_RESPONING = 4;
	public static final int IS_CALLING = 5;
	public static final int SW_NOT_RESPONDING = 7;
	public static final int SW_RESPONDING = 8;
	public static final int COMMUNICATION_LOST = 9;
	public static final int POLLING_FINISHED_OK = 10;
	public static final int UNKNOWN = 11;
	public static final int PROTOCOL_ERROR = 12;
	public static final int POLLING_ERROR = 13;
	public static final int UNEXPECTED_ERROR = 14;
	public static final int CONNECT_ERROR = 15;
	public static final int WAIT = 16;
	public static final int ALARM = 6;
	public static final int WARNING = 7;
	public static final int OK = 8;
	public static final int TIMER_SCHEDULING = 10000;
	public static final int TIMER_SCHEDULING_FREQUENT = 1000;
	public static final int DISABLED = 0;
	public static final int ALL_DATA = 2;
	public static final int CALIBRATION_DATA = 1;
	public static final int VISIBLE_ITEM = 10;
	public static final String MODEM = "modem";
	public static final String ROUTER = "router";
	public static final String LAN = "lan";
	public static final String PROXY = "proxy";
	public static final Integer XML = 28;
	public static final int INFORMATIC_STATUS_ALARM = 1;
	public static final int INFORMATIC_STATUS_WARNING = 2;
	public static final int INFORMATIC_STATUS_OK = 3;
	public static final String URL_FOR_IMAGE = "./mapsService?function=getMap";
	public static final String DEFAULT_MAP_NAME = "piemonte.png";

}
