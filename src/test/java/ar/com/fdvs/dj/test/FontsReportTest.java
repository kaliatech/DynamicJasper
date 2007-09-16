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


import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.view.JasperDesignViewer;
import net.sf.jasperreports.view.JasperViewer;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.Stretching;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.util.SortUtils;

import com.lowagie.text.pdf.BaseFont;

public class FontsReportTest extends TestCase {

	public DynamicReport buildReport() throws Exception {


		/**
		 * Creates the DynamicReportBuilder and sets the basic options for
		 * the report
		 */
		FastReportBuilder drb = new FastReportBuilder();
		drb.addColumn("State", "state", String.class.getName(),30)
			.addColumn("Branch", "branch", String.class.getName(),30)
			.addColumn("Product Line", "productLine", String.class.getName(),50)
			.addColumn("Item", "item", String.class.getName(),50)
			.addColumn("Item Code", "id", Long.class.getName(),30,true)
			.addColumn("Quantity", "quantity", Long.class.getName(),60,true)
			.addColumn("Amount", "amount", Float.class.getName(),70,true)
			.addGroups(2)
			.addTitle("November 2006 sales report")
			.addSubtitle("This report was generated at " + new Date())
			.addUseFullPageWidth(true);	
		
		DJStyle title = new DJStyle();
		Font font1 = new Font(18,"Bauhaus 93",false);
		title.setFont(font1);
		title.setVerticalAlign(VerticalAlign.MIDDLE);
		
		DJStyle subtitle = new DJStyle();
		Font font2 = new Font(14,"Calibri",false);
		subtitle.setStreching(Stretching.RELATIVE_TO_BAND_HEIGHT);
		subtitle.setFont(font2);
		DJStyle columnHeader = null;
		DJStyle columDetail = null;
		drb.addSubtitleHeight(25);
		drb.addTitleHeight(30);
		drb.addDefaultStyles(title, subtitle, columnHeader, columDetail);

		DynamicReport dr = drb.build();	
		
		return dr;
	}

	public void testReport() {
		try {
			DynamicReport dr = buildReport();
			Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
			dummyCollection = SortUtils.sortCollection(dummyCollection,dr.getColumns());
						
			JRDataSource ds = new JRBeanCollectionDataSource(dummyCollection);		//Create a JRDataSource, the Collection used
																											//here contains dummy hardcoded objects...
			
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);	//Creates the JasperPrint object, we pass as a Parameter
																											//the DynamicReport, a new ClassicLayoutManager instance (this
			
			Map fontsMap = new HashMap();
			URL url = this.getClass().getClassLoader().getResource("fonts/BAUHS93.TTF");
			FontKey key = new FontKey("Bauhaus 93", false, false);
			PdfFont font = new PdfFont(url.getPath().substring(1),BaseFont.IDENTITY_H,true); 
			fontsMap.put(key, font);

			url = this.getClass().getClassLoader().getResource("fonts/calibri.ttf");
			key = new FontKey("Calibri", false, false);
			font = new PdfFont(url.getPath().substring(1),BaseFont.IDENTITY_H,true); 
			fontsMap.put(key, font);
																											//one does the magic) and the JRDataSource 
			ReportExporter.exportReport(jp, System.getProperty("user.dir")+ "/target/FontReportTest.pdf", fontsMap);
			JasperViewer.viewReport(jp);	//finally display the report report
			JasperReport jr = DynamicJasperHelper.generateJasperReport(dr,  new ClassicLayoutManager());
			JasperDesignViewer.viewReportDesign(jr);
		} catch (Exception e) {
//			e.getCause().printStackTrace();
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		FontsReportTest test = new FontsReportTest();
		test.testReport();
	}

}
