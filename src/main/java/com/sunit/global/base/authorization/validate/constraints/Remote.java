package com.sunit.global.base.authorization.validate.constraints;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * 远程校验
 * 
 * 类名称：Remote
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Mar 15, 2016 2:35:11 PM
 * 修改人：joye
 * 修改时间：Mar 15, 2016 2:35:11 PM
 * 修改备注：
 * @version 
 *
 */
public class Remote {
  
	String  action;
	String  webPath;
	String  message; 
	String  fieldName;
	String  isReverse = "false";   
	HttpServletRequest reqeust;
	HttpServletResponse response;
	
	Map<String, String> args = new HashMap<String, String>();

	public String getMessage() {
		return message;
	} 
 
	public void setMessage(String message) { 
		this.message = message;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getIsReverse() {
		return isReverse;
	}
 
	public void setIsReverse(String isReverse) {
		this.isReverse = isReverse;
	}

	public HttpServletRequest getReqeust() {
		return reqeust;
	}

	public void setReqeust(HttpServletRequest reqeust) {
		this.reqeust = reqeust;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}


}
