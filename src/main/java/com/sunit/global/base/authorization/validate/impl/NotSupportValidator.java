package com.sunit.global.base.authorization.validate.impl;

import com.sunit.global.base.authorization.validate.ValidateInterface;

public class NotSupportValidator  implements ValidateInterface<String,String> {
   
	public boolean isValid(String value) { 
		return value == null; 
	} 

 
 

	public void initialize(String o) {
		// TODO Auto-generated method stub
		
	}


 
}
