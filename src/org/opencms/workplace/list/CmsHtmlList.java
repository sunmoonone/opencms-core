/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/workplace/list/CmsHtmlList.java,v $
 * Date   : $Date: 2006/06/08 09:33:01 $
 * Version: $Revision: 1.35.4.3 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.workplace.list;

import org.opencms.i18n.CmsMessageContainer;
import org.opencms.i18n.CmsMessages;
import org.opencms.main.CmsIllegalArgumentException;
import org.opencms.util.CmsStringUtil;
import org.opencms.workplace.CmsDialog;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.tools.A_CmsHtmlIconButton;
import org.opencms.workplace.tools.CmsHtmlIconButtonStyleEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * The main class of the html list widget.<p>
 * 
 * @author Michael Moossen  
 * 
 * @version $Revision: 1.35.4.3 $ 
 * 
 * @since 6.0.0 
 */
public class CmsHtmlList {

    /** Standard list button location. */
    public static final String ICON_LEFT = "list/leftarrow.png";

    /** Standard list button location. */
    public static final String ICON_RIGHT = "list/rightarrow.png";

    /** Constant for item separator char used for coding/encoding multiselection. */
    public static final String ITEM_SEPARATOR = "|";

    /** Var name for error message if no item has been selected. */
    public static final String NO_SELECTION_HELP_VAR = "noSelHelp";

    /** Var name for error message if number of selected items does not match. */
    public static final String NO_SELECTION_MATCH_HELP_VAR = "noSelMatchHelp";

    /** Current displayed page number. */
    private int m_currentPage;

    /** Current sort order. */
    private CmsListOrderEnum m_currentSortOrder;

    /** Filtered list of items or <code>null</code> if no filter is set and not sorted. */
    private List m_filteredItems;

    /** Dhtml id. */
    private final String m_id;

    /** Maximum number of items per page. */
    private int m_maxItemsPerPage = 20;

    /** Metadata for building the list. */
    private CmsListMetadata m_metadata;

    /** Display Name of the list. */
    private CmsMessageContainer m_name;

    /** Really content of the list. */
    private final List m_originalItems = new ArrayList();

    /** printable flag. */
    private boolean m_printable;

    /** Search filter text. */
    private String m_searchFilter;

    /** Column name to be sorted. */
    private String m_sortedColumn;

    /** Items currently displayed. */
    private List m_visibleItems;

    /**
     * Default Constructor.<p>
     * 
     * @param id unique id of the list, is used as name for controls and js functions and vars
     * @param name the display name 
     * @param metadata the list's metadata
     */
    public CmsHtmlList(String id, CmsMessageContainer name, CmsListMetadata metadata) {

        m_id = id;
        m_name = name;
        m_metadata = metadata;
        m_currentPage = 1;
    }

    /**
     * Generates the list of html option elements for a html select control to select a page of a list.<p>
     * 
     * @param nrPages the total number of pages
     * @param itemsPage the maximum number of items per page
     * @param nrItems the total number of items
     * @param curPage the current page
     * @param locale the locale
     * 
     * @return html code
     */
    public static String htmlPageSelector(int nrPages, int itemsPage, int nrItems, int curPage, Locale locale) {

        StringBuffer html = new StringBuffer(256);
        for (int i = 0; i < nrPages; i++) {
            int displayedFrom = i * itemsPage + 1;
            int displayedTo = (i + 1) * itemsPage < nrItems ? (i + 1) * itemsPage : nrItems;
            html.append("\t\t\t\t<option value='");
            html.append(i + 1);
            html.append("'");
            html.append((i + 1) == curPage ? " selected" : "");
            html.append(">");
            html.append(Messages.get().getBundle(locale).key(
                Messages.GUI_LIST_PAGE_ENTRY_3,
                new Integer(i + 1),
                new Integer(displayedFrom),
                new Integer(displayedTo)));
            html.append("</option>\n");
        }
        return html.toString();
    }

    /**
     * Adds a collection new list items to the content of the list.<p>
     * 
     * If you need to add or remove items from or to the list at a later step, use the
     * <code>{@link #insertAllItems(Collection, Locale)}</code> or 
     * <code>{@link #removeAllItems(Collection, Locale)}</code> methods.<p>
     * 
     * @param listItems the collection of list items to add
     * 
     * @see List#addAll(Collection)
     */
    public void addAllItems(Collection listItems) {

        m_originalItems.addAll(listItems);
    }

