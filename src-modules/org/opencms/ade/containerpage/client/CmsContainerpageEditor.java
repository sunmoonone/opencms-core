/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/ade/containerpage/client/Attic/CmsContainerpageEditor.java,v $
 * Date   : $Date: 2010/04/06 14:22:07 $
 * Version: $Revision: 1.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.ade.containerpage.client;

import org.opencms.ade.containerpage.client.ui.CmsToolbarClickHandler;
import org.opencms.ade.containerpage.client.ui.CmsToolbarClipboardMenu;
import org.opencms.ade.containerpage.client.ui.CmsToolbarEditButton;
import org.opencms.ade.containerpage.client.ui.CmsToolbarMoveButton;
import org.opencms.ade.containerpage.client.ui.CmsToolbarPropertiesButton;
import org.opencms.ade.containerpage.client.ui.CmsToolbarPublishButton;
import org.opencms.ade.containerpage.client.ui.CmsToolbarRemoveButton;
import org.opencms.ade.containerpage.client.ui.CmsToolbarSelectionButton;
import org.opencms.ade.containerpage.client.ui.I_CmsContainerpageToolbarButton;
import org.opencms.gwt.client.A_CmsEntryPoint;
import org.opencms.gwt.client.draganddrop.I_CmsLayoutBundle;
import org.opencms.gwt.client.ui.CmsImageButton;
import org.opencms.gwt.client.ui.CmsToolbar;
import org.opencms.gwt.client.ui.css.I_CmsImageBundle;
import org.opencms.gwt.client.util.CmsDomUtil;
import org.opencms.gwt.client.util.CmsDomUtil.Style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The container page editor.<p>
 * 
 * @author Tobias Herrmann
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 8.0.0
 */
public class CmsContainerpageEditor extends A_CmsEntryPoint {

    /** The editor instance. */
    public static CmsContainerpageEditor INSTANCE;

    private int m_bodyMarginTop;

    /** The currently active button. */
    private I_CmsContainerpageToolbarButton m_currentButton;

    /** The tool-bar. */
    private CmsToolbar m_toolbar;

    /** List of buttons of the tool-bar. */
    private List<I_CmsContainerpageToolbarButton> m_toolbarButtons;

    /**
     * Returns the currently active button. May return <code>null</code>, if none is active.<p>
     * 
     * @return the current button
     */
    public I_CmsContainerpageToolbarButton getCurrentButton() {

        return m_currentButton;
    }

    /**
     * Returns the tool-bar widget.<p>
     * 
     * @return the tool-bar widget
     */
    public CmsToolbar getToolbar() {

        return m_toolbar;
    }

    /**
     * Returns the list of registered tool-bar buttons.<p>
     * 
     * @return the tool-bar buttons
     */
    public List<I_CmsContainerpageToolbarButton> getToolbarButtons() {

        return m_toolbarButtons;
    }

    /**
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    @Override
    public void onModuleLoad() {

        super.onModuleLoad();
        INSTANCE = this;
        m_toolbarButtons = new ArrayList<I_CmsContainerpageToolbarButton>();
        m_toolbarButtons.add(new CmsToolbarPublishButton());
        m_toolbarButtons.add(new CmsToolbarSelectionButton());
        m_toolbarButtons.add(new CmsToolbarMoveButton());
        m_toolbarButtons.add(new CmsToolbarEditButton());
        m_toolbarButtons.add(new CmsToolbarRemoveButton());
        m_toolbarButtons.add(new CmsToolbarPropertiesButton());
        m_toolbarButtons.add(new CmsToolbarClipboardMenu());
        initToolbar();
        CmsContainerpageDataProvider.init();

        I_CmsLayoutBundle.INSTANCE.dragdropCss().ensureInjected();
        org.opencms.ade.containerpage.client.ui.css.I_CmsLayoutBundle.INSTANCE.containerpageCss().ensureInjected();

    }

    /**
     * Sets the current button.<p>
     * 
     * @param button the current button
     */
    public void setCurrentButton(I_CmsContainerpageToolbarButton button) {

        m_currentButton = button;
    }

    /**
     * Shows the tool-bar.<p>
     * 
     * @param show if <code>true</code> the tool-bar will be shown
     */
    public void showToolbar(boolean show) {

        Element body = Document.get().getBody();
        if (show) {
            body.addClassName(org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.toolbarCss().toolbarShow());
            body.removeClassName(org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.toolbarCss().toolbarHide());
            body.getStyle().setMarginTop(m_bodyMarginTop + 36, Unit.PX);
        } else {
            body.removeClassName(org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.toolbarCss().toolbarShow());
            body.addClassName(org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.toolbarCss().toolbarHide());
            body.getStyle().setMarginTop(m_bodyMarginTop, Unit.PX);
        }
    }

    /**
     * Returns if the tool-bar is visible.<p>
     * 
     * @return <code>true</code> if the tool-bar is visible
     */
    public boolean toolbarVisible() {

        return !CmsDomUtil.hasClass(
            org.opencms.gwt.client.ui.css.I_CmsLayoutBundle.INSTANCE.toolbarCss().toolbarHide(),
            Document.get().getBody());
    }

    /**
     * Initialises the tool-bar and its buttons.<p>
     */
    private void initToolbar() {

        m_bodyMarginTop = CmsDomUtil.getCurrentStyleInt(Document.get().getBody(), Style.marginTop);
        m_toolbar = new CmsToolbar();
        CmsToolbarClickHandler handler = new CmsToolbarClickHandler();
        Iterator<I_CmsContainerpageToolbarButton> it = m_toolbarButtons.iterator();
        while (it.hasNext()) {
            I_CmsContainerpageToolbarButton button = it.next();
            button.addClickHandler(handler);
            if (button.showLeft()) {
                m_toolbar.addLeft((Widget)button);
            } else {
                m_toolbar.addRight((Widget)button);
            }
        }
        RootPanel.get().add(m_toolbar);
        showToolbar(false);
        CmsImageButton toggleToolbarButton = new CmsImageButton(I_CmsImageBundle.INSTANCE.style().opencmsLogo(), true);
        RootPanel.get().add(toggleToolbarButton);
        toggleToolbarButton.addClickHandler(new ClickHandler() {

            /**
             * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
             */
            public void onClick(ClickEvent event) {

                showToolbar(!toolbarVisible());

            }

        });

        toggleToolbarButton.getElement().getStyle().setPosition(Position.FIXED);
        toggleToolbarButton.getElement().getStyle().setTop(-3, Unit.PX);
        toggleToolbarButton.getElement().getStyle().setRight(50, Unit.PX);
        toggleToolbarButton.getElement().getStyle().setZIndex(10010);

    }

}
