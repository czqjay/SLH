package com.sunit.global.base.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transaction;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.exception.SLHException;
import com.sunit.global.util.Paging;
 

/** 
 * BaseService 的实现类。 不要求一定继承此类，可以另外实现BaseService 
 * @author joye
 *
 * @param <T>
 */
public abstract class AbstractBaseServiceImpl<T> implements BaseService<T> {

	private T entity;

	protected abstract BaseDAO getDao(); 
 
	Class<T> entityClass = (Class<T>) ((ParameterizedType) this.getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];

	public T get(Serializable id) {
		return getDao().getHibernateTemplate().get(entityClass, id);
	}

	public void save(T entity) {
		getDao().getHibernateTemplate().saveOrUpdate(entity);
	}

	public void delete(final String[] ids) {
		DetachedCriteria dcrt = DetachedCriteria.forClass(entityClass);
		dcrt.add(Restrictions.in("id", ids));
		List<T> list = getDao().getHibernateTemplate().findByCriteria(dcrt);
		getDao().getHibernateTemplate().deleteAll(list);
	}

	public List<T> getAll() { 
		return getDao().getHibernateTemplate().loadAll(entityClass);
	}

	public List<T> find(String queryString, Object... value) {
		return getDao().getHibernateTemplate().find(queryString, value);
	}

	public List<T> find(String queryString) {
		return getDao().getHibernateTemplate().find(queryString,
				(Object[]) null);
	} 
	
