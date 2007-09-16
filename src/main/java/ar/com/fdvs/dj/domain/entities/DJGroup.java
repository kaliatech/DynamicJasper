/*
 * Dynamic Jasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2007  FDV Solutions (http://www.fdvsolutions.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 *
 * License as published by the Free Software Foundation; either
 *
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 */

package ar.com.fdvs.dj.domain.entities;

import java.util.ArrayList;
import java.util.List;

import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.columns.DJPropertyColumn;

/**
 * Entity created to handle groups of columns.</br>
 * Multiple groups can be created for a single report. In this case the result </br>
 * would be a nesting with the latest groups added to the report being the inner ones.
 */
public class DJGroup implements Entity {

	//The column used to group by
	private DJPropertyColumn columnToGroupBy;

	public DJPropertyColumn getColumnToGroupBy() {
		return columnToGroupBy;
	}

	public void setColumnToGroupBy(DJPropertyColumn columnToGroupBy) {
		this.columnToGroupBy = columnToGroupBy;
	}

	//<DJGroupVariable>
	private List headerVariables = new ArrayList();
	//<DJGroupVariable>
	private List footerVariables = new ArrayList();
	private Integer headerHeight = new Integer(20);
	private Integer footerHeight = new Integer(20);
	private GroupLayout layout = GroupLayout.VALUE_IN_HEADER;
	private List footerSubreports = new ArrayList();
	private List headerSubreports = new ArrayList();
	
	/**
	 * Default Style for variables when showing in footer.
	 * Firts looks for the style at the DJGroupVariable, then the default, finally
	 * it usses the columns style.
	 */
	private DJStyle defaulFooterStyle;
	
	/**
	 * Default Style for variables when showing in header.
	 * The lookup order is the same as for "defaulFooterStyle"
	 */
	private DJStyle defaulHeaderStyle;

	public DJStyle getDefaulFooterStyle() {
		return defaulFooterStyle;
	}

	public void setDefaulFooterStyle(DJStyle defaulFooterStyle) {
		this.defaulFooterStyle = defaulFooterStyle;
	}

	public DJStyle getDefaulHeaderStyle() {
		return defaulHeaderStyle;
	}

	public void setDefaulHeaderStyle(DJStyle defaulHeaderStyle) {
		this.defaulHeaderStyle = defaulHeaderStyle;
	}

	public List getFooterVariables() {
		return footerVariables;
	}

	public void setFooterVariables(ArrayList footerVariables) {
		this.footerVariables = footerVariables;
	}

	public List getHeaderVariables() {
		return headerVariables;
	}

	public void setHeaderVariables(ArrayList headerVariables) {
		this.headerVariables = headerVariables;
	}

	public Integer getFooterHeight() {
		return footerHeight;
	}

	public void setFooterHeight(Integer footerHeight) {
		this.footerHeight = footerHeight;
	}

	public Integer getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(Integer headerHeight) {
		this.headerHeight = headerHeight;
	}

	public GroupLayout getLayout() {
		return layout;
	}

	public void setLayout(GroupLayout layout) {
		this.layout = layout;
	}

	public List getFooterSubreports() {
		return footerSubreports;
	}

	public List getHeaderSubreports() {
		return headerSubreports;
	}

}
