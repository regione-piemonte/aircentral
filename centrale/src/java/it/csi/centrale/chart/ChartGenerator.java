/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
package it.csi.centrale.chart;

import it.csi.centrale.Centrale;
import it.csi.centrale.PropertyUtil;
import it.csi.centrale.data.station.Analyzer;
import it.csi.centrale.data.station.GenericElement;
import it.csi.centrale.data.station.ScalarElement;
import it.csi.centrale.data.station.Station;
import it.csi.centrale.ui.client.UserParamsException;
import it.csi.centrale.ui.client.data.common.DataObject;

import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * Class that manage the chart on Centrale
 * 
 * @author isabella.vespa@csi.it
 *
 */
public class ChartGenerator {

	static Logger logger = Logger.getLogger("uiservice."
			+ ChartGenerator.class.getSimpleName());

	private SimpleDateFormat sdfTimestamp = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");

	private PropertyUtil propertyUtil;

	private static final String DECIMAL_SEPARATOR = ".";

	private String locale;

	private Double yThresholdLow;

	private Double yThresholdHigh;

	private String[][] fieldsMatrix;

	public ChartGenerator(String locale) {
		try {
			propertyUtil = new PropertyUtil("it/csi/centrale/MessageBundleCore");
		} catch (IOException e) {
			propertyUtil = null;
		}
		this.locale = locale;
	}

