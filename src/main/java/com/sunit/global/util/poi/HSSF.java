package com.sunit.global.util.poi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
public class HSSF {

	static Logger logger = Logger.getLogger(HSSF.class);

	private static String getContent(Cell cell){
		if(cell==null)   
			return "";
		
		if(cell.getCellType()== Cell.CELL_TYPE_NUMERIC){ 
			if(cell.getNumericCellValue() ==  (long)cell.getNumericCellValue()) {
				return String.valueOf((long)cell.getNumericCellValue());
			}
			return String.valueOf(cell.getNumericCellValue());
		}
		 
		if(cell.getCellType()== Cell.CELL_TYPE_STRING){
			 return cell.getStringCellValue();	
		}
		
		return "";
	}
	
	
	public static void readxls(File xlsFile,ReadActionInterface action) throws Exception{
		
		Workbook workbook = WorkbookFactory.create(xlsFile);  
		int sheetSize = workbook.getNumberOfSheets();  
		List rowContent=new LinkedList(); 
		for(int k=0;k<sheetSize;k++){ 
			
			Sheet sheet=workbook.getSheetAt(k);
			for (int i = sheet.getFirstRowNum(); i  <= sheet.getLastRowNum(); i++) {
				Row row =sheet.getRow(i);
				rowContent.clear();
				rowContent.add(sheet.getSheetName());
				if( sheet.getLastRowNum()!=0) 
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
		workbook.close();
		
}
	
	
	
	public static void readxls(InputStream in,ReadActionInterface action,int fromToRow) throws Exception{
		
		Workbook workbook = WorkbookFactory.create(in);   
		int sheetSize = workbook.getNumberOfSheets();  
		List rowContent=new LinkedList(); 
		for(int k=0;k<sheetSize;k++){ 
			
			Sheet sheet=workbook.getSheetAt(k);
			 
			for (int i = fromToRow-1; i <= sheet.getLastRowNum(); i++) {
				Row row =sheet.getRow(i);
				rowContent.clear();
				logger.debug("sheet.getSheetName()="
						+ sheet.getSheetName()); 
				logger.debug("row.getFirstCellNum()="
						+ row.getFirstCellNum()); 
				logger.debug("row.getLastCellNum()="  
						+ row.getLastCellNum()); 
				rowContent.add(sheet.getSheetName());
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
		workbook.close();
		
}
		
	
	
public static <T> void  wirtexls(String xlsFile,WriteActionInterface<T> action,List<T> list) throws Exception{
		
	   // 创建Excel的工作书册 Workbook,对应到一个excel文档
	                 HSSFWorkbook wb = new HSSFWorkbook();
	 
	                 // 创建Excel的工作sheet,对应到一个excel文档的tab
	                 HSSFSheet sheet = wb.createSheet("sheet1");
	 

	                 // 设置excel每列宽度
//	                 sheet.setColumnWidth(0, 4000);
//	                 sheet.setColumnWidth(1, 3500);
//
//	                 // 创建字体样式
//	                 HSSFFont font = wb.createFont();
//	                 font.setFontName("Verdana");
//	                 font.setBoldweight((short) 100);
//	                 font.setFontHeight((short) 300);
//	                 font.setColor(HSSFColor.BLUE.index);
//
//	                 // 创建单元格样式
//	                 HSSFCellStyle style = wb.createCellStyle();
//	                 style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//	                 style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
//	                 style.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
//	                 style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//
//	                 // 设置边框
//	                 style.setBottomBorderColor(HSSFColor.RED.index);
//	                 style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//	                 style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//	                 style.setBorderRight(HSSFCellStyle.BORDER_THIN);
//	                 style.setBorderTop(HSSFCellStyle.BORDER_THIN);
//
//	                 style.setFont(font);// 设置字体        
	
	                 for (int i = 0; i < list.size(); i++) {
//						array_type array_element = <T>[i];
	                	 HSSFRow row = sheet.createRow(i);
	                	 action.doAction(row, list.get(i)); 
					}
	                 wb.close();
	                 if(sheet.getLastRowNum()>0){ 
	                	  FileOutputStream os = new FileOutputStream(xlsFile);
	                      wb.write(os);
	                      os.close();
	                 }
}
	 


public static  <T>  HSSFWorkbook   wirtexls( WriteActionInterface<T> action, List<T> list ) throws Exception{
	
	                 HSSFWorkbook wb = new HSSFWorkbook();
	                 HSSFSheet sheet = wb.createSheet("sheet1"); 
	                 for (int i = 0; i < list.size(); i++) {
	                	 HSSFRow row = sheet.createRow(i);
	                	 action.doAction(row, list.get(i)); 
					}
	                 wb.close();
	                 if(sheet.getLastRowNum()>0){ 
	                 }
	                 return wb;
} 


	
public static void main(String[] args)  {
//	List list =new ArrayList<String>();
//	Map m =new HashMap<String ,String>();
//	m.put("a", "1");
//	
//	Map m2 =new HashMap<String ,String>();
//	m2.put("b", "1");
//	
//	Map m3 =new HashMap<String ,String>();
//	m3.put("c", "1");
//	
//	list.add(m);
//	list.add(m2); 
//	list.add(m3);
//	
//	List titleList  =   new ArrayList<String>(); 
//	titleList.add("id");
//	titleList.add("title"); 
//	titleList.add("content");
//	
//	
//	try {
//		HSSF.wirtexls("c:/123.xls", new WriteActionInterface<Map<String,String>>() {
//			public boolean doAction(HSSFRow row, Map<String, String> rowObject) {
//				int i=0;
//				for( Map.Entry<String, String>  entry :  rowObject.entrySet()) {
//					HSSFCell cell = cell = row.createCell(i++) ;
//					cell.setCellValue( entry.getValue());
//				}
//				return true;
//			}
//		}, list); 
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
	
	
	
	String filestr= "D:\\My Documents\\WeChat Files\\coco22m\\Files\\基础数据配置(1)\\基础数据配置";
	
	List  l  =new ArrayList() ;
	l.add(filestr+"\\供应商等级.xlsx");
//	l.add(filestr+"\\供应商性质.xlsx");
//	l.add(filestr+"\\国家.xlsx");
//	l.add(filestr+"\\基础数据来源.xlsx");
//	l.add(filestr+"\\省份.xlsx");
//	l.add(filestr+"\\税率.xlsx");
//	l.add(filestr+"\\运输方式.xlsx"); 
	
	
	for (int i = 0; i < l.size(); i++) {
		 File xlsFile = new File (l.get(i).toString());
		 try {
			HSSF.readxls(xlsFile, new ReadActionInterface() {
				public boolean doAction(List<String> rowContent, int index) {
					for (int j = 0; j < rowContent.size(); j++) {
						System.out.print(rowContent.get(j));
						System.out.println(index);
						
					}
					return true;
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}
	
	
	
	
	
	
	
	
	
	 
	
}
	
}
