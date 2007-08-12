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

package ar.com.fdvs.dj.core.layout;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.charts.design.JRDesignBarPlot;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.base.JRBaseChartPlot;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.base.JRBaseVariable;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignRectangle;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJChart;
import ar.com.fdvs.dj.domain.DJChartOptions;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.DynamicReportOptions;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DataSetFactory;
import ar.com.fdvs.dj.domain.entities.ColumnsGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;

/**
 * Abstract Class used as base for the different Layout Managers.</br>
 * </br>
 * A Layout Manager is always invoked after the entities registration stage.</br>
 * A subclass should be created whenever we want to give the users the chance to </br>
 * easily apply global layout changes to their reports. Example: Ignore groups </br>
 * and styles for an Excel optimized report.
 */
public abstract class AbstractLayoutManager implements LayoutManager {

	private static final Log log = LogFactory.getLog(AbstractLayoutManager.class);
	protected static final String EXPRESSION_TRUE_WHEN_ODD = "new java.lang.Boolean(((Number)$V{REPORT_COUNT}).doubleValue() % 2 == 0)";

	private JasperDesign design;
	private DynamicReport report;

	protected abstract void transformDetailBandTextField(AbstractColumn column, JRDesignTextField textField);

	public void applyLayout(JasperDesign design, DynamicReport report) throws LayoutException {
		log.debug("Applying Layout...");
		try {
			setDesign(design);
			setReport(report);
			ensureStyles();
			startLayout();
			transformDetailBand();
			endLayout();
		} catch (RuntimeException e) {
			throw new LayoutException(e.getMessage(),e);
		}
	}
	
	protected void startLayout() {
		setColumnsFinalWidth();
	}

	protected void endLayout() {
		layoutCharts();
		setBandsFinalHeight();
	}

	/**
	 * Sets a default style for every element that doesn't have one
	 */
	protected void ensureStyles() {
		Style defaultDetailStyle = getReport().getOptions().getDefaultDetailStyle();
		Style defaultHeaderStyle = getReport().getOptions().getDefaultHeaderStyle();
//		Style defaultFooterStyle = getReport().getOptions().getDefaultFooterStyle();
		for (Iterator iter = report.getColumns().iterator(); iter.hasNext();) {
			AbstractColumn column = (AbstractColumn) iter.next();
			if (column.getStyle() == null) column.setStyle(defaultDetailStyle);
			if (column.getHeaderStyle() == null) column.setHeaderStyle(defaultHeaderStyle);
//			if (column.getFooterStyle() == null) column.setFooterStyle(defaulyFooterStyle);	
		}

//		Style defaultGroupHeaderStyle = getReport().getOptions().getDefaultGroupHeaderStyle();
//		Style defaultGroupFooterStyle = getReport().getOptions().getDefaultGroupFooterStyle();
//		for (Iterator iter = report.getColumnsGroups().iterator(); iter.hasNext();) {
//			ColumnsGroup group = (ColumnsGroup) iter.next();
//			if (group.getHeaderSyle() == null) group.setHeaderStyle(defaultGroupHeaderStyle);
//			if (group.getFooterSyle() == null) group.setFooterStyle(defaultGroupFooterStyle);
//		}
	}
	
	private void transformDetailBand() {
		log.debug("transforming Detail Band...");
		JRDesignBand detail = (JRDesignBand) design.getDetail();
		detail.setHeight(report.getOptions().getDetailHeight().intValue());

		if (getReport().getOptions().isPrintBackgroundOnOddRows()){
			decorateOddRows(detail);
		}

		for (Iterator iter = report.getColumns().iterator(); iter.hasNext();) {

			AbstractColumn column = (AbstractColumn)iter.next();

			if (column.getConditionalStyles() != null && !column.getConditionalStyles().isEmpty() ){
        		for (Iterator iterator = column.getConditionalStyles().iterator(); iterator.hasNext();) {
					ConditionalStyle condition = (ConditionalStyle) iterator.next();
					JRDesignTextField textField = generateTextFieldFromColumn(column, getReport().getOptions().getDetailHeight().intValue(), null);
					transformDetailBandTextField(column, textField);
					applyStyleToElement(condition.getStyle(), textField);
					textField.setPrintWhenExpression(getExpressionForConditionalStyle(condition.getName(), column.getTextForExpression()));
					detail.addElement(textField);
				}
        	} else {
        		JRDesignTextField textField = generateTextFieldFromColumn(column, getReport().getOptions().getDetailHeight().intValue(), null);
        		transformDetailBandTextField(column, textField);
        		detail.addElement(textField);
        	}
        }
	}

