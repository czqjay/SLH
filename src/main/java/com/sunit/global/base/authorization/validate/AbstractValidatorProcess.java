package com.sunit.global.base.authorization.validate;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sunit.global.base.authorization.validate.action.ValidateObject;
import com.sunit.global.base.authorization.validate.constraints.Pattern;
import com.sunit.global.util.SunitStringUtil;

public   abstract class AbstractValidatorProcess { 

	
	 
	public abstract  boolean process(HttpServletRequest reqeust,HttpServletResponse response, ValidateObject validate,  
			String[]  filedParamenter,Map resultMap , Map args);
	 
	public String getMsg(Map<String, String> args , String defaultsMsg){
		 return (String) (SunitStringUtil.isBlankOrNull(args.get("message"))?defaultsMsg:args.get("message"));
	}
 

} 
