


package com.sunit.sysmanager.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitBeanUtils;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.RoleManager;
import com.sunit.sysmanager.service.SysResourceManager;
import com.sunit.sysmanager.service.UserManager;

/**
 *  权限管理
 * 类名称：RoleAction
 * 类描述：角色管理，为用户分配权限。
 * 创建人：liangrujian
 * 创建时间：Jul 18, 2013 11:17:30 AM
 * 修改人：liangrujian
 * 修改时间：Jul 18, 2013 11:17:30 AM
 * 修改备注：
 * @version 
 *
 */
@Controller
@RequestMapping("/role")
public class RoleAction extends BaseAction {

	static Logger logger = Logger.getLogger(RoleAction.class);
	
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private SysResourceManager sysresourceManager;
	
/**
 * 加载数据
* @Title: getRoleList 
* @Description: 获得请求，返回到role/roleList.jsp 页面。
* @param @param request
* @param @param response
* @param @return     
* @return String  
* @throws 
* @author liangrujian 
* Jul 18, 2013 10:58:02 AM
 */
	@RequestMapping("/getRoleList.action")
	public String getRoleList(HttpServletRequest request,
			HttpServletRequest response) {
		logger.debug("getRoleList.action......");
		
		return "role/roleList";
	}
	
	
	/**
	 * 显示权限记录数据到页面上。
	* @Title: loadRoleListDataGrid 
	* @Description: 加载权限记录数据显示在页面中。
	* @param @param pw
	* @param @param request
	* @param @param response
	* @param @param rows
	* @param @param page
	* @param @param para
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 18, 2013 11:03:26 AM
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/loadRoleListDataGrid.action")
	public String loadRoleListDataGrid(PrintWriter pw, HttpServletRequest request,String userId,
			HttpServletRequest response, final int rows, int page,
			RoleSearchPara para) {
		logger.debug("role load loadClientDataGrid....");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("select new map(t.id as id , t.roleName as roleName) from Role t where  t.id not in(select r.id from Role r join r.user as u where  t.id not in('0','1','2','3') and u.id='"+userId+"')");
		if (!StringUtils.isBlank(para.getRoleName())) {
			para.setRoleName("%" + para.getRoleName() + "%");
			hqlBuf.append("  and t.roleName like:RoleName");
		}
		hqlBuf.append("  order by t.roleName desc  ");
		Paging paging = roleManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total",paging.getTotalPage());// 总页数
		map.put("page",   paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}
	
/**
 * 
* @Title: addRole 
* @Description: 增加或修改保存权限记录
* @param @param pw
* @param @param role
* @param @param request
* @param @return
* @param @throws ParseException     
* @return String  
* @throws 
* @author liangrujian 
* Jul 18, 2013 11:07:52 AM
 */
	
