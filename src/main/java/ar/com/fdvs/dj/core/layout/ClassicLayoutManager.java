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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.registration.ColumnsGroupVariablesRegistrationManager;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJOperation;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DJImageBanner;
import ar.com.fdvs.dj.domain.DJStyle;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.DJSubreport;
import ar.com.fdvs.dj.domain.entities.columns.DJColumn;
import ar.com.fdvs.dj.domain.entities.columns.DJGlobalGroupColumn;
import ar.com.fdvs.dj.util.SubReportUtil;

/**
 * Main Layout Manager recommended for most cases.</br>
 * </br>
 * It provides DJ full features (styles, groups, conditional styles, </br>
 * expressions, group and total variables, etc)
 */
public class ClassicLayoutManager extends AbstractLayoutManager {

	private static final Log log = LogFactory.getLog(ClassicLayoutManager.class);
	
	protected static final String EXPRESSION_TRUE_WHEN_NOT_FIRST_PAGE = "new java.lang.Boolean(((Number)$V{PAGE_NUMBER}).doubleValue() != 1)";
	protected static final String EXPRESSION_TRUE_WHEN_FIRST_PAGE = "new java.lang.Boolean(((Number)$V{PAGE_NUMBER}).doubleValue() == 1)";

	protected void startLayout() {
		super.startLayout();
		generateTitleBand();
		generateHeaderBand();
		applyHeaderAutotexts();
		
		if (getReport().getColumnsGroups() != null)
			layoutGroups();
	}

	/**
	 * 
	 */
	protected void applyHeaderAutotexts() {
		if (getReport().getAutoTexts() == null)
			return;
		/**
		 * Apply the autotext in footer if any
		 */
		JRDesignBand headerband = (JRDesignBand) getDesign().getPageHeader();
		if (headerband == null ) {
			headerband = new JRDesignBand();
			getDesign().setPageHeader(headerband);
		}
		
		ArrayList positions = new ArrayList();
		positions.add(HorizontalBandAlignment.LEFT);
		positions.add(HorizontalBandAlignment.CENTER);
		positions.add(HorizontalBandAlignment.RIGHT);
		
		ArrayList autotexts = new ArrayList(getReport().getAutoTexts());
		Collections.reverse(autotexts);

		int totalYoffset = findTotalOffset(positions,autotexts, AutoText.POSITION_HEADER);
		moveBandsElemnts(totalYoffset, headerband);
		
		for (Iterator iterator = positions.iterator(); iterator.hasNext();) {
			HorizontalBandAlignment currentAlignment = (HorizontalBandAlignment) iterator.next();
			int yOffset = 0;
		
			for (Iterator iter = getReport().getAutoTexts().iterator(); iter.hasNext();) {
				AutoText text = (AutoText) iter.next();
				if (text.getPosition() == AutoText.POSITION_HEADER && text.getAlignment().equals(currentAlignment)) {
					CommonExpressionsHelper.add(yOffset,getDesign(), getReport(), headerband, text);
					yOffset += text.getHeight().intValue();
				}
			}		
		}
		
		/** END */
	}

	/**
	 * Finds the highest sum of height for each posible aligment (left, center, right)
	 * @param aligments
	 * @param autotexts
	 * @return
	 */
	private int findTotalOffset(ArrayList aligments, ArrayList autotexts, byte position) {
		int total = 0;
		for (Iterator iterator = aligments.iterator(); iterator.hasNext();) {
			HorizontalBandAlignment currentAlignment = (HorizontalBandAlignment) iterator.next();
			int aux = 0;
			for (Iterator iter = getReport().getAutoTexts().iterator(); iter.hasNext();) {
				AutoText text = (AutoText) iter.next();
				if (text.getPosition() == position && currentAlignment.equals(text.getAlignment())) {
					aux += text.getHeight().intValue();
				}
			}			
			if (aux > total)
				total = aux;
		}
		return total;
	}

	/**
	 * Moves the elements contained in "band" in the Y axis "yOffset"
	 * @param intValue
	 * @param band 
	 */
	private void moveBandsElemnts(int yOffset, JRDesignBand band) {
		if (band == null)
			return;
		
		for (Iterator iterator = band.getChildren().iterator(); iterator.hasNext();) {
			JRDesignElement elem = (JRDesignElement) iterator.next();
			elem.setY(elem.getY()+yOffset);
		}
	}

	protected void endLayout() {
		super.endLayout();
		applyBanners();
		applyFooterAutotexts();
		setBandsFinalHeight();
	}

