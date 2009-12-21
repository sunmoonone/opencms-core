/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/xml/sitemap/Attic/CmsSiteEntryBean.java,v $
 * Date   : $Date: 2009/12/21 10:40:13 $
 * Version: $Revision: 1.7 $
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

package org.opencms.xml.sitemap;

import org.opencms.file.CmsPropertyDefinition;
import org.opencms.util.CmsUUID;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One entry in a sitemap.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.7 $ 
 * 
 * @since 7.6 
 */
public class CmsSiteEntryBean {

    /** The entry extension. */
    private final String m_extension;

    /** The entry name. */
    private final String m_name;

    /** The configured properties. */
    private final Map<String, String> m_properties;

    /** The file's structure id. */
    private final CmsUUID m_resourceId;

    /** The list of sub-entries. */
    private final List<CmsSiteEntryBean> m_subEntries;

    /** The entry title. */
    private final String m_title;

    /**
     * Creates a new sitemap entry bean.<p> 
     * 
     * @param resourceId the file's structure id
     * @param name the entry's name
     * @param extension the entry's extension
     * @param title the entry's title
     * @param properties the properties as a map of name/value pairs
     * @param subEntries the list of sub-entries
     **/
    public CmsSiteEntryBean(
        CmsUUID resourceId,
        String name,
        String extension,
        String title,
        Map<String, String> properties,
        List<CmsSiteEntryBean> subEntries) {

        m_resourceId = resourceId;
        m_name = name;
        m_extension = extension;
        m_title = title;
        m_subEntries = (subEntries == null
        ? Collections.<CmsSiteEntryBean> emptyList()
        : Collections.unmodifiableList(subEntries));
        // do not freeze the properties
        m_properties = (properties == null ? new HashMap<String, String>() : properties);
    }

    /**
     * Returns a clone, but without the sub-entries.<p>
     * 
     * @return a clone, but without the sub-entries
     */
    public CmsSiteEntryBean cloneWithoutSubEntries() {

        return new CmsSiteEntryBean(m_resourceId, m_name, m_extension, m_title, m_properties, null);
    }

    /**
     * Returns the extension.<p>
     *
     * @return the extension
     */
    public String getExtension() {

        return m_extension;
    }

    /**
     * Returns the name.<p>
     *
     * @return the name
     */
    public String getName() {

        return m_name;
    }

    /**
     * Returns the configured properties.<p>
     * 
     * @return the configured properties
     */
    public Map<String, String> getProperties() {

        return Collections.unmodifiableMap(m_properties);
    }

    /**
     * Returns the file's structure id.<p>
     *
     * @return the file's structure id
     */
    public CmsUUID getResourceId() {

        return m_resourceId;
    }

    /**
     * Returns the sub-entries.<p>
     *
     * @return the sub-entries
     */
    public List<CmsSiteEntryBean> getSubEntries() {

        return m_subEntries;
    }

    /**
     * Returns the title.<p>
     *
     * @return the title
     */
    public String getTitle() {

        return m_title;
    }

    /**
     * Sets the position of this entry in the level.<p>
     * 
     * @param position the position to set
     */
    public void setPosition(int position) {

        m_properties.put(CmsPropertyDefinition.PROPERTY_NAVPOS, String.valueOf(position));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(getName()).append(getExtension()).append(getResourceId()).append(getTitle()).append(getProperties());
        return sb.toString();
    }
}
