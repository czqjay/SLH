package com.sunit.global.util;

import org.apache.commons.lang.StringUtils;


/**
 * 各种转义需求 转义类
 * 
 * 类名称：EscapeExpr
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Sep 18, 2017 11:23:52 AM
 * 修改人：joye
 * 修改时间：Sep 18, 2017 11:23:52 AM
 * 修改备注：
 * @version 
 *
 */
public class EscapeExpr {

	
	/**
	 * 正则表达式的敏感字的转义
	* @Title: escapeExprSpecialWord 
	* @Description: 
	* @param @param keyword
	* @param @return     
	* @return String  
	* @throws 
	* @author joye 
	* Sep 18, 2017 11:27:18 AM
	 */
	public static String escapeExprSpecialWord(String keyword) {  
	    if (StringUtils.isNotBlank(keyword)) {  
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };  
	        for (String key : fbsArr) {  
	            if (keyword.contains(key)) {  
	                keyword = keyword.replace(key, "\\" + key);  
	            }  
	        }  
	    }  
	    return keyword;  
	}  
}