    /**
     * Adds a new item to the content of the list.<p>
     * 
     * If you need to add or remove an item from or to the list at a later step, use the
     * <code>{@link #insertItem(CmsListItem, Locale)}</code> or
     * <code>{@link #removeItem(String, Locale)}</code> methods.<p> 
     * 
     * @param listItem the list item
     * 
     * @see List#add(Object)
     */
    public void addItem(CmsListItem listItem) {

        m_originalItems.add(listItem);
    }

    /**
     * This method resets the content of the list (no the metadata).<p>
     * 
     * @param locale the locale for sorting/searching
     */
    public void clear(Locale locale) {

        m_originalItems.clear();
        m_filteredItems = null;
        if (m_visibleItems != null) {
            m_visibleItems.clear();
        }
        setSearchFilter("", locale);
        m_sortedColumn = null;
    }

    /**
     * Returns all list items in the list, may be not visible and sorted.<p> 
     * 
     * @return all list items
     */
    public List getAllContent() {

        return Collections.unmodifiableList(m_originalItems);
    }

    /**
     * Returns the filtered list of list items.<p>
     * 
     * Equals to <code>{@link #getAllContent()}</code> if no filter is set.<p>
     * 
     * @return the filtered list of list items
     */
    public List getContent() {

        if (m_filteredItems == null) {
            return getAllContent();
        } else {
            return Collections.unmodifiableList(m_filteredItems);
        }
    }

    /**
     * returns the number of the current page.<p>
     * 
     * @return the number of the current page
     */
    public int getCurrentPage() {

        return m_currentPage;
    }

    /**
     * Returns all items of the current page.<p>
     * 
     * @return all items of the current page, a list of {@link CmsListItem} objects
     */
    public List getCurrentPageItems() {

        if (getSize() == 0) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(getContent().subList(displayedFrom() - 1, displayedTo()));
    }

    /**
     * Returns the current used sort order.<p>
     * 
     * @return the current used sort order
     */
    public CmsListOrderEnum getCurrentSortOrder() {

        return m_currentSortOrder;
    }

    /**
     * Returns the id.<p>
     *
     * @return the id
     */
    public String getId() {

        return m_id;
    }

