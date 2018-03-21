package com.sunit.global.base.authorization.validate.constraints;


public class Pattern   {
	
	String  regpx;
	String  flag;
	String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRegpx() { 
		return regpx;
	}
	public void setRegpx(String regpx) {
		this.regpx = regpx; 
	}
	public String getFlag() {
		return flag; 
	}
	public void setFlag(String flag) { 
		this.flag = flag;
	}
	
}
