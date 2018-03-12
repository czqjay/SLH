package com.sunit.relations.service;

import java.util.List;

import com.sunit.global.base.AbstractID;
import com.sunit.global.base.exception.SLHException;
import com.sunit.relations.po.RelationTree;
 
/**
 * 
 * 实现多层的用户关系分布
 * 类名称：RelationsInterface
 * 类描述：
 * 创建人：Administrator
 * 创建时间：Nov 2, 2015 4:23:07 PM
 * 修改人：joye
 * 修改时间：Nov 2, 2015 4:23:07 PM
 * 修改备注：
 * @version 
 *
 */
public interface RelationsInterface {

	
/**
 * 获取 当前 className 下 所有 parent 的子元纛 
 * @param id
 * @param poName
 * @return 
 */
	public  List  getChilds( String parent,String poName);
	

	/**
	 * 获取 当前 className 下 所有 level =1 的 relation 
	 * @param c    relation 指向的实体类
	 * @return
	 */
//	public  List  getRoot();	    
	/** 
	 * 从classId获取 relation 对象
	 * @param classId
	 * @param className
	 * @return
	 * @throws SLHException
	 */
	public  RelationTree getRelationByClassId(String classId,String className) ; 
	
	/**
	 *  添加并排序关系树
	 * @param classId
	 * @param parentId
	 * @param className 
	 * @return
	 */
	public boolean addRelation(String classId ,String parentId ,String className,String remark) ; 
	
	
	
	/**
	 * 获取当前用户的关系分布层级 
	* @Title: getLevel 
	* @Description: 
	* @param @param classId
	* @param @return
	* @param @throws Exception     
	* @return int  
	* @throws 
	* @author joye 
	* Nov 9, 2015 7:14:40 PM
	 */
	public  int  getLevel(String classId) throws Exception;
	 
	/**
	 * 获取所有上级
	* @Title: getUpLevels 
	* @Description: 
	* @param @param classId
	* @param @return
	* @param @throws Exception     
	* @return List  
	* @throws 
	* @author joye 
	* Nov 9, 2015 7:11:59 PM
	 */
	public List<RelationTree> getUpLevels(String classId)  throws Exception;
	
	/**
	 * 获取所有下级
	* @Title: getDownLevels 
	* @Description:  
	* @param @param classId
	* @param @return
	* @param @throws Exception     
	* @return List  
	* @throws 
	* @author joye 
	* Nov 9, 2015 7:12:16 PM
	 */
	public List<RelationTree>   getDownLevels(String classId) throws Exception;  
	 
	/**
	 * 获取直接下级, 不包括间接下级
	* @Title: getFisrtDownLevels 
	* @Description:  
	* @param @param classId
	* @param @param level
	* @param @return
	* @param @throws Exception     
	* @return List<Relations>  
	* @throws 
	* @author joye 
	* Jun 15, 2016 2:50:39 PM 
	 */
	public List<RelationTree>   getFisrtDownLevels (String classId) throws Exception;  
	

	
	
	/**
	 * 对userId编码并输出
	 * @param UserId 
	 * @return  
	 */
	public String getRelationsCode(String UserId);
	 
	
	/**
	 * 删除业务对象及其子对象
	 * @param ids
	 * @param poName
	 */
	public void delete(final String[] ids,String poName); 

}
