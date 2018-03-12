package com.sunit.sysmanager.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.sysmanager.po.Jobs;
import com.sunit.sysmanager.service.JobsManager;


/**
 * 
 * 
 * 类名称：JobsManagerImpl
 * 类描述：实现职务接口类
 * 创建人：liangrujian
 * 创建时间：Jul 24, 2013 10:31:52 AM
 * 修改人：liangrujian
 * 修改时间：Jul 24, 2013 10:31:52 AM
 * 修改备注：
 * @version 
 *
 */
@Service 
public class JobsManagerImpl extends AbstractBaseServiceImpl<Jobs>  implements JobsManager   {

	@Autowired 
	BaseDAO dao; 
	
	protected BaseDAO getDao() {
		return dao;
	} 
	




	 
	

}

