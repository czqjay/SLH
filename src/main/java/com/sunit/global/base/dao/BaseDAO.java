package com.sunit.global.base.dao;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * 
 * 
 * 类名称：BaseDAO
 * 类描述：   数据库操作基类, 依赖于 org.springframework.orm.hibernate3.HibernateTemplate
 * 创建人：Administrator
 * 创建时间：Aug 31, 2013 1:50:07 PM
 * 修改人：joye
 * 修改时间：Aug 31, 2013 1:50:07 PM
 * 修改备注：
 * @version 
 * 
 */
public class BaseDAO extends HibernateDaoSupport {

	/**
	 * 得到可用的 hibernate session  通常情况下不推荐使用
	* @Title: getValidSession 
	* @Description: 
	* @param @return     
	* @return Session  
	* @throws 
	* @author Administrator 
	* Jul 22, 2013 10:12:03 AM
	 */
	public  Session getValidSession(){
		 
		this.getHibernateTemplate().setAllowCreate(false);
		return this.getSession(); 
		
	}
	
	 
}
 