	protected void applyFooterAutotexts() {
		if (getReport().getAutoTexts() == null)
			return;
		
		JRDesignBand footerband = (JRDesignBand) getDesign().getPageFooter();
		if (footerband == null ) {
			footerband = new JRDesignBand();
			getDesign().setPageFooter(footerband);
		}

		ArrayList positions = new ArrayList();
		positions.add(HorizontalBandAlignment.LEFT);
		positions.add(HorizontalBandAlignment.CENTER);
		positions.add(HorizontalBandAlignment.RIGHT);
		
		for (Iterator iterator = positions.iterator(); iterator.hasNext();) {
			HorizontalBandAlignment currentAlignment = (HorizontalBandAlignment) iterator.next();
			int yOffset = 0;
			/**
			 * Apply the autotext in footer if any
			 */
			for (Iterator iter = getReport().getAutoTexts().iterator(); iter.hasNext();) {
				AutoText text = (AutoText) iter.next();
				if (text.getPosition() == AutoText.POSITION_FOOTER && text.getAlignment().equals(currentAlignment) ) {
					CommonExpressionsHelper.add(yOffset,getDesign(), getReport(), footerband, text);
					yOffset += text.getHeight().intValue();
				}
			}
			
		}
		
	}

	/**
	 * Create the image elements for the banners tha goes into the
	 * title and header bands depending on the case
	 *
	 */
	protected void applyBanners() {
		/**
		 * First create image banners for the first page only
		 */
		JRDesignBand title = (JRDesignBand) getDesign().getTitle();
		//if there is no title band, but there are banner images for the first page, we create a title band
		if (title == null && !getReport().getOptions().getFirstPageImageBanners().isEmpty()){
			title = new JRDesignBand();
			getDesign().setTitle(title);
		}		
		applyImageBannersToBand(title,getReport().getOptions().getFirstPageImageBanners().values(),null);
		
		/**
		 * Now create image banner for the rest of the pages
		 */
		JRDesignBand pageHeader = (JRDesignBand) getDesign().getPageHeader();
		//if there is no title band, but there are banner images for the first page, we create a title band
		if (pageHeader == null && !getReport().getOptions().getImageBanners().isEmpty()){
			pageHeader = new JRDesignBand();
			getDesign().setPageHeader(pageHeader);
		}	
		JRDesignExpression printWhenExpression = null;
		if (!getReport().getOptions().getFirstPageImageBanners().isEmpty()){
			printWhenExpression = new JRDesignExpression();
			printWhenExpression.setValueClass(Boolean.class);
			printWhenExpression.setText(EXPRESSION_TRUE_WHEN_NOT_FIRST_PAGE);			
		}
		applyImageBannersToBand(pageHeader,getReport().getOptions().getImageBanners().values(),printWhenExpression);
		
		
	}
	/**
	 * Create the image elements for the banners tha goes into the
	 * title band
	 * @param printWhenExpression 
	 *
	 */
	protected void applyImageBannersToBand(JRDesignBand band, Collection imageBanners, JRDesignExpression printWhenExpression ) {
		int maxHeight = 0;
		for (Iterator iter = imageBanners.iterator(); iter.hasNext();) {
			DJImageBanner imageBanner = (DJImageBanner) iter.next();
			if (imageBanner.getHeight() > maxHeight)
				maxHeight = imageBanner.getHeight();
		}
		
		if (band != null){
			//move everything down
			for (Iterator iter =band.getChildren().iterator(); iter.hasNext();) {
				JRDesignElement element = (JRDesignElement) iter.next();
				element.setY(element.getY() + maxHeight);
			}
			
			for (Iterator iter = imageBanners.iterator(); iter.hasNext();) {
				DJImageBanner imageBanner = (DJImageBanner) iter.next();
				String path = "\"" + imageBanner.getImagePath().replaceAll("\\\\", "/") + "\"";
				JRDesignImage image = new JRDesignImage(new JRDesignStyle().getDefaultStyleProvider());
				JRDesignExpression imageExp = new JRDesignExpression();
				imageExp.setText(path);
				
				imageExp.setValueClass(String.class);
				image.setExpression(imageExp);
				image.setHeight(imageBanner.getHeight());
				image.setWidth(imageBanner.getWidth());
				image.setPrintWhenExpression(printWhenExpression);
				image.setRemoveLineWhenBlank(true);
				
				if (imageBanner.getAlign() == DJImageBanner.ALIGN_LEFT)				
					image.setX(0);
				else if (imageBanner.getAlign() == DJImageBanner.ALIGN_RIGHT)
					image.setX(getReport().getOptions().getPage().getWidth() -  getReport().getOptions().getLeftMargin().intValue() - getReport().getOptions().getRightMargin().intValue() - imageBanner.getWidth());
				else if (imageBanner.getAlign() == DJImageBanner.ALIGN_CENTER){
					int x = (getReport().getOptions().getPage().getWidth() - 
							getReport().getOptions().getRightMargin().intValue() - 
							getReport().getOptions().getLeftMargin().intValue() - imageBanner.getWidth()) / 2;
					image.setX(getReport().getOptions().getLeftMargin().intValue() + x);
				}
				
				image.setY(0);
				band.addElement(image);
				
			}			
			band.setHeight(band.getHeight() + maxHeight);
		}
	}

