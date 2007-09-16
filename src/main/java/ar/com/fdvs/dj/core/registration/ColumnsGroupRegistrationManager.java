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

package ar.com.fdvs.dj.core.registration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.Entity;
import ar.com.fdvs.dj.domain.entities.columns.DJGlobalGroupColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJPropertyColumn;

/**
 * Manager invoked to register groups of columns. A DJGroup is read and </br>
 * transformed into a JRDesignGroup.</br>
 * </br>
 * @see DJGroup
 */
public class ColumnsGroupRegistrationManager extends AbstractEntityRegistrationManager {

	private static final Log log = LogFactory.getLog(ColumnsGroupRegistrationManager.class);

	public ColumnsGroupRegistrationManager(DynamicJasperDesign jd, DynamicReport dr) {
		super(jd,dr);
	}

	protected void registerEntity(Entity entity) {
		log.debug("registering group...");
		DJGroup columnsGroup = (DJGroup) entity;
		try {
			JRDesignGroup group = (JRDesignGroup)transformEntity(columnsGroup);
			getDjd().addGroup(group);
			//Variables are registered right after the group where they belong.
			new ColumnsGroupVariablesRegistrationManager(ColumnsGroupVariablesRegistrationManager.HEADER, columnsGroup.getColumnToGroupBy().getColumnProperty().getProperty(), getDjd(),getDynamicReport()).registerEntities(columnsGroup.getHeaderVariables());
			new ColumnsGroupVariablesRegistrationManager(ColumnsGroupVariablesRegistrationManager.FOOTER, columnsGroup.getColumnToGroupBy().getColumnProperty().getProperty(), getDjd(),getDynamicReport()).registerEntities(columnsGroup.getFooterVariables());
		} catch (JRException e) {
			throw new EntitiesRegistrationException(e.getMessage());
		}
	}

	//DJPropertyColumn only can be used for grouping (not DJOperationColumn)
	protected Object transformEntity(Entity entity) {
		log.debug("transforming group...");
		DJGroup columnsGroup = (DJGroup) entity;
		DJPropertyColumn column = columnsGroup.getColumnToGroupBy();
		JRDesignGroup group = new JRDesignGroup();

		if (column instanceof DJGlobalGroupColumn){
			group.setName("global_column_" + getDynamicReport().getColumnsGroups().indexOf(columnsGroup));
		} else {
			group.setName(column.getTitle());
		}
		
		group.setCountVariable(new JRDesignVariable());
		group.setGroupFooter(new JRDesignBand());
		group.setGroupHeader(new JRDesignBand());

		JRDesignExpression jrExpression = new JRDesignExpression();
		jrExpression.setText(column.getTextForExpression());
		jrExpression.setValueClassName(column.getValueClassNameForExpression());

		group.setExpression(jrExpression);
		group.setCountVariable(new JRDesignVariable());

		return group;
	}



}
