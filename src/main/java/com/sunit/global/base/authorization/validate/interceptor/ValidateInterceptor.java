package com.sunit.global.base.authorization.validate.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sunit.global.base.authorization.validate.AbstractValidatorProcess;
import com.sunit.global.base.authorization.validate.action.ValidataeActions;
import com.sunit.global.base.authorization.validate.action.ValidateObject;
import com.sunit.global.base.authorization.validate.constraints.Script;
import com.sunit.global.base.authorization.validate.impl.NotBlankValidator;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.SunitStringUtil.ContainsString2Oject;

public class ValidateInterceptor extends HandlerInterceptorAdapter {
	
	static Logger logger = Logger.getLogger(HandlerInterceptorAdapter.class);
	
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
	
		logger.debug("ValidateInterceptor.preHandle()");
//		ValidataeActions.reconfig();  
		
		Map resultMap = new HashMap();
		resultMap.put("success", false);
		resultMap.put("msg", ""); 

		Enumeration en = request.getAttributeNames(); 
		
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			logger.debug(key + " = " + request.getAttribute(key));

		}
		logger.info("parameter:");
		en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			logger.info(key + " = " + request.getParameter(key));

		}
		logger.info("");

		String path = (String) request
				.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		if (!SunitStringUtil.isBlankOrNull(path)) {
			boolean b = true;
			List<ValidateObject> list = ValidataeActions
					.getActionValidateObj(path);
			if (list != null)
				for (int i = 0; i < list.size(); i++) {
					logger.info(list.get(i));
					ValidateObject validate = list.get(i);  
//					if(validate==null) continue;
					String[]  filedParamenter = request.getParameterValues(validate.getFieldName());
					Object methodArgs = validate.getMethodNameArgs();
					logger.info("validate.getFieldName()="
							+ validate.getFieldName());
					logger.info("validate.getMethodName()=" 
							+ validate.getMethodName());
					
					//通过  ValidateObject配置的验证方式 来获取处理类的class 
//					 eg: ValidataeActions.addValidate("/user/userRegister.action","accountName","requiredValidator");
//					 例子中的 requiredValidator  == validate.getMethodName()
					Class clazz = ValidataeActions.getValidateMethod(validate
							.getMethodName());
					
					//通过class 获取已注册的 处理类
					AbstractValidatorProcess process = ValidataeActions.getValidateProcess(clazz);
					Map  args =new HashMap();
					
					 
				 	
					
					if(validate.getMethodNameArgs() instanceof Script.AbstractScriptExecute)
						 args.put("scriptEx", validate.getMethodNameArgs());  
					else if(!SunitStringUtil.isBlankOrNull( validate.getMethodNameArgs()))
						args  =JSONObject.fromObject(validate.getMethodNameArgs());
					 
					 
					String basePath = request.getScheme() + "://" 
					+ request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
					
					
					
					args.put("xwt_web_app_name", basePath); // 供远程效验处理类使用
					//优先判断必填验证  
					boolean isRequired =SunitStringUtil.listContainsString2Oject(validate.getFieldName(), list, new ContainsString2Oject<ValidateObject>(){
						public boolean Contains(String source,
								ValidateObject target) { 
							return source.equals(target.getFieldName())&&target.getMethodName().equals("requiredValidator");
						}
					});
					  
					
					//字段的 必填与空值处理 , 优先处理字段的必填验证,必须首先满足必填验证, 如果没有必填验证且字段值为空则此次不验证.但 scriptEx 类的验证则必定会生效
					if(isRequired){
						AbstractValidatorProcess requiredProcess = ValidataeActions.getValidateProcess(NotBlankValidator.class);
						b =	requiredProcess.process(request,response,validate, filedParamenter, resultMap, args);
						if(!b){ 
							response.getWriter().print(
									JSONObject.fromObject(resultMap).toString()); 
							return false;
						}
					}else{ // 如果没有必填效验
						
						//且字段值为空 不再本次效验  脚本效验例外
						if(!validate.getMethodName().equals("javaValidator"))
						if(filedParamenter ==null ||  SunitStringUtil.isBlankOrNull(SunitStringUtil.getStringbyCommaSeparate(filedParamenter,""))){
							continue;
						}
					}
					 
					
					if (clazz != null && process!=null) {
							b =process.process(request,response,validate, filedParamenter, resultMap, args);
					}
					
					if(!b){ 
						response.getWriter().print(
								JSONObject.fromObject(resultMap).toString()); 
						return false;
					}
				}
		}
		if (handler
				.getClass()
				.isAssignableFrom(
						org.springframework.web.bind.annotation.support.HandlerMethodInvoker.class)) {
			org.springframework.web.bind.annotation.support.HandlerMethodInvoker m = ((org.springframework.web.bind.annotation.support.HandlerMethodInvoker) handler);
			System.out.println("m=" + m);
			// AuthPassport authPassport = ((HandlerMethod)
			// handler).getMethodAnnotation(AuthPassport.class);
		}
		return true;
	} 
	
	
	
	public static void main(String[] args){   
		 
		 String s ="^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?$";
		 s="^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
		 java.util.regex.Pattern  pattern =  java.util.regex.Pattern.compile(s,java.util.regex.Pattern.CASE_INSENSITIVE);    
		 Matcher m = pattern.matcher( "1@163.com" ); 
		 System.out.println(m.matches());
		
		 
	}
}