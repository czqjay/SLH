package com.sunit.sysmanager.service.impl;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.UserManager;


/**
 * 
 * 
 * 类名称：UserManagerImpl
 * 类描述：用户接口实现类,实现UserManage接口并重写该接口中的方法
 * 创建人：liangrujian
 * 创建时间：Jul 24, 2013 10:29:39 AM
 * 修改人：liangrujian
 * 修改时间：Jul 24, 2013 10:29:39 AM
 * 修改备注：
 * @version 
 *
 */
@Service 
public class UserManagerImpl extends AbstractBaseServiceImpl<User>  implements UserManager   {

	@Autowired 
	BaseDAO dao; //注入BaseDAO 数据库操作基类
	
	protected BaseDAO getDao() {
		return dao;
	} 
	

}

