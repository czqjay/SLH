package com.sunit.workflow.deploy.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.junit.Test;

import com.sunit.global.util.httpClient.HttpProtocolHandler;
import com.sunit.global.util.httpClient.HttpRequest;
import com.sunit.global.util.httpClient.HttpResponse;
import com.sunit.global.util.httpClient.HttpResultType;

public class TestDeploy extends TestCase {

	
	@Test
	public void testComplete(){
		
		HttpRequest request= new HttpRequest(HttpResultType.BYTES);
		request.setCharset("UTF-8"); 
		
		List<NameValuePair> list =new ArrayList();
		list.add(new NameValuePair("j_username","sh"));
		list.add(new NameValuePair("j_password","1234"));
		list.add(new NameValuePair("clientIP",",clientIP")); 
		request.setParameters(list.toArray(new NameValuePair[list.size()])); 
  		HttpProtocolHandler httpHandle = HttpProtocolHandler.getInstance();
		HttpResponse response;
		try {
			request.setUrl("http://joyepc:7075/XWT/front/j_spring_security_check");
			response = httpHandle.execute(request,"", "");
			String  str = new String (response.getByteResult(),"UTF-8"); 
			System.out.println(str);
			request.setUrl("http://joyepc:7075/XWT/html/xwt/front/page/userType/agency/account.jsp");
//			joyepc:7075/XWT/deploy/complete.action?poID=402880475146ccb501514826bb970011&taskID=6941&passVarName=utAuditPass
			response = httpHandle.execute(request,"", "");
			str = new String (response.getByteResult(),"UTF-8");
			System.out.println(str);
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	@Test
	public void testNullMap(){
		
		Map map  = new HashMap();
		for ( Object s : map.entrySet()) {
			System.out.println(s.toString());
			
		}
		
	}
	
}