    /**
     * This method returns the item identified by the parameter id.<p>
     * 
     * Only current visible item can be retrieved using this method.<p> 
     * 
     * @param id the id of the item to look for
     * 
     * @return the requested item or <code>null</code> if not found
     */
    public CmsListItem getItem(String id) {

        Iterator it = m_originalItems.iterator();
        while (it.hasNext()) {
            CmsListItem item = (CmsListItem)it.next();
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns the maximum number of items per page.<p>
     *
     * @return the maximum number of items per page
     */
    public int getMaxItemsPerPage() {

        return m_maxItemsPerPage;
    }

    /**
     * Returns the metadata.<p>
     *
     * @return the metadata
     */
    public CmsListMetadata getMetadata() {

        return m_metadata;
    }

    /**
     * Returns the name of the list.<p>
     * 
     * @return the list's name
     */
    public CmsMessageContainer getName() {

        return m_name;
    }

    /**
     * Returns the filtered number of pages.<p>
     * 
     * Equals to <code>{@link #getTotalNumberOfPages()}</code> if no filter is set.<p>
     * 
     * @return the filtered of pages
     */
    public int getNumberOfPages() {

        return (int)Math.ceil((double)getSize() / getMaxItemsPerPage());
    }

    /**
     * Returns the search filter.<p>
     *
     * @return the search filter
     */
    public String getSearchFilter() {

        return m_searchFilter;
    }

    /**
     * Return the filtered number of items.<p>
     * 
     * Equals to <code>{@link #getTotalSize()}</code> if no filter is set.<p>
     * 
     * @return the filtered number of items
     */
    public int getSize() {

        return getContent().size();
    }

    /**
     * Returns the sorted column's name.<p>
     * 
     * @return the sorted column's name
     */
    public String getSortedColumn() {

        return m_sortedColumn;
    }

    /**
     * Returns a filled list state.<p>
     * 
     * @return the state of the list
     */
    public CmsListState getState() {

        return new CmsListState(this);
    }

    /**
     * Returns the total number of pages.<p> 
     * 
     * @return the total number of pages
     */
    public int getTotalNumberOfPages() {

        return (int)Math.ceil((double)getAllContent().size() / getMaxItemsPerPage());
    }

    /**
     * Return the total number of items.<p>
     * 
     * @return the total number of items
     */
    public int getTotalSize() {

        return getAllContent().size();
    }

    /**
     * Inserts a collection of list items in an already initialized list.<p>
     * 
     * Keeping care of all the state like sorted column, sorting order, displayed page and search filter.<p>
     * 
     * @param listItems the collection of list items to insert
     * @param locale the locale
     */
    public void insertAllItems(Collection listItems, Locale locale) {

        CmsListState state = null;
        if (m_filteredItems != null || m_visibleItems != null) {
            state = getState();
        }
        addAllItems(listItems);
        if (m_filteredItems != null) {
            m_filteredItems.addAll(listItems);
        }
        if (m_visibleItems != null) {
            m_visibleItems.addAll(listItems);
        }
        if (state != null) {
            setState(state, locale);
        }
    }

    /**
     * Inserts an item in an already initialized list.<p>
     * 
     * Keeping care of all the state like sorted column, sorting order, displayed page and search filter.<p>
     * 
     * @param listItem the list item to insert
     * @param locale the locale
     */
    public void insertItem(CmsListItem listItem, Locale locale) {

        CmsListState state = null;
        if (m_filteredItems != null || m_visibleItems != null) {
            state = getState();
        }
        addItem(listItem);
        if (m_filteredItems != null) {
            m_filteredItems.add(listItem);
        }
        if (m_visibleItems != null) {
            m_visibleItems.add(listItem);
        }
        if (state != null) {
            setState(state, locale);
        }
    }

    /**
     * Returns the printable flag.<p>
     *
     * @return the printable flag
     */
    public boolean isPrintable() {

        return m_printable;
    }

    /**
     * Generates the csv output for the list.<p>
     * 
     * Synchronized to not collide with <code>{@link #listHtml(CmsWorkplace)}</code>.<p> 
     * 
     * @param wp the workplace object
     * 
     * @return csv output
     */
    public synchronized String listCsv(CmsWorkplace wp) {

        StringBuffer csv = new StringBuffer(5120);
        csv.append(m_metadata.csvHeader(wp));
        if (getContent().isEmpty()) {
            csv.append(m_metadata.csvEmptyList());
        } else {
            Iterator itItems = getContent().iterator();
            while (itItems.hasNext()) {
                CmsListItem item = (CmsListItem)itItems.next();
                csv.append(m_metadata.csvItem(item, wp));
            }
        }
        return wp.resolveMacros(csv.toString());
    }

    /**
     * Generates the html code for the list.<p>
     * 
     * Synchronized to not collide with <code>{@link #printableHtml(CmsWorkplace)}</code>.<p> 
     * 
     * @param wp the workplace object
     * 
     * @return html code
     */
    public synchronized String listHtml(CmsWorkplace wp) {

        if (isPrintable()) {
            m_visibleItems = new ArrayList(getContent());
        } else {
            m_visibleItems = new ArrayList(getCurrentPageItems());
        }

        StringBuffer html = new StringBuffer(5120);
        html.append(htmlBegin(wp));
        if (!isPrintable()) {
            html.append(htmlTitle(wp));
            html.append(htmlToolBar(wp));
        } else {
            html.append("<style type='text/css'>\n");
            html.append("td.listdetailitem, \n");
            html.append(".linkdisabled {\n");
            html.append("\tcolor: black;\n");
            html.append("}\n");
            html.append(".list th {\n");
            html.append("\tborder: 1px solid black;\n");
            html.append("}\n");
            html.append(".list {\n");
            html.append("\tborder: 1px solid black;\n");
            html.append("}\n");
            html.append("</style>");
        }
        html.append("<table width='100%' cellpadding='1' cellspacing='0' class='list'>\n");
        html.append(m_metadata.htmlHeader(this, wp));
        if (m_visibleItems.isEmpty()) {
            html.append(m_metadata.htmlEmptyTable(wp.getLocale()));
        } else {
            Iterator itItems = m_visibleItems.iterator();
            boolean odd = true;
            while (itItems.hasNext()) {
                CmsListItem item = (CmsListItem)itItems.next();
                html.append(m_metadata.htmlItem(item, wp, odd, isPrintable()));
                odd = !odd;
            }
        }
        html.append("</table>\n");
        if (!isPrintable()) {
            html.append(htmlPagingBar(wp));
        }
        html.append(htmlEnd(wp));
        return wp.resolveMacros(html.toString());
    }

    /**
     * Generate the need js code for the list.<p>
     * 
     * @param locale the locale
     * 
     * @return js code
     */
    public String listJs(Locale locale) {

        StringBuffer js = new StringBuffer(1024);
        CmsMessages messages = Messages.get().getBundle(locale);
        js.append("<script type='text/javascript' src='");
        js.append(CmsWorkplace.getSkinUri());
        js.append("admin/javascript/list.js'></script>\n");
        if (!m_metadata.getMultiActions().isEmpty()) {
            js.append("<script type='text/javascript'>\n");
            js.append("\tvar ");
            js.append(NO_SELECTION_HELP_VAR);
            js.append(" = '");
            js.append(CmsStringUtil.escapeJavaScript(messages.key(Messages.GUI_LIST_ACTION_NO_SELECTION_0)));
            js.append("';\n");
            Iterator it = m_metadata.getMultiActions().iterator();
            while (it.hasNext()) {
                CmsListMultiAction action = (CmsListMultiAction)it.next();
                if (action instanceof CmsListRadioMultiAction) {
                    CmsListRadioMultiAction rAction = (CmsListRadioMultiAction)action;
                    js.append("\tvar ");
                    js.append(NO_SELECTION_MATCH_HELP_VAR);
                    js.append(rAction.getId());
                    js.append(" = '");
                    js.append(CmsStringUtil.escapeJavaScript(messages.key(
                        Messages.GUI_LIST_ACTION_NO_SELECTION_MATCH_1,
                        new Integer(rAction.getSelections()))));
                    js.append("';\n");
                }
            }
            js.append("</script>\n");
        }
        return js.toString();
    }

    /**
     * Returns a new list item for this list.<p>
     * 
     * @param id the id of the item has to be unique 
     * @return a new list item
     */
    public CmsListItem newItem(String id) {

        return new CmsListItem(getMetadata(), id);
    }

    /**
     * Returns html code for printing the list.<p>
     * 
     * Synchronized to not collide with <code>{@link #listHtml(CmsWorkplace)}</code>.<p>
     *  
     * @param wp the workplace object
     * 
     * @return html code
     */
    public synchronized String printableHtml(CmsWorkplace wp) {

        m_printable = true;
        String html = listHtml(wp);
        m_printable = false;
        return html;
    }

    /**
     * Removes a collection of list items from the list.<p> 
     * 
     * Keeping care of all the state like sorted column, sorting order, displayed page and search filter.<p>
     * 
     * Try to use it instead of <code>{@link A_CmsListDialog#refreshList()}</code>.<p>
     * 
     * @param ids the collection of ids of the items to remove
     * @param locale the locale
     * 
     * @return the list of removed list items
     */
    public List removeAllItems(Collection ids, Locale locale) {

        List removedItems = new ArrayList();
        Iterator itItems = m_originalItems.iterator();
        while (itItems.hasNext()) {
            CmsListItem listItem = (CmsListItem)itItems.next();
            if (ids.contains(listItem.getId())) {
                removedItems.add(listItem);
            }
        }
        if (removedItems.isEmpty()) {
            return removedItems;
        }
        CmsListState state = null;
        if (m_filteredItems != null || m_visibleItems != null) {
            state = getState();
        }
        m_originalItems.removeAll(removedItems);
        if (m_filteredItems != null) {
            m_filteredItems.removeAll(removedItems);
        }
        if (m_visibleItems != null) {
            m_visibleItems.removeAll(removedItems);
        }
        if (state != null) {
            setState(state, locale);
        }
        return removedItems;
    }

    /**
     * Removes an item from the list.<p> 
     * 
     * Keeping care of all the state like sorted column, sorting order, displayed page and search filter.<p>
     * 
     * Try to use it instead of <code>{@link A_CmsListDialog#refreshList()}</code>.<p>
     * 
     * @param id the id of the item to remove
     * @param locale the locale
     * 
     * @return the removed list item
     */
    public CmsListItem removeItem(String id, Locale locale) {

        CmsListItem item = null;
        Iterator itItems = m_originalItems.iterator();
        while (itItems.hasNext()) {
            CmsListItem listItem = (CmsListItem)itItems.next();
            if (listItem.getId().equals(id)) {
                item = listItem;
                break;
            }
        }
        if (item == null) {
            return null;
        }
        CmsListState state = null;
        if (m_filteredItems != null || m_visibleItems != null) {
            state = getState();
        }
        m_originalItems.remove(item);
        if (m_filteredItems != null) {
            m_filteredItems.remove(item);
        }
        if (m_visibleItems != null) {
            m_visibleItems.remove(item);
        }
        if (state != null) {
            setState(state, locale);
        }
        return item;
    }

    /**
     * Replace a list of items in the list.<p> 
     * 
     * Keeping care of all the state like sorted column, sorting order, displayed page and search filter.<p>
     * 
     * If the list already contains an item with the id of a given list item, it will be removed and
     * replaced by the new list item. if not, this method is the same as the 
     * <code>{@link #insertAllItems(Collection, Locale)}</code> method.
     * 
     * Try to use it instead of <code>{@link A_CmsListDialog#refreshList()}</code>.<p>
     * 
     * @param listItems the list of <code>{@link CmsListItem}</code>s to replace
     * @param locale the locale
     * 
     * @return the removed list item, or <code>null</code>
     */
    public List replaceAllItems(List listItems, Locale locale) {

        List removedItems = new ArrayList();
        Iterator itItems = m_originalItems.iterator();
        while (itItems.hasNext()) {
            CmsListItem listItem = (CmsListItem)itItems.next();
            Iterator itNewItems = listItems.iterator();
            while (itNewItems.hasNext()) {
                if (listItem.equals(((CmsListItem)itNewItems.next()).getId())) {
                    removedItems.add(listItem);
                }
            }
        }
        CmsListState state = null;
        if (m_filteredItems != null || m_visibleItems != null) {
            state = getState();
        }
        if (!removedItems.isEmpty()) {
            m_originalItems.removeAll(removedItems);
        }
        addAllItems(listItems);
        if (state != null) {
            setState(state, locale);
        }
        return removedItems;
    }

    /**
     * Replace an item in the list.<p> 
     * 
     * Keeping care of all the state like sorted column, sorting order, displayed page and search filter.<p>
     * 
     * If the list already contains an item with the id of the given list item, it will be removed and
     * replaced by the new list item. if not, this method is the same as the 
     * <code>{@link #insertItem(CmsListItem, Locale)}</code> method.
     * 
     * Try to use it instead of <code>{@link A_CmsListDialog#refreshList()}</code>.<p>
     * 
     * @param listItem the listItem to replace
     * @param locale the locale
     * 
     * @return the removed list item, or <code>null</code>
     */
    public CmsListItem replaceItem(CmsListItem listItem, Locale locale) {

        CmsListItem item = null;
        Iterator itItems = m_originalItems.iterator();
        while (itItems.hasNext()) {
            CmsListItem tmp = (CmsListItem)itItems.next();
            if (tmp.getId().equals(listItem.getId())) {
                item = tmp;
                break;
            }
        }
        CmsListState state = null;
        if (m_filteredItems != null || m_visibleItems != null) {
            state = getState();
        }
        if (item != null) {
            m_originalItems.remove(item);
        }
        addItem(listItem);
        if (state != null) {
            setState(state, locale);
        }
        return item;
    }

    /**
     * Sets the current page.<p>
     *
     * @param currentPage the current page to set
     * 
     * @throws CmsIllegalArgumentException if the argument is invalid
     */
    public void setCurrentPage(int currentPage) throws CmsIllegalArgumentException {

        if (getSize() != 0) {
            if (currentPage < 1 || currentPage > getNumberOfPages()) {
                throw new CmsIllegalArgumentException(Messages.get().container(
                    Messages.ERR_LIST_INVALID_PAGE_1,
                    new Integer(currentPage)));
            }
        }
        m_currentPage = currentPage;
    }

    /**
     * Sets the maximum number of items per page.<p>
     *
     * @param maxItemsPerPage the maximum number of items per page to set
     */
    public void setMaxItemsPerPage(int maxItemsPerPage) {

        this.m_maxItemsPerPage = maxItemsPerPage;
    }

    /**
     * Sets the name of the list.<p>
     * 
     * @param name the name of the list
     */
    public void setName(CmsMessageContainer name) {

        m_name = name;
    }

    /**
     * Sets the search filter.<p>
     *
     * @param searchFilter the search filter to set
     * @param locale the used locale for searching/sorting
     */
    public void setSearchFilter(String searchFilter, Locale locale) {

        if (!m_metadata.isSearchable()) {
            return;
        }
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(searchFilter)) {
            // reset content if filter is empty
            m_filteredItems = null;
            m_searchFilter = "";
            getMetadata().getSearchAction().getShowAllAction().setVisible(false);
        } else {
            if (!m_metadata.isSelfManaged()) {
                m_filteredItems = getMetadata().getSearchAction().filter(getAllContent(), searchFilter);
            }
            m_searchFilter = searchFilter;
            getMetadata().getSearchAction().getShowAllAction().setVisible(true);
        }
        String sCol = m_sortedColumn;
        m_sortedColumn = "";
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(sCol)) {
            CmsListOrderEnum order = getCurrentSortOrder();
            setSortedColumn(sCol, locale);
            if (order == CmsListOrderEnum.ORDER_DESCENDING) {
                setSortedColumn(sCol, locale);
            }
        }
        setCurrentPage(1);
    }

