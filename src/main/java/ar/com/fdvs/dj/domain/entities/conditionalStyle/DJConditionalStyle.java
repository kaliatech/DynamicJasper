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

package ar.com.fdvs.dj.domain.entities.conditionalStyle;

import ar.com.fdvs.dj.domain.DJCustomExpression;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.entities.Entity;

/**
 * Entity used to handle Conditional style.
 */
public class DJConditionalStyle implements Entity {

	private DJCustomExpression condition;
	private DJStyle style;

	//Internal condition name. ColumnName_style_ConditionNumber
	private String name;

	public DJConditionalStyle(DJCustomExpression condition, DJStyle style) {
		this.condition = condition;
		this.style = style;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DJCustomExpression getCondition() {
		return condition;
	}
	public void setCondition(DJCustomExpression condition) {
		this.condition = condition;
	}
	public DJStyle getStyle() {
		return style;
	}
	public void setStyle(DJStyle style) {
		this.style = style;
	}

}
