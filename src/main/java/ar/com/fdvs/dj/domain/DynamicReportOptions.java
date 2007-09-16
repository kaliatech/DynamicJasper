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

package ar.com.fdvs.dj.domain;

import java.util.HashMap;

import ar.com.fdvs.dj.domain.constants.Page;

/**
 * Class that defines the report configuration.
 */
public class DynamicReportOptions {

	private Integer headerHeight = new Integer(30);
	private Integer footerHeight = new Integer(30);
	private Integer detailHeight = new Integer(15);

	private Integer leftMargin = new Integer(30);
	private Integer rightMargin =  new Integer(10);
	private Integer topMargin =  new Integer(20);
	private Integer bottomMargin =  new Integer(10);

	private Integer columnsPerPage = new Integer(1);
	private Integer columnSpace = new Integer(0);

	private boolean useFullPageWidth = false;

	private Page page = Page.Page_A4_Portrait();

	private boolean printBackgroundOnOddRows = false;
	private DJStyle oddRowBackgroundStyle = new DJStyle("swfaultOddRowStyle");
	private Integer titleHeight =  new Integer(30);
	private boolean titleNewPage =  false;
	private Integer subtitleHeight = new Integer(15);
	
	private DJStyle defaultHeaderStyle = new DJStyle("defaultHeaderStyle");
	private DJStyle defaultDetailStyle = new DJStyle("defaultDetailStyle");
	private DJStyle defaultFooterStyle = new DJStyle("defaultFooterStyle");
	private DJStyle defaultGroupHeaderStyle = new DJStyle("defaultGroupHeaderStyle");
	private DJStyle defaultGroupFooterStyle = new DJStyle("defaultGroupFooterStyle");
	
	/**
	 * Key: Byte (ImageBanner.ALIGN_RIGHT, ImageBanner.ALIGN_LEFT, ImageBanner.ALIGN_CENTER)<br/>
	 * value: ImageBanner
	 */
	private HashMap imageBanners = new HashMap();
	
	/**
	 * Key: Byte (ImageBanner.ALIGN_RIGHT, ImageBanner.ALIGN_LEFT, ImageBanner.ALIGN_CENTER)<br/>
	 * value: ImageBanner
	 */	
	private HashMap firstPageImageBanners = new HashMap();
	
	public HashMap getImageBanners() {
		return imageBanners;
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

	public Integer getDetailHeight() {
		return detailHeight;
	}

	public void setDetailHeight(Integer detailHeight) {
		this.detailHeight = detailHeight;
	}

	public Integer getLeftMargin() {
		return leftMargin;
	}

	public void setLeftMargin(Integer leftMargin) {
		this.leftMargin = leftMargin;
	}

	public boolean isUseFullPageWidth() {
		return useFullPageWidth;
	}

	public void setUseFullPageWidth(boolean useFullPageWidth) {
		this.useFullPageWidth = useFullPageWidth;
	}

	public Integer getBottomMargin() {
		return bottomMargin;
	}

	public void setBottomMargin(Integer bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public Integer getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(Integer rightMargin) {
		this.rightMargin = rightMargin;
	}

	public Integer getTopMargin() {
		return topMargin;
	}

	public void setTopMargin(Integer topMargin) {
		this.topMargin = topMargin;
	}

	public Integer getColumnsPerPage() {
		return columnsPerPage;
	}

	public void setColumnsPerPage(Integer columnsPerPage) {
		this.columnsPerPage = columnsPerPage;
	}

	public Integer getColumnSpace() {
		return columnSpace;
	}

	public void setColumnSpace(Integer columnSpace) {
		this.columnSpace = columnSpace;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	/**
	 * Gives the printableWidth between the page margins. </br>
	 * pageTotalWidth - leftMargin - rightMargin
	 * @return int
	 */
	public int getPrintableWidth(){
		return page.getWidth() - leftMargin.intValue() - rightMargin.intValue();
	}

	/**
	 * Gives the printableWidth between the page margins that also considers the </br>
	 * number of columns per page and the space between them.</br>
	 * @return int
	 */
	public int getColumnWidth(){
		int usableWidth = page.getWidth()-getLeftMargin().intValue()-getRightMargin().intValue()-((getColumnsPerPage().intValue()-1)*getColumnSpace().intValue());
		return usableWidth / getColumnsPerPage().intValue();
	}

	public DJStyle getOddRowBackgroundStyle() {
		return oddRowBackgroundStyle;
	}

	public void setOddRowBackgroundStyle(DJStyle oddRowBackgroundStyle) {
		this.oddRowBackgroundStyle = oddRowBackgroundStyle;
	}

	public boolean isPrintBackgroundOnOddRows() {
		return printBackgroundOnOddRows;
	}

	public void setPrintBackgroundOnOddRows(boolean printBackgroundOnOddRows) {
		this.printBackgroundOnOddRows = printBackgroundOnOddRows;
	}

	public Integer getTitleHeight() {
		return this.titleHeight;
	}

	public Integer getSubtitleHeight() {
		return this.subtitleHeight;
	}

	public void setSubtitleHeight(Integer subtitleHeight) {
		this.subtitleHeight = subtitleHeight;
	}

	public void setTitleHeight(Integer titleHeight) {
		this.titleHeight = titleHeight;
	}

	public HashMap getFirstPageImageBanners() {
		return firstPageImageBanners;
	}

	public DJStyle getDefaultDetailStyle() {
		return defaultDetailStyle;
	}

	public void setDefaultDetailStyle(DJStyle defaultDetailStyle) {
		this.defaultDetailStyle = defaultDetailStyle;
	}

	public DJStyle getDefaultFooterStyle() {
		return defaultFooterStyle;
	}

	public void setDefaultFooterStyle(DJStyle defaultFooterStyle) {
		this.defaultFooterStyle = defaultFooterStyle;
	}

	public DJStyle getDefaultHeaderStyle() {
		return defaultHeaderStyle;
	}

	public void setDefaultHeaderStyle(DJStyle defaultHeaderStyle) {
		this.defaultHeaderStyle = defaultHeaderStyle;
	}

	public DJStyle getDefaultGroupHeaderStyle() {
		return defaultGroupHeaderStyle;
	}

	public void setDefaultGroupHeaderStyle(DJStyle defaultGroupHeaderStyle) {
		this.defaultGroupHeaderStyle = defaultGroupHeaderStyle;
	}

	public DJStyle getDefaultGroupFooterStyle() {
		return defaultGroupFooterStyle;
	}

	public void setDefaultGroupFooterStyle(DJStyle defaultGroupFooterStyle) {
		this.defaultGroupFooterStyle = defaultGroupFooterStyle;
	}

	public boolean isTitleNewPage() {
		return titleNewPage;
	}

	public void setTitleNewPage(boolean titleNewPage) {
		this.titleNewPage = titleNewPage;
	}

}
