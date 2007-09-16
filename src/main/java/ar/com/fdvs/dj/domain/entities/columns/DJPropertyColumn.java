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

import ar.com.fdvs.dj.domain.DJOperation;
import ar.com.fdvs.dj.domain.DJColumnProperty;
import ar.com.fdvs.dj.domain.DJCustomExpression;

/**
 * Basic abstract column type representing a property from the obtained </br>
 * result set. Only subclasses of this class can be grouped.
 */
public abstract class DJPropertyColumn extends DJColumn {

	private DJColumnProperty columnProperty;
	private DJCustomExpression expressionToGroupBy;

	public DJColumnProperty getColumnProperty() {
		return columnProperty;
	}

	public void setColumnProperty(DJColumnProperty columnProperty) {
		this.columnProperty = columnProperty;
	}

	public DJCustomExpression getExpressionToGroupBy() {
		return expressionToGroupBy;
	}

	public void setExpressionToGroupBy(DJCustomExpression expressionToGroupBy) {
		this.expressionToGroupBy = expressionToGroupBy;
	}

	public String getGroupVariableName(String type, String columnToGroupByProperty) {
		return "variable-"+type+"_"+columnToGroupByProperty+"_"+getColumnProperty().getProperty();
	}

	public String getVariableClassName(DJOperation op) {
		if (op == DJOperation.COUNT)
			return Long.class.getName();
		else
			return getColumnProperty().getValueClassName();
	}

	public String getInitialExpression(DJOperation op) {
		if (op == DJOperation.COUNT)
			return "new java.lang.Long(\"0\")";
		else if (op == DJOperation.SUM)
			return "new " + getColumnProperty().getValueClassName()+"(\"0\")";
		else return null;
	}

}