	/**
	 * Adds title and subtitle to the TitleBand when it applies.
	 * If title is not present then subtitle will be ignored
	 */
	private void generateTitleBand() {
		log.debug("Generating title band...");
		JRDesignBand band = (JRDesignBand) getDesign().getPageHeader();
		int yOffset = 0;
	
		//If title is not present then subtitle will be ignored
		if (getReport().getTitle() == null)
			return;

		if (band != null && !getDesign().isTitleNewPage()){
			//Title and subtitle comes afer the page header
			yOffset = band.getHeight();
			
		} else {
			band = (JRDesignBand) getDesign().getTitle();
			if (band == null){
				band = new JRDesignBand();
				getDesign().setTitle(band);
			}
		}

		JRDesignExpression printWhenExpression = new JRDesignExpression();
		printWhenExpression.setValueClass(Boolean.class);
		printWhenExpression.setText(EXPRESSION_TRUE_WHEN_FIRST_PAGE);		
		
		JRDesignTextField title = new JRDesignTextField();
		JRDesignExpression exp = new JRDesignExpression();
		exp.setText("\"" + getReport().getTitle() + "\"");
		exp.setValueClass(String.class);
		title.setExpression(exp);
		title.setWidth(getReport().getOptions().getPrintableWidth());
		title.setHeight(getReport().getOptions().getTitleHeight().intValue());
		title.setY(yOffset);
		title.setPrintWhenExpression(printWhenExpression);
		title.setRemoveLineWhenBlank(true);
		applyStyleToElement(getReport().getTitleStyle(), title);
		band.addElement(title);

		JRDesignTextField subtitle = new JRDesignTextField();
		if (getReport().getSubtitle() != null) {
			JRDesignExpression exp2 = new JRDesignExpression();
			exp2.setText("\"" + getReport().getSubtitle() + "\"");
			exp2.setValueClass(String.class);
			subtitle.setExpression(exp2);
			subtitle.setWidth(getReport().getOptions().getPrintableWidth());
			subtitle.setHeight(getReport().getOptions().getSubtitleHeight().intValue());
			subtitle.setY(title.getY() + title.getHeight());
			subtitle.setPrintWhenExpression(printWhenExpression);
			subtitle.setRemoveLineWhenBlank(true);
			applyStyleToElement(getReport().getSubtitleStyle(), subtitle);
			band.addElement(subtitle);
		}

	}

	/**
	 * Layout columns in groups by reading the corresponding report options.
	 * @throws LayoutException
	 */
	private void layoutGroups() {
		log.debug("Starting groups layout...");
		int i = 0;
		for (Iterator iter = getReport().getColumnsGroups().iterator(); iter.hasNext();) {
			DJGroup columnsGroup = (DJGroup) iter.next();
			JRDesignGroup jgroup = (JRDesignGroup) getDesign().getGroupsList().get(i++);
		
			JRDesignBand header = (JRDesignBand) jgroup.getGroupHeader();
			JRDesignBand footer = (JRDesignBand) jgroup.getGroupFooter();
			header.setHeight(columnsGroup.getHeaderHeight().intValue());
			footer.setHeight(columnsGroup.getFooterHeight().intValue());
			if (columnsGroup.getLayout().isShowColumnNames()) {
				for (Iterator iterator =  getReport().getColumns().iterator(); iterator.hasNext();) {
					DJColumn col = (DJColumn) iterator.next();

					JRDesignTextField designStaticText = new JRDesignTextField();
					JRDesignExpression exp = new JRDesignExpression();
					exp.setText("\"" + col.getTitle() + "\"");
					exp.setValueClass(String.class);
					designStaticText.setExpression(exp);
					designStaticText.setHeight(columnsGroup.getHeaderHeight().intValue());
					designStaticText.setWidth(col.getWidth().intValue());
					designStaticText.setX(col.getPosX().intValue());
					designStaticText.setY(col.getPosY().intValue());

					applyStyleToElement(col.getHeaderStyle(), designStaticText);

					header.addElement(designStaticText);
				}
			}
			layoutGroupVariables(columnsGroup, jgroup);
			layoutGroupSubreports(columnsGroup, jgroup);
		}
	}
	
