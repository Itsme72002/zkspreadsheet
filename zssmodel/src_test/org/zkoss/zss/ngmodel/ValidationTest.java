package org.zkoss.zss.ngmodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.zkoss.poi.ss.util.CellReference;
import org.zkoss.util.Locales;
import org.zkoss.zss.ngapi.NRanges;
import org.zkoss.zss.ngapi.impl.DataValidationHelper;
import org.zkoss.zss.ngmodel.NCell.CellType;
import org.zkoss.zss.ngmodel.NCellStyle.Alignment;
import org.zkoss.zss.ngmodel.NCellStyle.BorderType;
import org.zkoss.zss.ngmodel.NCellStyle.FillPattern;
import org.zkoss.zss.ngmodel.NChart.NChartType;
import org.zkoss.zss.ngmodel.NDataValidation.OperatorType;
import org.zkoss.zss.ngmodel.NDataValidation.ValidationType;
import org.zkoss.zss.ngmodel.NFont.Boldweight;
import org.zkoss.zss.ngmodel.NFont.TypeOffset;
import org.zkoss.zss.ngmodel.NFont.Underline;
import org.zkoss.zss.ngmodel.NHyperlink.HyperlinkType;
import org.zkoss.zss.ngmodel.NPicture.Format;
import org.zkoss.zss.ngmodel.chart.NGeneralChartData;
import org.zkoss.zss.ngmodel.chart.NSeries;
import org.zkoss.zss.ngmodel.impl.AbstractBookSeriesAdv;
import org.zkoss.zss.ngmodel.impl.AbstractCellAdv;
import org.zkoss.zss.ngmodel.impl.BookImpl;
import org.zkoss.zss.ngmodel.impl.AbstractSheetAdv;
import org.zkoss.zss.ngmodel.impl.RefImpl;
import org.zkoss.zss.ngmodel.impl.SheetImpl;
import org.zkoss.zss.ngmodel.sys.dependency.DependencyTable;
import org.zkoss.zss.ngmodel.sys.dependency.Ref;
import org.zkoss.zss.ngmodel.util.CellStyleMatcher;
import org.zkoss.zss.ngmodel.util.FontMatcher;

public class ValidationTest {

	@Before
	public void beforeTest() {
		Locales.setThreadLocal(Locale.TAIWAN);
		SheetImpl.DEBUG = true;
	}
	
