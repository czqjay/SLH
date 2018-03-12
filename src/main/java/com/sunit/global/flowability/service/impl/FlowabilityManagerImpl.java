package com.sunit.global.flowability.service.impl;
 
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.global.flowability.po.Flowability;
import com.sunit.global.flowability.service.FlowabilityManager;


@Service
public class FlowabilityManagerImpl extends AbstractBaseServiceImpl<Flowability> implements FlowabilityManager {

	static Logger logger = Logger.getLogger(FlowabilityManagerImpl.class);
	
	@Autowired
	BaseDAO dao;
	
	 
	protected BaseDAO getDao() { 
		return dao; 
	}
}
 