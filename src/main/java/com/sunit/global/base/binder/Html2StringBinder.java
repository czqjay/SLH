package com.sunit.global.base.binder;

import org.springframework.web.util.HtmlUtils;

/**
 * 
 * 
 * 类名称：Html2StringBinder
 * 类描述：  过滤http请求中的html字符
 * 创建人：Administrator
 * 创建时间：Aug 23, 2013 8:50:36 AM
 * 修改人：joye
 * 修改时间：Aug 23, 2013 8:50:36 AM
 * 修改备注：
 * @version 
 *
 */
public class Html2StringBinder extends java.beans.PropertyEditorSupport{
	
	
	public  void setAsText(String text) throws IllegalArgumentException{ 
		this.setValue(HtmlUtils.htmlEscape(HtmlUtils.htmlEscape(text.trim())));
		//this.setValue(HtmlUtils.htmlEscape(HtmlUtils.htmlEscape(text.trim())));
//		this.setValue(text.trim().replaceAll("<", "《").replaceAll(">", "》")
//				.replaceAll("&", ""));
		
	}
}

