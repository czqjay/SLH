package com.sunit.global.base.exception;

public class ExceptionManager {

	public static void throwEx(String msg) throws SLHException{
			throw new SLHException(msg); 
	}
	
	public static void throwRuntimeEx(String msg) {
			throw new SLHRuntimeException(msg);   
	}
	
	public static void throwEx(String msg,boolean isThrow) throws SLHException{
		if(isThrow){
			throw new SLHException(msg); 
		}
	}
	
	public static void throwRuntimeEx(String msg,boolean isThrow) {
		if(isThrow){
			throw new SLHRuntimeException(msg);  
		}
	}
	
}
