package com.sunit.global.base.authorization.validate.constraints;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import com.sunit.global.base.authorization.validate.action.ValidataeActions;
import com.sunit.global.base.service.BaseService;
import com.sunit.global.util.SessionContext;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.SunitStringUtil;

public class Script { 
	
	static Logger logger = Logger.getLogger(Script.class); 
	

	private HttpServletResponse response;   
	private HttpServletRequest request;    
	private String scriptType = "java";   
	private AbstractScriptExecute scriptEx = null; // 执行器 , 每个check 任务都需实现自己的执行器 
	private String msg;   //  验证 不通过时, 返回给请求的提示
	private String fieldName; // 要验证的form字段  
	private String value;    // 要验证的form字段值
	 
	
	public static abstract class AbstractScriptExecute<T, K> {
		
		/**
		 * 验证方法
		* @Title: execute 
		* @Description: 
		* @param @param script
		* @param @return     
		* @return boolean  true : 通过  false: 不通过  
		* @throws 
		* @author joye 
		* Sep 21, 2016 11:14:18 AM
		 */
		public abstract boolean execute(Script script);
	} 

	public static  class isExtistsExcute extends AbstractScriptExecute {
 
		private  Class<? extends BaseService> managerClass;  
		private Class entityClass;
		private String msg;
		private boolean isReverse =false;  //是否反转结果的boolean值  
		
		
		public Class<? extends BaseService> getManagerClass() {
			return managerClass;
		}

		public void setManagerClass(Class<? extends BaseService> managerClass) {
			this.managerClass = managerClass;
		}

		public Class getEntityClass() {
			return entityClass;
		}

		public void setEntityClass(Class entityClass) {
			this.entityClass = entityClass;
		}


		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg; 
		}

		public isExtistsExcute (Class managerClass,Class entityClass  ){
			this.entityClass= entityClass; 
			this.managerClass = managerClass; 
			
		}
		public isExtistsExcute (Class managerClass,Class entityClass,String msg  ){
			this.entityClass= entityClass; 
			this.managerClass = managerClass; 
			this.msg= msg;
			
		}
		
		public isExtistsExcute (Class managerClass,Class entityClass,String msg ,boolean isReverse  ){
			this.entityClass= entityClass; 
			this.managerClass = managerClass; 
			this.msg= msg;
			this.isReverse = isReverse;
			
		}
		
		
		@SuppressWarnings("unchecked") 
		public boolean execute(Script script) {
			boolean b =false; 
			try {  
				BaseService  manager =  SpringContextUtils.getSpringContext().getBean(managerClass);
				SessionContext.getAttribute(script.getRequest(), ""); 
				Object entity= entityClass.newInstance(); 
				String filedName = script.getFieldName();
				String id = script.getRequest().getParameter("id");
				if(!SunitStringUtil.isBlankOrNull(id)){
					PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(this.entityClass, "id");
					pd.getWriteMethod().invoke(entity, id);
				}
				
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(this.entityClass, filedName);
				pd.getWriteMethod().invoke(entity, script.getValue());
				List list=Collections.EMPTY_LIST;
				list =manager.isExtists( entity, id!=null);
				if(list.isEmpty()){ 
					b =true;
				}else{
					b=false;
				}
				
				if(this.isReverse())
					b=!b;
				
				if(!b)
					script.setMsg(SunitStringUtil.isBlankOrNull(this.getMsg())?"用户已存在":this.getMsg());
				
				 
				
			} catch (Exception e) { 
				// TODO Auto-generated catch block
				script.setMsg("验证出现错误"); 
				logger.error("验证出现错误",e);
			}
			return b;
		
		}

		public boolean isReverse() {
			return isReverse;
		}

		public void setReverse(boolean isReverse) {
			this.isReverse = isReverse;
		}

		
	}
	
	
	public static  class ParamterWhiteListEx   extends  AbstractScriptExecute<Object, Object>{
		
		public ParamterWhiteListEx(String [] arr){
			whiteString = SunitStringUtil.getStringbyCommaSeparate(arr,"");
		}
		
		public boolean execute(Script script) {
			
			boolean b=true;
			  
			for(Object s :script.getRequest().getParameterMap().keySet()){
				if(whiteString.indexOf(s.toString())==-1){
					return false;
				} 
			}
			return b;
		}
		
		String whiteString="";
//		private List<String> whiteList = new ArrayList<String>();
//		public  List<String> getWhiteList() {
//			return whiteList;
//		}
//		private void setWhiteList(List<String> whiteList) {
//			this.whiteList = whiteList; 
//		}
		
	
		  
		  
	}
	
	
 
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public AbstractScriptExecute getScriptEx() {
		return scriptEx;
	}

	public void setScriptEx(AbstractScriptExecute Ex) {
		this.scriptEx = Ex;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

}
