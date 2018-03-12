package com.sunit.sysmanager.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.util.transport.Request;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitBeanUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.Depart;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.DepartManager;
import com.sunit.sysmanager.service.SysResourceManager;
import com.sunit.sysmanager.service.UserManager;
/**
 * 
 * 
 * 类名称：DepartAction
 * 类描述：部门管理的增删查改
 * 创建人：liangrujian
 * 创建时间：Jul 22, 2013 2:39:05 PM
 * 修改人：liangrujian
 * 修改时间：Jul 22, 2013 2:39:05 PM
 * 修改备注：
 * @version 
 *
 */
@Controller
@RequestMapping("/dept")
public class DepartAction extends BaseAction {

	static Logger logger = Logger.getLogger(DepartAction.class);
	
	@Autowired
	private DepartManager departManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private SysResourceManager sysresourceManager;
	
	/**
	 * 部门list页面数据提供者
	* @Title: loadDeptListDataGrid 
	* @Description: 部门list页面数据提供者
	* @param @param pw 打印流参数pw
	* @param @param request
	* @param @param response
	* @param @param rows 列表显示数据 行数 设置参数rows
	* @param @param page 列表显示数据 总页数 设置参数page
	* @param @param para 搜索类参数para 
	* @param @return     
	* @return String  返回类型
	* @throws 
	* @author 梁汝健
	* Mar 10, 2014 10:39:07 AM
	*/
	@SuppressWarnings("unchecked")
	@RequestMapping("/loadDeptListDataGrid.action")
	public String loadDeptListDataGrid(PrintWriter pw, HttpServletRequest request,
			HttpServletRequest response, final int rows, int page,
			DepartSearchPara para) {
		logger.debug("DepartAction.loadDeptListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("select new map(t.id as id ,t.deptno as deptno, t.deptname as deptname,t.location as location,t.note as note,t.isenabled as isenabled,t.sources as sources,pd.deptname as parentDeptName) from Depart t left outer join t.parentDepart pd where t.flag='1'");
		if (!StringUtils.isBlank(para.getDeptNo())) {
			para.setDeptNo("%" + para.getDeptNo() + "%");
			hqlBuf.append("  and t.deptno like:DeptNo");
			
		}
		if (!StringUtils.isBlank(para.getDepartName())) {
			para.setDepartName("%" + para.getDepartName() + "%");
			hqlBuf.append("  and t.deptname like:DepartName");
			 
		}
		if (!StringUtils.isBlank(para.getDepartLocation())) {
			para.setDepartLocation("%" + para.getDepartLocation() + "%");
			hqlBuf.append("  and t.location like:DepartLocation");
			
		}
		if (!StringUtils.isBlank(para.getSources())) {
			para.setSources("%" + para.getSources() + "%");
			hqlBuf.append("  and t.sources like:Sources");
			
		}
	
		hqlBuf.append(" order by t.deptno");
		Paging paging = departManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total",paging.getTotalPage());// 总页数
		map.put("page",  paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	
	/**
	 * 
	* @Title: updateDepart 
	* @Description: 修改部门信息记录经过的 Action
	* @param @param pw
	* @param @param id
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 22, 2013 2:52:16 PM
	 */
	@RequestMapping("/deptUpdate.action")
	public String deptUpdate(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletRequest response) {
		
		//request.setAttribute("TABLE_ID", id);
		logger.debug("DepartAction.deptUpdate()");
		String url="";
		if(null!=id){
			Depart depart = departManager.get(id);
			request.setAttribute("depart", depart);
			List<Depart> depart_List = departManager.getAll();
			request.setAttribute("depart_List", depart_List);
			url="dept/deptUpdate";
		}
		return url;
	}

	
	
	/**
	 * 删除部门记录信息
	* @Title: deptDelete 
	* @Description: 删除部门记录信息
	* @param @param pw 打印流pw
	* @param @param ids 要删除的部门记录id集合
	* @param @return     
	* @return String  返回类型
	* @throws 
	* @author 梁汝健
	* Mar 12, 2014 10:31:40 AM
	 */
	@RequestMapping("/deptDelete.action")
	public String deptDelete(PrintWriter pw, String ids) {
		logger.debug("DepartAction.deptDelete()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(","); 
			SunitStringUtil util = new SunitStringUtil();
			String str = util.getStringUtil( idArr );
			Integer integer = departManager.executeUpdate("update Depart t set t.flag='0' where t.id in("+str+")",idArr);
			if(integer.intValue()>0){
				map.put("success", true);
			}else{
			    map.put("success", false);
			    map.put("msg", "记录不存在");
			}
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除部门记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	
	
	/**
	 * 保存部门记录信息
	* @Title: deptSave 
	* @Description: 保存部门记录信息
	* @param @param pw 打印流pw
	* @param @param depart 当前部门实体
	* @param @param id 部门记录id
	* @param @param parentDepartStr 上级部门id
	* @param @param request
	* @param @return
	* @param @throws ParseException     
	* @return String  返回类型
	* @throws 
	* @author 梁汝健
	* Mar 12, 2014 10:01:06 AM
	 */
	@RequestMapping("/deptSave.action")
	public String deptSave(PrintWriter pw, Depart depart,String id,String parentDepartStr , HttpServletRequest request)
			throws ParseException {
		logger.debug("DepartAction.deptSave()");
		String actionErrType = "";
		String deptID="";
		if (!StringUtils.isBlank(depart.getId())) {
			actionErrType = "修改部门记录";
		} else { 
			actionErrType = "新增部门记录";
			depart.setSources("0");
			depart.setFlag("1");//1表示记录有0效 0表示记录无效
		}

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			if(!StringUtils.isBlank(parentDepartStr) && !"0".equals(parentDepartStr) ){
				depart.setParentDepart( departManager.get( parentDepartStr ) );
			}	
			if("0".equals(parentDepartStr)){
				Depart d = new Depart();
				d.setId("0");
				depart.setParentDepart(d);
			}
			Depart dept=null;
			if(!StringUtils.isBlank(depart.getId())){
				dept =departManager.get(depart.getId());	
				depart.setUsers( dept.getUsers());
				SunitBeanUtils.copyProperties(depart,dept); //form 表单对象 depart 覆盖数据库中数据对象 dept
				departManager.save(dept);
			}else{
				departManager.save(depart);
				if(!"0".equals(depart.getParentDepart().getId())){
					deptID = depart.getParentDepart().getDeptRefCode()+"_"+depart.getId();
				}else{
					deptID = depart.getId();
				}
				departManager.executeUpdate("update Depart t set t.deptRefCode=? where t.id =?",deptID,depart.getId());
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
	 * 点击查看详细信息
	* @Title: showDeptDetails 
	* @Description: 点击查看详细信息
	* @param @param pw 打印流
	* @param @param request
	* @param @param deptId 当前部门id
	* @param @param response
	* @param @return     
	* @return String  返回类型
	* @throws 
	* @author 梁汝健
	* Mar 12, 2014 10:35:51 AM
	 */
	@RequestMapping("/showDeptDetails.action")
	public String showDeptDetails(PrintWriter pw, HttpServletRequest request,String deptId,
			HttpServletRequest response) {
		logger.debug("DepartAction.showDeptDetails()");
		Depart dept =null;
		if(!StringUtils.isBlank( deptId )){
			dept = departManager.get( deptId );
		}
		request.setAttribute("depart", dept);
		return "dept/showDeptDetailsView";
	}
	
	
	/**
	 * 保存用户到当前部门中
	* @Title: allocateUserForDept 
	* @Description: 保存用户到当前部门中
	* @param @param pw 打印流pw
	* @param @param deptId 当前部门id
	* @param @param ids 被选中加入用户记录id集合
	* @param @param request
	* @param @throws ParseException     
	* @return void   返回类型
	* @throws 
	* @author 梁汝健
	* Mar 12, 2014 1:44:56 PM
	 */
	@RequestMapping("/allocateUserForDept.action")
	public void allocateUserForDept(PrintWriter pw, String deptId,String ids,HttpServletRequest request) throws ParseException {
		logger.debug("DepartAction.allocateUserForDept()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false); 
		try {
		 if(!StringUtils.isBlank(deptId)){
			 Depart dept = departManager.get( deptId );
			  Set<User> userSet =  dept.getUsers();
			   	if(!StringUtils.isBlank(ids)){
					  String []userIds  =	ids.split(",");
					  for(int index=0;index<userIds.length;index++){
						  boolean flag=false;
	            			for(User u : userSet){
	            				if(userIds[index].equals(u.getId())){
	            					flag = true;
	            				}	         			
	            			}
	            			if(!flag){
								  User user = userManager.get( userIds[ index ] );
								   dept.getUsers().add(user);	
	            		 }
					  }	  
					  departManager.save(dept);
					  map.put("success", true); 
			     }
		 }else{
				map.put("success", false);
				map.put("msg", "部门不存在!");
			}
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error(e);
		}
		pw.print(JSONObject.fromObject(map).toString());

	}
	
	
	/**
	 * 删除部门中选中的用户记录
	* @Title: userForDeptDel 
	* @Description: 删除部门中选中的用户记录
	* @param @param pw 打印流pw
	* @param @param ids 删除部门记录
	* @param @param deptID 当前部门id 
	* @param @return     
	* @return String  返回类型
	* @throws 
	* @author 梁汝健
	* Mar 12, 2014 1:49:45 PM
	 */
	@RequestMapping("/userForDeptDel.action")
	public String userForDeptDel(PrintWriter pw, String ids,String deptID) {
		logger.debug("DepartAction.userForDeptDel()");
		Map<String,Object> map = new HashMap<String,Object>();
		Depart dept =null;
    	try {
			if(!StringUtils.isBlank(deptID)){ 
				 dept = departManager.get( deptID );
			}
			 map.put("success", false);	
			Set<User> userSet = dept.getUsers();
			for(Iterator iterator = userSet.iterator(); iterator.hasNext();) {
					User user = (User) iterator.next();
					if(ids.indexOf(user.getId())>-1){ 
						iterator.remove();
					}	
			 }
				departManager.save( dept );
				map.put("success", true);	
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除部门中选中的用户记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	

	/**
	 * 加载所有部门列表，获得选中部门。
	* @Title: getParentDepartSelId 
	* @Description: 加载所有部门列表，获得选中部门。
	* @param @param pw
	* @param @param id
	* @param @param request
	* @param @param response     
	* @return void  
	* @throws 
	* @author liangrujian 
	* Aug 13, 2013 3:29:16 PM
	 */
	@RequestMapping("/getParentDepartSelId.action")
	public void getParentDepartSelId(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletRequest response) {
		logger.debug("DepartAction.getParentDepartSelId()");
		Map<String,Object> map = new HashMap<String,Object>();
        List deptList = departManager.find("select t.id as deptId, t.deptname as deptname from Depart t where t.flag='1' order by t.deptname ");
        if(null!=deptList && !deptList.isEmpty() && deptList.size()>0){
        	Map<String,Object> itemMap = null;
    		itemMap = new HashMap<String,Object>();
    		List<Map<String,Object>> lst = new ArrayList<Map<String,Object>>();
    		Object[] obj = null;
    		String var_deptId = "";
    		for(int i=0; i<deptList.size(); i++){
    			itemMap = new HashMap<String,Object>();
    			obj = (Object[])deptList.get(i);
    			var_deptId = obj[0]==null?"":obj[0].toString();
    			itemMap.put("id", var_deptId);
    			itemMap.put("name", obj[1]==null?"":obj[1].toString());
    			lst.add(itemMap);	
    		}
    		map.put("list", lst);
        }
        map.put("success", true);
        pw.print(JSONObject.fromObject(map).toString());
		
	}
	
	/**
	 * 上级部门select框数据提供者
	* @Title: getDeptParentSelectBody 
	* @Description: 上级部门select框数据提供者
	* @param @param pw 打印流pw
	* @param @param id 
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String   返回类型
	* @throws 
	* @author 梁汝健
	* Mar 12, 2014 9:33:51 AM
	 */
	@RequestMapping("/getDeptParentSelectBody.action")
	public String getDeptParentSelectBody(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("DepartAction.getDeptParentSelectBody()");
		List<Depart> list=departManager.find("from Depart t where t.flag='1'") ; 
		JsonConfig jc = new JsonConfig();
		jc.setJsonPropertyFilter(new PropertyFilter(){
			public boolean apply(Object arg0, String arg1, Object arg2) {
				if(arg1.equals("id")||arg1.equals("deptname")||arg1.equals("list")||arg1.equals("success"))
					return false;
				else
					return true;
			}
		});
		Map map = new HashMap();
		map.put("success", true);
		map.put("list",list);
		String s =JSONObject.fromObject(map,jc).toString();
		pw.print(s);
		return null;
	}
	
		/**
		 * 
		* @Title: isExists 
		* @Description: 验证部门是否已经存在 
		* @param @param user
		* @param @param pw
		* @param @return     
		* @return String  
		* @throws 
		* @author liangrujian 
		* Jul 24, 2013 10:26:34 AM
		 */
	@RequestMapping("/isExists.action")
	public String isExists(Depart depart, PrintWriter pw) {
		logger.debug("DepartAction.isExists()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		depart.setFlag("1");
		List<Depart> list;
		try {
			list = departManager.isExtists(depart,depart.getId()!=null);
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

		

		public DepartManager getDepartManager() {
			return departManager;
		}


		public void setDepartManager(DepartManager departManager) {
			this.departManager = departManager;
		}

		
		
		
		public SysResourceManager getSysresourceManager() {
			return sysresourceManager;
		}


		public void setSysresourceManager(SysResourceManager sysresourceManager) {
			this.sysresourceManager = sysresourceManager;
		}


		/**
		 * 
		* @Title: loadChildren 
		* @Description: 部门树的数据提供者
		* @param @param pw
		* @param @param id     
		* @return void  
		* @throws 
		* @author joye 
		* Aug 1, 2013 3:06:37 PM 
		 */
		@RequestMapping("/loadChildren.action") 
		public void loadChildren(PrintWriter pw, String id,HttpServletRequest request) {
			logger.debug("DepartAction.getTreeMenu()");
			String reString;
			if (!StringUtils.isBlank(id))
				if (id.equals("root")) {
					List<Depart> list = null;
						/**
						 * 原始读取部门树结构
						 */
						list = departManager
								.find(
										"from Depart where ( parentid=? or parentid=null )  and flag=? order by deptname desc",
										"0", "1");

					JsonConfig jc = new JsonConfig();
					jc.setJsonPropertyFilter(new PropertyFilter() {
						public boolean apply(Object source, String name,
								Object value) {
							if (name.equals("deptname") || name.equals("id"))
								return false;
							else
								return true;
						}
					});
					reString = JSONArray.fromObject(list, jc).toString();
					pw.write(reString);
				} else {

					JsonConfig jc = new JsonConfig();
					jc.setJsonPropertyFilter(new PropertyFilter() {
						public boolean apply(Object source, String name,
								Object value) {
							if (name.equals("deptname") || name.equals("userName")||name.equals("deptno")
									|| name.equals("id"))
								return false;
							else
								return true;
						}
					});

					Depart depart = departManager.get(id);
					Set<User> userSet = depart.getUsers();

					List list = new ArrayList();
					for (User u : userSet) {
						if ("1".equals(u.getFlag()) && "0".equals(u.getIsOpen())
								&& !"0".equals(u.getId())) {
									list.add(u);

						}
					}
					
						List<Depart> childDepartList = depart.getChildDepart();
						for (Depart d : childDepartList) {
							if ("1".equals(d.getFlag())) {
								list.add(d);
							}
						}
					reString = JSONArray.fromObject(list, jc).toString();
					pw.write(reString);
				}
		}

		
	/***
	 * 部门树的数据提供者 OnlyDepart
	* @Title: getTreeMenuOnlyDepart 
	* @Description:  部门树的数据提供者 OnlyDepart
	* @param @param pw
	* @param @param id     
	* @return void  
	* @throws 
	* @author joye 
	* Aug 8, 2013 11:05:49 AM
	 */
		@RequestMapping("/loadChildrenOnlyDepart.action")
		public void getTreeMenuOnlyDepart(PrintWriter pw, String id) {
			logger.debug("DepartAction.getTreeMenuOnlyDepart()");
			String reString; 
			if(!StringUtils.isBlank(id))
			if (id.equals("root")) {
				List<Depart> list =departManager.find("from Depart where parentid=? and flag=? order by deptname desc", "0","1");	 
				JsonConfig jc =new JsonConfig();
				jc.setJsonPropertyFilter(new PropertyFilter(){
					public boolean apply(Object source, String name,
							Object value) { 
						if(name.equals("deptname")||name.equals("id")) 
							return false;
						else
							return true;  
					}
				}); 
				reString =JSONArray.fromObject(list,jc).toString();
				pw.write(reString);
			} else { 
				
				JsonConfig jc =new JsonConfig();
				jc.setJsonPropertyFilter(new PropertyFilter(){
					public boolean apply(Object source, String name,
							Object value) { 
						if(name.equals("deptname")||name.equals("userName")||name.equals("id"))
							return false;
						else 
							return true;  
					}
				}); 
				
				Depart depart =departManager.get(id);
				List<Depart> childDepartList = depart.getChildDepart();
				List list =new ArrayList(); 
				for (Depart d : childDepartList) { 
					if("1".equals(d.getFlag())){ 
						list.add(d); 
					}	
				} 
				reString =JSONArray.fromObject(list,jc).toString();
				pw.write(reString); 
			} 
		}
		
		
		
		
		/***
		 * 获得菜单跟节点数据提供者。
		* @Title: getTreeMenuRootNode 
		* @Description: 获得菜单跟节点数据提供者。
		* @param @param pw
		* @param @param id     
		* @return void  
		* @throws 
		* @author 梁汝健
		* 2014-4-2 上午11:21:21
		 */
			@RequestMapping("/getTreeMenuRootNode.action")
			public void getTreeMenuRootNode(PrintWriter pw, String id,HttpServletRequest request ) {
				logger.debug("DepartAction.getTreeMenuRootNode()");
				String reString; 
				String str = request.getParameter("treeType")==null?"":request.getParameter("treeType");
//				if(!StringUtils.isBlank(id))
				if (id.equals("root")) {
					List<SysResource> list =sysresourceManager.find("from SysResource where parent=? and sourceType<>? order by moduleCaption desc", "0","4");	 
					
					JsonConfig jc =new JsonConfig();
					jc.setJsonPropertyFilter(new PropertyFilter(){
						public boolean apply(Object source, String name,
								Object value) { 
							if(name.equals("moduleCaption")||name.equals("id")||name.equals("sourceType")) 
								return false;
							else
								return true;  
						}
					}); 
					reString =JSONArray.fromObject(list,jc).toString();
					pw.write(reString); 
					
				} else if(str.equals("menuNode")) { 
				
					JsonConfig jc =new JsonConfig();
					jc.setJsonPropertyFilter(new PropertyFilter(){
						public boolean apply(Object source, String name,
								Object value) { 
							if(name.equals("moduleCaption")||name.equals("id")||name.equals("sourceType"))
								return false;
							else 
								return true;  
						}
					}); 
					
//					SysResource sysResource = sysresourceManager.get(id);
					List<SysResource> subSysSysResource =sysresourceManager.find("from SysResource t where t.parent=? and t.sourceType=?", id,"3");
					List list =new ArrayList(); 
					for (SysResource s : subSysSysResource) { 
							list.add(s); 
					} 
					reString =JSONArray.fromObject(list,jc).toString();
					pw.write(reString); 
				
			}
			}
			
			
			

			/***
			 *  获得资源树
			* @Title: getTreeMenuOnlySysResource 
			* @Description: 
			* @param @param pw
			* @param @param id     
			* @return void  
			* @throws 
			* @author 梁汝健
			* 2014-6-24 下午05:29:47
			 */
			@RequestMapping("/loadChildrenSysResource.action")
			public void getTreeMenuOnlySysResource(PrintWriter pw, String id) {
				logger.debug("DepartAction.getTreeMenuOnlySysResource()");
				String reString;
				if (!StringUtils.isBlank(id))
					if (id.equals("root")) {
						List<SysResource> list = sysresourceManager
								.find(
										"from SysResource where parent=? and sourceType=? order by moduleCaption desc",
										"0","2"); 
						JsonConfig jc = new JsonConfig();
						jc.setJsonPropertyFilter(new PropertyFilter() {
							public boolean apply(Object source, String name,
									Object value) {
								if (name.equals("moduleCaption") || name.equals("id"))
									return false;
								else
									return true;
							}
						});
						reString = JSONArray.fromObject(list, jc).toString();
						pw.write(reString);
					} else {

						JsonConfig jc = new JsonConfig();
						jc.setJsonPropertyFilter(new PropertyFilter() {
							public boolean apply(Object source, String name,
									Object value) {
								if (name.equals("moduleCaption") || name.equals("moduleCaption")
										|| name.equals("id"))
									return false;
								else
									return true;
							}
						});

						SysResource sysResource = sysresourceManager.get(id);
						List<SysResource> childSysResourceList =sysresourceManager.find("from SysResource where parent=?", id) ;
						List list = new ArrayList();
						for (SysResource srs : childSysResourceList) {
								list.add(srs);
						}
						reString = JSONArray.fromObject(list, jc).toString();
						pw.write(reString);
					}
			}
			
			
			/***
			 * 提供获得除根菜单资源和类型4资源外的其他所有资源
			* @Title: getTreeAllSysResource 
			* @Description: 
			* @param @param pw
			* @param @param id     
			* @return void  
			* @throws 
			* @author 梁汝健
			* 2014-6-24 下午05:30:15
			 */
			@RequestMapping("/getTreeAllSysResource.action")
			public void getTreeAllSysResource(PrintWriter pw, String id) {
				logger.debug("DepartAction.getTreeAllSysResource()");
				String reString;
				if (!StringUtils.isBlank(id))
					if (id.equals("root")) {
						List<SysResource> list = sysresourceManager
								.find(
										"from SysResource where sourceType not in (?,?) order by moduleCaption desc","2","4");
						JsonConfig jc = new JsonConfig();
						jc.setJsonPropertyFilter(new PropertyFilter() {
							public boolean apply(Object source, String name,
									Object value) {
								if (name.equals("moduleCaption") || name.equals("id"))
									return false;
								else
									return true;
							}
						});
						reString = JSONArray.fromObject(list, jc).toString();
						pw.write(reString);
					} else {

						JsonConfig jc = new JsonConfig();
						jc.setJsonPropertyFilter(new PropertyFilter() {
							public boolean apply(Object source, String name,
									Object value) {
								if (name.equals("moduleCaption") || name.equals("moduleCaption")
										|| name.equals("id"))
									return false;
								else
									return true;
							}
						});

//						SysResource sysResource = sysresourceManager.get(id);
//						List<SysResource> childSysResourceList =sysresourceManager.find("from SysResource where parent=?", id) ;
//						List list = new ArrayList();
//						for (SysResource srs : childSysResourceList) {
//								list.add(srs);
//						}
//						reString = JSONArray.fromObject(list, jc).toString();
//						pw.write(reString);
					}
			}
			

			
			
	/**
	 * VO by 搜索
	 * 
	 * @author liangrujian
	 * 
	 */
	public static class DepartSearchPara {

		private String deptNo;
		
		private String departName;

		private String departLocation;
		
		private String sources;

		public String getDepartName() {
			return departName;
		}

		public void setDepartName(String departName) {
			this.departName = departName;
		}

		public String getDeptNo() {
			return deptNo;
		}

		public void setDeptNo(String deptNo) {
			this.deptNo = deptNo;
		}

		public String getDepartLocation() {
			return departLocation;
		}

		public void setDepartLocation(String departLocation) {
			this.departLocation = departLocation;
		}

		public String getSources() {
			return sources;
		}

		public void setSources(String sources) {
			this.sources = sources;
		}
		

	}
	
}