	/**
	 * If there is a SubReport on a Group, we do the layout here
	 * @param columnsGroup
	 * @param jgroup
	 */
	private void layoutGroupSubreports(DJGroup columnsGroup, JRDesignGroup jgroup) {
		log.debug("Starting subreport layout...");
		JRDesignBand footerBand = (JRDesignBand) jgroup.getGroupFooter();
		JRDesignBand headerBand = (JRDesignBand) jgroup.getGroupHeader();
		
		layOutSubReportInBand(columnsGroup, headerBand, DJConstants.HEADER);
		layOutSubReportInBand(columnsGroup, footerBand, DJConstants.FOOTER);
		
	}

	/**
	 * @param columnsGroup
	 * @param band
	 * @param position 
	 */
	private void layOutSubReportInBand(DJGroup columnsGroup, JRDesignBand band, String position) {
		
		List footerSubreportsList = DJConstants.FOOTER.equals(position) 
				? columnsGroup.getFooterSubreports() 
				: columnsGroup.getHeaderSubreports();
				
		for (Iterator iterator = footerSubreportsList.iterator(); iterator.hasNext();) {
			DJSubreport sr = (DJSubreport) iterator.next();
			JRDesignSubreport subreport = new JRDesignSubreport(new JRDesignStyle().getDefaultStyleProvider());
			
			//The data source
			subreport.setDataSourceExpression(SubReportUtil.getDataSourceExpression(sr));
			
			//the subreport design
			JRDesignExpression srExpression = new JRDesignExpression();
			String paramname = "subreport_" + position + "_" + getReport().getColumnsGroups().indexOf(columnsGroup) + "_" + footerSubreportsList.indexOf(sr);
			((DynamicJasperDesign)getDesign()).getParametersWithValues().put(paramname, sr.getReport());
			srExpression.setText("("+JasperReport.class.getName()+")$P{REPORT_PARAMETERS_MAP}.get( \""+ paramname +"\" )");
			srExpression.setValueClass(JasperReport.class);
			subreport.setExpression(srExpression );
			
			
			//set the parameters
			subreport.setParametersMapExpression(SubReportUtil.getParameterExpression(sr));
			
			
			//some other options (cosmetical)
			//subreport.setStretchType(JRDesignElement.STRETCH_TYPE_NO_STRETCH);
			int offset = findVerticalOffset(band);
			subreport.setY(offset);
			subreport.setX(-getReport().getOptions().getLeftMargin().intValue());
			subreport.setWidth(getReport().getOptions().getPage().getWidth());
			subreport.setHeight(100);
			subreport.setPositionType(JRElement.POSITION_TYPE_FIX_RELATIVE_TO_TOP);
			subreport.setStretchType(JRElement.STRETCH_TYPE_NO_STRETCH);
			
			if (sr.getStyle() != null)
				applyStyleToElement(sr.getStyle(), subreport);
			
			//adding to the band
			band.addElement(subreport);
		}
	}

	/**
	 * If variables are present for a given group, they are placed in it's
	 * header/footer band.
	 * @param DJGroup group
	 * @param JRDesignGroup jgroup
	 * @throws LayoutException
	 */
	private void layoutGroupVariables(DJGroup group, JRDesignGroup jgroup) {
		log.debug("Starting groups variables layout...");
		JRDesignBand headerBand = (JRDesignBand) jgroup.getGroupHeader();
		JRDesignBand footerBand = (JRDesignBand) jgroup.getGroupFooter();

		int headerOffset = changeHeaderBandHeightForVariables(headerBand, group);

		if (group.getLayout().isShowValueInHeader())
			insertValueInHeader(headerBand, group, headerOffset);

		placeVariableInBand(group.getHeaderVariables(), group, jgroup, DJConstants.HEADER, headerBand, headerOffset);
		placeVariableInBand(group.getFooterVariables(), group, jgroup, DJConstants.FOOTER, footerBand, 0);
	}

