/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: widget that displays charts.
* Change log:
*   2009-02-10: initial version
* ----------------------------------------------------------------------------
* $Id: ShowChartWidget.java,v 1.8 2015/01/19 12:01:05 vespa Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client;

import it.csi.centrale.ui.client.pagecontrol.UIPage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget that displays charts.
 * 
 * @author Isabella Vespa - CSI Piemonte (isabella.vespa@csi.it)
 * 
 */
public class ShowChartWidget extends UIPage {

	private VerticalPanel panel;

	private Label title;

	private Boolean buttonAlreadyShown = null;

	private String chartName = null;

	private String lblTitle;

	public ShowChartWidget() {

		PanelButtonWidget panelButtonWidget = new PanelButtonWidget(PanelButtonWidget.BLUE);
		panelButtonWidget.addButton(CentraleUIConstants.BACK, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (buttonAlreadyShown != null)
					CentraleUI.historyTableWidget.setButtonAlreadyShown(buttonAlreadyShown);
				CentraleUI.goToPreviousPage();
			}

		});
		VerticalPanel externalPanel = CentraleUI.getTitledExternalPanel(CentraleUI.getMessages().chart_title(),
				panelButtonWidget, false);

		// Label and panel for subdevice info
		title = new Label();
		title.setStyleName("gwt-Label-title-blue");

		// panel that contains subdevice info and buttons
		panel = new VerticalPanel();
		panel.setStyleName("gwt-post-boxed-blue");

		externalPanel.add(title);
		externalPanel.add(panel);
		initWidget(externalPanel);

	}// end constructor

	public void setChartNameAndTitle(String chartName, String title) {
		this.chartName = chartName;
		this.lblTitle = title;
	}

	@Override
	protected void reset() {
		buttonAlreadyShown = null;
		panel.clear();
	}

	@Override
	protected void loadContent() {
		if (chartName == null)
			return;
		title.setText(lblTitle);
		ChartImage chartImage = new ChartImage();
		ChartImage image = chartImage.displayChart(chartName);
		panel.add(image);
	}

	public Boolean getButtonAlreadyShown() {
		return buttonAlreadyShown;
	}

	public void setButtonAlreadyShown(Boolean buttonAlreadyShown) {
		this.buttonAlreadyShown = buttonAlreadyShown;
	}

}// end class
