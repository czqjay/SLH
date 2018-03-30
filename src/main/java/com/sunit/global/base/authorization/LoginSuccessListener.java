package com.sunit.global.base.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;

import com.sunit.global.singleLogin.ClusterSingleLoginManager;
import com.sunit.global.singleLogin.po.ClusterSingleLogin;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.date.DateUtil;
import com.sunit.operator.po.Operator;
import com.sunit.operator.service.OperatorManager;
import com.sunit.sysmanager.po.Depart;
import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.RoleManager;
import com.sunit.sysmanager.service.SysResourceManager;
import com.sunit.sysmanager.service.UserManager;

@Component
public class LoginSuccessListener implements
		ApplicationListener<InteractiveAuthenticationSuccessEvent> {
	static Logger logger = Logger.getLogger(LoginSuccessListener.class);

	@Autowired
	UserManager um;
	@Autowired 
	OperatorManager opm; 
	@Autowired
	SysResourceManager sm;
	@Autowired
	RoleManager rm;
	
	@SuppressWarnings("unchecked")
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent arg0) {
		logger.debug("LoginSuccessListener.onApplicationEvent()");
		User u = new User();
		List allResourceList = new ArrayList(); // 所有可访问内容 
		List allButtonResourceList = new ArrayList(); // 所有可访问按钮
		List<Map> planManagerRootMeunList = new ArrayList(); // 计划管理菜单根节点
		List<Map> systemManagerRootMeunList = new ArrayList(); // 系统管理菜单根节点
		
		List<SysResource> accordionList=new ArrayList();  //首页可折叠菜单的数据源
		
		u.setAccountName(arg0.getAuthentication().getName());
		u.setFlag("1");
		List list = um.findByExample(u);
		if (list != null && list.size() > 0) {
			u = (User) list.get(0);
			List<Role> roleList = u.getRoles();
			/**
			 *超级管理员不受角色数据的限制 
			 */
			boolean isAdmin= u.getId().equals("0");			
			if(roleList.isEmpty() && isAdmin){
				roleList.add(new Role());   //给超级管理员一个虚拟角色
				
			} 
			
			for (int i = 0; i < roleList.size(); i++) {
				Role role =roleList.get(i);
				List<SysResource> resourceslist =null;
				if(isAdmin){ 
					roleList.clear(); 
//					resourceslist=sm.getAll();
					resourceslist = sm.find("from SysResource order by orderNum  ");
				} else{
					resourceslist = role.getSysResources(); 
				} 
				for (SysResource sysResource : resourceslist) {
					allResourceList.add(sysResource.getContent());
					if(!(u.getId().equals("0"))){  
						//读取子资源 
						List subList=new ArrayList();
						subList=sm.find("select re.content from SysResource re where re.id in (select item.subordinateResource from  SysResourceItem item where  item.superiorResource =?)",sysResource.getId());
						//List subList=um.excuteSQL("select re.content from  tb_sysresource re where  id in  (select item.subordinateresource  from tb_sysresource_item item  where  item.superiorresource='"+sysResource.getId()+"')");
//						List subList2 =sysResource.getSysResourceItem(); 
						allResourceList.addAll(subList);  
					}
					allButtonResourceList.add(sysResource.getCode());
					if (sysResource.getSourceType().equals("2")) { // 是否菜单树根节点类型
						if(sysResource.getParent().equals("0")) { // 顶级根节点
							accordionList.add(sysResource);
						}
						boolean isRepeatMenu = false; // 是否存在相同的节点
						for (Map m : planManagerRootMeunList) {
							if (m.containsValue(sysResource.getId()))
								isRepeatMenu = true;
						}
						for (Map m : systemManagerRootMeunList) {
							if (m.containsValue(sysResource.getId()))
								isRepeatMenu = true;
						}
						if (isRepeatMenu)
							continue;
						if (sysResource.getCode().startsWith("authorization")
								|| sysResource.getCode().startsWith("user")
								|| sysResource.getCode().startsWith("admin")
								|| sysResource.getCode().startsWith("sys_") 
								
						) {
							    
							Map map = new HashMap();
							map.put("id", sysResource.getId());
							map.put("caption", sysResource.getModuleCaption());
							systemManagerRootMeunList.add(map);
						} else {
							Map map = new HashMap();
							map.put("id", sysResource.getId());
							map.put("caption", sysResource.getModuleCaption());
							planManagerRootMeunList.add(map);
						}
					}
				}
			}
		}
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
		.getSession().setAttribute("accordionList",
				accordionList);
		
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("systemManagerRootMeunList",
						systemManagerRootMeunList);
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("planManagerRootMeunList",
						planManagerRootMeunList);
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("authorityButtonList",
						allButtonResourceList);
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("authorityList", allResourceList);
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("userId", u.getId());
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("userName", u.getUserName());
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
		.getSession().setAttribute("userAccount", u.getAccountName());
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest() 
				.getSession().setAttribute("user", u);
		Hibernate.initialize(u.getDeparts());
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute("departs", u.getDeparts());
		
		
		
		List departIdList = new ArrayList();
		for (Depart depart : u.getDeparts()) {
			departIdList.add(depart.getId());
		}
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().setAttribute(
						"departIds",
						Arrays.toString(departIdList.toArray()).replaceAll(" ",
								"").replaceAll("\\[|\\]", "'").replaceAll(",",
								"','"));
		
		departIdList.clear();
		for (Depart depart : u.getDeparts()) {
			departIdList.add(depart.getDeptname());
		}
		((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
		.getSession().setAttribute(
				"departNames",
				Arrays.toString(departIdList.toArray()).replaceAll(" ",
						"").replaceAll("\\[|\\]", ""));
		
		
		Operator operator = new Operator();
		operator.setUserId(u.getId());
		operator.setOperatortime(DateUtil.getCurrentTime());
		operator.setUserName(u.getUserName());
		operator.setUserOperator("登录系统");
		operator.setOperatorState("0");
		operator.setHostIp(((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest().getRemoteAddr());
		operator.setAccountName(u.getAccountName());
		opm.save(operator);
		
		
		
		// 集群环境下的单一登录
        ClusterSingleLoginManager  cslmpl =SpringContextUtils.getSpringContext().getBean(ClusterSingleLoginManager.class);
        ClusterSingleLogin csl = new ClusterSingleLogin();  
        csl.setAccount(arg0.getAuthentication().getName());
        List<ClusterSingleLogin> clsList =cslmpl.findByExample(csl);  
        if(!clsList.isEmpty()){
        	csl =clsList.get(0);  
        }
        csl.setSessionId(((SecurityContextImpl) SecurityContextHolder.getContext()).getRequest()
				.getSession().getId()); 
        csl.setTryCount(0); 
        cslmpl.save(csl);  
		
		
		
		

	}

	
	
}