	/**
	 * Places a square as DetailBand background for odd rows.
	 * @param JRDesignBand detail
	 */
	private void decorateOddRows(JRDesignBand detail) {
		JRDesignExpression expression = new JRDesignExpression();
		expression.setValueClass(Boolean.class);
		expression.setText(EXPRESSION_TRUE_WHEN_ODD);

		JRDesignRectangle rectangle = new JRDesignRectangle();
		rectangle.setPrintWhenExpression(expression);
		DynamicReportOptions options = getReport().getOptions();
		rectangle.setHeight(options.getDetailHeight().intValue());
		rectangle.setWidth(options.getColumnWidth());
		rectangle.setStretchType(JRDesignRectangle.STRETCH_TYPE_RELATIVE_TO_TALLEST_OBJECT);
		JRBaseStyle style = options.getOddRowBackgroundStyle().transform();
		style.setForecolor(options.getOddRowBackgroundStyle().getBackgroundColor());
		rectangle.setStyle(style);
		detail.addElement(rectangle);
	}

	/**
	 * Creates and returns the expression used to apply a conditional style.
	 * @param String paramName
	 * @param String textForExpression
	 * @return JRExpression
	 */
	private JRExpression getExpressionForConditionalStyle(String paramName, String textForExpression) {
		 String text = "(("+CustomExpression.class.getName()+")$P{"+paramName+"})."+CustomExpression.EVAL_METHOD_NAME+"("+textForExpression+")";
		 JRDesignExpression expression = new JRDesignExpression();
		 expression.setValueClass(Boolean.class);
		 expression.setText(text);
		 return expression;
	}

	protected final void generateHeaderBand(JRDesignBand band) {
		log.debug("Generating header band...");
		band.setHeight(report.getOptions().getHeaderHeight().intValue());

		for (Iterator iter = report.getColumns().iterator(); iter.hasNext();) {

			AbstractColumn col = (AbstractColumn) iter.next();
			if (col.getTitle() == null)
				continue;

			JRDesignExpression expression = new JRDesignExpression();
			JRDesignTextField textField = new JRDesignTextField();
			expression.setText("\""+ col.getTitle() + "\"");

			expression.setValueClass(String.class);

			textField.setKey("header_"+col.getTitle());
			textField.setExpression(expression);

			textField.setX(col.getPosX().intValue());
			textField.setY(col.getPosY().intValue());
			textField.setHeight(band.getHeight());
			textField.setWidth(col.getWidth().intValue());

			textField.setPrintWhenDetailOverflows(true);
			textField.setBlankWhenNull(true);

			Style headerStyle = col.getHeaderStyle();
			if (headerStyle == null)
				headerStyle = report.getOptions().getDefaultHeaderStyle();

			applyStyleToElement(headerStyle, textField);

			band.addElement(textField);
		}
	}

	protected final void applyStyleToElement(Style style, JRDesignElement textElement) {
		textElement.setStyle(style.transform());
		if (textElement instanceof JRDesignTextElement ) {
			JRDesignTextElement textField = (JRDesignTextElement) textElement;
			textField.setStretchType(style.getStreching().getValue());
			textField.setPositionType(JRTextField.POSITION_TYPE_FLOAT);
		}
		if (textElement instanceof JRDesignTextField ) {
			JRDesignTextField textField = (JRDesignTextField) textElement;
			textField.setStretchWithOverflow(style.isStretchWithOverflow());
		}
	}

