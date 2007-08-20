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

package ar.com.fdvs.dj.test.subreport;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.SubReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.Subreport;
import ar.com.fdvs.dj.test.ChartReportTest;
import ar.com.fdvs.dj.test.ReportExporter;
import ar.com.fdvs.dj.test.TestRepositoryProducts;
import ar.com.fdvs.dj.test.domain.DummyObject;
import ar.com.fdvs.dj.test.domain.Product;

public class ConcatenatedReportTest extends TestCase {

	private Map params = new HashMap();

	public DynamicReport buildReport() throws Exception {

		Style headerStyle = new Style();
		headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
		headerStyle.setBorder(Border.MEDIUM);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);

		Style titleStyle = new Style();
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
		Style importeStyle = new Style();
		importeStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		Style oddRowStyle = new Style();
		oddRowStyle.setBorder(Border.NO_BORDER);
		oddRowStyle.setBackgroundColor(Color.LIGHT_GRAY);
		oddRowStyle.setTransparency(Transparency.OPAQUE);

		DynamicReportBuilder drb = new DynamicReportBuilder();
		Integer margin = new Integer(20);
		drb
			.addTitleStyle(titleStyle)
			.addTitle("Concatenated reports")					//defines the title of the report
			.addSubtitle("All the reports shown here are concatenated as sub reports")				
			.addDetailHeight(new Integer(15)).addLeftMargin(margin)
			.addRightMargin(margin).addTopMargin(margin).addBottomMargin(margin)
			.addPrintBackgroundOnOddRows(true)
			.addAutoText(AutoText.AUTOTEXT_PAGE_X_OF_Y, AutoText.POSITION_FOOTER,AutoText.ALIGMENT_CENTER)
			.addOddRowBackgroundStyle(oddRowStyle)
			.addField("data1", Collection.class.getName())
			.addField("data2", Collection.class.getName());

		

		SubReportBuilder srb1 = new SubReportBuilder();
		Subreport subreport1 = srb1.addDataSource( DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD,
				DJConstants.DATA_SOURCE_TYPE_COLLECTION, 
				"data2")
				.addReport(createHeaderSubreport())
				.build();
		
		SubReportBuilder srb2 = new SubReportBuilder();
					Subreport subreport2 = srb2.addDataSource( DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD, 
							DJConstants.DATA_SOURCE_TYPE_COLLECTION, 
					"data2")
				.addReport(createFooterSubreport())
				.build();
					
//		params.put("statistics", Product.statistics_.toArray()  );
//
//		params.put("subreportsDataSource", TestRepositoryProducts.getDummyCollection()  );
		
//		drb.addConcatenatedReport(subreport1);
//		drb.addConcatenatedReport(subreport2);
//		drb.addConcatenatedReport(subreport1);
//		drb.addConcatenatedReport(subreport2);

		ChartReportTest crt = new ChartReportTest();
		JasperReport chartJr = DynamicJasperHelper.generateJasperReport(crt.buildReport(), new ClassicLayoutManager());
		SubReportBuilder srb3 = new SubReportBuilder();
		Subreport subreport3 = srb3.addDataSource( DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD, 
									DJConstants.DATA_SOURCE_TYPE_COLLECTION, 
									"data1")
			.addReport(chartJr)
			.build();		
		
		drb.addConcatenatedReport(subreport3);
//		drb.addConcatenatedReport(subreport1);
//		drb.addConcatenatedReport(subreport3);

		drb.addUseFullPageWidth(true);
		
		DynamicReport dr = drb.build();
		
		return dr;
	}

	private JasperReport createHeaderSubreport() throws Exception {
		FastReportBuilder rb = new FastReportBuilder();
		DynamicReport dr = rb
			.addColumn("Date", "date", Date.class.getName(), 100)
			.addColumn("Average", "average", Float.class.getName(), 50)
			.addColumn("%", "percentage", Float.class.getName(), 50)
			.addColumn("Amount", "amount", Float.class.getName(), 50)
			.addMargins(5, 5, 20, 20)
			.addUseFullPageWidth(true)
			.addTitle("Subreport for this group")
			.build();
		return DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager());
	}

	private JasperReport createFooterSubreport() throws Exception {
		FastReportBuilder rb = new FastReportBuilder();
		DynamicReport dr = rb
		.addColumn("Area", "name", String.class.getName(), 100)
		.addColumn("Average", "average", Float.class.getName(), 50)
		.addColumn("%", "percentage", Float.class.getName(), 50)
		.addColumn("Amount", "amount", Float.class.getName(), 50)
		.addGroups(1)
		.addMargins(5, 5, 20, 20)
		.addUseFullPageWidth(true)
		.addTitle("Subreport for this group")
		.build();
		return DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager());
	}

	public void testReport() {
	try {
		DynamicReport dr = buildReport();
		Collection dummyCollection = new ArrayList(); 
		DummyObject dummyObject = getDummyObject();
		dummyCollection.add(dummyObject);
		
		JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds,params);
		ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/ConcatenatedReportTest.pdf");
		JasperViewer.viewReport(jp);
//		JasperDesignViewer.viewReportDesign(DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager()));
	} catch (Exception e) {
		e.printStackTrace();
	}
}

	private DummyObject getDummyObject() {
		DummyObject dum = new DummyObject();
		dum.setData1(TestRepositoryProducts.getDummyCollection());
		dum.setData2(Product.statistics_);
		return dum;
	}

	public static void main(String[] args) {
		ConcatenatedReportTest test = new ConcatenatedReportTest();
		test.testReport();
	}

}

