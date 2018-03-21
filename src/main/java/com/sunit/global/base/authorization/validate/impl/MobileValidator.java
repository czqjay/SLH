package com.sunit.global.base.authorization.validate.impl;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import com.sunit.global.base.authorization.validate.ValidateInterface;
import com.sunit.global.base.authorization.validate.constraints.Pattern;
 
 
 
public class MobileValidator  implements ValidateInterface<Pattern,String> { 
 
	private java.util.regex.Pattern pattern; 

	public void initialize(Pattern parameters) { 
		try {
			pattern = java.util.regex.Pattern.compile( parameters.getRegpx() ); 
		}
		catch ( PatternSyntaxException e ) {
			throw new IllegalArgumentException( "Invalid regular expression.", e );
		}
	} 

	public boolean isValid(String value) {
		if ( value == null ) { 
			return true;
		}
		Matcher m = pattern.matcher( value );
		return m.matches();
	}

}
