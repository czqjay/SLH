package com.sunit.global.base.authorization.validate.impl;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpException;

import com.sunit.global.base.authorization.validate.constraints.Length;
import com.sunit.global.base.authorization.validate.constraints.Remote;
 
import com.sunit.global.base.authorization.validate.ValidateInterface;
import com.sunit.global.util.HttpUtil;
import com.sunit.global.util.SunitStringUtil;

public class RemoteValidator  implements ValidateInterface<Remote, String>{

	Remote remote;
	
	public void initialize(Remote o) {
		this.remote = o;
	}
 
	public boolean isValid(String value) {
		
		
		String url = remote.getAction();
		url=remote.getWebPath()+url;
		if(SunitStringUtil.isBlankOrNull(url)){
			return false;
		}
		HttpUtil  ht =   new HttpUtil(); 
		try {
			
			ht.putParameter(remote.getFieldName(), value );
			String  result =ht.doAction(url);
			Map<String,String> m =JSONObject.fromObject(result);
			boolean b =String.valueOf(m.get("success")).equals("1");
			return remote.getIsReverse().equals("true")?!b:b;
//			if(result.equals("{\"success\":1}")){ 
//				return true;
//			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}  
	

}



