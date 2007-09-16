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

package ar.com.fdvs.dj.domain.entities.columns;

import java.util.ArrayList;
import java.util.List;
import ar.com.fdvs.dj.domain.DJOperation;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.entities.Entity;

/**
 * Abstract Class used as base for the different Column types.
 */
public abstract class DJColumn implements Entity {

	//Internal column name
	private String name;

	private String title;
	private Integer posX = new Integer(0);
	private Integer posY = new Integer(0);
	private Integer width = new Integer(100);
	private Boolean fixedWidth = Boolean.FALSE;
	private DJStyle style = new DJStyle();
	private DJStyle headerStyle = new DJStyle();
	private String pattern;
	private Boolean printRepeatedValues = Boolean.TRUE;
	private Boolean blankWhenNull = Boolean.TRUE;

	private List conditionalStyles = new ArrayList();

	public List getConditionalStyles() {
		return conditionalStyles;
	}

	public void setConditionalStyles(List conditionalStyles) {
		this.conditionalStyles = conditionalStyles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String label) {
		this.title = label;
	}

	public Integer getPosX() {
		return posX;
	}

	public void setPosX(Integer posX) {
		this.posX = posX;
	}

	public Integer getPosY() {
		return posY;
	}

	public void setPosY(Integer posY) {
		this.posY = posY;
	}

	public DJStyle getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(DJStyle headerStyle) {
		this.headerStyle = headerStyle;
	}

	public DJStyle getStyle() {
		return style;
	}

	public void setStyle(DJStyle style) {
		this.style = style;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Boolean getPrintRepeatedValues() {
		return printRepeatedValues;
	}

	public void setPrintRepeatedValues(Boolean printRepeatedValues) {
		this.printRepeatedValues = printRepeatedValues;
	}

	public abstract String getTextForExpression();

	public abstract String getValueClassNameForExpression();

	public abstract String getGroupVariableName(String type, String columnToGroupByProperty);

	public abstract String getVariableClassName(DJOperation op);

	public abstract String getInitialExpression(DJOperation op);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getBlankWhenNull() {
		return blankWhenNull;
	}

	public void setBlankWhenNull(Boolean blankWhenNull) {
		this.blankWhenNull = blankWhenNull;
	}

	public Boolean getFixedWidth() {
		return fixedWidth;
	}

	public void setFixedWidth(Boolean fixedWidth) {
		this.fixedWidth = fixedWidth;
	}

}