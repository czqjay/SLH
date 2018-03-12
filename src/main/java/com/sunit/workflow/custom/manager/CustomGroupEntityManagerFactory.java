
package com.sunit.workflow.custom.manager;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;



public class CustomGroupEntityManagerFactory implements SessionFactory {

	@Autowired
	CustomGroupEntityManager cgm;
	 
	
	public Class<?> getSessionType() {
		return GroupIdentityManager.class;
	}

	public Session openSession() {
		return cgm;
	} 

}
