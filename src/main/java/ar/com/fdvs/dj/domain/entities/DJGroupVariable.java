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

import ar.com.fdvs.dj.domain.DJOperation;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;

/**
 * Entity used to handle global and group variables that represent the value of </br>
 * an operation applied to the corresponding rows.</br>
 * </br>
 * @see DJOperation
 */
public class DJGroupVariable implements Entity {

	private DJColumn columnToApplyOperation;
	private DJOperation operation;
	private DJStyle style;

	public DJGroupVariable(DJColumn columnToApplyOperation, DJOperation operation) {
		this.columnToApplyOperation = columnToApplyOperation;
		this.operation = operation;
	}

	public DJGroupVariable(DJColumn columnToApplyOperation, DJOperation operation, DJStyle style) {
		this.columnToApplyOperation = columnToApplyOperation;
		this.operation = operation;
		this.style = style;
	}

	public DJStyle getStyle() {
		return style;
	}

	public void setStyle(DJStyle style) {
		this.style = style;
	}


	public DJColumn getColumnToApplyOperation() {
		return columnToApplyOperation;
	}

	public void setColumnToApplyOperation(DJColumn columnToApplyOperation) {
		this.columnToApplyOperation = columnToApplyOperation;
	}

	public DJOperation getOperation() {
		return operation;
	}

	public void setOperation(DJOperation operation) {
		this.operation = operation;
	}

}
