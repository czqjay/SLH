package com.sunit.global.util.poi;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

 
/**
 * 
 * 处理 xls
 * 类名称：XSSF
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Nov 14, 2015 3:55:53 PM
 * 修改人：joye
 * 修改时间：Nov 14, 2015 3:55:53 PM
 * 修改备注：
 * @version 
 *
 */ 
public class XSSF { 

	static Logger logger = Logger.getLogger(XSSF.class);

	private static String getContent(Cell cell){
		if(cell==null)   
			return "";
		
		
		if(cell.getCellType()== Cell.CELL_TYPE_NUMERIC){ 
			return String.valueOf(cell.getNumericCellValue());
		}
		 
		if(cell.getCellType()== Cell.CELL_TYPE_STRING){
			 return cell.getStringCellValue();	
		}
		
		return "";
	}
	
	
	public static void readxls(File xlsFile,ReadActionInterface action) throws Exception{
		
			OPCPackage pkg = OPCPackage.open(xlsFile.toString());  
			XSSFWorkbook workbook = new XSSFWorkbook(pkg);  
			int sheetSize = workbook.getNumberOfSheets(); 
			List rowContent=new LinkedList(); 
			for(int k=0;k<sheetSize;k++){  
				Sheet sheet=workbook.getSheetAt(k);
				for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
					Row row =sheet.getRow(i);
					rowContent.clear();
					logger.debug("sheet.getSheetName()="
							+ sheet.getSheetName());
					logger.debug("row.getFirstCellNum()=" 
							+ row.getFirstCellNum()); 
					logger.debug("row.getLastCellNum()="  
							+ row.getLastCellNum());
					for (int j = row.getFirstCellNum(); j <row.getLastCellNum(); j++) { 
							
							String content  =   getContent(row.getCell(j));  
							rowContent.add(content);
							logger.debug(i + ":" + j+":"+content);
					}
				if(!action.doAction(rowContent,i)){ 
					break; 
				}
			}
			}   
//			workbook.close();
			pkg.close();  
			
	} 
	
	public static void readxls(InputStream in,ReadActionInterface action) throws Exception{
		
		OPCPackage pkg = OPCPackage.open(in);  
		XSSFWorkbook workbook = new XSSFWorkbook(pkg);  
		int sheetSize = workbook.getNumberOfSheets(); 
		List rowContent=new LinkedList(); 
		for(int k=0;k<sheetSize;k++){  
			Sheet sheet=workbook.getSheetAt(k);
			for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
				Row row =sheet.getRow(i);
				rowContent.clear();
				logger.debug("sheet.getSheetName()="
						+ sheet.getSheetName());
				logger.debug("row.getFirstCellNum()="
						+ row.getFirstCellNum()); 
				logger.debug("row.getLastCellNum()="  
						+ row.getLastCellNum());
				for (int j = row.getFirstCellNum(); j <row.getLastCellNum(); j++) { 
						
						String content  =   getContent(row.getCell(j));  
						rowContent.add(content);
						logger.debug(i + ":" + j+":"+content);
				}
			if(!action.doAction(rowContent,i)){  
				break; 
			}
		}
		}   
//		workbook.close();
		pkg.close();  
		
}
	
	
	public static void main(String[] args) {
		
		List list = new ArrayList();
		list.add(1);
		list.add(null);
		list.add(null);
		list.add(10);
		System.out.println(list.toString());
		
	}
}