    /**
     * Sets the sorted column.<p>
     *
     * @param sortedColumn the sorted column to set
     * @param locale the used locale for sorting
     * 
     * @throws CmsIllegalArgumentException if the <code>sortedColumn</code> argument is invalid
     */
    public void setSortedColumn(String sortedColumn, Locale locale) throws CmsIllegalArgumentException {

        if (getMetadata().getColumnDefinition(sortedColumn) == null
            || !getMetadata().getColumnDefinition(sortedColumn).isSorteable()) {
            return;
        }
        // check if the parameter is valid
        if (m_metadata.getColumnDefinition(sortedColumn) == null) {
            throw new CmsIllegalArgumentException(Messages.get().container(
                Messages.ERR_LIST_INVALID_COLUMN_1,
                sortedColumn));
        }
        // reset view
        setCurrentPage(1);
        // only reverse order if the column to sort is already sorted
        if (sortedColumn.equals(m_sortedColumn)) {
            if (m_currentSortOrder == CmsListOrderEnum.ORDER_ASCENDING) {
                m_currentSortOrder = CmsListOrderEnum.ORDER_DESCENDING;
            } else {
                m_currentSortOrder = CmsListOrderEnum.ORDER_ASCENDING;
            }
            if (!m_metadata.isSelfManaged()) {
                Collections.reverse(m_filteredItems);
            }
            return;
        }
        // sort new column
        m_sortedColumn = sortedColumn;
        m_currentSortOrder = CmsListOrderEnum.ORDER_ASCENDING;
        I_CmsListItemComparator c = getMetadata().getColumnDefinition(sortedColumn).getListItemComparator();
        if (m_filteredItems == null) {
            m_filteredItems = new ArrayList(getAllContent());
        }
        if (!m_metadata.isSelfManaged()) {
            Collections.sort(m_filteredItems, c.getComparator(sortedColumn, locale));
        }
    }

