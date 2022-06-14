/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa 
 * Purpose of file:   Page for manage  measure unit fields of common
 * configuration.
 * Change log:
 *   2009-04-08: initial version
 * ----------------------------------------------------------------------------
 * $Id: MeasureUnitWidget.java,v 1.11 2014/09/18 09:46:57 vespa Exp $
 * ----------------------------------------------------------------------------
 */
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.data.MeasureUnitInfo;
import it.csi.centrale.ui.client.pagecontrol.UIPage;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page for manage measure unit fields of common configuration.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class MeasureUnitWidget extends UIPage {

	private static final String VV2MPV = "vv2mpv";

	private static final String NULLVALUE = "-1";

	private static final String DEFAULT_CONVERSION_MULTIPLYER = "1.0";

	private VerticalPanel externalPanel;

	private PanelButtonWidget panelButton;

	private FlexTable table = new FlexTable();

	private TextBox measureNameTextBox;

	private TextBox descriptionTextBox;

	private ListBox physicalDimensionListBox;

	private CheckBox allowedForAnalyzerCheckBox;

	private CheckBox allowedForAcquisitionCheckBox;

	private TextBox conversionMultiplyerTextBox;

	private TextBox conversionAddendumTextBox;

	private ListBox conversionFormulaListBox;

	private String measureName;

	public MeasureUnitWidget() {

		panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);
		panelButton.addButton(CentraleUIConstants.BACK, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CentraleUI.setCurrentPage(CentraleUI.commonCfgWidget);
			}
		});

		ClickHandler helpClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("./help/" + CentraleUI.getLocale()
						+ "/index.html#conf_measureUnitWidget", "", "");
			}
		};
		panelButton.addButton(CentraleUIConstants.HELP, helpClickHandler);
		externalPanel = CentraleUI.getTitledExternalPanel(
				CentraleUI.getMessages().common_cfg_title(), panelButton, false);

		Label measureLabel = new Label();
		measureLabel.setStyleName("gwt-Label-title");
		measureLabel.setText(CentraleUI.getMessages().measure_config());

		// panel that contains info for new physical dimension
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed");

		table.getCellFormatter().setWidth(0, 0, "180px");
		table.getCellFormatter().setWidth(0, 1, "220px");
		table.getCellFormatter().setWidth(0, 2, "180px");
		table.getCellFormatter().setWidth(0, 3, "220px");
		table.setCellPadding(5);
		table.setCellSpacing(5);
		panel.add(table);

		table.setText(0, 0, "* " + CentraleUI.getMessages().measure_name());
		measureNameTextBox = new TextBox();
		measureNameTextBox.setStyleName("gwt-bg-text-orange");
		measureNameTextBox.setWidth("180px");
		table.setWidget(0, 1, measureNameTextBox);

		table.setText(0, 2, "* " + CentraleUI.getMessages().description());
		descriptionTextBox = new TextBox();
		descriptionTextBox.setStyleName("gwt-bg-text-orange");
		descriptionTextBox.setWidth("220px");
		table.setWidget(0, 3, descriptionTextBox);

		table.setText(1, 0, CentraleUI.getMessages().physical_dim());
		physicalDimensionListBox = new ListBox();
		physicalDimensionListBox.setStyleName("gwt-bg-text-orange");
		physicalDimensionListBox.setWidth("180px");
		table.setWidget(1, 1, physicalDimensionListBox);

		TextBoxKeyPressHandler positiveKeyPreesHandler = new TextBoxKeyPressHandler(
				true);
		TextBoxKeyPressHandler numericKeyPressHandler = new TextBoxKeyPressHandler(
				false);
		table.setText(1, 2, CentraleUI.getMessages().conversion_addendum());
		conversionAddendumTextBox = new TextBox();
		conversionAddendumTextBox.setStyleName("gwt-bg-text-orange");
		conversionAddendumTextBox.setWidth("220px");
		conversionAddendumTextBox.addKeyPressHandler(numericKeyPressHandler);
		table.setWidget(1, 3, conversionAddendumTextBox);

		table.setText(2, 0, CentraleUI.getMessages().allowed_for_analyzer());
		allowedForAnalyzerCheckBox = new CheckBox();
		allowedForAnalyzerCheckBox.setStyleName("gwt-bg-text-orange");
		allowedForAnalyzerCheckBox.setValue(true);
		table.setWidget(2, 1, allowedForAnalyzerCheckBox);

		table.setText(2, 2, CentraleUI.getMessages().conversion_multiplyer());
		conversionMultiplyerTextBox = new TextBox();
		conversionMultiplyerTextBox.setStyleName("gwt-bg-text-orange");
		conversionMultiplyerTextBox.setWidth("220px");
		conversionMultiplyerTextBox.addKeyPressHandler(positiveKeyPreesHandler);
		table.setWidget(2, 3, conversionMultiplyerTextBox);

		table.setText(3, 0, CentraleUI.getMessages().allowed_for_acquisition());
		allowedForAcquisitionCheckBox = new CheckBox();
		allowedForAcquisitionCheckBox.setStyleName("gwt-bg-text-orange");
		allowedForAcquisitionCheckBox.setValue(true);
		table.setWidget(3, 1, allowedForAcquisitionCheckBox);

		table.setText(3, 2, CentraleUI.getMessages().conversion_formula());
		conversionFormulaListBox = new ListBox();
		conversionFormulaListBox.setStyleName("gwt-bg-text-orange");
		conversionFormulaListBox.setWidth("220px");
		conversionFormulaListBox.addItem(CentraleUI.getMessages().none(), NULLVALUE);
		// MODIFICATO per gwt 2.5
		// conversionFormulaListBox.addItem(CentraleUI.messages.none(), null);
		conversionFormulaListBox.addItem(VV2MPV, VV2MPV);
		table.setWidget(3, 3, conversionFormulaListBox);

		// create buttons for undo and save
		PanelButtonWidget panelButton2 = new PanelButtonWidget(
				PanelButtonWidget.ORANGE);

		// button cancel
		ClickHandler cancelClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
				setField();
			}
		};
		panelButton2.addButton(CentraleUIConstants.UNDO, cancelClickHandler);

		// button send/verify
		ClickHandler sendVerifyClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MeasureUnitInfo measureInfo = new MeasureUnitInfo();
				measureInfo.setMeasureName(measureNameTextBox.getText());
				measureInfo.setDescription(descriptionTextBox.getText());
				// verify mandatory fields
				if (measureInfo.getMeasureName().equals("")
						|| measureInfo.getDescription().equals("")) {
					Window.alert(CentraleUI.getMessages().mandatory());
					reset();
					setField();
				} else {
					measureInfo.setPhysicalDimension(physicalDimensionListBox
							.getValue(physicalDimensionListBox
									.getSelectedIndex()));
					measureInfo.setConversionFormula(conversionFormulaListBox
							.getValue(conversionFormulaListBox
									.getSelectedIndex()));
					if (measureInfo.getConversionFormula().equals(NULLVALUE))
						measureInfo.setConversionFormula(null);
					Double convAddedndum = null;
					if (!conversionAddendumTextBox.getText().equals(""))
						convAddedndum = new Double(
								conversionAddendumTextBox.getText());
					measureInfo.setConversionAddendum(convAddedndum);
					Double convMultiplyer = null;
					if (!conversionMultiplyerTextBox.getText().equals(""))
						convMultiplyer = new Double(
								conversionMultiplyerTextBox.getText());
					measureInfo.setConversionMultiplyer(convMultiplyer);
					measureInfo.setAllowedForAcquisition(allowedForAcquisitionCheckBox
							.getValue());
					measureInfo.setAllowedForAnalyzer(allowedForAnalyzerCheckBox
							.getValue());
					if (!allowedForAcquisitionCheckBox.getValue()
							&& !allowedForAnalyzerCheckBox.getValue()) {
						// error: one must be checked
						Window.alert(CentraleUI.getMessages().mandatory_check());
					} else {
						CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
								.create(CentraleUIService.class);
						ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
						endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()
								+ "uiservice");
						if (measureName.equals("")) {
							// case of new measure unit
							AsyncCallback<String> callback = new UIAsyncCallback<String>() {
								public void onSuccess(String errStr) {
									if (errStr != null)
										Window.alert(errStr);
									else {
										Window.alert(CentraleUI.getMessages()
												.measure_inserted());
										CentraleUI
												.setCurrentPage(CentraleUI.commonCfgWidget);
									}// end else
								}// end onSuccess
							};
							centraleService.insertMeasureUnit(measureInfo,
									callback);
						} else {
							// case of existing measure unit
							AsyncCallback<String> callback = new UIAsyncCallback<String>() {
								public void onSuccess(String errStr) {
									if (errStr != null)
										Window.alert(errStr);
									else {
										Window.alert(CentraleUI.getMessages()
												.measure_updated());
										CentraleUI
												.setCurrentPage(CentraleUI.commonCfgWidget);
									}// end else
								}// end onSuccess
							};
							centraleService.updateMeasureUnit(measureInfo,
									callback);
						}// end else
					}// end else
				}// end else mandatory
			}// end onClick
		};
		panelButton2.addButton(CentraleUIConstants.SAVE, sendVerifyClickHandler);

		// panel that contains button for connection info
		HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setStyleName("gwt-button-panel");
		hPanel2.add(panelButton2);
		hPanel2.setSpacing(10);
		hPanel2.setCellHorizontalAlignment(panelButton2,
				HasHorizontalAlignment.ALIGN_CENTER);
		panel.add(hPanel2);
		panel.add(new Label(CentraleUI.getMessages().mandatory()));

		externalPanel.add(measureLabel);
		externalPanel.add(panel);

		initWidget(externalPanel);
	}// end constructor

	private void setField() {
		CentraleUIServiceAsync centraleService = (CentraleUIServiceAsync) GWT
				.create(CentraleUIService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) centraleService;
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "uiservice");

		// load physical dimension
		AsyncCallback<List<String>> callback = new UIAsyncCallback<List<String>>() {
			public void onSuccess(List<String> elementList) {
				Iterator<String> it = elementList.iterator();
				while (it.hasNext()) {
					String name = it.next();
					physicalDimensionListBox.addItem(name, name);
				}// end while
				if (physicalDimensionListBox.getItemCount() != 0) {
					if (!measureName.equals("")) {
						// load measure unit
						CentraleUIServiceAsync centraleService2 = (CentraleUIServiceAsync) GWT
								.create(CentraleUIService.class);
						ServiceDefTarget endpoint2 = (ServiceDefTarget) centraleService2;
						endpoint2.setServiceEntryPoint(GWT.getModuleBaseURL()
								+ "uiservice");
						AsyncCallback<MeasureUnitInfo> callback2 = new UIAsyncCallback<MeasureUnitInfo>() {
							public void onSuccess(MeasureUnitInfo measureInfo) {
								measureNameTextBox
										.setText(measureInfo.getMeasureName());
								descriptionTextBox
										.setText(measureInfo.getDescription());
								for (int i = 0; i < physicalDimensionListBox
										.getItemCount(); i++) {
									if (physicalDimensionListBox
											.getValue(i)
											.equals(measureInfo.getPhysicalDimension()))
										physicalDimensionListBox
												.setItemSelected(i, true);
								}// end for
								allowedForAcquisitionCheckBox
										.setValue(measureInfo.isAllowedForAcquisition());
								allowedForAnalyzerCheckBox
										.setValue(measureInfo.isAllowedForAnalyzer());
								conversionAddendumTextBox
										.setText((measureInfo.getConversionAddendum() != null ? measureInfo.getConversionAddendum()
												.toString() : ""));
								conversionMultiplyerTextBox
										.setText((measureInfo.getConversionMultiplyer() != null ? measureInfo.getConversionMultiplyer()
												.toString()
												: DEFAULT_CONVERSION_MULTIPLYER));
								for (int i = 0; i < conversionFormulaListBox
										.getItemCount(); i++) {
									if (conversionFormulaListBox
											.getValue(i)
											.equals(measureInfo.getConversionFormula()))
										conversionFormulaListBox
												.setSelectedIndex(i);
								}// end for

							}// end onSuccess
						};
						centraleService2.getMeasureUnitInfo(measureName,
								callback2);
					} // end if
				} else {
					Window.alert(CentraleUI.getMessages()
							.insert_physical_dimension());
					CentraleUI
							.setCurrentPage(CentraleUI.physicalDimensionWidget);
				}

			}// end onSuccess
		};
		centraleService.getPhysicalDimension(callback);
	}// end setField

	public void setParameter(String name) {
		this.measureName = name;
	}// end setParameter

	@Override
	protected void reset() {
		physicalDimensionListBox.clear();
		measureNameTextBox.setText("");
		descriptionTextBox.setText("");
		allowedForAcquisitionCheckBox.setValue(true);
		allowedForAnalyzerCheckBox.setValue(true);
		conversionAddendumTextBox.setText("");
		for (int i = 0; i < conversionFormulaListBox.getItemCount(); i++) {
			conversionFormulaListBox.setItemSelected(i, false);
		}// end for
		conversionMultiplyerTextBox.setText(DEFAULT_CONVERSION_MULTIPLYER);
	}// end reset

	@Override
	protected void loadContent() {
		setField();
	}

}// end class