	/**
	 * Sets the columns width by reading some report options like the
	 * printableArea and useFullPageWidth.
	 * columns with fixedWidth property set in TRUE will not be modified
	 */
	private void setColumnsFinalWidth() {
		log.debug("Setting columns final width...");
		float factor = 1;
		int printableArea = report.getOptions().getColumnWidth();

		log.debug("printableArea = " + printableArea );

		if (report.getOptions().isUseFullPageWidth()) {
			int columnsWidth = 0;
			int notRezisableWidth = 0;
			for (Iterator iterator =  report.getColumns().iterator(); iterator.hasNext();) {
				AbstractColumn col = (AbstractColumn) iterator.next();
				columnsWidth += col.getWidth().intValue();
				if (col.getFixedWidth().booleanValue())
					notRezisableWidth += col.getWidth().intValue();
			}

			log.debug("columnsWidth = "+ columnsWidth);
			log.debug("notRezisableWidth = "+ notRezisableWidth);

			factor = (float) (printableArea-notRezisableWidth) / (float) (columnsWidth-notRezisableWidth);
			log.debug("factor = "+ factor);
			int acu = 0;
			int colFinalWidth = 0;

			Collection resizableColumns = CollectionUtils.select( report.getColumns(),new Predicate() {
				public boolean evaluate(Object arg0) {
					return !((AbstractColumn)arg0).getFixedWidth().booleanValue();
				}

			}) ;

			for (Iterator iter = resizableColumns.iterator(); iter.hasNext();) {
				AbstractColumn col = (AbstractColumn) iter.next();

				if (!iter.hasNext()) {
					col.setWidth(new Integer(printableArea - notRezisableWidth - acu));
				} else {
					colFinalWidth = (new Float(col.getWidth().intValue() * factor)).intValue();
					acu += colFinalWidth;
					col.setWidth(new Integer(colFinalWidth));
				}
			}
		}

		// If the columns width changed, the X position must be setted again.
		int posx = 0;
		for (Iterator iterator =  report.getColumns().iterator(); iterator.hasNext();) {
			AbstractColumn col = (AbstractColumn) iterator.next();
			col.setPosX(new Integer(posx));
			posx += col.getWidth().intValue();
		}
	}

	/**
	 * Sets the necessary height for all bands in the report, to hold their children
	 */
	protected void setBandsFinalHeight() {
		log.debug("Setting bands final height...");
		
		setBandFinalHeight((JRDesignBand) design.getPageHeader());
		setBandFinalHeight((JRDesignBand) design.getPageFooter());
		setBandFinalHeight((JRDesignBand) design.getColumnHeader());
		setBandFinalHeight((JRDesignBand) design.getColumnFooter());
		setBandFinalHeight((JRDesignBand) design.getSummary());
		setBandFinalHeight((JRDesignBand) design.getBackground());
		setBandFinalHeight((JRDesignBand) design.getDetail());
		setBandFinalHeight((JRDesignBand) design.getLastPageFooter());
		setBandFinalHeight((JRDesignBand) design.getTitle());
		setBandFinalHeight((JRDesignBand) design.getPageFooter());
		
		for (Iterator iter = design.getGroupsList().iterator(); iter.hasNext();) {
			JRGroup jrgroup = (JRGroup) iter.next();
			setBandFinalHeight((JRDesignBand) jrgroup.getGroupHeader());
			setBandFinalHeight((JRDesignBand) jrgroup.getGroupFooter());
		}
	}

	/**
	 * Sets the band's height to hold all its children
	 * @param band Band to be resized
	 */
	protected void setBandFinalHeight(JRDesignBand band) {
		if (band != null) {
			int finalHeight = findVerticalOffset(band);
			band.setHeight(finalHeight);
		}
	}

	/**
	 * Finds "Y" corrdinate value in with more elements could be added in the band
	 * @param band
	 * @return
	 */
	protected int findVerticalOffset(JRDesignBand band) {
		int finalHeight = 0;
		if (band != null) {
			for (Iterator iter = band.getChildren().iterator(); iter.hasNext();) {
				JRDesignElement element = (JRDesignElement) iter.next();
				int currentHeight = element.getY() + element.getHeight();
				if (currentHeight > finalHeight) finalHeight = currentHeight;
			}
			return finalHeight;
		}
		return finalHeight;
	}

