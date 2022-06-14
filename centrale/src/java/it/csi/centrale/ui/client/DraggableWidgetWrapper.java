package it.csi.centrale.ui.client;

/*
 * Copyright (c) 2011 Nathan Byrd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a draggable widget. See
 * http://chaoticjava.com/posts/drag-and-drop-in-gwt-the-how-to/ 
 * 
 */
public class DraggableWidgetWrapper extends SimplePanel implements
		HasMouseDownHandlers, HasMouseUpHandlers, HasMouseMoveHandlers,
		Event.NativePreviewHandler {

	private boolean dragging = false;
	private int dragStartX = 0;
	private int dragStartY = 0;

	@SuppressWarnings("unused")
	private int minWidth;

	@SuppressWarnings("unused")
	private int minHeight;

	@SuppressWarnings("unused")
	private int maxWidth;

	@SuppressWarnings("unused")
	private int maxHeight;

	public DraggableWidgetWrapper(Widget innerWidget, Integer x, Integer y,
			final int minWidth, final int minHeight, final int maxWidth,
			final int maxHeight) {
		super();
		add(innerWidget);
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		getElement().getStyle().setProperty( "position", "absolute");
		if (x != null && y != null) {
			getElement().getStyle().setProperty( "left", x.toString() + "px");
			getElement().getStyle().setProperty( "top", y.toString() + "px");
		}

		DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement())
				| Event.MOUSEEVENTS);

		Event.addNativePreviewHandler(this);

		addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				dragging = true;
				DOM.setCapture(getElement());
				dragStartX = event.getRelativeX(getElement());
				dragStartY = event.getRelativeY(getElement());
			}
		});

		addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent mouseUpEvent) {
				dragging = false;
				DOM.releaseCapture(getElement());
			}
		});

		addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (dragging) {
					int newX = Math.max(0, event.getRelativeX(getElement())
							+ getAbsoluteLeft() - dragStartX);
					int newY = Math.max(0, event.getRelativeY(getElement())
							+ getAbsoluteTop() - dragStartY);
					setPosition(newX, newY);

					// correct values for avoiding widget from going out of the
					// map
					if (newX < minWidth)
						newX = minWidth;
					if ((newX + ((Widget) event.getSource()).getOffsetWidth()) > (minWidth + maxWidth))
						newX = (maxWidth + minWidth - ((Widget) event
								.getSource()).getOffsetWidth());
					if (newY < minHeight)
						newY = minHeight;
					if ((newY + ((Widget) event.getSource()).getOffsetHeight()) > (minHeight + maxHeight))
						newY = (maxHeight + minHeight - ((Widget) event
								.getSource()).getOffsetHeight());
					((Widget) event.getSource()).getElement().getStyle().setProperty(
							"left", new Integer(newX).toString() + "px");
					((Widget) event.getSource()).getElement().getStyle().setProperty(
							"top", new Integer(newY).toString() + "px");
					// setting new values in cfgMapWidget
					CentraleUI.cfgMapWidget.setNewXY(
							((Widget) event.getSource()), new Integer(newX),
							new Integer(newY));

				}
			}
		});

	}

	public void show() {
		RootPanel.get().add(this);
	}

	public void show(int startX, int startY) {
		setPosition(startX, startY);
		show();
	}

	public void showDragging(int startX, int startY, int draggingStartX,
			int draggingStartY) {
		show(startX, startY);
		dragging = true;
		this.dragStartX = draggingStartX;
		this.dragStartY = draggingStartY;
	}

	public void setPosition(int left, int top) {
		setPosition(null, left, top);
	}

	public void setPosition(final UIObject relativeObject, int left, int top) {
		if (relativeObject != null) {
			left += relativeObject.getAbsoluteLeft();
			top += relativeObject.getAbsoluteTop();
		}

		// Account for difference between absolute position and the body's
		// positioning context
		left -= Document.get().getBodyOffsetLeft();
		top -= Document.get().getBodyOffsetTop();

		getElement().getStyle().setPropertyPx("left", left);
		getElement().getStyle().setPropertyPx("top", top);

	}

	@Override
	public void onUnload() {
		RootPanel.get().remove(this);
		super.onUnload();
	}

	public static Widget makeDraggable(Widget innerWidget, Integer x,
			Integer y, final int minWidth, final int minHeight,
			final int maxWidth, final int maxHeight) {
		return new DraggableWidgetWrapper(innerWidget, x, y, minWidth,
				minHeight, maxWidth, maxHeight);
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEUP:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEOVER:
		case Event.ONMOUSEOUT:
			DomEvent.fireNativeEvent(event, this);
			break;
		}
	}

	@Override
	public HandlerRegistration addMouseDownHandler(
			MouseDownHandler mouseDownHandler) {
		return addDomHandler(mouseDownHandler, MouseDownEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(
			MouseMoveHandler mouseMoveHandler) {
		return addDomHandler(mouseMoveHandler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler mouseUpHandler) {
		return addDomHandler(mouseUpHandler, MouseUpEvent.getType());
	}

	@Override
	public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
		Event e = Event.as(event.getNativeEvent());
		if (DOM.eventGetType(e) == Event.ONMOUSEDOWN
				&& getElement().isOrHasChild( DOM.eventGetTarget(e))) {
			e.preventDefault();
		}
	}

	public void setImageDimensions(int minWidth, int minHeight, int maxWidth,
			int maxHeight) {
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
	}
}
