package com.sunit.global.base.authorization.validate.impl;

import com.sunit.global.base.authorization.validate.ValidateInterface;
import com.sunit.global.base.authorization.validate.constraints.Range;

public class NumberRangeValidator  implements ValidateInterface<Range,String> { 
	private String min;
	private String max;
	
	
	public boolean isValid(String value) { 
		if ( value == null ) {
			return false;
		}
		double  d  = Double.valueOf(value);
		double  l  = Double.valueOf(min);
		double  r  = Double.valueOf(max);
		return (d>=l && d<=r);
	}   
 
	public void initialize(Range parameters) {
		min = parameters.getMin(); 
		max = parameters.getMax();  
		validateParameters();
	}
	
	private void validateParameters() {
		
		try{
			double  l  = Double.valueOf(min);
			double  r  = Double.valueOf(max); 
		}catch (Exception e) {
			throw new IllegalArgumentException( "  输入的数据不合法 " );
		}
				
//		if ( min < 0 ) {
//			throw new IllegalArgumentException( "The min parameter  cannot be negative." );
//		}
//		if ( max < 0 ) {
//			throw new IllegalArgumentException( "The max parameter cannot be negative." );
//		}
//		if ( max < min ) {
//			throw new IllegalArgumentException( "The length cannot be negative." );
//		}
	}

 
}
