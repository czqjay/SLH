package com.sunit.global.base.authorization.validate.impl;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import com.sunit.global.base.authorization.validate.ValidateInterface;
import com.sunit.global.util.SunitStringUtil;

public class NotBlankValidator  implements ValidateInterface<String,String> {
   
	public boolean isValid(String value) { 
		return !SunitStringUtil.isBlankOrNull(value); 
	} 

 
 

	public void initialize(String o) {
		// TODO Auto-generated method stub
		
	}


 
}
