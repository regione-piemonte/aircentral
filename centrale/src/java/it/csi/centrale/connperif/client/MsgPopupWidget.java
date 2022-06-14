/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
/* ----------------------------------------------------------------------------
* Original Author of file: Silvia Vergnano
* Purpose of file: popup page for displaying a message
* Change log:
*   2008-10-27: initial version
* ----------------------------------------------------------------------------
* $Id: MsgPopupWidget.java,v 1.4 2015/10/15 12:10:24 pfvallosio Exp $
* ----------------------------------------------------------------------------
*/
package it.csi.centrale.connperif.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Popup page for displaying a message
 * 
 * @author Silvia Vergnano - CSI Piemonte (silvia.vergnano@consulenti.csi.it)
 * 
 */
public class MsgPopupWidget extends PopupPanel {

	public final MsgPopupWidget msgPopupWidget;

	public MsgPopupWidget() {
		super(false, true);
		msgPopupWidget = this;

		// panel that contains IOUser info and buttons
		VerticalPanel panel = new VerticalPanel();
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		Label msgLabel = new Label(ConnPerif.messages.timeout());
		panel.add(msgLabel);

		Button okButton = new Button();
		okButton.setStyleName("gwt-button-ok-blue");
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Utils.unlockForPopup("popup");
				msgPopupWidget.hide();
				Utils.closeWindow();
			}
		});
		panel.add(okButton);

		this.add(panel);
		this.show();

	} // end constructor

	/**
	 * Show the popup
	 */
	public void show() {
		super.show();

		int cWidth = Window.getClientWidth();
		int cHeight = Window.getClientHeight();
		// int myWidth = getOffsetWidth();
		// int myHeight = getOffsetHeight();
		// Utils.alert("clientwidth:"+cWidth+" clientHeight:"+cHeight+"
		// offsetwidth:"+myWidth+" offsetHeight:"+myHeight);
		// setPopupPosition((cWidth-myWidth)/2,(cHeight-myHeight)/2);
		setPopupPosition(((cWidth / 2) - 150), ((cHeight / 2) - 100));
		setWidth("300px");
		setHeight("200px");
		setStyleName("gwt-popup-panel-blue");
		// DOM.setStyleAttribute(getElement(), "border", " 1px solid #FF8D17");
	}

}
