package com.sunit.operator.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.operator.po.Operator;
import com.sunit.operator.service.OperatorManager;


@Service
public class OperatorManagerImpl extends AbstractBaseServiceImpl<Operator> implements OperatorManager {

	static Logger logger = Logger.getLogger(OperatorManagerImpl.class);
	
	@Autowired
	BaseDAO dao;
	
	
	protected BaseDAO getDao() { 
		return dao; 
	}
}
 