    /**
     * Sets the list state.<p>
     * 
     * This may involve sorting, filtering and paging.<p>
     * 
     * @param listState the state to be set
     * @param locale the locale
     */
    public void setState(CmsListState listState, Locale locale) {

        m_filteredItems = null;
        if (m_visibleItems != null) {
            m_visibleItems.clear();
        }
        setSearchFilter(listState.getFilter(), locale);
        setSortedColumn(listState.getColumn(), locale);
        if (listState.getOrder() == CmsListOrderEnum.ORDER_DESCENDING) {
            setSortedColumn(listState.getColumn(), locale);
        }
        if (listState.getPage() > 0) {
            if (listState.getPage() <= getNumberOfPages()) {
                setCurrentPage(listState.getPage());
            } else {
                setCurrentPage(1);
            }
        }
    }

    /**
     * Sets the metadata for this list.<p>
     * 
     * Should only be used by the <code>{@link A_CmsListDialog}</code> class
     * for temporaly removing the metadata object while the list is saved in the 
     * <code>{@link org.opencms.workplace.CmsWorkplaceSettings}</code>.<p>     
     * 
     * @param metadata the list metadata
     */
    /*package*/void setMetadata(CmsListMetadata metadata) {

        m_metadata = metadata;
    }

    /**
     * Returns the number (from 1) of the first displayed item.<p>
     * 
     * @return the number (from 1) of the first displayed item, or zero if the list is empty
     */
    private int displayedFrom() {

        if (getSize() != 0) {
            if (isPrintable()) {
                return 1;
            } else {
                return (getCurrentPage() - 1) * getMaxItemsPerPage() + 1;
            }
        }
        return 0;
    }

