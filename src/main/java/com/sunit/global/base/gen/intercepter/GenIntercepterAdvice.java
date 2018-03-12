package com.sunit.global.base.gen.intercepter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * 
 * 拦截器类 拦截内容由spring-core.xml 配置
 * 类名称：GenIntercepterAdvice
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Jun 1, 2015 3:14:41 PM
 * 修改人：joye
 * 修改时间：Jun 1, 2015 3:14:41 PM
 * 修改备注：
 * @version 
 *
 */
public class GenIntercepterAdvice implements MethodInterceptor { 

	static Logger logger = Logger.getLogger(GenIntercepterAdvice.class); 
	

	private String interceptorClassPackage="com.sunit.global.base.gen.intercepter.impl";

	
	public Object invoke(MethodInvocation invoke) throws Throwable {
		
		logger.debug("GenIntercepterAdvice.invoke()"); 
		String invokeMethodName=invoke.getMethod().getName();
		String invokeClassName= invoke.getThis().getClass().getSimpleName();
		/**
		 * 要调用的拦截器类名  以 managerImpl的 命名规则 + 拦截的方法名
		 */
		String interceptorClassName = interceptorClassPackage+"."+invokeClassName+invokeMethodName.substring(0, 1).toUpperCase()+invokeMethodName.substring(1);
		logger.debug("classForName : " + interceptorClassName); 
		try {
			Class clazz = Class.forName(interceptorClassName);
			if(clazz!=null){
				GenIntercepterInterface gii = (GenIntercepterInterface) clazz.newInstance(); 
				gii.process(invoke);
			}
		} catch (java.lang.ClassNotFoundException e) { 
			logger.debug("找不到类:"+interceptorClassName); 
		}
		
		
//		if(invoke.getMethod().getName().equals("save")){ 
//			Object  o = invoke.getArguments()[0]; 
//			
//			if(o instanceof com.sunit.slhdemo.songlista.po.Songlista){
//				GenIntercepterInterface gii =	new SonglistaSave();
//				gii.process(invoke);
//			} 
//		}
		Object retVal = null;
		retVal = invoke.proceed(); 
		return retVal; 
	}
	
	public static void main(String[] args) {
		
		String s ="saveOrUpdate";
		System.out.println(s.substring(0, 1).toUpperCase()+s.substring(1));
		System.out.println(s.substring(1));
		
		System.out.println("s.charAt(0)=" + s.charAt(0) );
	}

	
} 
