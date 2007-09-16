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

package ar.com.fdvs.dj.test;

import java.util.Collection;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;
import ar.com.fdvs.dj.util.SortUtils;

public class PlainReportTest extends TestCase {

	public DynamicReport buildReport() throws Exception {

		DJStyle detailStyle = new DJStyle();
		DJStyle headerStyle = new DJStyle();

		DJStyle titleStyle = new DJStyle();
		DJStyle subtitleStyle = new DJStyle();
		DJStyle amountStyle = new DJStyle(); amountStyle.setHorizontalAlign(HorizontalAlign.RIGHT);

		/**
		 * Creates the DynamicReportBuilder and sets the basic options for
		 * the report
		 */
		DynamicReportBuilder drb = new DynamicReportBuilder();
		drb.addTitle("November 2006 sales report")					//defines the title of the report
			.addSubtitle("The items in this report correspond "
					+"to the main products: DVDs, Books, Foods and Magazines")					
			.addDetailHeight(15)						//defines the height for each record of the report
			.addMargins(30, 20, 30, 15)							//define the margin space for each side (top, bottom, left and right)
			.addDefaultStyles(titleStyle, subtitleStyle, headerStyle, detailStyle)
			.addColumnsPerPage(1);						//defines columns per page (like in the telephone guide)

		/**
		 * Note that we still didn�t call the build() method
		 */

		/**
		 * Column definitions. We use a new ColumnBuilder instance for each
		 * column, the ColumnBuilder.getInstance() method returns a new instance
		 * of the builder
		 */
		DJColumn columnState = ColumnBuilder.getInstance()		//creates a new instance of a ColumnBuilder
			.addColumnProperty("state", String.class.getName())			//defines the field of the data source that this column will show, also its type
			.addTitle("State")											//the title for the column
			.addWidth(85)									//the width of the column		
			.build();													//builds and return a new DJColumn

		//Create more columns
		DJColumn columnBranch = ColumnBuilder.getInstance()
			.addColumnProperty("branch", String.class.getName())
			.addTitle("Branch").addWidth(85)
			.build();

		DJColumn columnaProductLine = ColumnBuilder.getInstance()
			.addColumnProperty("productLine", String.class.getName())
			.addTitle("Product Line").addWidth(85)
			.build();

		DJColumn columnaItem = ColumnBuilder.getInstance()
			.addColumnProperty("item", String.class.getName())
			.addTitle("Item").addWidth(85)
			.build();

		DJColumn columnCode = ColumnBuilder.getInstance()
			.addColumnProperty("id", Long.class.getName())
			.addTitle("ID").addWidth(40)
			.build();

		DJColumn columnaCantidad = ColumnBuilder.getInstance()
			.addColumnProperty("quantity", Long.class.getName())
			.addTitle("Quantity").addWidth(80)
			.build();

		DJColumn columnAmount = ColumnBuilder.getInstance()
			.addColumnProperty("amount", Float.class.getName())
			.addTitle("Amount").addWidth(90)
			.addPattern("$ 0.00")		//defines a pattern to apply to the values swhown (uses TextFormat)
			.addStyle(amountStyle)		//special style for this column (align right)
			.build();

		/**
		 * We add the columns to the report (through the builder) in the order
		 * we want them to appear
		 */
		drb.addColumn(columnState);
		drb.addColumn(columnBranch);
		drb.addColumn(columnaProductLine);
		drb.addColumn(columnaItem);
		drb.addColumn(columnCode);
		drb.addColumn(columnaCantidad);
		drb.addColumn(columnAmount);

		/**
		 * add some more options to the report (through the builder)
		 */
		drb.addUseFullPageWidth(true);	//we tell the report to use the full width of the page. this rezises
										//the columns width proportionally to meat the page width.


		DynamicReport dr = drb.build();	//Finally build the report!

		return dr;
	}

	public void testReport() {
		try {
			DynamicReport dr = buildReport();

			Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
			dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());

			JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);	//Create a JRDataSource, the Collection used
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);	//Creates the JasperPrint object, we pass as a Parameter
																											//one does the magic) and the JRDataSource
			ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/PlainReportTest.pdf");
			JasperViewer.viewReport(jp);	//finally display the report report
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args) {
		PlainReportTest test = new PlainReportTest();
		test.testReport();
	}

}
