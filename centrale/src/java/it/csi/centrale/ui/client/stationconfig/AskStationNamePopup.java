/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Isabella Vespa
* Purpose of file: dialog box for saving the configuration
* Change log:
*   2008-11-21: initial version
*   2014-05-23: remodeled by Pierfrancesco Vallosio
* ----------------------------------------------------------------------------
* $Id: AskStationNamePopup.java,v 1.1 2014/05/30 10:03:42 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.ui.client.stationconfig;

import it.csi.centrale.ui.client.CentraleUI;
import it.csi.centrale.ui.client.CentraleUIConstants;
import it.csi.centrale.ui.client.PanelButtonWidget;
import it.csi.centrale.ui.client.Utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * dialog box for saving the configuration
 * 
 * @author pierfrancesco.vallosio@consulenti.csi.it
 * 
 */
public class AskStationNamePopup extends PopupPanel {

	private TextBox textShortname;

	public AskStationNamePopup(final SaveAction saveAction) {
		super(false, true);
		Utils.blockForPopup("popup");

		// panel that contains IOUser info and buttons
		VerticalPanel panel = new VerticalPanel();
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Label lblMessage = new Label();
		lblMessage.setText(CentraleUI.getMessages().lbl_insert_shortname());
		panel.add(lblMessage);

		FlexTable table = new FlexTable();
		table.getCellFormatter().setWidth(0, 0, "150px");
		table.getCellFormatter().setWidth(0, 1, "320px");
		table.setCellPadding(2);
		table.setCellSpacing(2);
		panel.add(table);

		textShortname = new TextBox();
		textShortname.setStyleName("gwt-bg-text-orange");
		textShortname.setWidth("180px");
		textShortname.setMaxLength(16);
		table.setWidget(1, 1, textShortname);

		PanelButtonWidget panelButton = new PanelButtonWidget(PanelButtonWidget.ORANGE);

		// button undo
		ClickHandler undoHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Utils.unlockForPopup("popup");
				hide();
			}
		};
		panelButton.addButton(CentraleUIConstants.UNDO, undoHandler);

		// button send/verify
		ClickHandler saveHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveAction.execute(textShortname.getText().trim());
				Utils.unlockForPopup("popup");
				hide();
			}
		};
		panelButton.addButton(CentraleUIConstants.SAVE, saveHandler);

		panel.add(panelButton);
		add(panel);
		show();
	} // end constructor

	@Override
	public void show() {
		super.show();
		int cWidth = Window.getClientWidth();
		int cHeight = Window.getClientHeight();
		setPopupPosition(((cWidth / 2) - 150), ((cHeight / 2) - 100));
		setWidth("300px");
		setHeight("200px");
		setStyleName("gwt-popup-panel");
	}

}