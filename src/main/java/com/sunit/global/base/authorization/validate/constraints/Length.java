package com.sunit.global.base.authorization.validate.constraints;

public class Length {

	int min;
	int max; 
	String message;
	public int getMin() {
		return min;
	} 
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
