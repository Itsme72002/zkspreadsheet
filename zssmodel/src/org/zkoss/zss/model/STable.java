/* STable.java

	Purpose:
		
	Description:
		
	History:
		Dec 9, 2014 2:24:34 PM, Created by henrichen

	Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zss.model;

import java.util.List;

import org.zkoss.poi.ss.formula.ptg.TablePtg.Item;

/**
 * Table
 * @author henri
 * @since 3.8.0
 */
public interface STable {
	SBook getBook(); //ZSS-967
	SAutoFilter getFilter(); // turn off header Row; then this is null; ref and region is the same; then no totals row
	void enableFilter(boolean enable);
	
	void addColumn(STableColumn column);
	List<STableColumn> getColumns();
	STableColumn getColumnAt(int colIdx); //ZSS-967
	STableStyleInfo getTableStyleInfo();
	
//	SNamedStyle getDataCellStyle(); // dataCellStyle
//	SNamedStyle getTotalsRowCellStyle(); // totalsRowCellStyle
//	SNamedStyle getHeaderRowCellStyle(); // headerRowCellStyle
//	SCellStyle getTotalsRowStyle(); //totalsRowDxfId
//	SCellStyle getDataStyle(); //dataDxfId
//	SCellStyle getHeaderRowStyle(); //headerRowDxfId
	int getTotalsRowCount(); //totalsRowCount; 0 if not show; default is 0
	void setTotalsRowCount(int count);
	int getHeaderRowCount(); //headerRowCount; 0 if not show; default is 1
	void setHeaderRowCount(int count);
	String getName(); //name (Name used in formula)
	void setName(String name);
	String getDisplayName();
	void setDisplayName(String name);
	
	SheetRegion getAllRegion(); // #All
	SheetRegion getDataRegion(); // #Data (=== Table's Name region)
	SheetRegion getColumnsRegion(String columnName1, String columnName2); // [#All],[column1]:[column2]
	SheetRegion getHeadersRegion(); // [#Headers]
	SheetRegion getTotalsRegion(); // [#Totals]
	SheetRegion getThisRowRegion(int rowIdx); //[#This Row]
	SheetRegion getItemRegion(Item item, int rowIdx);
}
