
package com.sunit.workflow.custom.manager;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.RoleManager;
import com.sunit.sysmanager.service.UserManager;



@Component
public class CustomGroupEntityManager extends GroupEntityManager {
	
	static Logger logger = Logger.getLogger(CustomGroupEntityManager.class);
	
	@Autowired
	RoleManager rm;  
	
	@Autowired
	UserManager um;
	
	/**
	 * 通过 accountName 得到roles
	 * 
	 */
	  public List<Group> findGroupsByUser(String userId) {  
		  logger.debug("CustomGroupEntityManager.findGroupsByUser()");
//		  List<Role> list = um.get(userId).getRoles(); 
		  List<Role> list = new ArrayList();;
		  User u =new User();
		  u.setAccountName(userId);
		  List<User> ulist =um.findByExample(u);
		  if(!ulist.isEmpty())
			  list = ulist.get(0).getRoles(); 
			  
		  List<Group> groupList=new ArrayList();
		  for (int i = 0; i < list.size(); i++) { 
			  Group g = new GroupEntity(); 
			  g.setId(list.get(i).getRoleName());
			  groupList.add(g);
		  }
		  return groupList;  
	  }
	  
	  
	  public List<Group> convertSLHToACT(List<Role> roles){
		  
		  List<Group> groups =new ArrayList();
		  
		  for (Role r  : roles) {
			  if(r!=null){
				  Group g =new GroupEntity();
				  g.setId(r.getRoleName());
				  g.setName(r.getRoleName());
				  groups.add(g);
			  }
		  }
		  
		  return groups;
	  }
	  
	  
	  public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
//		    return getDbSqlSession().selectList("selectGroupByQueryCriteria", query, page);
		  
		  List<Group> groups =null;
		  List<Role> roles=null;
		  Role role =new Role();
		  role.setRoleName(query.getId());
		  if(!StringUtils.isBlank(query.getUserId())){
			  
			  User u = um.get(query.getUserId());
			  roles = u.getRoles();
			  return groups;
		  }
		  roles = rm.findByExample(role);
		  groups = this.convertSLHToACT(roles);
		  return groups;
		  
		  }
		  
	  
	  
	
}