	public  List<T>  findToResultTransformer( final String hql, final Object para , final String... values) {
		List list = null; 
		list = getDao().getHibernateTemplate().executeFind( 
				new HibernateCallback() {
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						Query  q = session.createQuery(hql).setResultTransformer(new AliasToBeanResultTransformer(entityClass));
						if(para!=null)
							q.setProperties(para);
						if (values != null) {
							for (int i = 0; i < values.length; i++) { 
								q.setParameter(i, values[i]); 
							}
						}
						
						return q.list();
					}
				});
		return list;
	}
	
	
	public  List<T>  findByPara( final String hql, final Object para , final String... values) {
		List list = null;
		list = getDao().getHibernateTemplate().executeFind( 
				new HibernateCallback() {
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						Query  q = session.createQuery(hql);
						if(para!=null)
							q.setProperties(para);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]); 
							}
						}
						
						return q.list();
					}
				});
		return list;
	}
 
	public Paging paging(final String getCountSql, final String hql,
			final int rows, final int page, final Object para) {
		final Paging paging = new Paging();
		paging.setRows(rows>100?100:rows);  
		paging.setRows(rows);
		List list = null;
		list = getDao().getHibernateTemplate().executeFind( 
				new HibernateCallback() {
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						Query q = session.createQuery(getCountSql);
						q.setProperties(para);
					    Object obj = q.uniqueResult();
					    if(obj==null){
					    	paging.setTotalRow(0);
					    }else{
					    	paging.setTotalRow(Long.valueOf(obj.toString()));
					    }
					    paging
						.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
								.getTotalRow()
								/ paging.getRows() 
								: paging.getTotalRow() / paging.getRows() + 1); 
					    paging.setPage( (int) (page>paging.getTotalPage()?paging.getTotalPage():page));  
					    int beginRow = ( paging.getPage() - 1) * rows; 
						q = session.createQuery(hql);
						q.setProperties(para);
						q.setFirstResult(beginRow);
						q.setMaxResults(rows);
						return q.list();
					}
				});
		paging.setList(list);
		
		return paging;
	}

	public Paging paging(final String getCountSql, final String hql,
			final int rows, final int page, final Object para ,final String... values ) { 
		 
		final Paging paging = new Paging();
		paging.setRows(rows>100?100:rows);  
		List list = null;
		list = getDao().getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public List doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query q = session.createQuery(getCountSql);
						if(para!=null)
							q.setProperties(para);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]);  
							}
						}
					    Object obj = q.uniqueResult();
					    if(obj==null){
					    	paging.setTotalRow(0);
					    }else{
					    	paging.setTotalRow(Long.valueOf(obj.toString()));
					    }
					    paging
						.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging 
								.getTotalRow()
								/ paging.getRows() 
								: paging.getTotalRow() / paging.getRows() + 1); 
					    paging.setPage( (int) (page>paging.getTotalPage()?paging.getTotalPage():page));  
					    int beginRow = ( paging.getPage() - 1) * rows; 
						q = session.createQuery(hql);
						if(para!=null)
						q.setProperties(para);
						if (values != null) {
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]); 
							}
						}
						
						q.setFirstResult(beginRow);
						q.setMaxResults(rows);
						return q.list();
					}
				});
		paging.setList(list);
		
		return paging;
	}

	public List findByExample(Object exampleEntity) throws DataAccessException {
		return getDao().getHibernateTemplate().findByExample(exampleEntity);
	}

	public List<T> findByVO(String queryString, Object VO) {
		return getDao().getHibernateTemplate().findByValueBean(queryString, VO);
	}
 
	public List<T> isExtists(T entity, boolean excludeSelf) throws Exception { 
		List<T> list = getDao().getHibernateTemplate()
				.findByExample(entity);
		Object id=null;  
		Object listId=null;
		
			 id =entity.getClass().getMethod("getId", null).invoke(entity,null);
			 if (excludeSelf && !StringUtils.isBlank(id.toString()))
					for (int i = 0; i < list.size(); i++) {
						T e = list.get(i);  
						listId =e.getClass().getMethod("getId", null).invoke(e,null);
						if (listId.toString().equals(id.toString())) 
							list.remove(e); 
					} 
		
		return list;
	}
	
	public Integer executeUpdate(final String hql, final Object... values) {
		Integer i =getDao().getHibernateTemplate().execute(
				new HibernateCallback<Integer>() {
					public Integer doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query q = session.createQuery(hql);
						if (values != null) { 
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]);
							}
						}
						return q.executeUpdate();
					}
				}); 
		return i;
	}
	
	public void delete(List<T> list){
		getDao().getHibernateTemplate().deleteAll(list);
	}
	
	

	
	public Integer executeUpdateSQL(final String sql, final Object... values) {
		Integer i =getDao().getHibernateTemplate().execute(
				new HibernateCallback<Integer>() {
					public Integer doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query q = session.createSQLQuery(sql);
						if (values != null) { 
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]);
							}
						}
						int  k =  q.executeUpdate();
						return k;
					}
				}); 
		return i;
	}

	
	public List<Object[]> excuteSQL(final String sql,final Object... values) { 
		List<Object[]> list = null; 
		list = getDao().getHibernateTemplate().executeFind( 
				new HibernateCallback() {
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						Query q = session.createSQLQuery(sql);
						if (values != null) { 
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]);
							}
						}
						return q.list();
					}
				});
		return list;
	}
	
	
	public List<T> excuteSQLByEntity(final String sql,final Object... values) { 
		List<T> list = null; 
		list = getDao().getHibernateTemplate().executeFind( 
				new HibernateCallback() {
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						Query q = session.createSQLQuery(sql).addEntity(entityClass);
						if (values != null) { 
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]);
							}
						}
						return q.list(); 
					}
				}); 
		return list;
	}

	
	public T findSingleByExample(Object exampleEntity) throws SLHException {
		
	 	List<T> results =  getDao().getHibernateTemplate().findByExample(exampleEntity);
	    if (results.size() == 1) {
	      return results.get(0);
	    } else if (results.size() > 1) {
	     throw new SLHException("查找到多个对象,数据不合法"); 
	    }  
	    return null;
}
	/**
	 * 
	* @Title: pagingSql 
	* @Description: 
	* @param @param getCountSql
	* @param @param Sql
	* @param @param rows
	* @param @param page
	* @param @param para
	* @param @return     
	* @return Paging   
	* @throws 
	* @author joye 
	* Dec 28, 2015 5:01:01 PM
	 */
	public Paging pagingSql(final String getCountSql, final String Sql,
			final int rows, final int page, final Object para, final List<String> argsList) {
		
		final Paging paging = new Paging();
		paging.setRows(rows>100?100:rows); 
		List list = null;
		list = getDao().getHibernateTemplate().executeFind(
				new HibernateCallback() { 
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						SQLQuery q = session.createSQLQuery(getCountSql); 
						q.setProperties(para);
						if (argsList != null) {  
							for (int i = 0; i < argsList.size(); i++) {
								q.setParameter(i, argsList.get(i));  
							}
						}
						q.addScalar("sqlCount",new LongType());  
						Long obj = (Long) q.uniqueResult();   
					    if(obj==null){ 
					    	paging.setTotalRow(0);
					    }else{
					    	paging.setTotalRow(Long.valueOf(obj.toString()));
					    }
					    paging
						.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
								.getTotalRow()
								/ paging.getRows() 
								: paging.getTotalRow() / paging.getRows() + 1); 
					    paging.setPage( (int) (page>paging.getTotalPage()?paging.getTotalPage():page));  
					    int beginRow = ( paging.getPage() - 1) * rows; 
					    SQLQuery sq  = session.createSQLQuery(Sql);
					    sq.setProperties(para);
						if (argsList != null) {   
							for (int i = 0; i < argsList.size(); i++) {
								sq.setParameter(i, argsList.get(i));  
							}
						}  
					    sq.setFirstResult(beginRow);  
					    sq.setMaxResults(rows).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); 
						return sq.list(); 
					}
				});
		paging.setList(list);
		return paging;
	}
	
	public Paging pagingSql(final String getCountSql, final String Sql,
			final int rows, final int page, final Object para,final Object... values){
		

		
		final Paging paging = new Paging();
		paging.setRows(rows>100?100:rows); 
		List list = null;
		list = getDao().getHibernateTemplate().executeFind(
				new HibernateCallback() { 
					public List doInHibernate(Session session) 
							throws HibernateException, SQLException {
						SQLQuery q = session.createSQLQuery(getCountSql); 
						if(para!=null)
							q.setProperties(para);
						if (values != null) {  
							for (int i = 0; i < values.length; i++) {
								q.setParameter(i, values[i]);  
							} 
						}
						q.addScalar("sqlCount",new LongType());  
						Long obj = (Long) q.uniqueResult();   
					    if(obj==null){ 
					    	paging.setTotalRow(0);
					    }else{
					    	paging.setTotalRow(Long.valueOf(obj.toString()));
					    }
					    paging
						.setTotalPage(paging.getTotalRow() % paging.getRows() == 0 ? paging
								.getTotalRow()
								/ paging.getRows() 
								: paging.getTotalRow() / paging.getRows() + 1); 
					    paging.setPage( (int) (page>paging.getTotalPage()?paging.getTotalPage():page));  
					    int beginRow = ( paging.getPage() - 1) * rows; 
					    SQLQuery sq  = session.createSQLQuery(Sql);
					    if(para!=null)
					    	sq.setProperties(para);
					    if (values != null) {   
							for (int i = 0; i < values.length; i++) {
								sq.setParameter(i, values[i]);  
							} 
						}  
					    sq.setFirstResult(beginRow);  
					    sq.setMaxResults(rows).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP); 
						return sq.list(); 
					}
				});
		paging.setList(list);
		return paging;
		
	}
	
	
	
}
