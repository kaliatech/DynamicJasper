package ar.com.fdvs.dj.domain.entities;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.domain.DJStyle;
import net.sf.jasperreports.engine.JasperReport;

public class DJSubreport {
	
	private JasperReport report;
	
	/**
	 * This expression must point to a JRDataSource object
	 */
	private String dataSourceExpression;
	
	/**
	 * Tells form where to look up the datasource expression
	 */
	private int dataSourceOrigin = DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_PARAMETER;

	private int dataSourceType = DJConstants.DATA_SOURCE_TYPE_COLLECTION;
	
	
	private DJStyle style;
	
	/**
	 * By default true,
	 */
	private boolean useParentReportParameters = true;
	
	/**
	 * This expression should point to a java.util.Map object
	 * which will be use as the parameters map for the subreport
	 */
	private String parametersExpression;
	
	/**
	 * Tells if the parameters maps origin is a parameter of the parent report, or a value of the current row (field)<br>
	 * It's value must be SUBREPORT_PARAMETER_MAP_ORIGIN_PARAMETER or SUBREPORT_PARAMETER_MAP_ORIGIN_FIELD
	 */
	private int parametersMapOrigin = DJConstants.SUBREPORT_PARAMETER_MAP_ORIGIN_PARAMETER;
	
	public int getParametersMapOrigin() {
		return parametersMapOrigin;
	}
	public void setParametersMapOrigin(int parametersMapOrigin) {
		this.parametersMapOrigin = parametersMapOrigin;
	}
	
	public String getParametersExpression() {
		return parametersExpression;
	}
	public void setParametersExpression(String parametersExpression) {
		this.parametersExpression = parametersExpression;
	}
	public boolean isUseParentReportParameters() {
		return useParentReportParameters;
	}
	public void setUseParentReportParameters(boolean useParentReportParameters) {
		this.useParentReportParameters = useParentReportParameters;
	}
	public DJStyle getStyle() {
		return style;
	}
	public void setStyle(DJStyle style) {
		this.style = style;
	}
	public JasperReport getReport() {
		return report;
	}
	public void setReport(JasperReport design) {
		this.report = design;
	}
	public String getDataSourceExpression() {
		return dataSourceExpression;
	}
	public void setDataSourceExpression(String dataSourceExpression) {
		this.dataSourceExpression = dataSourceExpression;
	}
	public int getDataSourceOrigin() {
		return dataSourceOrigin;
	}
	public void setDataSourceOrigin(int dataSourceOrigin) {
		this.dataSourceOrigin = dataSourceOrigin;
	}
	public int getDataSourceType() {
		return dataSourceType;
	}
	public void setDataSourceType(int dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

}
