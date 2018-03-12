package com.sunit.global.base.gen.intercepter.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import com.sunit.global.base.gen.intercepter.GenIntercepterInterface;
import com.sunit.global.util.SessionContext;
import com.sunit.sysmanager.po.User;

public class SonglistaManagerImplSave  implements GenIntercepterInterface{

	static Logger logger = Logger.getLogger(SonglistaManagerImplSave.class);  
	
	public void process(MethodInvocation invoke) {

		User u = SessionContext.getUser(SessionContext.getHttpServletRequest());
	} 

}
