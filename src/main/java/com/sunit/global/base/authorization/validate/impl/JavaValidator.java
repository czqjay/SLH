package com.sunit.global.base.authorization.validate.impl;

import com.sunit.global.base.authorization.validate.ValidateInterface;
import com.sunit.global.base.authorization.validate.constraints.Script;
import com.sunit.global.base.exception.SLHException;


public class JavaValidator  implements ValidateInterface<Script, String>{

	Script script;
	 
	public void initialize(Script o) {
		this.script = o;
	} 
 
	public boolean isValid(String value) {
		
		boolean b = script.getScriptEx().execute(script); 
		return b;
	}  
	

}



