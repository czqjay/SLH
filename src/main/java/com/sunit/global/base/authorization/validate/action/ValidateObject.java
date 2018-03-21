package com.sunit.global.base.authorization.validate.action;

import com.sunit.global.util.SunitStringUtil;

/**
 * 后端验证对象. 每一个form字段的每一个验证方法对应一个本对象.eg: 
 * fieldName =pwd
 * otherFieldName = pwd2
 * methodName= isEqual
 * 以上设置用于完成两控件值一致效验
 * 
 * fieldName =username
 * methodName= isExists
 * methodNameArgs= /user/isExists.action
 *  以上设置用于完成数据库是否存在效验 
 * 
 * 
 * 类名称：ValidateObject
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Jan 13, 2016 5:59:37 PM
 * 修改人：joye
 * 修改时间：Jan 13, 2016 5:59:37 PM
 * 修改备注：
 * @version 
 *
 */
public class ValidateObject {

	private String fieldName;
	private String otherFieldName; 
	private String methodName;
	private Object methodNameArgs;
	
	
	public String getFieldName() {
		return fieldName; 
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getOtherFieldName() {
		return otherFieldName;
	}
	public void setOtherFieldName(String otherFieldName) {
		this.otherFieldName = otherFieldName; 
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object getMethodNameArgs() {
		return methodNameArgs;
	}
	public void setMethodNameArgs(Object methodNameArgs) {
		this.methodNameArgs = methodNameArgs;
	}


	 
	
	
}
