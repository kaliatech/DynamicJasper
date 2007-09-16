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

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.commons.beanutils.BeanUtils;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.DJConditionalStyle;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.DJStatusLightCondition;
import ar.com.fdvs.dj.util.SortUtils;

public class ConditionalStylesReportTest extends TestCase {

	public DynamicReport buildReport() throws Exception {

		DJStyle detailStyle = new DJStyle();
		DJStyle headerStyle = new DJStyle();
		headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD); headerStyle.setBorder(Border.MEDIUM);
		headerStyle.setHorizontalAlign(HorizontalAlign.CENTER); headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);

		DJStyle titleStyle = new DJStyle();
		titleStyle.setFont(new Font(18,Font._FONT_VERDANA,true));
		DJStyle amountStyle = new DJStyle();
		amountStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
		DJStyle oddRowStyle = new DJStyle();
		oddRowStyle.setBorder(Border.NO_BORDER);
		Color veryLightGrey = new Color(230,230,230);
		oddRowStyle.setBackgroundColor(veryLightGrey);oddRowStyle.setTransparency(Transparency.OPAQUE);

		DynamicReportBuilder drb = new DynamicReportBuilder();
		Integer margin = new Integer(20);
		drb.addTitle("November 2006 sales report")					
			.addSubtitle("The items in this report correspond "
				+"to the main products: DVDs, Books, Foods and Magazines")		
			.addTitleStyle(titleStyle).addTitleHeight(new Integer(30))
			.addSubtitleHeight(new Integer(20))
			.addDetailHeight(new Integer(15))
			.addLeftMargin(margin)
			.addRightMargin(margin)
			.addTopMargin(margin)
			.addBottomMargin(margin)
			.addPrintBackgroundOnOddRows(true)
			.addOddRowBackgroundStyle(oddRowStyle)
			.addColumnsPerPage(new Integer(1))
			.addColumnSpace(new Integer(5));

		DJColumn columnState = ColumnBuilder.getInstance().addColumnProperty("state", String.class.getName())
			.addTitle("State").addWidth(new Integer(85))
			.addStyle(detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnBranch = ColumnBuilder.getInstance().addColumnProperty("branch", String.class.getName())
			.addTitle("Branch").addWidth(new Integer(85))
			.addStyle(detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnaProductLine = ColumnBuilder.getInstance().addColumnProperty("productLine", String.class.getName())
			.addTitle("Product Line").addWidth(new Integer(85))
			.addStyle(detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnaItem = ColumnBuilder.getInstance().addColumnProperty("item", String.class.getName())
			.addTitle("Item").addWidth(new Integer(85))
			.addStyle(detailStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnCode = ColumnBuilder.getInstance().addColumnProperty("id", Long.class.getName())
			.addTitle("ID").addWidth(new Integer(40))
			.addStyle(amountStyle).addHeaderStyle(headerStyle).build();

		DJColumn columnaCantidad = ColumnBuilder.getInstance().addColumnProperty("quantity", Long.class.getName())
			.addTitle("Quantity").addWidth(new Integer(80))
			.addStyle(amountStyle).addHeaderStyle(headerStyle).build();


		//Define Conditional Styles

		ArrayList conditionalStyles = createConditionalStyles(amountStyle);

		DJColumn columnAmount = ColumnBuilder.getInstance().addColumnProperty("amount", Float.class.getName())
			.addTitle("Amount").addWidth(new Integer(90)).addPattern("$ 0.00")
			.addConditionalStyles(conditionalStyles)
			.addStyle(amountStyle)
			.addHeaderStyle(headerStyle).build();

		drb.addColumn(columnState);
		drb.addColumn(columnBranch);
		drb.addColumn(columnaProductLine);
		drb.addColumn(columnaItem);
		drb.addColumn(columnCode);
		drb.addColumn(columnaCantidad);
		drb.addColumn(columnAmount);

		drb.addUseFullPageWidth(true);

		DynamicReport dr = drb.build();
		return dr;
	}

	/**
	 * Create a Conditional Styles that redefines the text color of the values of the "amount" column
	 * @param baseStyle
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	private ArrayList createConditionalStyles(DJStyle baseStyle) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		DJStyle style0 = (DJStyle) BeanUtils.cloneBean(baseStyle);
		style0.setTextColor(Color.RED);
		DJStyle style1 = (DJStyle) BeanUtils.cloneBean(baseStyle);
		style1.setTextColor(Color.YELLOW);
		DJStyle style2 = (DJStyle) BeanUtils.cloneBean(baseStyle);
		style2.setTextColor(Color.GREEN);

		DJStatusLightCondition status0 = new DJStatusLightCondition(new Double(0), new Double(5000));
		DJStatusLightCondition status1 = new DJStatusLightCondition(new Double(5000), new Double(7000));
		DJStatusLightCondition status2 = new DJStatusLightCondition(new Double(7000),new Double(100000));

		DJConditionalStyle condition0 = new DJConditionalStyle(status0,style0);
		DJConditionalStyle condition1 = new DJConditionalStyle(status1,style1);
		DJConditionalStyle condition2 = new DJConditionalStyle(status2,style2);

		ArrayList conditionalStyles = new ArrayList();
		conditionalStyles.add(condition0);
		conditionalStyles.add(condition1);
		conditionalStyles.add(condition2);
		return conditionalStyles;
	}

	public void testReport() {
		try {
			DynamicReport dr = buildReport();
			Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
			dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());
						
			JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);		//Create a JRDataSource, the Collection used
			
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
			ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/ConditionalStylesReportTest.pdf");
			JasperViewer.viewReport(jp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ConditionalStylesReportTest test = new ConditionalStylesReportTest();
		test.testReport();
	}

}
