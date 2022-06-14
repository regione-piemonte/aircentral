/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.polling;
/**
 * Possible status of polling
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public enum PollingStatus {
	NONE, //
	WAITING, //
	CONNECTING, //
	CONNECT_ERROR, //
	RUNNING, //
	OK, //
	IO_ERROR, //
	SOFTWARE_NOT_RESPONDING, //
	PROTOCOL_ERROR, //
	POLLING_ERROR, //
	UNEXPECTED_ERROR
}