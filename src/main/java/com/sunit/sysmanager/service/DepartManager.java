package com.sunit.sysmanager.service;

import java.util.List;

import com.sunit.global.base.service.BaseService;
import com.sunit.sysmanager.po.Depart;

/**
 * 类名称：DeptManager
 * 类描述：部门管理接口
 * 创建人：liangrujian
 * 创建时间：Jul 24, 2013 10:32:10 AM
 * 修改人：liangrujian
 * 修改时间：Jul 24, 2013 10:32:10 AM
 * 修改备注：
 * @version 
 *
 */
public interface DepartManager extends BaseService<Depart>{
	
	
	/**
	 * 
	* @Title: findBySQL 
	* @Description:  支持sql查询
	* @param @param sql
	* @param @return     
	* @return List  
	* @throws 
	* @author joye 
	* Sep 25, 2013 10:30:19 AM
	 */
	public List findBySQL(final String sql);
}
