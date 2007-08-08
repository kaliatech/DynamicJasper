package ar.com.fdvs.dj.domain.entities;

import net.sf.jasperreports.engine.JasperReport;

public class Subreport {
	
	private JasperReport report;
	private String dataSourceExpression;
	
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

}
