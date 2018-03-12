package com.sunit.global.util.jsonlibConvert;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class DateProcessor implements JsonValueProcessor { 
	private String formatDateTime = "yyyy-MM-dd HH:mm:ss"; 
	private String formatDate = "yyyy-MM-dd";
	public Object processArrayValue(Object value, JsonConfig arg1) {
		String[] obj = {}; 
		String valueDate="";
	    if(value instanceof Date[]){    
	        SimpleDateFormat sdfDateTime = new SimpleDateFormat(formatDateTime);
	        SimpleDateFormat sdfDate = new SimpleDateFormat(formatDate);  
	        Date[] dates = (Date[])value;    
	        obj = new String[dates.length];    
	        for(int i=0;i<dates.length;i++){
	        	valueDate=sdfDateTime.format(dates[i]).trim();
	        	if(valueDate.indexOf("00:00:00")>1)
	        		obj[i] = sdfDate.format(dates[i]).trim();
	        	else
	               obj[i] = valueDate;
	            
	        }    
	    }    
	    return obj;    
	}

	public Object processObjectValue(String arg0, Object value,
			JsonConfig arg2) {
		if(value instanceof Date){
			 SimpleDateFormat sdfDateTime = new SimpleDateFormat(formatDateTime);
		     SimpleDateFormat sdfDate = new SimpleDateFormat(formatDate);
		     String valueDate="";
		     String str="";
		     valueDate=sdfDateTime.format(value).trim();
	        	if(valueDate.indexOf("00:00:00")>1)
	        		str = sdfDate.format(value).trim();
	        	else
	        		str = valueDate;    
	        return str.trim();    
	    }     
	    return value==null?null:value;     
	} 
}