	/**
	 * Generate the chart
	 * @param title
	 * @param legend
	 * @param measureUnit
	 * @param interpolated
	 * @param type
	 * @return chart
	 * @throws UserParamsException
	 */
	public JFreeChart generateChart(String title, String legend,
			String measureUnit, boolean interpolated, int type)
			throws UserParamsException {
		/*
		 * Hold our stored chart name, it will be returned to the GWT caller.
		 */

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = null;
		series = new XYSeries(legend);

		for (int i = 0; i < fieldsMatrix.length; i++) {
			String value = fieldsMatrix[i][0];
			String dateStr = fieldsMatrix[i][1];
			// create data
			Date date = null;
			try {
				date = sdfTimestamp.parse(dateStr);
			} catch (ParseException e) {
				logger.error("", e);
				throw new UserParamsException(propertyUtil.getProperty(locale,
						"date_error"));
			}
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(date);

			Double dato = null;
			if (value != null && !value.equals("")) {
				dato = new Double(value);
			}

			// add point to series
			series.add(c.getTimeInMillis(), dato);
		}// end for

		dataset.addSeries(series);

		// Add prevalidation thresholds, if requested
		if (yThresholdHigh != null && fieldsMatrix.length > 0) {
			XYSeries thresholdHigh = new XYSeries(propertyUtil.getProperty(
					locale, "threshold_max"));
			try {
				Date date = sdfTimestamp.parse(fieldsMatrix[0][1]);
				thresholdHigh.add(date.getTime(), yThresholdHigh);
				date = sdfTimestamp
						.parse(fieldsMatrix[fieldsMatrix.length - 1][1]);
				thresholdHigh.add(date.getTime(), yThresholdHigh);
				dataset.addSeries(thresholdHigh);
			} catch (ParseException e) {
				throw new UserParamsException(propertyUtil.getProperty(locale,
						"date_error_threshold_max"));
			}
		}

		if (yThresholdLow != null && fieldsMatrix.length > 0) {
			XYSeries thresholdLow = new XYSeries(propertyUtil.getProperty(
					locale, "threshold_min"));
			try {
				Date date = sdfTimestamp.parse(fieldsMatrix[0][1]);
				thresholdLow.add(date.getTime(), yThresholdLow);
				date = sdfTimestamp
						.parse(fieldsMatrix[fieldsMatrix.length - 1][1]);
				thresholdLow.add(date.getTime(), yThresholdLow);
				dataset.addSeries(thresholdLow);
			} catch (ParseException e) {
				throw new UserParamsException(propertyUtil.getProperty(locale,
						"date_error_threshold_min"));
			}
		}

		// NOTE: the chart title is set to null because the title is already
		// shown outside the chart
		JFreeChart chart = ChartFactory.createXYLineChart(title, // chart title
				propertyUtil.getProperty(locale, "date"), // x axis label
				propertyUtil.getProperty(locale, "value"), // y axis label
				(XYDataset) dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		// set graphic option
		chart.setBackgroundPaint(Color.white);
		// set graphic border
		RectangleInsets rect = new RectangleInsets(15, 5, 15, 15);
		chart.setPadding(rect);

		// get a reference to the plot for further customization...
		final XYPlot plot = chart.getXYPlot();

		// sfondo bianco
		plot.setBackgroundPaint(Color.white);

		plot.setDomainGridlinePaint(Color.lightGray);

		plot.setRangeGridlinePaint(Color.lightGray);

		plot.setNoDataMessage(propertyUtil.getProperty(locale, "no_data"));

		// Asse X -> domain axis
		DateAxis dateAxis = new DateAxis();
		dateAxis.setDateFormatOverride(sdfTimestamp);
		dateAxis.setAutoRange(true);
		dateAxis.setLabel(propertyUtil.getProperty(locale, "date"));
		plot.setDomainAxis(0, dateAxis);
		plot.setRenderer(new XYLineAndShapeRenderer(interpolated,
				(type == 1 ? false : true)));
		// Asse Y -> range axis
		NumberAxis numberAxis = new NumberAxis();
		numberAxis.setLabel(propertyUtil.getProperty(locale, "value")
				+ (measureUnit == null || measureUnit.equals("") ? "" : " ["
						+ measureUnit + "]"));

		numberAxis.setNumberFormatOverride(new DecimalFormat("###.##;-###.##"));
		plot.setRangeAxis(0, numberAxis);

		return chart;
	}// end generateChartImpl

	public void prepareParameter(List<DataObject> dataList,
			String elementIdStr, boolean showMinMax) throws UserParamsException {
		DecimalFormatSymbols dfs = null;
		DecimalFormat df = null;

		dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(DECIMAL_SEPARATOR.charAt(0));
		df = new DecimalFormat();
		df.setDecimalFormatSymbols(dfs);

		df.setGroupingUsed(false);

		if (dataList != null) {
			fieldsMatrix = new String[dataList.size()][2];
			Iterator<DataObject> iterator = dataList.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				DataObject dataObj = iterator.next();
				df.setMinimumFractionDigits(dataObj.getNumDec());
				df.setMaximumFractionDigits(dataObj.getNumDec());
				fieldsMatrix[i][0] = dataObj.getValue() != null ? df
						.format(dataObj.getValue()) : "";
				fieldsMatrix[i][1] = sdfTimestamp.format(dataObj.getDate());
				i++;
			}// end while
			double[] minMax = getRanges(new Integer(elementIdStr).intValue());
			yThresholdLow = showMinMax ? minMax[0] : null;
			yThresholdHigh = showMinMax ? minMax[1] : null;

		} else {
			throw new UserParamsException(propertyUtil.getProperty(locale,
					"no_data"));
		}

	}

	private double[] getRanges(int elementId) {
		double[] ret = new double[2];
		boolean founded = false;
		Vector<Station> stVect = Centrale.getInstance().getConfigInfo()
				.getStVector();
		for (int i = 0; i < stVect.size() && !founded; i++) {
			Station station = stVect.get(i);
			for (int j = 0; j < station.getAnalyzersVect().size() && !founded; j++) {
				Analyzer analyzer = station.getAnalyzersVect().get(j);
				for (int k = 0; k < analyzer.getElements().size() && !founded; k++) {
					GenericElement element = analyzer.getElements().get(k);
					if (element instanceof ScalarElement) {
						founded = true;
						ret[0] = ((ScalarElement) (element)).getMinValue();
						ret[1] = ((ScalarElement) (element)).getMaxValue();
					}
				}
			}
		}
		return ret;
	}
}
