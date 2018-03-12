package com.sunit.sysmanager.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.deser.std.StdDeserializer.LongDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitBeanUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.service.RoleManager;
import com.sunit.sysmanager.service.SysResourceManager;
/**
 * 资源管理
 * 
 * 类名称：SysResourceAction
 * 类描述：
 * 创建人：liangrujian
 * 创建时间：Jul 10, 2013 4:54:16 PM
 * 修改人：liangrujian
 * 修改时间：Jul 10, 2013 4:54:16 PM
 * 修改备注： 
 * @version 
 *
 */
@Controller
@RequestMapping("/sysresource")
public class SysResourceAction extends BaseAction{

	static Logger logger = Logger.getLogger(SysResourceAction.class);
	
	@Autowired
	private SysResourceManager sysresourceManager;
	
	@Autowired
	private RoleManager roleManager;
	
	/**
	 * 加载数据
	* @Title: getPlanList 
	* @Description: 
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 10, 2013 4:15:34 PM
	 */ 
	@RequestMapping("/getSysResourceList.action")
	public String getSysResourceList(HttpServletRequest request,
			HttpServletRequest response) {
		logger.debug("SysResourceAction.getPlanList()");
		return "sysresource/sysResourceList";
	}
	
	
	/**
	 * 显示资源记录数据到页面上。
	* @Title: loadSysResourceListDataGrid 
	* @Description: 
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
	* Jul 10, 2013 4:55:16 PM
	 */
	
