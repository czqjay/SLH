package com.sunit.global.base.authorization.validate.impl;

import com.sunit.global.base.authorization.validate.constraints.Length;
 
import com.sunit.global.base.authorization.validate.ValidateInterface;

public class LengthValidator  implements ValidateInterface<Length, String>{ 
	private int min;
	private int max;
	
	public void initialize(Length parameters) {  
		min = parameters.getMin(); 
		max = parameters.getMax(); 
		validateParameters();
	}
	public boolean isValid(String value) {
		if ( value == null ) {
			return true;
		}
		int length = value.length(); 
		return length >= min && length <= max;
	} 


	private void validateParameters() { 
		if ( min < 0 ) {
			throw new IllegalArgumentException( "The min parameter  cannot be negative." );
		}
		if ( max < 0 ) {
			throw new IllegalArgumentException( "The max parameter cannot be negative." );
		}
		if ( max < min ) {
			throw new IllegalArgumentException( "The length cannot be negative." );
		}
	}


}



