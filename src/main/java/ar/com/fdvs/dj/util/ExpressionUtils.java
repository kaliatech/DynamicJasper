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

package ar.com.fdvs.dj.util;

import java.sql.Connection;
import java.sql.ResultSet;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.domain.DJDataSource;
import ar.com.fdvs.dj.domain.entities.Subreport;
import ar.com.fdvs.dj.domain.entities.SubreportParameter;

public class ExpressionUtils {

	private static final String REPORT_PARAMETERS_MAP = "$P{REPORT_PARAMETERS_MAP}";

	/**
	 * Returns an expression that points to a java.util.Map object with the parameters to
	 * be used during the subreport fill time.
	 * Posibilities are:<br>
	 * - Use Partent report Map<br>
	 * - Use a Map that is a parameter of the partents map<br>
	 * - Use a property of the current row.
	 * @param sr
	 * @return
	 */
	public static JRDesignExpression getParameterExpression(Subreport sr) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClassName(java.util.Map.class.getName());
		if (sr.isUseParentReportParameters()){
			exp.setText(REPORT_PARAMETERS_MAP);
			return exp;
		}

		if (sr.getParametersExpression() == null)
			return null;

		if (sr.getParametersMapOrigin() == DJConstants.SUBREPORT_PARAMETER_MAP_ORIGIN_PARAMETER){
			exp.setText(REPORT_PARAMETERS_MAP + ".get( \""+ sr.getParametersExpression() +"\" )");
			return exp;
		}

		if (sr.getParametersMapOrigin() == DJConstants.SUBREPORT_PARAMETER_MAP_ORIGIN_FIELD){
			exp.setText("$F{"+ sr.getParametersExpression() +"}");
			return exp;
		}

		return null;
	}

	/**
	 * Returns the expression string required
	 * @param ds
	 * @return
	 */
	public static JRDesignExpression getDataSourceExpression(DJDataSource ds) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClass(JRDataSource.class);

		String dsType = getDataSourceTypeStr(ds.getDataSourceType());
		String expText = null;
		if (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_FIELD){
			expText = dsType + "$F{" + ds.getDataSourceExpression() + "})";
		} else if (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_PARAMETER){
			expText = dsType + REPORT_PARAMETERS_MAP + ".get( \""+ ds.getDataSourceExpression() +"\" ) )";
		} else if (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_TYPE_SQL_CONNECTION) {
			expText = dsType + REPORT_PARAMETERS_MAP + ".get( \""+ ds.getDataSourceExpression() +"\" ) )";
		}

		exp.setText(expText);

		return exp;
	}
	public static JRDesignExpression getConnectionExpression(DJDataSource ds) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClass(Connection.class);
		
		String dsType = getDataSourceTypeStr(ds.getDataSourceType());
		String expText = dsType + REPORT_PARAMETERS_MAP + ".get( \""+ ds.getDataSourceExpression() +"\" ) )";
		
		exp.setText(expText);
		
		return exp;
	}
	
	/**
	 * Returns a JRDesignExpression that points to the main report connection
	 * @return
	 */
	public static JRDesignExpression getReportConnectionExpression() {
		JRDesignExpression connectionExpression = new JRDesignExpression();
		connectionExpression.setText("$P{"+JRDesignParameter.REPORT_CONNECTION+"}");
		connectionExpression.setValueClass(Connection.class);
		return connectionExpression;
	}

	protected static String getDataSourceTypeStr(int datasourceType) {
		//TODO Complete all other possible types
		String dsType = "(";
		if (DJConstants.DATA_SOURCE_TYPE_COLLECTION == datasourceType){
			 dsType = "new "+JRBeanCollectionDataSource.class.getName()+"((java.util.Collection)";
		}
		else if (DJConstants.DATA_SOURCE_TYPE_ARRAY == datasourceType){
			dsType = "new "+JRBeanArrayDataSource.class.getName()+"((Object[])";
		}
		else if (DJConstants.DATA_SOURCE_TYPE_RESULTSET == datasourceType){
			dsType = "new "+JRResultSetDataSource.class.getName()+"(("+ResultSet.class.getName() +")";
		}
		else if (DJConstants.DATA_SOURCE_TYPE_JRDATASOURCE == datasourceType){
			dsType = "(("+JRDataSource.class.getName() +")";
		}
		else if (DJConstants.DATA_SOURCE_TYPE_SQL_CONNECTION == datasourceType){
			dsType = "(("+Connection.class.getName() +")";
		}
		return dsType;
	}

	public static JRDesignExpression createStringExpression(String text) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClass(String.class);
		exp.setText(text);
		return exp;
	}
	public static JRDesignExpression createExpression(String text, Class clazz) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClass(clazz);
		exp.setText(text);
		return exp;
	}
	public static JRDesignExpression createExpression(String text, String className) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClassName(className);
		exp.setText(text);
		return exp;
	}
	
	public static JRDesignExpression createExpression(SubreportParameter sp) {
		JRDesignExpression exp = new JRDesignExpression();
		exp.setValueClassName(sp.getClassName());
		String text = null;
		if (sp.getParameterOrigin()== DJConstants.SUBREPORT_PARAM_ORIGIN_FIELD){
			text = "$F{" + sp.getExpression() + "}";
		} else if (sp.getParameterOrigin()== DJConstants.SUBREPORT_PARAM_ORIGIN_PARAMETER){
			text = REPORT_PARAMETERS_MAP + ".get( \""+ sp.getExpression() +"\")";
		} else if (sp.getParameterOrigin()== DJConstants.SUBREPORT_PARAM_ORIGIN_VARIABLE){
			text = "$V{" + sp.getExpression() + "}";
		} else { //CUSTOM
			text = sp.getExpression();
		}
		exp.setText(text);
		return exp;
	}

}
