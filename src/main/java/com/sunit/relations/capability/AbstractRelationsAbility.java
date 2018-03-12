package com.sunit.relations.capability;

import java.util.List;

import javax.management.RuntimeErrorException;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.deser.std.StdDeserializer.LongDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.stereotype.Component;

import com.sunit.global.base.AbstractID;
import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.service.AbstractBaseServiceImpl;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.navimenu.action.NavimenuAction;
import com.sunit.navimenu.service.impl.NavimenuManagerImpl;
import com.sunit.relations.po.RelationTree;
import com.sunit.relations.service.RelationsManager;

@MappedSuperclass
public class AbstractRelationsAbility<T> extends AbstractID {

	private  T  parent;
	private  List<T> roots;
	private  List<T> childs; 
	private  List<T> child; 
	static Logger logger = Logger.getLogger(AbstractRelationsAbility.class);
	@Transient 
	public List<T> getRoots() { 
		logger.debug("AbstractRelationsAbility.getRoots()");
		RelationsManager relationsManager= SpringContextUtils.getSpringContext().getBean(RelationsManager.class);
		String poName =this.getClass().getName();
		String hql="  from  "+poName+" n where n.id in ( select classId from RelationTree t   where   t.className = ?  and  t.relationsLevel =?)";
		List<T> list = (List<T>) relationsManager.find(hql,poName,1 ); 
		return list; 
	
	} 
	public void setRoots(List<T> roots) {
		this.roots = roots;
	} 
	
	@Transient 
	public List<T> getChild() {   
		logger.debug("AbstractRelationsAbility.setChilds()");
		RelationsManager relationsManager= SpringContextUtils.getSpringContext().getBean(RelationsManager.class);
		String poName =this.getClass().getName(); 
		RelationTree rt =  relationsManager.getRelationByClassId(this.getId(), poName);  
		List<T> list = (List<T>) relationsManager.find("  from  "+poName+" n where n.id in ( select classId from RelationTree t   where   t.relationsLeft>?  and   t.relationsRight <? and t.className = ? and    t.relationsLevel= ? and  t.relationsAncestorId=? )",rt.getRelationsLeft(),rt.getRelationsRight(),poName, rt.getRelationsLevel()+1, rt.getRelationsAncestorId() );
		return list; 
	}  


	@Transient 
	public List<T> getChilds() {   
		logger.debug("AbstractRelationsAbility.setChilds()");
		RelationsManager relationsManager= SpringContextUtils.getSpringContext().getBean(RelationsManager.class); 
		String poName =this.getClass().getName(); 
		RelationTree rt =  relationsManager.getRelationByClassId(this.getId(), poName);  
		List<T> list = (List<T>) relationsManager.find("  from  "+poName+" n where n.id in ( select classId from RelationTree t   where   t.relationsLeft>?  and   t.relationsRight <? and t.className = ?  and  t.relationsAncestorId=? )",rt.getRelationsLeft(),rt.getRelationsRight(),poName, rt.getRelationsAncestorId() );
		return list; 
	}  

	
	@Transient 
	private T findParent() {
		
		String id = this.getId();
		if(SunitStringUtil.isBlankOrNull(id)) {  
			return null;
		} 
		RelationsManager relationsManager= SpringContextUtils.getSpringContext().getBean(RelationsManager.class);
		String poName =this.getClass().getName();
	
		RelationTree rt =  relationsManager.getRelationByClassId(id, poName);   
		List<T> list = (List<T>) relationsManager.find(" from  "+poName+" n where n.id in (   select classId   from  RelationTree  t where  t.relationsLeft<?  and t.relationsRight>? and t.className = ?  and relationsAncestorId=? and  t.relationsLevel=?  )", rt.getRelationsLeft(),rt.getRelationsRight(),poName,rt.getRelationsAncestorId(),rt.getRelationsLevel()-1);
		if(!list.isEmpty()) {
			return  list.get(0);  
		}
		return null; 
	}
	
	
	@Transient
	public T getParent() {
		logger.debug("AbstractRelationsAbility.getParent()");
		if(this.parent!=null) {
			return this.parent;
		}
		this.setParent( this.findParent());
		return this.parent;
	
	}
	public void setParent(T parent) {
		this.parent = parent;
	}
	public void setChild(List<T> child) {
		this.child = child;
	}
	public void setChilds(List<T> childs) {
		this.childs = childs;
	}
	
//	public List <?> getRoot(Class<? extends AbstractID> c){  
//		List<?> list = this.find("  from  "+c.getName()+" n where n.id in ( select classId from RelationTree t   where   t.className = ?  and  t.relationsLevel =?)",c.getName(),1 );
//		return list;
//	}
//	
//	public  List  getChilds(Class<? extends AbstractID>  c, String parent) {
//		
//		RelationTree rt =  this.getRelationByClassId(parent, c.getName()); 
//		
//		List<?> list = this.find("  from  "+c.getName()+" n where n.id in ( select classId from RelationTree t   where   t.className = ? and   relationsLevel= ? )",c.getName(), rt.getRelationsLevel()+1 );
//		return list;
//		
//	}
	
}
 