	/**
	 * Creates a JasperReport DesignTextField from a DynamicJasper AbstractColumn.
	 * @param AbstractColumn col
	 * @param int height
	 * @param ColumnsGroup group
	 * @return JRDesignTextField
	 */
	protected final JRDesignTextField generateTextFieldFromColumn(AbstractColumn col, int height, ColumnsGroup group) {
		JRDesignTextField textField = new JRDesignTextField();
		JRDesignExpression exp = new JRDesignExpression();

		if (col.getPattern() != null && "".equals(col.getPattern().trim())) {
			textField.setPattern(col.getPattern());
        }

		exp.setText(col.getTextForExpression());
		exp.setValueClassName(col.getValueClassNameForExpression());
		textField.setExpression(exp);
		textField.setWidth(col.getWidth().intValue());
		textField.setX(col.getPosX().intValue());
		textField.setY(col.getPosY().intValue());
		textField.setHeight(height);

		textField.setBlankWhenNull(col.getBlankWhenNull());

		textField.setPattern(col.getPattern());

		textField.setPrintRepeatedValues(col.getPrintRepeatedValues().booleanValue());

        textField.setPrintWhenDetailOverflows(true);

        Style columnStyle = col.getStyle();
        if (columnStyle == null)
        	columnStyle = report.getOptions().getDefaultDetailStyle();

		applyStyleToElement(columnStyle, textField);

        if (group != null) {
        	int index = getReport().getColumnsGroups().indexOf(group);
            JRDesignGroup previousGroup = (JRDesignGroup) getDesign().getGroupsList().get(index);
            textField.setPrintWhenGroupChanges(previousGroup);
        }
        return textField;
	}
	
	/*
	 * Takes all the report's charts and inserts them in their corresponding bands
	 */
	protected final void layoutCharts() {
		for (Iterator iter = getReport().getCharts().iterator(); iter.hasNext();) {
			DJChart djChart = (DJChart) iter.next();
			JRDesignChart chart = createChart(djChart);
			JRDesignBand band = getPositionBand(djChart);
			band.addElement(chart);
		}
	}
	
	/**
	 * Calculates and returns the band where the band is to be inserted
	 * @param djChart
	 * @return
	 */
	private JRDesignBand getPositionBand(DJChart djChart) {
		JRDesignGroup jgroup = getGroupFromColumnsGroup(djChart.getColumnsGroup());
		JRDesignGroup parentGroup = getParent(jgroup);
		
		JRDesignBand band = null;
		switch (djChart.getOptions().getPosition()) {
		case DJChartOptions.POSITION_HEADER:
			band = (JRDesignBand) ((parentGroup.equals(jgroup)) ? getDesign().getSummary(): parentGroup.getGroupHeader());
			break;
		case DJChartOptions.POSITION_FOOTER:
			band = (JRDesignBand) ((parentGroup.equals(jgroup)) ? getDesign().getSummary(): parentGroup.getGroupFooter());
		}
		return band;
	}

	private JRDesignChart createChart(DJChart djChart){

			JRDesignGroup jrGroup = getGroupFromColumnsGroup(djChart.getColumnsGroup());
			
			JRDesignChart chart = new JRDesignChart(new JRDesignStyle().getDefaultStyleProvider(), djChart.getType());
			chart.setDataset(DataSetFactory.getDataset(djChart.getType(), jrGroup, getParent(jrGroup), registerChartVariable(djChart)));
			interpeterOptions(djChart, chart);
			
			chart.setEvaluationTime(JRExpression.EVALUATION_TIME_REPORT);
			return chart;
	}
	
