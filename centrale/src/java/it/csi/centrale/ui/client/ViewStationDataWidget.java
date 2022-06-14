/*----------------------------------------------------------------------------
 * SRQA - Centrale
 * Copyright (C) 2008 by Regione Piemonte (http://www.regione.piemonte.it)
 * LICENSE and CREDITS
 * This program is free software and it's released under the terms of the
 * GNU General Public License(GPL) - (http://www.gnu.org/licenses/gpl.html)
 * Please READ carefully the doc/license.txt file for further details
 * Please READ the doc/credits.txt file for complete credits list
 * ----------------------------------------------------------------------------
 * Original Author of file: Silvia Vergnano
 * Purpose of file: A composite Widget that implements the view station data 
 * interface.
 * Change log:
 *   2008-12-05: initial version
 * ----------------------------------------------------------------------------
 * $Id: ViewStationDataWidget.java,v 1.25 2015/01/19 12:01:05 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.common.DataObject;
import it.csi.centrale.ui.client.data.common.WindDataObject;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A composite Widget that implements the view station data interface.
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnao@consulenti.csi.it)
 * 
 */
public class ViewStationDataWidget extends UIPage {

	private Label title = new Label();

	private final FlexTable table;

	private HashMap<Button, ButtonAccessories> map = new HashMap<Button, ButtonAccessories>();

	private int stationId;

	private String stationName;

	private NumberFormat nf;

	private NumberFormat dirNf;

	private DateTimeFormat df;

	private PanelButtonWidget panelButton;

	private List<DataObject> removedDataList = null;

