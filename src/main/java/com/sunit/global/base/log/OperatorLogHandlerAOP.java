package com.sunit.global.base.log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * 
 * 类名称：OperatorLogHandlerAOP
 * 类描述： 日志拦截类(*Action.*)
 * 创建人：Administrator
 * 创建时间：Dec 3, 2013 5:30:53 PM
 * 修改人：joye
 * 修改时间：Dec 3, 2013 5:30:53 PM
 * 修改备注：
 * @version 
 *
 */
public class OperatorLogHandlerAOP  implements MethodInterceptor, Serializable  {

	private static final long serialVersionUID = -8155465547798629911L; 

	private static Map<String,String> megMap =new HashMap<String,String>();
	static{
		megMap.put("saveClient", "新增用户"); 
		megMap.put("saveClient_update", "修改用户");
		megMap.put("delClient", "删除用户"); 
		megMap.put("savePlan", "新增计划"); 
		megMap.put("savePlan_update", "修改计划"); 
		megMap.put("delPlan", "删除计划"); 
		megMap.put("saveScore", "评分/投票"); 
		
	}
	
	public Object invoke(MethodInvocation arg0) throws Throwable {
		Object o =arg0.proceed();
		
		
	if(megMap.containsKey(arg0.getMethod().getName())){}
		return o;  
	}
}
