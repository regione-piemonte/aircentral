/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.stationreport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for station report
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class StationReport {

	private List<StationConfigHolder> holders;

	public StationReport(List<StationConfigHolder> holders) {
		this.holders = holders;
	}

	public List<String[]> makeFullReport() {
		List<String[]> report = new ArrayList<String[]>();
		report.add(new String[] { "", "REPORT STAZIONI" });
		report.addAll(makeStationReport());
		report.add(new String[] { "", "" });
		report.add(new String[] { "", "REPORT ANALIZZATORI" });
		report.addAll(makeAnalyzerReport());
		report.add(new String[] { "", "" });
		report.add(new String[] { "", "REPORT ELEMENTI" });
		report.addAll(makeElementReport());
		return report;
	}

	public List<String[]> makeStationReport() {
		String[] title = { "", "Nome breve", "Nome", "Provincia", "Comune", "Indirizzo", "Posizione", "Usa GPS",
				"Note" };
		List<String[]> listRows = new ArrayList<String[]>();
		for (StationConfigHolder sch : holders) {
			StationCfg st = sch.getStationConfig().getStation();
			int i = 0;
			String[] line = new String[title.length];
			line[i++] = str(sch.getCopId());
			line[i++] = str(st.getShortName());
			line[i++] = str(st.getName());
			line[i++] = str(st.getProvince());
			line[i++] = str(st.getCity());
			line[i++] = str(st.getAddress());
			line[i++] = str(st.getLocation());
			line[i++] = str(st.isGpsInstalled());
			line[i++] = str(st.getUserNotes());
			listRows.add(line);
		}
		Collections.sort(listRows, new StringArrayComparator(new int[] { 1 }));
		listRows.add(0, title);
		return listRows;
	}

	public List<String[]> makeAnalyzerReport() {
		String[] title = { "", "Stazione", "Analizzatore", "Descrizione", "Marca", "Modello", "Numero serie", "Stato",
				"Parametri driver", "Nome host o IP", "Porta IP", "Tensione min.", "Tensione max.",
				"Estensione range inf.", "Estensione range sup.", "Modo differenziale", "Note" };
		List<String[]> listRows = new ArrayList<String[]>();
		for (StationConfigHolder sch : holders) {
			StationCfg st = sch.getStationConfig().getStation();
			for (AnalyzerCfg an : st.getListAnalyzer()) {
				if (an.getDeletionDate() != null)
					continue;
				int i = 0;
				String[] line = new String[title.length];
				line[i++] = str(sch.getCopId());
				line[i++] = str(st.getShortName());
				line[i++] = str(an.getName());
				line[i++] = str(an.getDescription());
				line[i++] = str(an.getBrand());
				line[i++] = str(an.getModel());
				line[i++] = str(an.getSerialNumber());
				line[i++] = str(an.getStatusAsString());
				line[i++] = str(an.getDriverParams());
				line[i++] = str(an.getHostName());
				line[i++] = str(an.getIpPort());
				line[i++] = str(an.getMinVoltage());
				line[i++] = str(an.getMaxVoltage());
				line[i++] = str(an.getMinRangeExtension());
				line[i++] = str(an.getMaxRangeExtension());
				line[i++] = str(an.isDifferentialModeNeeded());
				line[i++] = str(an.getUserNotes());
				listRows.add(line);
			}
		}
		Collections.sort(listRows, new StringArrayComparator(new int[] { 1, 2 }));
		listRows.add(0, title);
		return listRows;
	}

	public List<String[]> makeElementReport() {
		String[] title = { "", "Stazione", "Analizzatore", "Parametro", "Abilitato", "Unità misura analizz.",
				"Zero di scala", "Fondo scala", "Coeff. correz.", "Costante correz.", "Periodo acq.", "Ritardo acq.",
				"Durata acq.", "Scarta dati non validi", "Unità misura", "Numero decimali", "Coeff. linear.",
				"Costante linear.", "Valore min", "Valore max", "Periodi mediazione" };
		List<String[]> listRows = new ArrayList<String[]>();
		for (StationConfigHolder sch : holders) {
			StationCfg st = sch.getStationConfig().getStation();
			for (AnalyzerCfg an : st.getListAnalyzer()) {
				if (an.getDeletionDate() != null)
					continue;
				for (ElementCfg el : an.getListElement()) {
					int i = 0;
					String[] line = new String[title.length];
					line[i++] = str(sch.getCopId());
					line[i++] = str(st.getShortName());
					line[i++] = str(an.getName());
					line[i++] = str(el.getParameterId());
					line[i++] = str(el.getEnabled());
					line[i++] = str(el.getAnalyzerMeasureUnitName());
					line[i++] = str(el.getRangeLow());
					line[i++] = str(el.getRangeHigh());
					line[i++] = str(el.getCorrectionCoefficient());
					line[i++] = str(el.getCorrectionOffset());
					line[i++] = str(el.getAcqPeriod());
					line[i++] = str(el.getAcqDelay());
					line[i++] = str(el.getAcqDuration());
					line[i++] = str(el.getDiscardDataNotValidForAnalyzer());
					line[i++] = str(el.getMeasureUnitName());
					line[i++] = str(el.getNumDec());
					line[i++] = str(el.getLinearizationCoefficient());
					line[i++] = str(el.getLinearizationOffset());
					line[i++] = str(el.getMinValue());
					line[i++] = str(el.getMaxValue());
					String avg = "";
					if (el.getAvgPeriod() != null)
						avg = str(el.getAvgPeriod());
					else
						for (Integer period : el.getAvgPeriods())
							avg += str(period) + " ";
					line[i++] = str(avg);
					listRows.add(line);
				}
			}
		}
		Collections.sort(listRows, new StringArrayComparator(new int[] { 3, 1, 2 }));
		listRows.add(0, title);
		return listRows;
	}

	private String str(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}

	private class StringArrayComparator implements Comparator<String[]> {

		private int[] indexes;

		StringArrayComparator(int[] indexes) {
			this.indexes = indexes;
		}

		@Override
		public int compare(String[] s1, String[] s2) {
			for (int index : indexes) {
				if (s1.length <= index || s2.length <= index)
					return 0;
				int result = s1[index].compareToIgnoreCase(s2[index]);
				if (result != 0)
					return result;
			}
			return 0;
		}
	}

}