	@RequestMapping("/loadSysResourceListDataGrid.action")
	public String loadSysResourceListDataGrid(PrintWriter pw, HttpServletRequest request,
			HttpServletRequest response, final int rows, int page,
			SysSearchPara para) {
		logger.debug("SysResourceAction.loadSysResourceListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("select new map(id as id , content as content , parent as parent, moduleCaption as moduleCaption,caption as caption ,code as code) from SysResource  where 1=1");
		if (!StringUtils.isBlank(para.getSysName())) {
			para.setSysName("%" + para.getSysName() + "%");
			hqlBuf.append("  and moduleCaption like:SysName");	
		}
		if (!StringUtils.isBlank(para.getSysResourceCaption())) {
			para.setSysResourceCaption("%" + para.getSysResourceCaption() + "%");
			hqlBuf.append("  and caption like:SysResourceCaption");	
		}
		if (!StringUtils.isBlank(para.getSysResourceCode())) {
			para.setSysResourceCode("%" + para.getSysResourceCode() + "%");
			hqlBuf.append("  and code like:SysResourceCode");	
		}
		if (!StringUtils.isBlank(para.getSysResourceContent())) {
			para.setSysResourceContent("%" + para.getSysResourceContent() + "%");
			hqlBuf.append("  and content like:SysResourceContent");	
		}
		hqlBuf.append("  order by moduleCaption  ");
		Paging paging = sysresourceManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total",paging.getTotalPage());// 总页数
		map.put("page",  page);// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}
	

	
	
	/**
	 * 删除资源
	* @Title: delSysResource 
	* @Description: 
	* @param @param pw
	* @param @param ids
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 11, 2013 8:58:35 AM
	 */
	
	@RequestMapping("/delSysResource.action")
	public String delSysResource(PrintWriter pw, String ids) {
		logger.debug("SysResourceAction.delSysResource()");
		Map map = new HashMap();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			sysresourceManager.delete(idArr);
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除资源记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	/**
	 * 
	* @Title: sysResourceAdd 
	* @Description: 增加资源记录时经过的Action
	* @param @param pw
	* @param @param id
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 30, 2013 5:35:19 PM
	 */
	@RequestMapping("/sysResourceAdd.action")
	public String sysResourceAdd(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletRequest response) {
		logger.debug("SysResourceAction.sysResourceAdd()");
		List<SysResource> sysResource_list = sysresourceManager.getAll(); //加载所有资源信息
		request.setAttribute("sysResource_list", sysResource_list);
		return "sysresource/sysResourceAdd";
	}
	
	/**
	 *  修改页面经过的 Action
	* @Title: setupTableMuster 
	* @Description: 
	* @param @param pw
	* @param @param id
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 10, 2013 4:56:05 PM
	 */
	@RequestMapping("/sysResourceUpdate.action")
	public String sysResourceUpdate(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletRequest response) {
		
		logger.debug("SysResourceAction.sysResourceUpdate()");
	
		if(null!=id){
			SysResource sysResource = sysresourceManager.get(id);
			request.setAttribute("sysResource", sysResource);
		}
		List<SysResource> sysResource_list = sysresourceManager.getAll(); //加载所有资源信息
		request.setAttribute("sysResource_list", sysResource_list);
		return "sysresource/sysResourceUpdate";
	}
	
	/**
	 * 通过id获取
	 * @param pw
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getSysResource.action")
	public void getSysResource(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletRequest response) {
		logger.debug("SysResourceAction.getSysResource()");
		Map map = new HashMap();
		map.put("success", false);
		try {
			
			if (!StringUtils.isBlank(id)) {
				SysResource sr =sysresourceManager.get(id);	
				map.put("data", sr);
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("获取资源时发生错误", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
	}
		

	
	/**
	 *  增加或修改保存资源记录
	* @Title: addSysResource 
	* @Description: 
	* @param @param pw
	* @param @param sysResource
	* @param @param request
	* @param @return
	* @param @throws ParseException     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 10, 2013 4:49:35 PM
	 */
		
		
		@RequestMapping("/saveSysResource.action")
		public String saveSysResource(PrintWriter pw, SysResource sysResource, HttpServletRequest request)
				throws ParseException {
			logger.debug("SysResourceAction.saveSysResource()");
			String actionErrType = "";
			Map map = new HashMap();
			map.put("success", false);
			try {
				
				if (!StringUtils.isBlank(sysResource.getId())) {
					actionErrType = "修改资源记录";
					SysResource sr =sysresourceManager.get(sysResource.getId());	
					SunitBeanUtils.copyProperties(sysResource, sr);
					sysresourceManager.save(sr);
					map.put("sysResource", sr);
				}else{
					actionErrType = "新增资源记录"; 
					sysResource.setId(null);
					sysresourceManager.save(sysResource);	 
					map.put("sysResource", sysResource);
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
		 * 验证资源的code资源编码不能重复。
		* @Title: isExists 
		* @Description: 验证资源的code资源编码不能重复。
		* @param @param sysResource
		* @param @param pw
		* @param @return     
		* @return String  
		* @throws 
		* @author liangrujian 
		* Aug 20, 2013 12:02:38 PM
		 */
		@RequestMapping("/isExists.action")
		public String isExists(SysResource sysResource, PrintWriter pw) {
			logger.debug("SysResourceAction.isExists()");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("success", false);
			List<SysResource> list;
			try {
				list = sysresourceManager.isExtists(sysResource, sysResource.getId() != null);
				if (list == null || list.size() == 0 || list.isEmpty()) {
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

		
		/**
		 *  加载所有资源，绑定在页面中的下拉框中数据。
		* @Title: getParentSysourSelId 
		* @Description: 加载所有资源，绑定在页面中的下拉框中数据。
		* @param @param pw
		* @param @param id
		* @param @param request
		* @param @param response     
		* @return void  
		* @throws 
		* @author liangrujian 
		* Aug 13, 2013 3:57:42 PM
		* 
		* 修改时间：2014-08-07 ，修改人：徐嘉谦
		* 修改内容：类型"4"的资源（Action资源）不用成为上层资源，将类型"4"的资源去掉，具体修改内容是修改hql语句
		 */
		@RequestMapping("/getParentSysourSelId.action")
		public void getParentSysourSelId(PrintWriter pw, String id,
				HttpServletRequest request, HttpServletRequest response) {
			logger.debug("SysResourceAction.getParentSysourSelId()");
			Map<String,Object> map = new HashMap<String,Object>();
	        List sysList = sysresourceManager.find("select t.id as sysId, t.moduleCaption as moduleCaption from SysResource t where t.sourceType <> '4'  order by t.moduleCaption ");
	        if(null!=sysList && !sysList.isEmpty() && sysList.size()>0){
	        	Map<String,Object> itemMap = null;
	    		itemMap = new HashMap<String,Object>();
	    		List<Map<String,Object>> lst = new ArrayList<Map<String,Object>>();
	    		Object[] obj = null;
	    		String var_sysId = "";
	    		for(int i=0; i<sysList.size(); i++){
	    			itemMap = new HashMap<String,Object>();
	    			obj = (Object[])sysList.get(i);
	    			var_sysId = obj[0]==null?"":obj[0].toString();
	    			itemMap.put("id", var_sysId);
	    			itemMap.put("name", obj[1]==null?"":obj[1].toString());
	    			lst.add(itemMap);	
	    		}
	    		map.put("list", lst);
	        }
	        map.put("success", true);
	        pw.print(JSONObject.fromObject(map).toString());
			
		}
		
		
		 
	/**
	 * 
	* @Title: getSysResourInfoList 
	* @Description: 显示所有资源
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  
	* @throws 
	* @author liangrujian 
	* Jul 18, 2013 4:04:03 PM
	 */
	@RequestMapping("/getSysResourInfoList.action")
	public String getSysResourInfoList(HttpServletRequest request,String roleId,
			HttpServletRequest response) {
		//加载所有资源信息

		logger.debug("SysResourceAction.getSysResourInfoList()");
//		List<SysResource> sysResource_list = sysresourceManager.getAll(); //加载所有资源信息
		List<SysResource> sysResource_list = sysresourceManager.getAll(); //加载所有资源信息
		request.setAttribute("sysResource_list", sysResource_list);
		StringBuffer sb=new StringBuffer(); 
		SysResource entity  =new SysResource();
		entity.setParent("0");
		entity.setSourceType("2");
		List<SysResource> rsList = sysresourceManager.findByExample(entity);
		for (SysResource rs : rsList) { 
			recursionResource(rs,sb);
		}
		request.setAttribute("sysResource_string", sb.toString());
		if(!StringUtils.isBlank(roleId)){
		    request.setAttribute("roleId",roleId); //保存当前点击角色的ID 在容器中
		    Role role = roleManager.get(roleId);
		    String sysResourceIds = "" ;
		    for(int i=0;i<role.getSysResources().size();i++){
		    	if(StringUtils.isBlank(sysResourceIds)){
		    		sysResourceIds=role.getSysResources().get(i).getId();
		    	}
		    	 sysResourceIds += ","+ role.getSysResources().get(i).getId();
		    }
		   
		    request.setAttribute("sysResourceIds",sysResourceIds); //保存当前角色中包含有的资源记录ID
		}
		
		return "sysresource/showSysResourList";
	} 
	
	/**
	 * 
	* @Title: recursionResource 
	* @Description: 递归输出树形结构方法。
	* @param @param rs
	* @param @param sb     
	* @return void  
	* @throws 
	* @author liangrujian 
	* Aug 6, 2013 10:32:44 AM
	 */
	public void recursionResource(SysResource rs,StringBuffer sb){
		List<SysResource> list =sysresourceManager.find(" from SysResource where parent=? ", rs.getId());
		
		if(list==null||list.size()==0) {
			sb.append("<div><input  id='"+rs.getId()+"' type='checkbox'><font face='微软雅黑' size='3px'>"); 
			sb.append(rs.getModuleCaption()+"*"+ rs.getCaption()); //没有子节点的结点，属于叶子节点
			sb.append("</font></div>"); 
		}else{
			sb.append("<div><input type='checkbox' id='"+rs.getId()+"' ><span><font face='微软雅黑' size='3px'>" +rs.getModuleCaption()+"*"+ rs.getCaption()+"</font></span>"); //有孩子节点的结点
		for (SysResource o : list) {
				recursionResource(o,sb); 
			}
			sb.append("</div>");
		}
	}

	
	/**
	 * 提交保存维护父菜单和子资源的关系
	* @Title: saveSupForsubResource 
	* @Description: 
	* @param @param pw
	* @param @param supResourceID
	* @param @param subResourceIDS
	* @param @param request
	* @param @return
	* @param @throws ParseException     
	* @return String  
	* @throws 
	* @author 梁汝健
	* 2014-6-20 下午04:44:52
	 */
	@RequestMapping("/saveSupForsubResource.action")
	public String saveSupForsubResource(PrintWriter pw, String supResourceID,String subResourceIDS, HttpServletRequest request)
			throws ParseException {
		logger.debug("SysResourceAction.saveSupForsubResource()");
		String actionErrType = "";
		Map map = new HashMap();
		map.put("success", false);
		try {
			String[] idArr = subResourceIDS.split(","); 
			SunitStringUtil util = new SunitStringUtil();
			String str = util.getStringUtil(idArr);
			Integer integer = sysresourceManager.executeUpdate(
					"update SysResource t set t.parent='"+supResourceID+"' where t.id in(" + str + ")",idArr);
			if (integer.intValue() > 0) {
				map.put("success", true);
			} else {
				map.put("msg", "记录不存在");
			}			
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error(actionErrType, e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	
	
	/**
	 * 动态搜索资源
	* @Title: searchModuleCaptionName 
	* @Description: 
	* @param @param pw
	* @param @param request
	* @param @param response     
	* @return void  
	* @throws 
	* @author 梁汝健
	* 2014-6-20 下午04:00:52
	 */
	@RequestMapping("/searchModuleCaptionName.action")
	public void searchModuleCaptionName(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,String moduleCaptionName) {
		logger.debug(" load searchModuleCaptionName.action"); 
		Map<String, Object> map = new HashMap<String, Object>();
		List<SysResource> lst =null;
		map.put("success", false);
		StringBuffer buffer = new StringBuffer();
		//t.sourceType's  valuelist:   0:页面 1:按钮 2: 树菜单根 3树菜单节点
		buffer.append("select new map(t.id as id, t.moduleCaption as moduleCaption) from SysResource t where 1=1 ");
		buffer.append(" and t.sourceType  in ('1','3') " 
//				+ 
//				"and (t.moduleCaption like '%"
//						+ moduleCaptionName
//						+ "%' or t.caption like '%"
//						+ moduleCaptionName
//		 
//						+ "%') order by t.moduleCaption" 
				);
		if (!StringUtils.isBlank(moduleCaptionName)) {
			moduleCaptionName="%" +moduleCaptionName + "%";
			buffer.append("  and moduleCaption like ?");	
			lst= sysresourceManager.find(buffer.toString(),moduleCaptionName);
		}else{
			lst= sysresourceManager.find(buffer.toString()); 
			
		}
		
		
		
		map.put("success", true);
		map.put("list", lst);
		pw.print(JSONObject.fromObject(map).toString());	
	}

	
	/**
	 *  时间：2014-08-08 ，作者：徐嘉谦
	 *  实现功能：将id传递给另一个页面使用 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/meunResourceAdd.action")
	public String meunResourceAdd(String id, HttpServletRequest request,
			HttpServletRequest response) {
		
		logger.debug("SysResourceAction.meunResourceAdd()");
	
		if(null!=id){
			request.setAttribute("parentId", id);
		}
		return "sysresource/meunResourceAdd";
	}
	
	
	/**
	 *  时间：2014-08-13 ，作者：徐嘉谦
	 *  实现功能：编辑的数据
	 *  逻辑描述 ：根据id查询数据，并将数据返回给页面
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/meunResourceUpdate.action")
	public String meunResourceUpdate(String id, HttpServletRequest request,
			HttpServletRequest response) {
		
		logger.debug("SysResourceAction.meunResourceUpdate()");
	
		if(null!=id){
			SysResource sysResource = sysresourceManager.get(id);
			request.setAttribute("sysResource", sysResource);
		}
		return "sysresource/meunResourceUpdate";
	}
	
	
	
	
	
	
	public SysResourceManager getSysresourceManager() {
		return sysresourceManager;
	}



	public void setSysresourceManager(SysResourceManager sysresourceManager) {
		this.sysresourceManager = sysresourceManager;
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
	 * @author joye
	 * 
	 */
	public static class SysSearchPara {

		private String sysName;
 
		private String sysResourceCaption;
		
		private String sysResourceCode;
		
		private String sysResourceContent;
		
		public String getSysName() {
			return sysName;
		}

		public void setSysName(String sysName) {
			this.sysName = sysName;
		}

		public String getSysResourceCaption() {
			return sysResourceCaption;
		}

		public void setSysResourceCaption(String sysResourceCaption) {
			this.sysResourceCaption = sysResourceCaption;
		}

		public String getSysResourceCode() {
			return sysResourceCode;
		}

		public void setSysResourceCode(String sysResourceCode) {
			this.sysResourceCode = sysResourceCode;
		}

		public String getSysResourceContent() {
			return sysResourceContent;
		}

		public void setSysResourceContent(String sysResourceContent) {
			this.sysResourceContent = sysResourceContent;
		}
		
	}
	
}