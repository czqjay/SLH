package com.sunit.sysmanager.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.RoleManager;


/**
 * Role 对象的 service
 * @author joye
 *
 */
@Service 
public class RoleManagerImpl extends AbstractBaseServiceImpl<Role>  implements RoleManager   {

	@Autowired 
	BaseDAO dao; 
	
	protected BaseDAO getDao() {
		dao.getHibernateTemplate().setCacheQueries(true);
		return dao;
	} 
	
	public Session  getSessionByManager(){ 
		return dao.getValidSession();
	}
	
	 
}

