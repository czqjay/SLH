package com.sunit.global.base.authorization.validate;

import java.util.Map;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

import com.sunit.global.util.SunitStringUtil;
 
public interface ValidateInterface<K,T> {
	
	public void initialize(K o);

	public boolean isValid(T value );
 
}