	private void interpeterOptions(DJChart djChart, JRDesignChart chart) {
		DJChartOptions options = djChart.getOptions();
	
		//size
		if (options.isCentered()) chart.setWidth(getReport().getOptions().getPrintableWidth());
		else chart.setWidth(options.getWidth());
		chart.setHeight(options.getHeight());
		
		//position
		chart.setX(options.getX());
		chart.setPadding(10);
		chart.setY(options.getY());
		arrangeBand(djChart, chart);
		
		//options
		chart.setShowLegend(options.isShowLegend());
		chart.setBackcolor(options.getBackColor());
		chart.setBorder(options.getBorder());
		
		//colors
		if (options.getColors() != null){
			int i = 1;
			for (Iterator iter = options.getColors().iterator(); iter.hasNext();i++) {
				Color color = (Color) iter.next();
				chart.getPlot().getSeriesColors().add(new JRBaseChartPlot.JRBaseSeriesColor(i, color));
			}
		}
		//Chart-dependant options
		if (djChart.getType() == DJChart.BAR_CHART) ((JRDesignBarPlot) chart.getPlot()).setShowTickLabels(options.isShowLabels());
	}

	//TODO: 5's must be replaced by a constant or a configurable number
	private void arrangeBand(DJChart djChart, JRDesignChart chart) {
		int index = getReport().getColumnsGroups().indexOf(djChart.getColumnsGroup());
		
		if (djChart.getOptions().getPosition() == DJChartOptions.POSITION_HEADER){
			JRDesignBand band = (JRDesignBand) getParent(((JRDesignGroup)getDesign().getGroupsList().get(index))).getGroupHeader();
			
			for (int i = 0; i < band.getElements().length; i++) {
				JRDesignElement element = (JRDesignElement) band.getElements()[i];
				element.setY(element.getY() + chart.getY() + chart.getHeight() + 5);
			}
		}
		else {
			JRDesignBand band = (JRDesignBand) getParent(((JRDesignGroup)getDesign().getGroupsList().get(index))).getGroupFooter();
			int max = 0;
			for (int i = 0; i < band.getElements().length; i++) {
				JRDesignElement element = (JRDesignElement) band.getElements()[i];
				if ( (element.getHeight() + element.getY()) > max);
				max = element.getHeight() + element.getY();
			}
			chart.setY(max +5 );
		}	
	}

	/**
	 * Creates and registers a variable to be used by the Chart
	 * @param chart Chart that needs a variable to be generated
	 * @return the generated variable
	 */
	private JRDesignVariable registerChartVariable(DJChart chart) {
		
		JRDesignGroup group = getGroupFromColumnsGroup(chart.getColumnsGroup());

		Class clazz = null;
		try { clazz = Class.forName(chart.getColumn().getValueClassNameForExpression());
		} catch (ClassNotFoundException e) { /*Should never happen*/ }
		
		JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$F{" + chart.getColumn().getTitle().toLowerCase() + "}");
		expression.setValueClass(clazz);
		
		JRDesignVariable var = new JRDesignVariable();
		var.setValueClass(clazz);
		var.setExpression(expression);
		var.setCalculation(chart.getOperation());
		var.setResetGroup(group);
		var.setResetType(JRBaseVariable.RESET_TYPE_GROUP);
		var.setName("CHART_" + group.getName() + "_" + chart.getColumn().getTitle() + "_" + chart.getOperation());
		
		try {
			getDesign().addVariable(var);
		} catch (JRException e) {
			throw new LayoutException(e.getMessage());
		}
		return var;
	}
	
	
	
	
	
	//TODO: Maybe a helper could calculate the following
	/**
	 * Finds the parent group of the given one and returns it
	 * @param group Group for which the parent is needed
	 * @return The parent group of the given one. If the given one is the first one, it returns the same group
	 */
	private JRDesignGroup getParent(JRDesignGroup group){
		int index = getDesign().getGroupsList().indexOf(group);
		JRDesignGroup parentGroup = (index > 0) ? (JRDesignGroup) getDesign().getGroupsList().get(index-1): group;
		return parentGroup;
	}
	
	protected JRDesignGroup getGroupFromColumnsGroup(ColumnsGroup group){
		int index = getReport().getColumnsGroups().indexOf(group);
		return (JRDesignGroup) getDesign().getGroupsList().get(index);
	}
	
	//TODO: Maybe a helper could calculate this (up to here)
	
	
	
	
	
	protected JasperDesign getDesign() {
		return design;
	}

	protected void setDesign(JasperDesign design) {
		this.design = design;
	}

	protected DynamicReport getReport() {
		return report;
	}

	protected void setReport(DynamicReport report) {
		this.report = report;
	}

}
