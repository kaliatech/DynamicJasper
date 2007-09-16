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

package ar.com.fdvs.dj.domain.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import ar.com.fdvs.dj.domain.DJColumnOperation;
import ar.com.fdvs.dj.domain.DJColumnProperty;
import ar.com.fdvs.dj.domain.DJCustomExpression;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJExpressionColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJOperationColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJSimpleColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.DJConditionalStyle;

/**
 * Builder created to give users a friendly way of adding columns to a report.</br>
 * </br>
 * Usage example: </br>
 * DJColumn columnState = ColumnBuilder.getInstance() </br>
 * .addColumnProperty("state", String.class.getName()) </br>
 * .addTitle("State").addWidth(new Integer(85)) </br>
 * .addStyle(detailStyle).addHeaderStyle(headerStyle).build(); </br>
 * </br>
 * Like with all DJ's builders, it's usage must end with a call to build() mehtod.
 * </br>
 */
public class ColumnBuilder {

	private String title;
	private Integer width = new Integer(50);
	private Boolean fixedWidth = Boolean.FALSE;
	private DJStyle style;
	private DJStyle headerStyle;
	private DJColumnProperty columnProperty;
	private DJCustomExpression customExpression;
	private DJCustomExpression customExpressionToGroupBy;
	private String pattern;
	private boolean printRepeatedValues = true;
	private ArrayList conditionalStyles = new ArrayList();
	private DJColumnOperation operation;
	private List operationColumns;

	public static ColumnBuilder getInstance(){
		return new ColumnBuilder();
	}

	public DJColumn build() throws ColumnBuilderException{
		if (customExpression == null && columnProperty == null){
			throw new ColumnBuilderException("Either a DJColumnProperty or a DJCustomExpression must be present");
		}

		if (columnProperty != null) {
			return buildSimpleColumn();
		} else if (customExpression==null) {
			return buildOperationColumn();
		} else {
			return buildExpressionColumn();
		}
	}

	private DJColumn buildExpressionColumn() {
		DJExpressionColumn column = new DJExpressionColumn();
		populateCommonAttributes(column);
		int random = new Random().nextInt();
		column.setColumnProperty(new DJColumnProperty("expressionColumn" + random,DJCustomExpression.class.getName()));
		column.setExpression(customExpression);
		column.setExpressionToGroupBy(customExpressionToGroupBy);
		return column;
	}

	private DJColumn buildSimpleColumn() {
		DJSimpleColumn column = new DJSimpleColumn();
		populateCommonAttributes(column);
		column.setColumnProperty(columnProperty);
		column.setExpressionToGroupBy(customExpressionToGroupBy);
		return column;
	}

	private DJColumn buildOperationColumn() {
		DJOperationColumn column = new DJOperationColumn();
		populateCommonAttributes(column);
		column.setColumnOperation(operation);
		column.setColumns(operationColumns);
		return column;
	}

	private void populateCommonAttributes(DJColumn column) {
		column.setTitle(title);
		column.setWidth(width);
		column.setPattern(pattern);
		column.setHeaderStyle(headerStyle);
		column.setStyle(style);
		column.setPrintRepeatedValues(Boolean.valueOf(printRepeatedValues));
		column.getConditionalStyles().addAll(conditionalStyles);
		column.setFixedWidth(fixedWidth);
	}

	public ColumnBuilder addTitle(String title) {
		this.title = title;
		return this;
	}

	public ColumnBuilder addPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}
	public ColumnBuilder addPrintRepeatedValues(boolean bool) {
		this.printRepeatedValues = bool;
		return this;
	}
	public ColumnBuilder addPrintRepeatedValues(Boolean bool) {
		this.printRepeatedValues = bool.booleanValue();
		return this;
	}

	public ColumnBuilder addWidth(Integer width) {
		this.width = width;
		return this;
	}
	public ColumnBuilder addWidth(int width) {
		this.width = new Integer(width);
		return this;
	}

	public ColumnBuilder addStyle(DJStyle style) {
		this.style = style;
		return this;
	}

	public ColumnBuilder addHeaderStyle(DJStyle style) {
		this.headerStyle = style;
		return this;
	}

	/**
	 * Adds a property to the column being created.</br>
	 * @param DJColumnProperty columnProperty : BeanUtils like syntax allowed here
	 * @return ColumnBuilder
	 */
	public ColumnBuilder addColumnProperty(DJColumnProperty columnProperty ){
		this.columnProperty = columnProperty;
		return this;
	}

	/**
	 * Adds a property to the column being created.</br>
	 * @param DJColumnProperty columnProperty : BeanUtils like syntax allowed here
	 * @param String valueClassName
	 * @return ColumnBuilder
	 */
	public ColumnBuilder addColumnProperty(String propertyName, String valueClassName ){
		DJColumnProperty columnProperty = new DJColumnProperty(propertyName,valueClassName);
		this.columnProperty = columnProperty;
		return this;
	}

	//FIXME This method should belong to the GroupBuilder.
	public ColumnBuilder addCustomExpressionToGroupBy(DJCustomExpression customExpression){
		this.customExpressionToGroupBy = customExpression;
		return this;
	}

	public ColumnBuilder addCustomExpression(DJCustomExpression customExpression){
		this.customExpression = customExpression;
		return this;
	}


	public ColumnBuilder addConditionalStyle(DJConditionalStyle conditionalStyle) {
		this.conditionalStyles.add(conditionalStyle);
		return this;
	}

	public ColumnBuilder addConditionalStyles(Collection conditionalStyles) {
		this.conditionalStyles.addAll(conditionalStyles);
		return this;
	}

	public ColumnBuilder addColumnOperation(DJColumnOperation operation, DJColumn[] operationColumns) {
		this.operation = operation;
		this.operationColumns = new ArrayList();
		for (int i = 0; i < operationColumns.length; i++) {
			this.operationColumns.add(operationColumns[i]);
		}
		return this;
	}
	
	public ColumnBuilder addFixedWidth(boolean bool) {
		this.fixedWidth = Boolean.valueOf(bool);
		return this;
	}	
	public ColumnBuilder addFixedWidth(Boolean bool) {
		this.fixedWidth = bool;
		return this;
	}	

}