/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.polling.data;
/**
 * Enumeration for common config result
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public enum CommonConfigResult {
	OK, UNPARSABLE, CONSISTENCY_ERROR, SAVE_ERROR, LOAD_ERROR, INCOMPATIBLE, CONFIG_LOAD_ERROR, CONFIG_START_ERROR
}
