package org.zkoss.zss.test.selenium.testcases.function;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.zkoss.zss.test.selenium.Setup;
import org.zkoss.zss.test.selenium.ZSSTestCase;
import org.zkoss.zss.test.selenium.entity.EditorWidget;
import org.zkoss.zss.test.selenium.entity.JQuery;
import org.zkoss.zss.test.selenium.entity.SheetCtrlWidget;
import org.zkoss.zss.test.selenium.entity.SpreadsheetWidget;


public class Issue900Test extends ZSSTestCase {
	
	@Ignore("vision, email")
	@Test
	public void testZSS900() throws Exception {}
	
	@Test
	public void testZSS903() throws Exception {
		getTo("issue3/903-paste-excel.zul");
		SpreadsheetWidget ss = focusSheet();
		SheetCtrlWidget ctrl = ss.getSheetCtrl();
		EditorWidget editor = ctrl.getInlineEditor();
		
		click(ctrl.getCell("A1"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		editor.toWebElement().sendKeys("A1");
		waitUntilProcessEnd(Setup.getTimeoutL0());
		click(ctrl.getCell("B1"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		editor.toWebElement().sendKeys("B1");
		waitUntilProcessEnd(Setup.getTimeoutL0());
		click(ctrl.getCell("C1"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		editor.toWebElement().sendKeys("C1");
		waitUntilProcessEnd(Setup.getTimeoutL0());
		click(ctrl.getCell("A2"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		editor.toWebElement().sendKeys("A2");
		waitUntilProcessEnd(Setup.getTimeoutL0());
		click(ctrl.getCell("B2"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		editor.toWebElement().sendKeys("B2");
		waitUntilProcessEnd(Setup.getTimeoutL0());
		click(ctrl.getCell("C2"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		editor.toWebElement().sendKeys("C2");
		waitUntilProcessEnd(Setup.getTimeoutL0());
		
		click(ctrl.getCell("A1"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		dragAndDrop(ctrl.getCell("A1").$n(), ctrl.getCell("C2").$n());
		waitUntilProcessEnd(Setup.getTimeoutL0());
		
		jq("body").toWebElement().sendKeys(Keys.chord(Keys.CONTROL, "c"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		
		jq("$textbox").toWebElement().sendKeys(Keys.chord(Keys.CONTROL, "v"));
		waitUntilProcessEnd(Setup.getTimeoutL0());
		
		assertEquals(jq("$textbox").val(), "A1\tB1\tC1\nA2\tB2\tC2\n");
	}
	
	@Ignore("need test in zssapp")
	@Test
	public void testZSS970() throws Exception {
		getTo("/#test.xlsx");
		SpreadsheetWidget ss = focusSheet();
		SheetCtrlWidget ctrl = ss.getSheetCtrl();
		EditorWidget editor = ctrl.getInlineEditor();
		int col = 0;
		int row = 0;
		String text = "c"; 
		for (int i = 0; i < 50; i++) {
			for (col = 0 ; col < 30 ; col++){
				for (row = 0 ; row <30 ; row++){
					click(ctrl.getCell(row, col));
					Thread.sleep(10);
					editor.toWebElement().sendKeys((i%10) + "");
					Thread.sleep(10);
				}
			}
			text = String.valueOf((char)(text.charAt(0) + 1));
		}
	}
}