	@RequestMapping("/addRole.action")
	public String addRole(PrintWriter pw, Role role, HttpServletRequest request)
			throws ParseException {
		logger.debug("RoleAction.addRole()");
		String actionErrType = "";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			role.setId(null);
			if(!StringUtils.isBlank(role.getId())){
				actionErrType = "修改权限记录";
			    Role r = roleManager.get( role.getId() );
				SunitBeanUtils.copyProperties(role,r); //form 表单对象 role 覆盖数据库中数据对象 r
				roleManager.save(r);  
			}else{
				actionErrType = "新增权限记录";
				roleManager.save(role);
			}	
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error(actionErrType, e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	
	
	/**
	* @Title: delRole 
	* @Description: 删除权限记录经过的Action
	* @param @param pw
	* @param @param ids
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 18, 2013 11:09:53 AM
	 */
	@RequestMapping("/delRole.action")
	public String delRole(PrintWriter pw, String ids) {
		logger.debug("delRole.Action.delRole()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			roleManager.delete(idArr);
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除权限记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	
	/**
	 * 
	* @Title: roleUpdate 
	* @Description: 修改权限记录时经过的Action方法。
	* @param @param pw
	* @param @param id
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 18, 2013 11:12:25 AM
	 */
	@RequestMapping("/roleUpdate.action")
	public String roleUpdate(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletRequest response) {
         logger.debug("RoleAction.roleUpdate()");
		String url="";
		if(null!=id){
			Role role = roleManager.get(id);
			request.setAttribute("role", role);
			url="role/roleUpdate"; //修改权限记录时返回的jsp页面
		}	
		return url;
	}

	

	/**
	 * 
	* @Title: getRoleCheckBox 
	* @Description: 当点击分配角色时 加载权限列表在页面中
	* @param @param pw
	* @param @param id
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 25, 2013 4:54:52 PM
	 */
	@RequestMapping("/roleCheckBox.action")
	public String getRoleCheckBox(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletRequest response) {
		logger.debug("RoleAction.getRoleCheckBox().........");
		request.setAttribute("userID", id);
		
		return "role/roleCheckBox"; 
	}
	
	
	/**
	 * 加载当前用户的角色
	* @Title: loadUsersAllotRoles 
	* @Description: 加载当前用户的角色 
	* @param @param pw
	* @param @param request
	* @param @param userId
	* @param @param response
	* @param @param rows
	* @param @param page
	* @param @param para
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Aug 12, 2013 11:19:34 AM
	 */
	@RequestMapping("/loadUsersAllotRoles.action")
	public String loadUsersAllotRoles(PrintWriter pw, HttpServletRequest request,String userId,
			HttpServletRequest response, final int rows, int page,
			RoleSearchPara para) {		
		logger.debug("RoleAction.loadUsersAllotRoles()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("select new map(t.id as id , t.roleName as roleName,u.accountName as accountName,u.userName as userName) from Role t left outer join t.user as u where u.id='"+userId+"'");
		hqlBuf.append(" and t.id not in('0','1','2','3')");
		if (!StringUtils.isBlank(para.getRoleName())) {
			para.setRoleName("%" + para.getRoleName() + "%");
			hqlBuf.append("  and t.roleName like:RoleName");
			
		}
		hqlBuf.append("  order by t.roleName desc  ");
		Paging paging = roleManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total",paging.getTotalPage());// 总页数
		map.put("page",   paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}
	
	
	
	/**
	 * 
	* @Title: loadRoleCheckBox 
	* @Description: 加载用户已经分配好的权限列表
	* @param @param pw
	* @param @param request
	* @param @param response
	* @param @param rows
	* @param @param page
	* @param @param para
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 26, 2013 10:41:31 AM
	 */
	@RequestMapping("/loadRoleCheckBox.action")
	public String loadRoleCheckBox(PrintWriter pw, HttpServletRequest request,
			HttpServletRequest response, final int rows, int page,
			RoleSearchPara para) {
		logger.debug("role load loadRoleCheckBox()....");
		String userID = request.getParameter("userID")==null?"": request.getParameter("userID");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("select new map(t.id as id , t.roleName as roleName,u.accountName as accountName,u.userName as userName) from Role t left outer join t.user as u where u.id='"+userID+"'");
		hqlBuf.append(" and t.id not in('0','1','2','3')");
		if (!StringUtils.isBlank(para.getRoleName())) {
			para.setRoleName("%" + para.getRoleName() + "%");
			hqlBuf.append("  and t.roleName like:RoleName");
			
		}
		hqlBuf.append("  order by t.roleName desc  ");
		Paging paging = roleManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total",paging.getTotalPage());// 总页数
		map.put("page",   paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}
	
	
	/**
	 *  删除当前用户中的选中的权限
	* @Title: delRoleToUser 
	* @Description: 删除用户选中的原有权限
	* @param @param pw
	* @param @param ids
	* @param @param userID
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 31, 2013 5:04:05 PM
	 */
	@RequestMapping("/delRoleToUser.action")
	public String delRoleToUser(PrintWriter pw, String ids,String userId) {
		logger.debug("RoleAction.delRoleToUser()");
		Map<String,Object> map = new HashMap<String,Object>();
		User user =null;
    	try {
			if(!StringUtils.isBlank(userId)){ 
				 user = userManager.get( userId );
			}
		    map.put("success", false);	
			List<Role> user_role = user.getRoles();
			for (int j = 0; j < user_role.size(); j++) {
				if(ids.indexOf(user_role.get(j).getId())>-1){
					user_role.remove(j);	
					j=j-1;
				}
		    }
				userManager.save(user);
				map.put("success", true);	
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除所选原有权限记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	/**
	 * 
	* @Title: getRoleCheckBoxList 
	* @Description: 加载列表
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 31, 2013 10:58:04 AM
	 */
	@RequestMapping("/getRoleCheckBoxList.action")
	public String getRoleCheckBoxList(HttpServletRequest request,String userId,
			HttpServletRequest response) {
		logger.debug("RoleAction.getRoleCheckBoxList()");
		request.setAttribute("userId", userId);
		return "role/roleCheckBoxList";
	}
	
	
	
	/**
	 * 保存分配的角色到相应的用户里面
	* @Title: saveUserToRole 
	* @Description: 保存分配的角色到相应的用户里面
	* @param @param request
	* @param @param pw
	* @param @param ids
	* @param @param userId     
	* @return void  
	* @throws 
	* @author liangrujian 
	* Jul 25, 2013 4:53:58 PM
	 */
	@RequestMapping("/saveUserToRole.action")
	public void saveUserToRole(HttpServletRequest request,PrintWriter pw, String ids,String userId) {
		logger.debug("RoleAction.saveUserToRole()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");	
            if(null==idArr){
            	map.put("success", false);
            	map.put("msg", "操作有误,保存失败!");
              }
            if(null!=userId){
            	User user = userManager.get(userId);       	
            	if(null==user){
            		map.put("success", false);
                	map.put("msg", "用户不存在!");
            	}else{
            		 List<Role> user_role = user.getRoles();
            		for(int index_i=0; index_i<idArr.length; index_i++){  
            			  boolean flag=false;
            			for(int i=0;i<user_role.size();i++){
            				if(idArr[index_i].equals(user_role.get(i).getId())){
            					flag = true;
            				}	         			
            			}
            			if(!flag){
        					Role role = roleManager.get( idArr[index_i] ); 
                			user.getRoles().add( role );
        				}  
            		}
            		userManager.save( user );
            		map.put("success", true);
            	}
            }   		
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("保存出现异常！", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
	}
	
	

	/**
	 * 
	* @Title: saveSysresourceForRole 
	* @Description: 保存当前选中的资源到角色中
	* @param @param request
	* @param @param pw
	* @param @param sysResourceIds
	* @param @param roleId     
	* @return void  
	* @throws 
	* @author liangrujian 
	* Aug 6, 2013 5:05:21 PM
	 */
	@RequestMapping("/saveSysresourceForRole.action")
	public void saveSysresourceForRole(HttpServletRequest request,PrintWriter pw, String sysResourceIds,String roleId) {
		logger.debug("saveSysresourceForRole() ....");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			String[] idArr = sysResourceIds.split(",");	
            if(null==idArr){
            	map.put("success", false);
            	map.put("msg", "操作有误,保存失败!");
              }
            if(!StringUtils.isBlank(roleId)){
            	Role role = roleManager.get(roleId);       	
            	if(null==role){
            		map.put("success", false);
                	map.put("msg", "该权限不存在!");
            	}else{
            		   List<SysResource> sysResourceList = role.getSysResources();
            		   role.getSysResources().clear();
            			for(int index_i=0; index_i<idArr.length; index_i++){  
               			    SysResource sysResource = sysresourceManager.get( idArr[index_i] ); 
                   			role.getSysResources().add(sysResource);
           				}  
            		   
            	}
            		roleManager.save( role );
            		map.put("success", true);
            	}  		
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("保存出现异常！..", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
	}
	
	
	
	

	/**
	 * 保存当前添加到角色中的用户记录 
	* @Title: saveRoleForUsers 
	* @Description: 保存当前添加到角色中的用户记录 
	* @param @param pw
	* @param @param roleId
	* @param @param ids
	* @param @param request
	* @param @throws ParseException     
	* @return void  
	* @throws 
	* @author liangrujian 
	* Aug 9, 2013 1:50:19 PM
	 */
	@RequestMapping("/saveRoleForUsers.action")
	public void saveRoleForUsers(PrintWriter pw, String roleId,String ids,HttpServletRequest request) throws ParseException {
		logger.debug("JobsAction.saveRoleForUsers()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false); 
		try {
		 if(!StringUtils.isBlank(roleId)){
			 Role role = roleManager.get( roleId );
			  List<User> userList =  role.getUser();
			   	if(!StringUtils.isBlank(ids)){
					  String []userIds  =	ids.split(",");
					  for(int index=0;index<userIds.length;index++){
						  boolean flag=false;
	            			for(User u : userList){
	            				if(userIds[index].equals(u.getId())){
	            					flag = true;
	            				}	         			
	            			}
	            			if(!flag){
								  User user = userManager.get( userIds[ index ] );
								   role.getUser().add( user );
	            		 }
					  }	  
					  roleManager.save( role );
					  map.put("success", true); 
			     }
		 }else{
				map.put("success", false);
				map.put("msg", "职务不存在!");
			}
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error(e);
		}
		pw.print(JSONObject.fromObject(map).toString());

	}
	
	
	/**
	 * 删除当前角色原有的选中的用户记录
	* @Title: deleteRoleForUsers 
	* @Description: 删除当前角色原有的选中的用户记录
	* @param @param pw
	* @param @param ids
	* @param @param roleId
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Aug 9, 2013 1:56:50 PM
	 */
	@RequestMapping("/deleteRoleForUsers.action")
	public String deleteRoleForUsers(PrintWriter pw, String ids,String roleId) {
		logger.debug("RoleAction.deleteRoleForUsers()");
		Map<String,Object> map = new HashMap<String,Object>();
		Role role =null;
    	try {
			if(!StringUtils.isBlank(roleId)){ 
				role = roleManager.get( roleId );
			}
		    map.put("success", false);	
			List<User> userList = role.getUser();
			for (int j = 0; j < userList.size(); j++) {
				if(ids.indexOf(userList.get(j).getId())>-1){
					userList.remove(j);	
					j=j-1;
				}
		    }
				roleManager.save( role );
				map.put("success", true);	
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除当前角色原有的选中的用户记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	
	
	/**
	 * 
	* @Title: isExists 
	* @Description: 验证是否已经存在权限名称
	* @param @param role
	* @param @param pw
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 26, 2013 2:24:49 PM
	 */
	@RequestMapping("/isExists.action")
	public String isExists(Role role, PrintWriter pw) {
		logger.debug("RoleAction.isExists()...");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", true);
		List<Role> list;
		try {
			list = roleManager.isExtists(role,role.getId()!=null);
			if ( list==null || list.size() == 0 || list.isEmpty()) {
				map.put("success", "1");
			}else{
				map.put("success", false);
			}
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("isExists：", e);
		}
		
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}





		
	public RoleManager getRoleManager() {
		return roleManager;
	}


	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	
	/**
	 * VO by 搜索
	 * 
	 * @author liangrujian
	 * 
	 */
	public static class RoleSearchPara {

		private String roleName;

		public String getRoleName() {
			return roleName;
		}

		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}

		

	}


	public UserManager getUserManager() {
		return userManager;
	}


	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}


	public SysResourceManager getSysresourceManager() {
		return sysresourceManager;
	}


	public void setSysresourceManager(SysResourceManager sysresourceManager) {
		this.sysresourceManager = sysresourceManager;
	}


}