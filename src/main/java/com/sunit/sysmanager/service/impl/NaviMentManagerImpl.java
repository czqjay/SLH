package com.sunit.sysmanager.service.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.sysmanager.po.NaviMenu;
import com.sunit.sysmanager.service.NaviMenuManager;
 

/**
 * Users 对象的 service
 * @author joye
 * 
 */
@Service 
public class NaviMentManagerImpl extends AbstractBaseServiceImpl<NaviMenu>  implements NaviMenuManager   {

	@Autowired  
	BaseDAO dao; 
	
	protected BaseDAO getDao() {
		return dao;
	} 
	
	public Session  getSessionByManager(){ 
		return dao.getValidSession();
	}
  
	public List<NaviMenu> isExtists(NaviMenu entity, boolean excludeSelf) {
		return null;
	}

 
	
	 
	

}

