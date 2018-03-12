package com.sunit.sysmanager.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Service;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.sysmanager.po.Depart;
import com.sunit.sysmanager.service.DepartManager;


/**
 * 
 * 
 * 类名称：DepartManagerImpl
 * 类描述：实现部门管理接口类
 * 创建人：liangrujian
 * 创建时间：Jul 24, 2013 10:32:28 AM
 * 修改人：liangrujian
 * 修改时间：Jul 24, 2013 10:32:28 AM
 * 修改备注：
 * @version 
 *
 */
@Service 
public class DepartManagerImpl extends AbstractBaseServiceImpl<Depart>  implements DepartManager   {

	@Autowired 
	BaseDAO dao; 
	
	protected BaseDAO getDao() {
		return dao;
	} 
	public Session  getSessionByManager(){  
		return dao.getValidSession();
	}
	 
	public List findBySQL(final String sql) {
	
		List list = null;
		list = getDao().getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query q = session.createSQLQuery(sql);
						return q.list();
					}
				});
		return list;  
	}

}

