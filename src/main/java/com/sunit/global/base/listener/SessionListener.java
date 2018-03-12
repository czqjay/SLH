package com.sunit.global.base.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener  implements HttpSessionListener
{ 

	public void sessionCreated(HttpSessionEvent arg0) {
		
//		System.out.println(DateUtil.getCurrentTime()+": SessionListener.sessionCreated()"+arg0.getSession().getId());
	}  
 
	public void sessionDestroyed(HttpSessionEvent arg0) { 
//		System.out.println(DateUtil.getCurrentTime()+": SessionListener.sessionDestroyed()"+arg0.getSession().getId());  
		
	} 

}
 