	private void placeVariableInBand(Collection variables, DJGroup columnsGroup, JRDesignGroup jgroup, String type, JRDesignBand band, int yOffset) {
		log.debug("Placing variables in "+type+" band...");
		if ((variables != null)&&(variables.size()>0)) {
			Iterator it = variables.iterator();
			while (it.hasNext()) {
				DJGroupVariable var = (DJGroupVariable) it.next();
				DJColumn col = (DJColumn) var.getColumnToApplyOperation();

				String variableName = col.getGroupVariableName(type, columnsGroup.getColumnToGroupBy().getColumnProperty().getProperty());

				JRDesignExpression expression = new JRDesignExpression();
				JRDesignTextField textField = new JRDesignTextField();
				expression.setText("$V{" + variableName + "}");
				expression.setValueClassName(col.getVariableClassName(var.getOperation()));
				if (var.getOperation() != DJOperation.COUNT)
					textField.setPattern(col.getPattern());

				textField.setKey(variableName);
				textField.setExpression(expression);

				textField.setX(col.getPosX().intValue());
				if (yOffset!=0)
					textField.setY(yOffset);

				textField.setHeight(columnsGroup.getHeaderHeight().intValue());
				textField.setWidth(col.getWidth().intValue());

				textField.setEvaluationTime(JRExpression.EVALUATION_TIME_GROUP);

				textField.setEvaluationGroup(jgroup);
				
				//Assign the style to the element.
				//First we look for the specific element style, then the default style for the group variables
				//and finally the column style.
				DJStyle defStyle = DJConstants.HEADER.equals(type)?columnsGroup.getDefaulHeaderStyle():columnsGroup.getDefaulFooterStyle();

				if (var.getStyle() != null)
					applyStyleToElement(var.getStyle(), textField);
				else if (defStyle != null)
					applyStyleToElement(defStyle, textField);
				else
					applyStyleToElement(col.getStyle(), textField);

				band.addElement(textField);

			}

			if (columnsGroup.getColumnToGroupBy() instanceof DJGlobalGroupColumn) {
				int totalWidth = 0;

				totalWidth = ((DJGroupVariable)variables.iterator().next()).getColumnToApplyOperation().getPosX().intValue();

				DJGlobalGroupColumn globalCol = (DJGlobalGroupColumn) columnsGroup.getColumnToGroupBy();

				JRDesignTextField globalTextField = new JRDesignTextField();
				JRDesignExpression globalExp = new JRDesignExpression();
				globalExp.setText(globalCol.getTextForExpression());
				globalExp.setValueClassName(globalCol.getValueClassNameForExpression());
				globalTextField.setExpression(globalExp);

				globalTextField.setHeight(band.getHeight());
				globalTextField.setWidth(totalWidth);
				globalTextField.setX(((DJColumn)getReport().getColumns().get(0)).getPosX().intValue());
				if (type.equals(ColumnsGroupVariablesRegistrationManager.HEADER))
					globalTextField.setY(yOffset);
				globalTextField.setKey("global_legend_"+type);

				applyStyleToElement(globalCol.getStyle(), globalTextField);

				band.addElement(globalTextField);
			}
		}
	}

	private void insertValueInHeader(JRDesignBand headerBand, DJGroup columnsGroup, int headerOffset) {
		JRDesignTextField textField = generateTextFieldFromColumn(columnsGroup.getColumnToGroupBy(), columnsGroup.getHeaderHeight().intValue(), columnsGroup);
		textField.setHorizontalAlignment(columnsGroup.getColumnToGroupBy().getStyle().getHorizontalAlign().getValue());
		textField.setStretchType(JRDesignElement.STRETCH_TYPE_NO_STRETCH); //XXX this is a patch for subreports, ensure it works well.
		textField.setY(textField.getY() + headerOffset);
		headerBand.addElement(textField);
	}

	private int changeHeaderBandHeightForVariables(JRDesignBand headerBand, DJGroup columnsGroup) {
		int result = 0;
		if (!headerBand.getChildren().isEmpty()) {
			int acu = headerBand.getHeight();
			headerBand.setHeight(headerBand.getHeight() + columnsGroup.getHeaderHeight().intValue());
			result = acu;
		} else
			headerBand.setHeight(getReport().getOptions().getFooterHeight().intValue());
		return result;
	}

	private void generateHeaderBand() {
		log.debug("generating header band...");
		JRDesignBand header = (JRDesignBand) getDesign().getColumnHeader();
		if (header == null) {
			header = new JRDesignBand();
			getDesign().setColumnHeader(header);
		}
		if (!DynamicJasperHelper.existsGroupWithColumnNames(getReport().getColumnsGroups()))
			generateHeaderBand(header);
	}

	protected void transformDetailBandTextField(DJColumn column, JRDesignTextField textField) {
		//TODO: Set default characters when null values are found.
		log.debug("transforming detail band text field...");
		DJGroup group = DynamicJasperHelper.getColumnGroup(column, getReport().getColumnsGroups());
		if (group!=null&&!group.getLayout().isShowValueForEach()) {
			textField.getStyle().setBorder((byte)0);
			JRDesignExpression exp = new JRDesignExpression();
			exp.setText("\" \"");
			exp.setValueClassName(String.class.getName());
			textField.setExpression(exp);
		}
	}
}