	public ViewStationDataWidget() {
		df = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");

		panelButton = new PanelButtonWidget(PanelButtonWidget.BLUE);

		ClickHandler backClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.goToUpperLevelPage();
			}
		};
		panelButton.addButton(CentraleUIConstants.BACK, backClickHandler);

		ClickHandler refreshClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.updateCurrentPage(null);
			}
		};
		panelButton.addButton(CentraleUIConstants.REFRESH, refreshClickHandler);

		ClickHandler refreshDeletedClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				writeDataOnTable(getRemovedDataList());
			}
		};
		panelButton.addButton(CentraleUIConstants.REFRESH_REMOVED,
				refreshDeletedClickHandler);

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#view_viewStationDataWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);

		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().view_station_data(), panelButton, false);

		title.setStyleName("gwt-Label-title-blue");

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed-blue");

		// Prepare info table's title
		FlexTable headerTable = new FlexTable();
		headerTable.setText(0, 0, CentraleUI.getMessages().link_sample_history());
		headerTable.setText(0, 1, CentraleUI.getMessages().link_mean_history());
		headerTable.setText(0, 2, CentraleUI.getMessages().analyzer());
		headerTable.setText(0, 3, CentraleUI.getMessages().value());
		headerTable.setText(0, 4, CentraleUI.getMessages().lbl_measure_unit());
		headerTable.setText(0, 5, CentraleUI.getMessages().data());
		headerTable.setText(0, 6, CentraleUI.getMessages().flag());
		headerTable.setText(0, 7, CentraleUI.getMessages().multiple_flag());
		headerTable.setText(0, 8, CentraleUI.getMessages().avg_period());

		headerTable.setStyleName("gwt-table-header");
		headerTable.setWidth("100%");
		headerTable.getCellFormatter().setWidth(0, 0, "115px");
		headerTable.getCellFormatter().setWidth(0, 1, "115px");
		headerTable.getCellFormatter().setWidth(0, 2, "220px");
		headerTable.getCellFormatter().setWidth(0, 3, "70px");
		headerTable.getCellFormatter().setWidth(0, 4, "90px");
		headerTable.getCellFormatter().setWidth(0, 5, "130px");
		headerTable.getCellFormatter().setWidth(0, 6, "90px");
		headerTable.getCellFormatter().setWidth(0, 7, "60px");
		headerTable.getCellFormatter().setWidth(0, 8, "75px");

		for (int j = 0; j < 9; j++) {
			headerTable.getCellFormatter().setStyleName(0, j,
					"gwt-table-header");
		}

		panel.add(headerTable);

		// Prepare table for channel info in a ScrollPanel
		ScrollPanel scrollPanel = new ScrollPanel();
		table = new FlexTable();

		table.setStyleName("gwt-table-data");
		table.setWidth("100%");
		scrollPanel.add(table);
		scrollPanel.setHeight("400px");
		panel.add(scrollPanel);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		externalPanel.add(title);
		externalPanel.add(panel);

		initWidget(externalPanel);
	}

	public void setStationInfos(int stId, String stName) {
		this.stationId = stId;
		this.stationName = stName;
	}

	public List<DataObject> getRemovedDataList() {
		return removedDataList;
	}

	public void setRemovedDataList(List<DataObject> removedDataList) {
		this.removedDataList = removedDataList;
	}

	private void setFields() {
		title.setText(CentraleUI.getMessages().view_station_data() + " "
				+ stationName);
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");
		AsyncCallback<Vector<List<DataObject>>> callback = new UIAsyncCallback<Vector<List<DataObject>>>() {
			public void onSuccess(Vector<List<DataObject>> dataListVector) {
				// lista di dati degli analizzatori attuali
				List<DataObject> dataList = dataListVector.elementAt(0);
				// lista di dati degli analizzatori cancellati
				setRemovedDataList(dataListVector.elementAt(1));
				List<DataObject> removedDataList = getRemovedDataList();
				if (removedDataList != null && removedDataList.size() != 0) {
					// se esistono dati di analizzatori cancellati mostro il
					// pulsante altrimenti no
					panelButton.setVisibleButton(CentraleUIConstants.REFRESH_REMOVED,
							true);
				} else
					panelButton.setVisibleButton(CentraleUIConstants.REFRESH_REMOVED,
							false);
				writeDataOnTable(dataList);
			}

		};

		centraleService.getMeansStationDataFields(stationId, callback);
	}

	protected void writeDataOnTable(List<DataObject> dataList) {
		// clear table
		Utils.clearTable(table);

		table.getCellFormatter().setWidth(0, 0, "115px");
		table.getCellFormatter().setWidth(0, 1, "115px");
		table.getCellFormatter().setWidth(0, 2, "220px");
		table.getCellFormatter().setWidth(0, 3, "70px");
		table.getCellFormatter().setWidth(0, 4, "90px");
		table.getCellFormatter().setWidth(0, 5, "130px");
		table.getCellFormatter().setWidth(0, 6, "90px");
		table.getCellFormatter().setWidth(0, 7, "60px");
		table.getCellFormatter().setWidth(0, 8, "75px");

		for (int i = 0; i < dataList.size(); i++) {
			DataObject dataObj = dataList.get(i);

			// prepare buttonAccessories
			ButtonAccessories buttonAccessories = new ButtonAccessories();
			buttonAccessories.stationName = stationName;
			buttonAccessories.stationId = stationId;
			buttonAccessories.analyzerName = dataObj.getAnalyzerName();
			buttonAccessories.elementName = dataObj.getElementName();
			buttonAccessories.analyzerId = new Integer(dataObj.getAnalyzerId())
					.toString();
			buttonAccessories.elementId = new Integer(dataObj.getElementId())
					.toString();
			buttonAccessories.measureUnit = dataObj.getMeasureUnit();
			if (dataObj instanceof WindDataObject)
				buttonAccessories.dirMeasureUnit = ((WindDataObject) (dataObj)).getDirMeasureUnit();
			buttonAccessories.avgPeriod = dataObj.getAvgPeriodId();
			buttonAccessories.numDec = dataObj.getNumDec();
			if (dataObj instanceof WindDataObject)
				buttonAccessories.dirNumDec = ((WindDataObject) dataObj).getDirNumDec();

			// create button for sample history
			PanelButtonWidget panelButton1 = new PanelButtonWidget(
					PanelButtonWidget.NO_COLOR, 3);

			// csv sample button
			ClickHandler csvSampleClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Utils.blockForPopup("popup");
					// create and show popup of ChoosePeriodWidget
					ButtonAccessories accessories = map.get(event.getSource());
					new ChoosePeriodWidget(false, true, accessories,
							ChoosePeriodWidget.SAMPLE_DOWNLOAD_CSV);
				}// end onClick
			};
			Button buttonCsvSample = panelButton1.addButton(
					CentraleUIConstants.CSV_SMALL, csvSampleClickHandler);

			map.put(buttonCsvSample, buttonAccessories);

			// chart sample button
			ClickHandler chartSampleClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Utils.blockForPopup("popup");
					// create and show popup of ChoosePeriodWidget
					ButtonAccessories accessories = map.get(event.getSource());
					new ChoosePeriodWidget(false, true, accessories,
							ChoosePeriodWidget.SAMPLE_DOWNLOAD_CHART);
				}// end onClick
			};
			Button buttonChartSample = panelButton1.addButton(
					CentraleUIConstants.CHART_SMALL, chartSampleClickHandler);

			map.put(buttonChartSample, buttonAccessories);

			// table sample button
			ClickHandler tableSampleClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Utils.blockForPopup("popup");
					// create and show popup of ChoosePeriodWidget
					ButtonAccessories accessories = map.get(event.getSource());
					accessories.windSample = true;
					new ChoosePeriodWidget(false, true, accessories,
							ChoosePeriodWidget.SAMPLE_HISTORY);
				}// end onClick
			};
			Button buttonTableSample = panelButton1.addButton(
					CentraleUIConstants.TABLE_SMALL, tableSampleClickHandler);

			map.put(buttonTableSample, buttonAccessories);

			table.setWidget(i, 0, panelButton1);
			table.getCellFormatter().setAlignment(i, 0,
					HasHorizontalAlignment.ALIGN_CENTER,
					HasVerticalAlignment.ALIGN_MIDDLE);
			table.getCellFormatter().setStyleName(i, 0, "gwt-table-data");

			// create button for means
			PanelButtonWidget panelButton2 = new PanelButtonWidget(
					PanelButtonWidget.NO_COLOR, 3);

			// means csv button
			ClickHandler csvMeansClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ButtonAccessories accessories = map.get(event.getSource());
					Utils.blockForPopup("popup");
					// create and show popup of ChoosePeriodWidget
					new ChoosePeriodWidget(false, true, accessories,
							ChoosePeriodWidget.MEANS_DOWNLOAD_CSV);

				}// end onClick
			};

			Button buttonCsvMeans = panelButton2.addButton(
					CentraleUIConstants.CSV_SMALL, csvMeansClickHandler);

			map.put(buttonCsvMeans, buttonAccessories);

			// means chart button
			ClickHandler chartMeansClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ButtonAccessories accessories = map.get(event.getSource());
					if (accessories.dirMeasureUnit != null) {
						// case of wind element
						Window.alert(CentraleUI.getMessages().chart_not_available());
					} else {
						Utils.blockForPopup("popup");
						// create and show popup of ChoosePeriodWidget
						new ChoosePeriodWidget(false, true, accessories,
								ChoosePeriodWidget.MEANS_DOWNLOAD_CHART);
					}
				}// end onClick
			};
			Button buttonMeansChart = panelButton2.addButton(
					CentraleUIConstants.CHART_SMALL, chartMeansClickHandler);

			map.put(buttonMeansChart, buttonAccessories);

			// means table button
			ClickHandler tableMeansClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Utils.blockForPopup("popup");
					// create and show popup of ChoosePeriodWidget
					ButtonAccessories accessories = map.get(event.getSource());
					accessories.windSample = false;
					new ChoosePeriodWidget(false, true, accessories,
							ChoosePeriodWidget.MEANS_HISTORY);
				}// end onClick
			};
			Button buttonTableMeans = panelButton2.addButton(
					CentraleUIConstants.TABLE_SMALL, tableMeansClickHandler);

			map.put(buttonTableMeans, buttonAccessories);

			table.setWidget(i, 1, panelButton2);
			table.getCellFormatter().setAlignment(i, 1,
					HasHorizontalAlignment.ALIGN_CENTER,
					HasVerticalAlignment.ALIGN_MIDDLE);
			table.getCellFormatter().setStyleName(i, 1, "gwt-table-data");

			table.setText(i, 2, dataObj.getAnalyzerName() + "/"
					+ dataObj.getElementName());
			StringBuffer strBuf = new StringBuffer();
			for (int z = 0; z < dataObj.getNumDec(); z++)
				strBuf.append("#");
			if (strBuf.length() > 0)
				nf = NumberFormat.getFormat("#." + strBuf.toString());
			else
				nf = NumberFormat.getFormat("#");
			if (dataObj instanceof WindDataObject) {
				strBuf = new StringBuffer();
				for (int z = 0; z < ((WindDataObject) (dataObj)).getDirNumDec(); z++)
					strBuf.append("#");
				if (strBuf.length() > 0)
					dirNf = NumberFormat.getFormat("#." + strBuf.toString());
				else
					dirNf = NumberFormat.getFormat("#");
				if (((WindDataObject) (dataObj)).getVectorialSpeed() != null
						&& ((WindDataObject) (dataObj)).getVectorialDirection() != null)
					table.setText(
							i,
							3,
							nf.format(((WindDataObject) (dataObj)).getVectorialSpeed())
									+ " | "
									+ dirNf.format(((WindDataObject) (dataObj)).getVectorialDirection()));
				else {
					if (((WindDataObject) (dataObj)).getVectorialSpeed() != null)
						table.setText(
								i,
								3,
								nf.format(((WindDataObject) (dataObj)).getVectorialSpeed()));
					else {
						if (((WindDataObject) (dataObj)).getVectorialDirection() != null)
							table.setText(
									i,
									3,
									nf.format(((WindDataObject) (dataObj)).getVectorialDirection()));
						else
							table.setText(i, 3, " ");
					}
				}
				table.setText(i, 4, dataObj.getMeasureUnit() + " | "
						+ ((WindDataObject) (dataObj)).getDirMeasureUnit());
			} else {
				if (dataObj.getValue() != null)
					table.setText(i, 3, nf.format(dataObj.getValue()));
				else
					table.setText(i, 3, " ");
				table.setText(i, 4, dataObj.getMeasureUnit());
			}
			table.setText(i, 5, df.format(dataObj.getDate()));
			// flag valid/not valid
			String strValid = "";
			if ("true".equals(new Boolean(dataObj.isFlag()).toString()))
				strValid = CentraleUI.getMessages().not_valid();
			else
				strValid = CentraleUI.getMessages().valid();
			table.setText(i, 6, strValid);
			ClickWidget clickWidget = new ClickWidget(dataObj.getTitle(),
					Integer.toHexString(dataObj.getMultipleFlag()));
			table.setWidget(i, 7, clickWidget);

			table.setText(i, 8, dataObj.getAvgPeriodId().toString());
			for (int j = 2; j < 9; j++) {
				table.getCellFormatter().setStyleName(i, j, "gwt-table-data");
			}
		}
	}

	@Override
	protected void loadContent() {
		setRemovedDataList(null);
		setFields();
	}

}// end class

/**
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 *         This class provides object to associate informations to button in the
 *         hashMap
 */
class ButtonAccessories {

	protected String analyzerName = null;

	protected String elementName = null;

	protected String measureUnit = null;

	protected String dirMeasureUnit = null;

	protected String analyzerId = null;

	protected String elementId = null;

	protected String stationName = null;

	protected int stationId;

	protected String alarmName = null;

	protected Integer avgPeriod = null;

	protected int alarmId = 0;

	protected int numDec = 0;

	protected int dirNumDec = 0;

	/*
	 * windSample:
	 * 
	 * true: case of wind sample
	 * 
	 * false:case of wind means
	 */

	protected boolean windSample = false;

	public ButtonAccessories() {

	}

}// end class ButtonAccessories
