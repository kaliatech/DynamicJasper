/*
 * DynamicJasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2008  FDV Solutions (http://www.fdvsolutions.com)
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

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import ar.com.fdvs.dj.domain.entities.ColumnsGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

/**
 * Charts to be displayed by dynamicjasper
 * @author msimone
 */
public class DJChart {

	//The possible chart types
	public static final byte PIE_CHART = JRDesignChart.CHART_TYPE_PIE;
	public static final byte BAR_CHART = JRDesignChart.CHART_TYPE_BAR;
//	public static final byte LINE_CHART = JRDesignChart.CHART_TYPE_LINE; //not yet... to much to think left

	//The possible calculation types
	public static final byte CALCULATION_COUNT = JRDesignVariable.CALCULATION_COUNT;
	public static final byte CALCULATION_SUM = JRDesignVariable.CALCULATION_SUM;

	//How to build the chart?
	private byte type;
	private ColumnsGroup columnsGroup;
//	private AbstractColumn column;
	
	/**
	 * List<AbstractColumn>
	 */
	private List columns = new ArrayList();
	

	private byte operation;

	//How to show the chart?
	private DJChartOptions chartOptions;

	public DJChart(){}

	public DJChart(byte type, ColumnsGroup columnsGroup, AbstractColumn column, byte operation, DJChartOptions chartOptions){
		this.type = type;
		this.columnsGroup = columnsGroup;
		
		if (column != null)
			this.columns.add(column);
		
		this.operation = operation;
		this.chartOptions = chartOptions;
	}
	
	public DJChart(byte type, ColumnsGroup columnsGroup, List columns, byte operation, DJChartOptions chartOptions){
		this.type = type;
		this.columnsGroup = columnsGroup;
		
		if (columns != null)
			this.columns.addAll(columns);
		
		this.operation = operation;
		this.chartOptions = chartOptions;
	}

//	public AbstractColumn getColumn() {
//		return column;
//	}
//
//	public void setColumn(AbstractColumn column) {
//		this.column = column;
//	}

	public byte getOperation() {
		return operation;
	}

	public void setOperation(byte operation) {
		this.operation = operation;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public ColumnsGroup getColumnsGroup() {
		return columnsGroup;
	}

	public void setColumnsGroup(ColumnsGroup columnsGroup) {
		this.columnsGroup = columnsGroup;
	}

	public DJChartOptions getOptions() {
		return chartOptions;
	}

	public void setOptions(DJChartOptions options) {
		this.chartOptions = options;
	}
	
	public List getColumns() {
		return columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}
	
}