    /**
     * Returns the number (from 1) of the last displayed item.<p>
     * 
     * @return the number (from 1) of the last displayed item, or zero if the list is empty
     */
    private int displayedTo() {

        if (getSize() != 0) {
            if (!isPrintable()) {
                if (getCurrentPage() * getMaxItemsPerPage() < getSize()) {
                    return getCurrentPage() * getMaxItemsPerPage();
                }
            }
        }
        return getSize();
    }

    /**
     * Generates the initial html code.<p>
     *
     * @param wp the workplace context
     *  
     * @return html code
     */
    private String htmlBegin(CmsWorkplace wp) {

        StringBuffer html = new StringBuffer(512);
        // help & confirmation text for actions if needed
        if (!isPrintable() && m_visibleItems != null && !m_visibleItems.isEmpty()) {
            Iterator cols = getMetadata().getColumnDefinitions().iterator();
            while (cols.hasNext()) {
                CmsListColumnDefinition col = (CmsListColumnDefinition)cols.next();
                Iterator actions = col.getDirectActions().iterator();
                while (actions.hasNext()) {
                    I_CmsListDirectAction action = (I_CmsListDirectAction)actions.next();
                    action.setItem((CmsListItem)m_visibleItems.get(0));
                    html.append(action.helpTextHtml(wp));
                    html.append(action.confirmationTextHtml(wp));
                }
                Iterator defActions = col.getDefaultActions().iterator();
                while (defActions.hasNext()) {
                    I_CmsListDirectAction action = (I_CmsListDirectAction)defActions.next();
                    action.setItem((CmsListItem)m_visibleItems.get(0));
                    html.append(action.helpTextHtml(wp));
                    html.append(action.confirmationTextHtml(wp));
                }
            }
        }
        // start list code
        html.append("<div class='listArea'>\n");
        html.append(((CmsDialog)wp).dialogBlock(CmsWorkplace.HTML_START, m_name.key(wp.getLocale()), false));
        html.append("\t\t<table width='100%' cellspacing='0' cellpadding='0' border='0'>\n");
        html.append("\t\t\t<tr><td>\n");
        return html.toString();
    }

