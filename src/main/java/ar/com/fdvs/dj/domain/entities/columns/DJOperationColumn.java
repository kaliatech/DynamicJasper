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

import java.util.Iterator;
import java.util.List;
import ar.com.fdvs.dj.domain.DJColumnOperation;
import ar.com.fdvs.dj.domain.DJOperation;

/**
 * Column created to handle numerical operations between two or more </br>
 * PropertyColumns.</br>
 * </br>
 * @see DJColumnOperation
 * @see DJPropertyColumn
 */
public class DJOperationColumn extends DJColumn {

	private DJColumnOperation columnOperation;
	private List columns;

	public DJColumnOperation getColumnOperation() {
		return columnOperation;
	}

	public void setColumnOperation(DJColumnOperation columnOperation) {
		this.columnOperation = columnOperation;
	}

	public List getColumns() {
		return columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}

	public String getTextForExpression() {
		StringBuffer exp = new StringBuffer();
		for (Iterator iter = columns.iterator(); iter
				.hasNext();) {
			DJSimpleColumn col = (DJSimpleColumn) iter.next();
			exp.append(" ((java.lang.Number)$F{" + col.getColumnProperty().getProperty()
					+ "}).doubleValue() ");
			if (iter.hasNext())
				exp.append(columnOperation.getValue());
		}
		return "new java.lang.Double(" + exp.toString() + ")";
	}

	public String getValueClassNameForExpression() {
		return "java.lang.Number";
	}

	public String getGroupVariableName(String type, String columnToGroupByProperty) {
		return "variable-"+type+"_"+columnToGroupByProperty+"_"+columnOperation.getValue();
	}

	public String getVariableClassName(DJOperation op) {
		if (op == DJOperation.COUNT)
			return Long.class.getName();
		else return Number.class.getName();
	}

	public String getInitialExpression(DJOperation op) {
		return "new java.lang.Long(\"0\")";
	}

}
