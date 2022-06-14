/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.stationreport;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for element configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 */
public abstract class ElementCfg {

	private String parameterId;
	private Boolean enabled;
	private String analyzerMeasureUnitName;
	private Double rangeLow;
	private Double rangeHigh;
	private Double correctionCoefficient;
	private Double correctionOffset;
	private Integer acqPeriod; // s
	private Integer acqDuration; // minutes
	private Integer acqDelay; // minutes
	private String measureUnitName;
	private Integer numDec;
	private Double linearizationCoefficient;
	private Double linearizationOffset;
	private Double minValue;
	private Double maxValue;
	private Boolean discardDataNotValidForAnalyzer;
	private Integer avgPeriod; // minutes
	private List<Integer> avgPeriods = new ArrayList<Integer>();

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getAnalyzerMeasureUnitName() {
		return analyzerMeasureUnitName;
	}

	public void setAnalyzerMeasureUnitName(String analyzerMeasureUnitName) {
		this.analyzerMeasureUnitName = analyzerMeasureUnitName;
	}

	public Double getRangeLow() {
		return rangeLow;
	}

	public void setRangeLow(Double rangeLow) {
		this.rangeLow = rangeLow;
	}

	public Double getRangeHigh() {
		return rangeHigh;
	}

	public void setRangeHigh(Double rangeHigh) {
		this.rangeHigh = rangeHigh;
	}

	public Double getCorrectionCoefficient() {
		return correctionCoefficient;
	}

	public void setCorrectionCoefficient(Double correctionCoefficient) {
		this.correctionCoefficient = correctionCoefficient;
	}

	public Double getCorrectionOffset() {
		return correctionOffset;
	}

	public void setCorrectionOffset(Double correctionOffset) {
		this.correctionOffset = correctionOffset;
	}

	public Integer getAcqPeriod() {
		return acqPeriod;
	}

	public void setAcqPeriod(Integer acqPeriod) {
		this.acqPeriod = acqPeriod;
	}

	public Integer getAcqDuration() {
		return acqDuration;
	}

	public void setAcqDuration(Integer acqDuration) {
		this.acqDuration = acqDuration;
	}

	public Integer getAcqDelay() {
		return acqDelay;
	}

	public void setAcqDelay(Integer acqDelay) {
		this.acqDelay = acqDelay;
	}

	public String getMeasureUnitName() {
		return measureUnitName;
	}

	public void setMeasureUnitName(String measureUnitName) {
		this.measureUnitName = measureUnitName;
	}

	public Integer getNumDec() {
		return numDec;
	}

	public void setNumDec(Integer numDec) {
		this.numDec = numDec;
	}

	public Double getLinearizationCoefficient() {
		return linearizationCoefficient;
	}

	public void setLinearizationCoefficient(Double linearizationCoefficient) {
		this.linearizationCoefficient = linearizationCoefficient;
	}

	public Double getLinearizationOffset() {
		return linearizationOffset;
	}

	public void setLinearizationOffset(Double linearizationOffset) {
		this.linearizationOffset = linearizationOffset;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Boolean getDiscardDataNotValidForAnalyzer() {
		return discardDataNotValidForAnalyzer;
	}

	public void setDiscardDataNotValidForAnalyzer(Boolean discardDataNotValidForAnalyzer) {
		this.discardDataNotValidForAnalyzer = discardDataNotValidForAnalyzer;
	}

	public Integer getAvgPeriod() {
		return avgPeriod;
	}

	public void setAvgPeriod(Integer avgPeriod) {
		this.avgPeriod = avgPeriod;
	}

	public List<Integer> getAvgPeriods() {
		return avgPeriods;
	}

	public void setAvgPeriods(List<Integer> avgPeriods) {
		this.avgPeriods = avgPeriods;
	}

}
