
package com.sunit.global.base.gen;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE; 
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD,TYPE}) 
@Retention(RUNTIME)
public @interface SLH {
	String value();
} 
 
 