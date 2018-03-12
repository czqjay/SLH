package com.sunit.global.singleLogin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.global.singleLogin.ClusterSingleLoginManager;
import com.sunit.global.singleLogin.po.ClusterSingleLogin;
 
@Service
public class ClusterSingleLoginManagerImpl extends AbstractBaseServiceImpl<ClusterSingleLogin> implements  ClusterSingleLoginManager {

	@Autowired  
	BaseDAO dao;
	
	protected BaseDAO getDao() { 
		return dao; 
	}
} 
  