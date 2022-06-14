/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.config;

import it.csi.centrale.data.station.ConfigInfo;
import it.csi.centrale.data.station.Station;
import it.csi.webauth.db.model.AmbitoAcl;
import it.csi.webauth.db.model.FunctionFlags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
/**
 * Wan connection
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 *
 */
public class UserSessionInfo {

	private Boolean readWrite = null;
	private Boolean localAccess = false;

	public UserSessionInfo() {
		super();
	}

	public UserSessionInfo(Boolean readWrite, Boolean localAccess, Map<Integer, FunctionFlags> permissionMap,
			List<AmbitoAcl> ambitiAclList, List<AmbitoAcl> retiAclList) {
		super();
		this.readWrite = readWrite;
		this.localAccess = localAccess;
		this.permissionMap = permissionMap;
		this.ambitiAclList = ambitiAclList;
		this.retiAclList = retiAclList;
	}

	public void setLocalAccess(Boolean localAccess) {
		this.localAccess = localAccess;
	}

	/**
	 * @return true if is local access or false if is rempte access
	 */
	public Boolean isLocalAccess() {
		return this.localAccess;
	}

	private Map<Integer, FunctionFlags> permissionMap;// Map che ha,
	// come chiavi, gli ambiti su cui l'utente ha la funzione centrale e, come
	// valori,
	// i flag associati alla funzione per ciascun ambito.

	private List<AmbitoAcl> ambitiAclList = new ArrayList<AmbitoAcl>();

	// lista ambitiAcl ome virtualcop - ambito permessi dal centrale e
	// dall'utente

	private List<AmbitoAcl> retiAclList = new ArrayList<AmbitoAcl>();

	// lista virtulkacop-reti permessi dal centrale e dall'utente

	/*
	 * public Map<Integer, FunctionFlags> getPermissionMap() { return
	 * permissionMap; }
	 */

	public List<AmbitoAcl> getRetiAclList() {
		return retiAclList;
	}

	public void setRetiAclList(List<AmbitoAcl> retiAclList) {
		this.retiAclList = retiAclList;
	}

	public void setPermissionMap(Map<Integer, FunctionFlags> permissionMap) {
		this.permissionMap = permissionMap;
	}

	public List<AmbitoAcl> getAmbitiAclList() {
		return ambitiAclList;
	}

	public void setAmbitiAclList(List<AmbitoAcl> ambitiAclList) {
		this.ambitiAclList = ambitiAclList;
	}

	/**
	 * @param virtualCopId
	 * @return true if user has read/write permission, false otherwise
	 */
	public Boolean isRW(Integer virtualCopId) {
		if (readWrite != null)
			return readWrite;
		else {
			// cerco gli ambiti associati al copid sugli ambitiacllist
			List<AmbitoAcl> ambitiCop = new ArrayList<AmbitoAcl>();
			for (int i = 0; i < ambitiAclList.size(); i++) {
				if (new Integer(ambitiAclList.get(i).getIdOggetto())
						.equals(virtualCopId))
					ambitiCop.add(ambitiAclList.get(i));
			}
			// dalla lista degli ambiti del cop cerco i permessi associati
			Boolean isWrite = false;
			if (ambitiCop.size() == 0)
				return null;
			for (int i = 0; i < ambitiCop.size(); i++) {
				if (permissionMap.containsKey(ambitiCop.get(i).getIdAmbito())) {
					isWrite = permissionMap.get(ambitiCop.get(i).getIdAmbito())
							.getWriteFlag();
					if (isWrite)
						return true;
				}
			}
			return false;
		}
	}

	public void setRW(Boolean readWrite) {
		this.readWrite = readWrite;
	}

	public Vector<Station> getReadableStationVector(ConfigInfo configInfo) {
		Vector<Station> stationVectorToReturn = new Vector<Station>();
		if (this.isLocalAccess() && this.readWrite != null) {
			// l'utente e' collegato da locale quindi se e' true scrive tutte le
			// stazioni
			stationVectorToReturn = configInfo.getStVector();
		} else {
			for (int i = 0; i < ambitiAclList.size(); i++) {
				for (int j = 0; j < configInfo.getVirtualCopVector().size(); j++) {
					Integer copId = configInfo.getVirtualCopVector()
							.elementAt(j).getVirtualCopId();
					if (copId.equals(new Integer(ambitiAclList.get(i)
							.getIdOggetto()))) {
						// se ho il cop certamente posso leggere (perche' al max
						// posso
						// anche scrivere)
						Vector<Station> stationVector = configInfo
								.getStVector();
						for (int k = 0; k < stationVector.size(); k++) {
							// ciclo in tutte le stazioni alla ricerca di quelle
							// con
							// il cop id considerato
							Station station = stationVector.elementAt(k);
							if (station.getStInfo().getCopId().equals(copId)) {
								if (!stationVectorToReturn.contains(station))
									stationVectorToReturn.add(station);
							}
						}
					}
				}// end for j
			}// end for i
		}// end else
		return stationVectorToReturn;
	}

	public Vector<Station> getWritableStationVector(ConfigInfo configInfo) {
		Vector<Station> stationVectorToReturn = new Vector<Station>();
		if (this.isLocalAccess() && this.readWrite != null && this.readWrite) {
			// l'utente e' collegato da locale quindi se e' false legge tutte le
			// stazioni
			stationVectorToReturn = configInfo.getStVector();
		} else {
			for (int i = 0; i < ambitiAclList.size(); i++) {
				for (int j = 0; j < configInfo.getVirtualCopVector().size(); j++) {
					Integer copId = configInfo.getVirtualCopVector()
							.elementAt(j).getVirtualCopId();
					if (copId.equals(new Integer(ambitiAclList.get(i)
							.getIdOggetto()))) {
						FunctionFlags ff = permissionMap.get(ambitiAclList.get(
								i).getIdAmbito());
						if (ff.getWriteFlag()) {

							// se ho i permessi di scrittura sull'ambito li ho
							// per
							// tutte
							// le stazioni del cop di appartenenza a
							// quell'ambito
							Vector<Station> stationVector = configInfo
									.getStVector();
							for (int k = 0; k < stationVector.size(); k++) {
								// ciclo in tutte le stazioni alla ricerca di
								// quelle
								// con il cop id considerato
								Station station = stationVector.elementAt(k);
								if (station.getStInfo().getCopId()
										.equals(copId)) {
									if (!stationVectorToReturn
											.contains(station))
										stationVectorToReturn.add(station);
								}
							}
						}
					}
				}// end for j
			}// end for i
		}// end else
		return stationVectorToReturn;
	}

	public boolean hasAdvancedFlags() {
		for (int i = 0; i < ambitiAclList.size(); i++) {
			if (permissionMap.containsKey(ambitiAclList.get(i).getIdAmbito())) {
				FunctionFlags ff = permissionMap.get(ambitiAclList.get(i)
						.getIdAmbito());
				if (ff.getAdvancedFlag())
					return true;
			}
		}
		return false;
	}
}