	protected NSheet initialDataGrid(NSheet sheet){
		return sheet;
	}

	
	@Test
	public void testDataValidation(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		sheet1.getCell(0, 0).setValue(1D);
		sheet1.getCell(0, 1).setValue(2D);
		sheet1.getCell(0, 2).setValue(3D);
		
		NDataValidation dv1 = sheet1.addDataValidation(new CellRegion(1,1));
		NDataValidation dv2 = sheet1.addDataValidation(new CellRegion(1,2));
		NDataValidation dv3 = sheet1.addDataValidation(new CellRegion(1,3));
		//LIST
		dv1.setValidationType(ValidationType.LIST);
		dv1.setFormula("A1:C1");
		
		Assert.assertEquals(3, dv1.getNumOfValue1());
		Assert.assertEquals(0, dv1.getNumOfValue2());
		Assert.assertEquals(1D, dv1.getValue1(0));
		Assert.assertEquals(2D, dv1.getValue1(1));
		Assert.assertEquals(3D, dv1.getValue1(2));
		
		
		dv2.setValidationType(ValidationType.INTEGER);
		dv2.setFormula("A1","C1");
		Assert.assertEquals(1, dv2.getNumOfValue1());
		Assert.assertEquals(1, dv2.getNumOfValue2());
		Assert.assertEquals(1D, dv2.getValue1(0));
		Assert.assertEquals(3D, dv2.getValue2(0));
		
		dv3.setValidationType(ValidationType.INTEGER);
		dv3.setFormula("AVERAGE(A1:C1)","SUM(A1:C1)");
		Assert.assertEquals(1, dv3.getNumOfValue1());
		Assert.assertEquals(1, dv3.getNumOfValue2());
		Assert.assertEquals(2D, dv3.getValue1(0));
		Assert.assertEquals(6D, dv3.getValue2(0));
		
		
		NRanges.range(sheet1,0,0).setEditText("2");
		NRanges.range(sheet1,0,1).setEditText("4");
		NRanges.range(sheet1,0,2).setEditText("6");
		
		Assert.assertEquals(3, dv1.getNumOfValue1());
		Assert.assertEquals(0, dv1.getNumOfValue2());
		Assert.assertEquals(2D, dv1.getValue1(0));
		Assert.assertEquals(4D, dv1.getValue1(1));
		Assert.assertEquals(6D, dv1.getValue1(2));
		
		Assert.assertEquals(1, dv2.getNumOfValue1());
		Assert.assertEquals(1, dv2.getNumOfValue2());
		Assert.assertEquals(2D, dv2.getValue1(0));
		Assert.assertEquals(6D, dv2.getValue2(0));
		
		Assert.assertEquals(1, dv3.getNumOfValue1());
		Assert.assertEquals(1, dv3.getNumOfValue2());
		Assert.assertEquals(4D, dv3.getValue1(0));
		Assert.assertEquals(12D, dv3.getValue2(0));
		
		DependencyTable table = ((AbstractBookSeriesAdv)book.getBookSeries()).getDependencyTable();
		
		Set<Ref> refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 0)));
		Assert.assertEquals(3, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 1)));
		Assert.assertEquals(2, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 2)));
		Assert.assertEquals(3, refs.size());
		
		sheet1.deleteDataValidation(dv1);
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 0)));
		Assert.assertEquals(2, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 1)));
		Assert.assertEquals(1, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 2)));
		Assert.assertEquals(2, refs.size());
		
		sheet1.deleteDataValidation(dv2);
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 0)));
		Assert.assertEquals(1, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 1)));
		Assert.assertEquals(1, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 2)));
		Assert.assertEquals(1, refs.size());
		
		sheet1.deleteDataValidation(dv3);
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 0)));
		Assert.assertEquals(0, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 1)));
		Assert.assertEquals(0, refs.size());
		refs = table.getDependents(new RefImpl((AbstractCellAdv)sheet1.getCell(0, 2)));
		Assert.assertEquals(0, refs.size());
	}
	
	@Test
	public void testDataValidationHelperInteger(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		sheet1.getCell(0, 0).setValue(1D);//min
		sheet1.getCell(0, 1).setValue(3D);//max
		NRanges.range(sheet1,0,2).setEditText("2013/1/1");//day start
		NRanges.range(sheet1,0,3).setEditText("2013/2/1");//day end
		NRanges.range(sheet1,0,4).setEditText("12:00");//time start
		NRanges.range(sheet1,0,5).setEditText("14:00");//time end
		
		Assert.assertEquals(CellType.NUMBER, sheet1.getCell(0, 2).getType());
		Assert.assertEquals("2013/1/1", NRanges.range(sheet1,0,2).getCellFormatText());
		Assert.assertEquals(CellType.NUMBER, sheet1.getCell(0, 3).getType());
		Assert.assertEquals("2013/2/1", NRanges.range(sheet1,0,3).getCellFormatText());
		Assert.assertEquals(CellType.NUMBER, sheet1.getCell(0, 4).getType());
		Assert.assertEquals("12:00", NRanges.range(sheet1,0,4).getCellFormatText());
		Assert.assertEquals(CellType.NUMBER, sheet1.getCell(0, 5).getType());
		Assert.assertEquals("14:00", NRanges.range(sheet1,0,5).getCellFormatText());
		
		
		NDataValidation dv1 = sheet1.addDataValidation(new CellRegion(1,1));
		//test any
		Assert.assertTrue(new DataValidationHelper(dv1).validate("123", "General"));
		
		
		//test integer
		dv1.setValidationType(ValidationType.INTEGER);
		dv1.setFormula("1", "3");
		dv1.setOperatorType(OperatorType.BETWEEN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1.3", "General"));//not integer
		
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("3", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("4", "General"));
		
		dv1.setOperatorType(OperatorType.NOT_BETWEEN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("3", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("4", "General"));
		
		dv1.setOperatorType(OperatorType.EQUAL);
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("3", "General"));
		
		dv1.setOperatorType(OperatorType.NOT_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("3", "General"));
		
		dv1.setOperatorType(OperatorType.GREATER_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		
		dv1.setOperatorType(OperatorType.GREATER_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		
		dv1.setOperatorType(OperatorType.LESS_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("0", "General"));
		
		dv1.setOperatorType(OperatorType.LESS_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("0", "General"));
	}
	
	@Test
	public void testDataValidationHelperDecimal(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		sheet1.getCell(0, 0).setValue(1D);//min
		sheet1.getCell(0, 1).setValue(2D);//max SUM(A1:A2)
		
		
		NDataValidation dv1 = sheet1.addDataValidation(new CellRegion(1,1));
		
		
		//test integer
		dv1.setValidationType(ValidationType.DECIMAL);
		dv1.setFormula("A1", "SUM(A1:B1)");//1-3
		dv1.setOperatorType(OperatorType.BETWEEN);
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1.3", "General"));//not integer
		
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("3", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("4", "General"));
		
		dv1.setOperatorType(OperatorType.NOT_BETWEEN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("3", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("4", "General"));
		
		dv1.setOperatorType(OperatorType.EQUAL);
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("3", "General"));
		
		dv1.setOperatorType(OperatorType.NOT_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("3", "General"));
		
		dv1.setOperatorType(OperatorType.GREATER_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		
		dv1.setOperatorType(OperatorType.GREATER_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("0", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2", "General"));
		
		dv1.setOperatorType(OperatorType.LESS_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("0", "General"));
		
		dv1.setOperatorType(OperatorType.LESS_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("1", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("0", "General"));
	}
	
	@Test
	public void testDataValidationHelperTextlength(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		sheet1.getCell(0, 0).setValue(1D);//min
		sheet1.getCell(0, 1).setValue(2D);//max SUM(A1:A2)
		
		
		NDataValidation dv1 = sheet1.addDataValidation(new CellRegion(1,1));
		
		
		//test integer
		dv1.setValidationType(ValidationType.TEXT_LENGTH);
		dv1.setFormula("A1", "SUM(A1:B1)");//1-3
		dv1.setOperatorType(OperatorType.BETWEEN);
		
		Assert.assertTrue(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AA", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AAA", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AAAA", "General"));
		
		dv1.setOperatorType(OperatorType.NOT_BETWEEN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AA", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AAA", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AAAA", "General"));
		
		dv1.setOperatorType(OperatorType.EQUAL);
		Assert.assertTrue(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AA", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AAA", "General"));
		
		dv1.setOperatorType(OperatorType.NOT_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AA", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AAA", "General"));
		
		dv1.setOperatorType(OperatorType.GREATER_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AA", "General"));
		
		dv1.setOperatorType(OperatorType.GREATER_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("AA", "General"));
		
		dv1.setOperatorType(OperatorType.LESS_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AA", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("", "General"));
		
		dv1.setOperatorType(OperatorType.LESS_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("AA", "General"));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("A", "General"));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("", "General"));
	}
	
	@Test
	public void testDataValidationHelperDate(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		NRanges.range(sheet1,0,0).setEditText("2013/1/10");//day start
		NRanges.range(sheet1,0,1).setEditText("2013/2/1");//day end
		NRanges.range(sheet1,0,2).setEditText("12:00");//time start
		NRanges.range(sheet1,0,3).setEditText("14:00");//time end
		
		
		NDataValidation dv1 = sheet1.addDataValidation(new CellRegion(1,1));
		
		
		//test integer
		dv1.setValidationType(ValidationType.DATE);
		dv1.setFormula("A1", "B1");//2013/1/10 - 2013/2/1
		dv1.setOperatorType(OperatorType.BETWEEN);
		String format = "yyyy/m/d";
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/15", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/2/1", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/9", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/2/2", format));
		
		dv1.setOperatorType(OperatorType.NOT_BETWEEN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/15", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/2/1", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/9", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/2/2", format));
		
		dv1.setOperatorType(OperatorType.EQUAL);
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/15", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/2/1", format));
		
		dv1.setOperatorType(OperatorType.NOT_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/15", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/2/1", format));
		
		dv1.setOperatorType(OperatorType.GREATER_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/9", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/15", format));
		
		dv1.setOperatorType(OperatorType.GREATER_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/9", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/15", format));
		
		dv1.setOperatorType(OperatorType.LESS_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/15", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/9", format));
		
		dv1.setOperatorType(OperatorType.LESS_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/15", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("2013/1/10", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("2013/1/9", format));
	}
	
	@Test
	public void testDataValidationHelperTime(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		NRanges.range(sheet1,0,0).setEditText("12:00");//time start
		NRanges.range(sheet1,0,1).setEditText("14:00");//time end
		
		
		NDataValidation dv1 = sheet1.addDataValidation(new CellRegion(1,1));
		
		
		//test integer
		dv1.setValidationType(ValidationType.TIME);
		dv1.setFormula("A1", "B1");//12:00 - 14:00
		dv1.setOperatorType(OperatorType.BETWEEN);
		String format = "h:mm";
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:01", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("14:00", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("11:59", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("14:01", format));
		
		dv1.setOperatorType(OperatorType.NOT_BETWEEN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:01", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("14:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("11:59", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("14:01", format));
		
		dv1.setOperatorType(OperatorType.EQUAL);
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:01", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("14:00", format));
		
		dv1.setOperatorType(OperatorType.NOT_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:01", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("14:00", format));
		
		dv1.setOperatorType(OperatorType.GREATER_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("11:59", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:01", format));
		
		dv1.setOperatorType(OperatorType.GREATER_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("11:59", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:01", format));
		
		dv1.setOperatorType(OperatorType.LESS_OR_EQUAL);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:01", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("11:59", format));
		
		dv1.setOperatorType(OperatorType.LESS_THAN);
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:01", format));
		Assert.assertFalse(new DataValidationHelper(dv1).validate("12:00", format));
		Assert.assertTrue(new DataValidationHelper(dv1).validate("11:59", format));
	}
	
	
	@Test
	public void testDataValidationHelperList(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		NRanges.range(sheet1,0,0).setEditText("A");
		NRanges.range(sheet1,0,1).setEditText("B");
		NRanges.range(sheet1,0,2).setEditText("C");
		NRanges.range(sheet1,1,0).setEditText("1");
		NRanges.range(sheet1,1,1).setEditText("2");
		NRanges.range(sheet1,1,2).setEditText("3");
		NRanges.range(sheet1,2,0).setEditText("2013/1/1");
		NRanges.range(sheet1,2,1).setEditText("2013/1/2");
		NRanges.range(sheet1,2,2).setEditText("2013/1/3");
		
		NDataValidation dv0 = sheet1.addDataValidation(new CellRegion(0,3));
		
		String dateFormat = "yyyy/m/d";
		String numberFormat = "General";
		//test integer
		dv0.setValidationType(ValidationType.LIST);
		dv0.setFormula("{1,2,3}");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("1", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("2", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("3", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("0", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("4", numberFormat));
		
		dv0.setFormula("{\"A\",\"B\",\"C\"}");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("A", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("B", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("C", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("D", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("E", numberFormat));
		
		dv0.setFormula("A1:C1");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("A", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("B", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("C", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("D", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("E", numberFormat));
		
		dv0.setFormula("A2:C2");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("1", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("2", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("3", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("0", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("4", numberFormat));
		
		dv0.setFormula("A3:C3");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("2013/1/1", dateFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("2013/1/2", dateFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("2013/1/3", dateFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("2013/1/4", dateFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("2013/1/5", dateFormat));
	}
	@Test
	public void testDataValidationHelperFormula(){
		NBook book = NBooks.createBook("book1");
		NSheet sheet1 = initialDataGrid(book.createSheet("Sheet1"));
		NRanges.range(sheet1,0,0).setEditText("A");
		NRanges.range(sheet1,0,1).setEditText("B");
		NRanges.range(sheet1,0,2).setEditText("C");
		NRanges.range(sheet1,0,3).setEditText("D");
		NRanges.range(sheet1,1,0).setEditText("1");
		NRanges.range(sheet1,1,1).setEditText("2");
		NRanges.range(sheet1,1,2).setEditText("3");
		NRanges.range(sheet1,1,3).setEditText("4");
		NRanges.range(sheet1,2,0).setEditText("2013/1/1");
		NRanges.range(sheet1,2,1).setEditText("2013/1/2");
		NRanges.range(sheet1,2,2).setEditText("2013/1/3");
		NRanges.range(sheet1,2,3).setEditText("2013/1/4");
		
		NDataValidation dv0 = sheet1.addDataValidation(new CellRegion(0,4));
		
		String dateFormat = "yyyy/m/d";
		String numberFormat = "General";
		//test integer
		dv0.setValidationType(ValidationType.LIST);
		dv0.setFormula("{1,2,3}");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=A2", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=B2", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=C2", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=D2", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=E2", numberFormat));
		
		dv0.setFormula("{\"A\",\"B\",\"C\"}");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=A1", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=B1", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=C1", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=D1", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=E1", numberFormat));
		
		dv0.setFormula("A1:C1");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=A1", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=B1", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=C1", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=D1", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=E1", numberFormat));
		
		dv0.setFormula("A2:C2");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=A2", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=B2", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=C2", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=D2", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=E2", numberFormat));
		
		dv0.setFormula("A3:C3");
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=A3", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=B3", numberFormat));
		Assert.assertTrue(new DataValidationHelper(dv0).validate("=C3", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=D3", numberFormat));
		Assert.assertFalse(new DataValidationHelper(dv0).validate("=E3", numberFormat));
	}
}