    /**
     * Generates the need html code for ending a lsit.<p>
     * 
     * @param wp the workplace context
     * 
     * @return html code
     */
    private String htmlEnd(CmsWorkplace wp) {

        StringBuffer html = new StringBuffer(512);
        html.append("\t\t\t</td></tr>\n");
        html.append("\t\t</table>\n");
        html.append(((CmsDialog)wp).dialogBlock(CmsWorkplace.HTML_END, m_name.key(wp.getLocale()), false));
        html.append("</div>\n");
        if (!isPrintable() && getMetadata().isSearchable()) {
            html.append("<script type='text/javascript'>\n");
            html.append("\tvar form = document.forms['");
            html.append(getId());
            html.append("-form'];\n");
            html.append("\tform.listSearchFilter.value='");
            html.append(getSearchFilter() != null ? CmsStringUtil.escapeJavaScript(getSearchFilter()) : "");
            html.append("';\n");
            html.append("</script>\n");
        }
        return html.toString();
    }

    /**
     * Generates the needed html code for the paging bar.<p>
     * 
     * @param wp the workplace instance
     * 
     * @return html code
     */
    private String htmlPagingBar(CmsWorkplace wp) {

        if (getNumberOfPages() < 2) {
            return "";
        }
        StringBuffer html = new StringBuffer(1024);
        CmsMessages messages = Messages.get().getBundle(wp.getLocale());
        html.append("<table width='100%' cellspacing='0' style='margin-top: 5px;'>\n");
        html.append("\t<tr>\n");
        html.append("\t\t<td class='main'>\n");
        // prev button
        String id = "listPrev";
        String name = messages.key(Messages.GUI_LIST_PAGING_PREVIOUS_NAME_0);
        String iconPath = ICON_LEFT;
        boolean enabled = getCurrentPage() > 1;
        String helpText = messages.key(Messages.GUI_LIST_PAGING_PREVIOUS_HELP_0);
        if (!enabled) {
            helpText = messages.key(Messages.GUI_LIST_PAGING_PREVIOUS_HELPDIS_0);
        }
        String onClic = "listSetPage('" + getId() + "', " + (getCurrentPage() - 1) + ")";
        html.append(A_CmsHtmlIconButton.defaultButtonHtml(
            wp.getJsp(),
            CmsHtmlIconButtonStyleEnum.SMALL_ICON_TEXT,
            id,
            name,
            helpText,
            enabled,
            iconPath,
            null,
            onClic));
        html.append("\n");
        // next button
        id = "listNext";
        name = messages.key(Messages.GUI_LIST_PAGING_NEXT_NAME_0);
        iconPath = ICON_RIGHT;
        enabled = getCurrentPage() < getNumberOfPages();
        helpText = messages.key(Messages.GUI_LIST_PAGING_NEXT_HELP_0);
        if (!enabled) {
            helpText = messages.key(Messages.GUI_LIST_PAGING_NEXT_HELPDIS_0);
        }
        onClic = "listSetPage('" + getId() + "', " + (getCurrentPage() + 1) + ")";
        html.append(A_CmsHtmlIconButton.defaultButtonHtml(
            wp.getJsp(),
            CmsHtmlIconButtonStyleEnum.SMALL_ICON_TEXT,
            id,
            name,
            helpText,
            enabled,
            iconPath,
            null,
            onClic));
        html.append("\n");
        // page selection list
        html.append("\t\t\t&nbsp;&nbsp;&nbsp;");
        html.append("\t\t\t<select name='listPageSet' id='id-page_set' onChange =\"listSetPage('");
        html.append(getId());
        html.append("', this.value);\" style='vertical-align: bottom;'>\n");
        html.append(htmlPageSelector(
            getNumberOfPages(),
            getMaxItemsPerPage(),
            getSize(),
            getCurrentPage(),
            wp.getLocale()));
        html.append("\t\t\t</select>\n");
        html.append("\t\t\t&nbsp;&nbsp;&nbsp;");
        if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_searchFilter)) {
            html.append(messages.key(Messages.GUI_LIST_PAGING_TEXT_2, new Object[] {
                m_name.key(wp.getLocale()),
                new Integer(getTotalSize())}));
        } else {
            html.append(messages.key(Messages.GUI_LIST_PAGING_FILTER_TEXT_3, new Object[] {
                m_name.key(wp.getLocale()),
                new Integer(getSize()),
                new Integer(getTotalSize())}));
        }
        html.append("\t\t</td>\n");
        html.append("\t</tr>\n");
        html.append("</table>\n");
        return html.toString();
    }

    /**
     * returns the html for the title of the list.<p>
     * 
     * @param wp the workplace context
     * 
     * @return html code
     */
    private String htmlTitle(CmsWorkplace wp) {

        StringBuffer html = new StringBuffer(512);
        CmsMessages messages = Messages.get().getBundle(wp.getLocale());
        html.append("<table width='100%' cellspacing='0'>");
        html.append("\t<tr>\n");
        html.append("\t\t<td align='left'>\n");
        html.append("\t\t\t");
        if (getTotalNumberOfPages() > 1) {
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_searchFilter)) {
                html.append(messages.key(Messages.GUI_LIST_TITLE_TEXT_4, new Object[] {
                    m_name.key(wp.getLocale()),
                    new Integer(displayedFrom()),
                    new Integer(displayedTo()),
                    new Integer(getTotalSize())}));
            } else {
                html.append(messages.key(Messages.GUI_LIST_TITLE_FILTERED_TEXT_5, new Object[] {
                    m_name.key(wp.getLocale()),
                    new Integer(displayedFrom()),
                    new Integer(displayedTo()),
                    new Integer(getSize()),
                    new Integer(getTotalSize())}));
            }
        } else {
            if (CmsStringUtil.isEmptyOrWhitespaceOnly(m_searchFilter)) {
                html.append(messages.key(Messages.GUI_LIST_SINGLE_TITLE_TEXT_2, new Object[] {
                    m_name.key(wp.getLocale()),
                    new Integer(getTotalSize())}));
            } else {
                html.append(messages.key(Messages.GUI_LIST_SINGLE_TITLE_FILTERED_TEXT_3, new Object[] {
                    m_name.key(wp.getLocale()),
                    new Integer(getSize()),
                    new Integer(getTotalSize())}));
            }
        }
        html.append("\n");
        html.append("\t\t</td>\n\t\t");
        html.append(getMetadata().htmlActionBar(wp));
        html.append("\n\t</tr>\n");
        html.append("</table>\n");
        return html.toString();
    }

    /**
     * Returns the html code for the toolbar (search bar + multiactions bar).<p>
     * 
     * @param wp the workplace context
     * 
     * @return html code
     */
    private String htmlToolBar(CmsWorkplace wp) {

        StringBuffer html = new StringBuffer(512);
        html.append("<table width='100%' cellspacing='0' style='margin-bottom: 5px'>\n");
        html.append("\t<tr>\n");
        html.append(m_metadata.htmlSearchBar(wp));
        html.append(m_metadata.htmlMultiActionBar(wp));
        html.append("\t</tr>\n");
        html.append("</table>\n");
        return html.toString();
    }
}