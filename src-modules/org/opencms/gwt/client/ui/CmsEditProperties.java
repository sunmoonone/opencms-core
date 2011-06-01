/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/ui/Attic/CmsEditProperties.java,v $
 * Date   : $Date: 2011/06/01 13:06:32 $
 * Version: $Revision: 1.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2011 Alkacon Software (http://www.alkacon.com)
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

package org.opencms.gwt.client.ui;

import org.opencms.gwt.client.CmsCoreProvider;
import org.opencms.gwt.client.property.CmsSimplePropertyEditorHandler;
import org.opencms.gwt.client.property.CmsVfsModePropertyEditor;
import org.opencms.gwt.client.rpc.CmsRpcAction;
import org.opencms.gwt.client.ui.contextmenu.I_CmsContextMenuCommand;
import org.opencms.gwt.client.ui.contextmenu.I_CmsContextMenuHandler;
import org.opencms.gwt.client.ui.contextmenu.I_CmsHasContextMenuCommand;
import org.opencms.gwt.shared.property.CmsPropertiesBean;
import org.opencms.util.CmsUUID;

/**
 * The class for the "edit properties" context menu entries.<p>
 * 
 * @author Georg Westenberger
 * 
 * @version $Revision: 1.4 $
 * 
 * @since 8.0.0
 */
public final class CmsEditProperties implements I_CmsHasContextMenuCommand {

    /**
     * Hidden utility class constructor.<p>
     */
    private CmsEditProperties() {

        // nothing to do
    }

    /**
     * Returns the context menu command according to 
     * {@link org.opencms.gwt.client.ui.contextmenu.I_CmsHasContextMenuCommand}.<p>
     * 
     * @return the context menu command
     */
    public static I_CmsContextMenuCommand getContextMenuCommand() {

        return new I_CmsContextMenuCommand() {

            public void execute(CmsUUID structureId, I_CmsContextMenuHandler handler) {

                if (handler.ensureLockOnResource(structureId)) {
                    editProperties(structureId, handler);
                }
            }

            public String getCommandIconClass() {

                return org.opencms.gwt.client.ui.css.I_CmsImageBundle.INSTANCE.contextMenuIcons().properties();
            }
        };
    }

    /**
     * Starts the property editor for the resource with the given structure id.<p>
     * 
     * @param structureId the structure id of a resource
     * @param contextMenuHandler the context menu handler
     */
    protected static void editProperties(final CmsUUID structureId, final I_CmsContextMenuHandler contextMenuHandler) {

        CmsRpcAction<CmsPropertiesBean> action = new CmsRpcAction<CmsPropertiesBean>() {

            @Override
            public void execute() {

                start(300, false);
                CmsCoreProvider.getVfsService().loadPropertyData(structureId, this);
            }

            @Override
            protected void onResponse(CmsPropertiesBean result) {

                stop(false);
                CmsSimplePropertyEditorHandler handler = new CmsSimplePropertyEditorHandler(contextMenuHandler);
                handler.setPropertiesBean(result);
                CmsVfsModePropertyEditor editor = new CmsVfsModePropertyEditor(result.getPropertyDefinitions(), handler);
                editor.setShowResourceProperties(!handler.isFolder());
                editor.start();
            }
        };
        action.execute();
    }

}
