package com.sunit.global.flowvariables.service.impl;
 
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.global.flowvariables.po.Flowvariables;
import com.sunit.global.flowvariables.service.FlowvariablesManager;


@Service
public class FlowvariablesManagerImpl extends AbstractBaseServiceImpl<Flowvariables> implements FlowvariablesManager {

	static Logger logger = Logger.getLogger(FlowvariablesManagerImpl.class);
	
	@Autowired
	BaseDAO dao;
	
	 
	protected BaseDAO getDao() { 
		return dao; 
	}
}
 