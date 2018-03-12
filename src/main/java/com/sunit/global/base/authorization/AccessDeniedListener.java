package com.sunit.global.base.authorization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;

import com.sunit.sysmanager.po.User;

@Component
public class AccessDeniedListener implements ApplicationListener<AuthorizationFailureEvent>  {
	
	public void onApplicationEvent(AuthorizationFailureEvent arg0) {
		 HttpServletRequest request = ((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest() ;
		 HttpSession session = request.getSession(false);
		 if(session!=null&&request.getSession().getAttribute("user")!=null){
		    User user =(User)(request.getSession().getAttribute("user"));
		 }
		 if(session!=null)
			 session.invalidate(); 
	} 
}
