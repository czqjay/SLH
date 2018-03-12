package com.sunit.global.util;

import java.util.Collections;
import java.util.List;

public class Paging {
	
	//每一页的行数 
	private int rows; 
	//当前页
	private int page;
	//数据库数据总行数
	private long totalRow ; 
	//数据总页数
	private long totalPage;
	//展示数据
	private List List= Collections.emptyList(); 
	
	private int beginRow;
	
	public List getList() {
		return List;
	}
	public void setList(List list) {
		List = list;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getPage() {
		return page;   
	}
	public void setPage(int l) { 
		this.page = l;
	}
	public long getTotalRow() {
		return totalRow; 
	}
	public void setTotalRow(long totalRow) {
		this.totalRow = totalRow;
	}
	public long getTotalPage() { 
		return totalPage;
	}
	public void setTotalPage(long totalPage) { 
		this.totalPage = totalPage; 
	} 
	
	/**
	 * 
	* @Title: getCountForHQL 
	* @Description:  为 hql 输出相匹配的 count 语句， 通常情况下用于分页时获取记录总数 
	* @param @param hqlBuf
	* @param @return     
	* @return StringBuffer  
	* @throws  
	* @author joye 
	* Jun 2, 2013
	 */
	public static StringBuffer  getCountForHQL(StringBuffer hqlBuf){
		int i= hqlBuf.indexOf("from ");
		StringBuffer countBuf =new StringBuffer(hqlBuf); 
		return    countBuf.delete(0, i).insert(0, "select count(*) "); 
	}
	
	
	
	/**
	 * 为 sql 输出相匹配的 count 语句， 通常情况下用于分页时获取记录总数 
	* @Title: getCountForSQL 
	* @Description: 
	* @param @param sqlBuf
	* @param @return     
	* @return StringBuffer  
	* @throws 
	* @author joye 
	* Dec 28, 2015 6:17:35 PM
	 */
	public static StringBuffer  getCountForSQL(StringBuffer sqlBuf){
		StringBuffer countBuf =new StringBuffer(sqlBuf);  
		return    countBuf.insert(0, "select count(*) as sqlCount from ( ").append(" ) joyeCount");
		
		
		
	}
	
	
	/**
	 * 为 hql 输出相匹配的 count 语句(不计算重复行)， 通常情况下用于分页时获取记录总数 
	* @Title: getCountForHQLByDistinct 
	* @Description: 为 hql 输出相匹配的 count 语句(不计算重复行)， 通常情况下用于分页时获取记录总数 
	* @param @param hqlBuf  
	* @param @param distinctCol  需要distinct的列名 
	* @param @return     
	* @return StringBuffer  
	* @throws 
	* @author joye 
	* Sep 27, 2013 2:40:23 PM
	 */
	public static StringBuffer  getCountForHQLByDistinct(StringBuffer hqlBuf,String distinctCol){ 
		int i= hqlBuf.indexOf("from ");
		StringBuffer countBuf =new StringBuffer(hqlBuf); 
		return    countBuf.delete(0, i).insert(0, "select count(distinct "+distinctCol+") ");   
	}
	
	/**
	 * 计算分页参数
	* @Title: calcPageArgs 
	* @Description: 
	* @param @param totalRow
	* @param @param rows   当前页面显示的行数 限制最大100行
	* @param @param page
	* @param @return     
	* @return Paging  
	* @throws 
	* @author joye 
	* Nov 25, 2015 10:46:20 AM
	 */
	public static Paging calcPageArgs(long totalRow,int rows,int page){
		Paging paging = new Paging();
		paging.setTotalRow(totalRow); 
		paging.setPage(page);
		paging.setRows(rows>100?100:rows); 
		paging
			.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
					.getTotalRow()
					/ paging.getRows() 
					: paging.getTotalRow() / paging.getRows() + 1); 
		    paging.setPage( (int) (page>paging.getTotalPage()?paging.getTotalPage():page));  
		    int beginRow = ( paging.getPage() - 1) * rows;
		    paging.setBeginRow(beginRow>0?beginRow:0);
		return paging; 
	}
	 
	 
	
	public int getBeginRow() {
		return beginRow;
	}
	public void setBeginRow(int beginRow) {
		this.beginRow = beginRow;
	}
	
}
