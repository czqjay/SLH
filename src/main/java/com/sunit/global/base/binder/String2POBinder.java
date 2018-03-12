package com.sunit.global.base.binder;

/**
 * 
 * 将ID字符串转成PO
 * 类名称：String2POBinder
 * 类描述：将ID字符串转成PO
 * 创建人：Administrator
 * 创建时间：Aug 13, 2013 10:25:01 AM
 * 修改人：joye
 * 修改时间：Aug 13, 2013 10:25:01 AM
 * 修改备注：
 * @version 
 *
 */
public abstract class String2POBinder extends java.beans.PropertyEditorSupport{
	
	
	/**
	 * 将ID字符串转成PO
	* @Title: binder 
	* @Description: 将ID字符串转成PO
	* @param      
	* @return void  
	* @throws 
	* @author joye 
	* Aug 13, 2013 10:25:40 AM
	 */ 
	public abstract void setAsText(String text) throws IllegalArgumentException;
	
} 
 