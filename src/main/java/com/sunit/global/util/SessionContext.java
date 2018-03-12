package com.sunit.global.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sunit.sysmanager.po.User;

/**
 * 
 * 
 * @class name：SessionContext
 * @desc： 获取session信息
 * @user：shanjizhou 
 * @createTime：Jul 19, 2013 2:26:15 PM
 * @update user：shanjizhou
 * @updateTime：Jul 19, 2013 2:26:15 PM
 * @update desc：
 * @version
 * 
 */
public class SessionContext {
	
	private static Map sessionsMap=new HashMap();
	
	 private static ThreadLocal<HttpServletRequest> sessionContext = new ThreadLocal<HttpServletRequest>();
 
	/**
	 * 保存session的集合
	* @Title: putSession 
	* @Description: 
	* @param @param key    用户帐号 
	* @param @param session  用户session   
	* @return void  
	* @throws 
	* @author joye 
	* Oct 29, 2013 9:56:21 AM
	 */
	public static void putSession(String key,HttpSession session){
		sessionsMap.put(key, session);
	}
	
	public static void invalidSession(String key){
		if(sessionsMap.get(key)!=null){
			javax.servlet.http.HttpSession session =(HttpSession) sessionsMap.get(key); 
		} 
	}
	
	public static void showSessions(){ 
		Iterator keys =sessionsMap.keySet().iterator(); 
		while (keys.hasNext()) {  
			String key =keys.next().toString();
		}
	}

	public static String getUserId(HttpServletRequest request) {
		Object retValue = request.getSession().getAttribute("userId");
		if (null != retValue) {
			return retValue.toString();
		}
		return null;
	}

	public static Object getAttribute(HttpServletRequest request,
			String attributeName) {

		Object obj = request.getSession().getAttribute(attributeName);
		if (null != obj) {
			return obj;
		}
		return null;
	}

	public static User getUser(HttpServletRequest request) {
 
		User obj = (User) request.getSession().getAttribute("user");
		if (null != obj) { 
			return obj;
		}
		return null;  
	}
	
	public static void setHttpServletRequest(HttpServletRequest request){
		sessionContext.set(request);
	}
	
	public static HttpServletRequest getHttpServletRequest(){
		return sessionContext.get(); 
	}
	

}
