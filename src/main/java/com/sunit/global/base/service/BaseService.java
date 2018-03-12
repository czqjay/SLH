package com.sunit.global.base.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.sunit.global.base.exception.SLHException;
import com.sunit.global.util.Paging;
  
/**
 * 一个接口 用于封装普遍的DB操作
 * @author joye
 *
 * @param <T>
 */
public interface BaseService<T> {
	  
	
	/**
	 * get entity by id
	* @Title: get 
	* @Description: 
	* @param @param id
	* @param @return     
	* @return T  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public T get(Serializable id);   
	 
	/**
	 * save entity
	* @Title: save 
	* @Description: 
	* @param @param entity     
	* @return void  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public void save(T entity);
	 
	/**
	 * del entity
	* @Title: delete 
	* @Description: 
	* @param @param ids     
	* @return void  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public void delete(String[] ids); 
	
	 
	
	/**
	 * del entity
	* @Title: delete 
	* @Description: 
	* @param @param ids     
	* @return void  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public void delete(List<T> list); 
	
	
	/**
	 * 获取所有记录 for entity 
	* @Title: getAll 
	* @Description: 
	* @param @return     
	* @return List<T>  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public List<T> getAll();  
	
	/**
	 * 执行hql 返回list
	* @Title: find 
	* @Description: 
	* @param @param queryString
	* @param @param value    可以是一个数组,也可以是多个参数
	* @param @return     
	* @return List  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public List<T> find(String queryString, Object... value); 
	
	
	/**
	 * 执行  hql , 返回封装后的   PO对象 ,  用于指定 select 字段的hql
	* @Title: findToResultTransformer 
	* @Description: 
	* @param @param hql
	* @param @param para
	* @param @param values
	* @param @return     
	* @return List<T>  
	* @throws 
	* @author joye 
	* 2018年1月25日 下午7:17:47
	 */
	public  List<T>  findToResultTransformer( final String hql, final Object para , final String... values) ;
	
	
	public List<T>  findByPara( final String hql, final Object para , final String... values) ; 
	
	/**
	 * 业务对象的分页方法
	* @Title: paging 
	* @Description: 
	* @param @param getCountSql  获取总数hql
	* @param @param hql          获取业务对象的hql 
	* @param @param rows         展示行数
	* @param @param page         当前页面数
	* @param @param para         搜索条件VO
	* @param @return     
	* @return Paging  
	* @throws 
	* @author joye  
	* Jun 16, 2013
	 */
	public Paging  paging(String getCountSql,String hql,final int rows, int page,
			Object para);
	
	public Paging  paging(String getCountSql,String hql,final int rows, int page,
			Object para,String...values);
	
	
	/**
	 *  获取 与entity 对象属性一致的所有对象(忽略ID)
	* @Title: findByExample 
	* @Description: 
	* @param @param exampleEntity
	* @param @return
	* @param @throws DataAccessException     
	* @return List<T>  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public List<T> findByExample(Object exampleEntity) throws DataAccessException ;
	
	/**
	 * 获取 与entity 对象属性一致的所有对象(需要的hql中明确指出)
	* @Title: findByVO 
	* @Description: 
	* @param @param queryString
	* @param @param VO
	* @param @return     
	* @return List<T>  
	* @throws 
	* @author joye 
	* Jun 16, 2013
	 */
	public List<T> findByVO(String queryString, Object VO); 
	
	/**
	 *entity 是否存在 
	* @Title: isExtists 
	* @Description: 
	* @param @param entity
	* @param @param excludeSelf  返回数据是否排除 entity 本身(基于ID判断)
	* @param @return     
	* @return List<T>  
	* @throws 
	* @author joye 
	* Jun 16, 2013 
	 */
	public List<T> isExtists(T entity,boolean excludeSelf) throws Exception;  
	
	 
	
	/**
	 * 执行 update hql  返回 update 影响的记录数
	* @Title: executeUpdate 
	* @Description: 
	* @param @param hql
	* @param @return     
	* @return Integer  
	* @throws 
	* @author Administrator 
	* Jul 22, 2013 10:21:50 AM
	 */
	public Integer executeUpdate(final String hql,final Object... values);
	
	
	/**
	 * 执行 sql  
	* @Title: excuteSQL 
	* @Description: 
	* @param @param sql
	* @param @return     
	* @return List<Object[]>  
	* @throws 
	* @author joye 
	* Dec 20, 2013 4:11:24 PM
	 */
	public List<Object[]> excuteSQL(final String sql,final Object... values);
	
	/**
	 *  执行 sql 查询 返回对应的实体 
	* @Title: excuteSQLByEntity 
	* @Description: 
	* @param @param sql 
	* @param @param values
	* @param @return     
	* @return List<T>  
	* @throws  
	* @author joye 
	* Dec 20, 2013 4:31:49 PM
	 */	
	public List<T> excuteSQLByEntity(final String sql,final Object... values);
	
	
	
	/**
	 *  执行sql 更新 返回影响行数
	* @Title: executeUpdateSQL 
	* @Description: 
	* @param @param sql
	* @param @param values
	* @param @return     
	* @return Integer  
	* @throws 
	* @author joye 
	* 2017年12月19日 下午2:14:21
	 */
	public Integer executeUpdateSQL(final String sql, final Object... values) ;
	
	public T findSingleByExample(Object exampleEntity) throws SLHException ;
	
	public Paging pagingSql(final String getCountSql, final String Sql,
			final int rows, final int page, final Object para, final List<String> argsList) ;
	
	
	public Paging pagingSql(final String getCountSql, final String Sql,
			final int rows, final int page, final Object para,final Object... values);
	
 }
