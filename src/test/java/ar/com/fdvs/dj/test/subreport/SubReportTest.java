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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperDesignViewer;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJOperation;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJSubreport;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJPropertyColumn;
import ar.com.fdvs.dj.test.ReportExporter;
import ar.com.fdvs.dj.test.TestRepositoryProducts;
import ar.com.fdvs.dj.util.SortUtils;

public class SubReportTest extends TestCase {

	public DynamicReport buildReport() throws Exception {

		DJStyle detailStyle = new DJStyle();
		DJStyle headerStyle = new DJStyle();
		headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
		headerStyle.setBorder(Border.MEDIUM);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);

		DJStyle titleStyle = new DJStyle();
		titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
		DJStyle importeStyle = new DJStyle();
		importeStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		DJStyle oddRowStyle = new DJStyle();
		oddRowStyle.setBorder(Border.NO_BORDER);
		oddRowStyle.setBackgroundColor(Color.LIGHT_GRAY);
		oddRowStyle.setTransparency(Transparency.OPAQUE);

		DynamicReportBuilder drb = new DynamicReportBuilder();
		Integer margin = new Integer(20);
		drb
			.addTitleStyle(titleStyle)
			.addTitle("November 2006 sales report")					//defines the title of the report
			.addSubtitle("The items in this report correspond "
					+"to the main products: DVDs, Books, Foods and Magazines")				
			.addDetailHeight(new Integer(15)).addLeftMargin(margin)
			.addRightMargin(margin).addTopMargin(margin).addBottomMargin(margin)
			.addPrintBackgroundOnOddRows(true)
			.addOddRowBackgroundStyle(oddRowStyle);

		DJColumn columnState = ColumnBuilder.getInstance()
				.addColumnProperty("state", String.class.getName()).addTitle(
						"State").addWidth(new Integer(85))
				.addStyle(detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnBranch = ColumnBuilder.getInstance()
				.addColumnProperty("branch", String.class.getName()).addTitle(
						"Branch").addWidth(new Integer(85)).addStyle(
						detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnaProductLine = ColumnBuilder.getInstance()
				.addColumnProperty("productLine", String.class.getName())
				.addTitle("Product Line").addWidth(new Integer(85)).addStyle(
						detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnaItem = ColumnBuilder.getInstance()
				.addColumnProperty("item", String.class.getName()).addTitle(
						"Item").addWidth(new Integer(85)).addStyle(detailStyle)
				.addHeaderStyle(headerStyle).build();

		DJColumn columnCode = ColumnBuilder.getInstance()
				.addColumnProperty("id", Long.class.getName()).addTitle("ID")
				.addWidth(new Integer(40)).addStyle(importeStyle)
				.addHeaderStyle(headerStyle).build();

		DJColumn columnaQuantity = ColumnBuilder.getInstance()
				.addColumnProperty("quantity", Long.class.getName()).addTitle(
						"Quantity").addWidth(new Integer(80)).addStyle(
						importeStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnAmount = ColumnBuilder.getInstance()
				.addColumnProperty("amount", Float.class.getName()).addTitle(
						"Amount").addWidth(new Integer(90))
				.addPattern("$ 0.00").addStyle(importeStyle).addHeaderStyle(
						headerStyle).build();

		GroupBuilder gb1 = new GroupBuilder();
		
//		 define the criteria column to group by (columnState)
		DJGroup g1 = gb1.addCriteriaColumn((DJPropertyColumn) columnState).addFooterVariable(columnAmount,
						DJOperation.SUM) // tell the group place a variable footer of the column "columnAmount" with the SUM of allvalues of the columnAmount in this group.
				.addFooterVariable(columnaQuantity,
						DJOperation.SUM) // idem for the columnaQuantity column
				.addGroupLayout(GroupLayout.VALUE_IN_HEADER_WITH_COLNAMES) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
				.build();

		DJStyle defaultFooterVariableStyle = new DJStyle();
		defaultFooterVariableStyle.setStreching(Stretching.NO_STRETCH);
		defaultFooterVariableStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		defaultFooterVariableStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
		
		GroupBuilder gb2 = new GroupBuilder(); // Create another group (using another column as criteria)
		DJGroup g2 = gb2.addCriteriaColumn((DJPropertyColumn) columnBranch) // and we add the same operations for the columnAmount and
				.addFooterVariable(columnAmount,DJOperation.SUM) // columnaQuantity columns
				.addFooterVariable(columnaQuantity,DJOperation.SUM)
				.addHeaderVariable(columnAmount,DJOperation.SUM) // columnaQuantity columns
				.addHeaderVariable(columnaQuantity,DJOperation.SUM)
				.addDefaultFooterVariableStyle(defaultFooterVariableStyle)
				.addDefaultHeaderVariableStyle(defaultFooterVariableStyle)
				.build();

		drb.addColumn(columnState);
		drb.addColumn(columnBranch);
		drb.addColumn(columnaProductLine);
		drb.addColumn(columnaItem);
		drb.addColumn(columnCode);
		drb.addColumn(columnaQuantity);
		drb.addColumn(columnAmount);
		
		drb.addField("statistics", List.class.getName());

		drb.addGroup(g1); // add group g1
		drb.addGroup(g2); // add group g2

		JasperReport jrHeaderSubreport = createHeaderSubreport();
		DJSubreport headerSubreport = new DJSubreport();
		headerSubreport.setReport(jrHeaderSubreport);
		headerSubreport.setDataSourceExpression("statistics");
		headerSubreport.setDataSourceOrigin(DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD);
		headerSubreport.setDataSourceType(DJConstants.DATA_SOURCE_TYPE_COLLECTION);
		g2.getHeaderSubreports().add(headerSubreport);

		JasperReport jrFooterSubreport = createFooterSubreport();
		DJSubreport footerSubreport = new DJSubreport();
		footerSubreport.setReport(jrFooterSubreport);
		footerSubreport.setDataSourceExpression("statistics");
		g2.getFooterSubreports().add(footerSubreport);
		
		DJStyle subreportStyle = new DJStyle();
		subreportStyle.setBorder(Border.MEDIUM);
		subreportStyle.setBorderColor(Color.blue);
		footerSubreport.setStyle(subreportStyle);
		footerSubreport.setDataSourceOrigin(DJConstants.SUBREPORT_DATA_SOURCE_ORIGIN_FIELD);
		footerSubreport.setDataSourceType(DJConstants.DATA_SOURCE_TYPE_COLLECTION);
		footerSubreport.setStyle(subreportStyle);
		
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
			.addTitle("DJSubreport for this group")
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
		.addTitle("DJSubreport for this group")
		.build();
		return DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager());
	}

	public void testReport() {
	try {
		DynamicReport dr = buildReport();
		Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
		dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());
		
		JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
		ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/SubReportTest.pdf");
//		JasperViewer.viewReport(jp);
//		JasperDesignViewer.viewReportDesign(DynamicJasperHelper.generateJasperReport(dr, new ClassicLayoutManager()));
	} catch (Exception e) {
		e.printStackTrace();
	}
}

	public static void main(String[] args) {
		SubReportTest test = new SubReportTest();
		test.testReport();
	}

}