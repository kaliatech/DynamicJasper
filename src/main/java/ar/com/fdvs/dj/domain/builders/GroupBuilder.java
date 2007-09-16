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

import java.util.Iterator;

import ar.com.fdvs.dj.domain.DJOperation;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.DJSubreport;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJPropertyColumn;

/**
 * Builder created to give users a friendly way of adding groups to a report.</br>
 * </br>
 * Usage example: </br>
 * GroupBuilder gb1 = new GroupBuilder();
 * DJGroup g1 = gb1.addCriteriaColumn((DJPropertyColumn) columnState)</br>
 * .addFooterVariable(columnAmount,DJOperation.SUM)</br>
 * .addFooterVariable(columnaQuantity,DJOperation.SUM)</br>
 * .addGroupLayout(GroupLayout.VALUE_IN_HEADER_WITH_COLNAMES)</br>
 * .build();</br>
 * </br>
 * Like with all DJ's builders, it's usage must end with a call to build() mehtod.
 * </br>
 */
public class GroupBuilder {

	private DJGroup group = new DJGroup();
	
	private DJStyle defaultFooterVariableStyle;
	private DJStyle defaultHeaderVariableStyle;

	public DJGroup build(){
		//Apply Styles if any (for variables)
		for (Iterator iterator = group.getHeaderVariables().iterator(); iterator.hasNext();) {
			DJGroupVariable var = (DJGroupVariable) iterator.next();
			if (defaultHeaderVariableStyle != null)
				var.setStyle(defaultHeaderVariableStyle);
		}
		
		for (Iterator iterator = group.getFooterVariables().iterator(); iterator.hasNext();) {
			DJGroupVariable var = (DJGroupVariable) iterator.next();
			if (defaultFooterVariableStyle != null)
				var.setStyle(defaultFooterVariableStyle);
		}
		
		return group;
	}

	public GroupBuilder addCriteriaColumn(DJPropertyColumn column) {
		group.setColumnToGroupBy(column);
		return this;
	}

	public GroupBuilder addHeaderVariable(DJGroupVariable variable) {
		group.getHeaderVariables().add(variable);
		return this;
	}

	public GroupBuilder addHeaderVariable(DJColumn column, DJOperation operation) {
		group.getHeaderVariables().add(new DJGroupVariable(column,operation));
		return this;
	}

	public GroupBuilder addFooterVariable(DJGroupVariable variable) {
		group.getFooterVariables().add(variable);
		return this;
	}

	public GroupBuilder addFooterVariable(DJColumn column3, DJOperation operation) {
		group.getFooterVariables().add(new DJGroupVariable(column3,operation));
		return this;
	}
	public GroupBuilder addFooterVariable(DJColumn column3, DJOperation operation, DJStyle style) {
		group.getFooterVariables().add(new DJGroupVariable(column3,operation,style));
		return this;
	}

	public GroupBuilder addHeaderHeight(Integer height) {
		group.setHeaderHeight(height);
		return this;
	}

	public GroupBuilder addFooterHeight(Integer height) {
		group.setFooterHeight(height);
		return this;
	}

	public GroupBuilder addGroupLayout(GroupLayout layout) {
		group.setLayout(layout);
		return this;
	}

	public GroupBuilder addDefaultFooterVariableStyle(DJStyle defaultFooterVariableStyle) {
		this.defaultFooterVariableStyle = defaultFooterVariableStyle;
		return this;
	}

	public GroupBuilder addDefaultHeaderVariableStyle(DJStyle defaultHeaderVariableStyle) {
		this.defaultHeaderVariableStyle = defaultHeaderVariableStyle;
		return this;
	}

	public GroupBuilder addHeaderSubreport(DJSubreport subreport) {
		group.getHeaderSubreports().add(subreport);
		return this;
	}

	public GroupBuilder addFooterSubreport(DJSubreport subreport) {
		group.getFooterSubreports().add(subreport);
		return this;
	}





}