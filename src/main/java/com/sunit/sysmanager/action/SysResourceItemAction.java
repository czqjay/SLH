package com.sunit.sysmanager.action;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.po.SysResourceItem;
import com.sunit.sysmanager.service.SysResourceItemManager;
import com.sunit.sysmanager.service.SysResourceManager;



@Controller
@RequestMapping("/sysresourceItem")
public class SysResourceItemAction extends BaseAction {

	static Logger logger = Logger.getLogger(DepartAction.class);
	
	@Autowired
	private SysResourceItemManager sysResourceItemManager;
	
	@Autowired
	private SysResourceManager sysResourceManager;
	
	
	/**
	 * 时间：2014-07-22 ，作者：徐嘉谦
	 * 实现功能：获取菜单根和节点形成的资源树
	 * @param pw
	 * @param id
	 */
	@RequestMapping("/getTreeMenuOnlySysResource.action")
	public void getTreeMenuOnlySysResource(PrintWriter pw, String id) {
		logger.debug("DepartAction.getTreeMenuOnlySysResource()");
		String reString;
		if (!StringUtils.isBlank(id))
			if (id.equals("root")) {
				//获取菜单根资源
				List<SysResource> list = sysResourceManager
						.find("from SysResource where parent=? and sourceType in (2,3) order by moduleCaption desc","0"); 
				JsonConfig jc = new JsonConfig();
				//过滤，保留资源名称和id
				jc.setJsonPropertyFilter(new PropertyFilter() {
					public boolean apply(Object source, String name,
							Object value) {
						if (name.equals("moduleCaption") || name.equals("id")|| name.equals("sourceType"))
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
						if (name.equals("moduleCaption") || name.equals("moduleCaption") || name.equals("sourceType")
								|| name.equals("id")) 
							return false;
						else
							return true;
					}
				});
 
				//查询菜单节点资源
				List<SysResource> childSysResourceList =sysResourceManager.find("from SysResource where parent=? and sourceType  in (2,3) order by orderNum ", id) ;
				List<SysResource> list = new ArrayList<SysResource>();
				for (SysResource srs : childSysResourceList) {
						list.add(srs);
				}
				reString = JSONArray.fromObject(list, jc).toString();
				pw.write(reString);
			}
	}
	

	/**
	 * 时间：2014-07-22 ，作者：徐嘉谦
	 * 实现功能：获取所有类型为“4”（Action类型）的资源
	 * 逻辑描述：查询出所有类型为“4”（Action类型）的资源，然后过滤，只保留该资源的id和名称，打印输出
	 * @param pw
	 */
	@RequestMapping("/getAllSubSysResource.action")
	public void getAllSubSysResource(PrintWriter pw) {
		logger.debug("SysResourceItemAction.getTreeSubSysResource()");
		String reString;
		//查询所有的Action资源
		List<SysResource> list = sysResourceManager.find("from SysResource where sourceType ='4' order by moduleCaption desc");
		JsonConfig jc = new JsonConfig();
		//过滤，只保留资源的名称和id
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
			
	}
	
	
	/**
	 * 时间：2014-07-22 ，作者：徐嘉谦
	 * 实现功能：动态搜索Action资源
	 * @param pw
	 * @param request
	 * @param response
	 */
	@RequestMapping("/searchSubCaptionName.action")
	public void searchSubCaptionName(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug(" load searchModuleCaptionName.action....");
		//获取页面上的值并判断该值是否为空
		String moduleCaptionName = request.getParameter("moduleCaptionName") == null ? ""
				: request.getParameter("moduleCaptionName");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		StringBuffer buffer = new StringBuffer();
		buffer.append("select new map(t.id as id, t.moduleCaption as moduleCaption) from SysResource t where t.sourceType ='4' ");
		//拼接查询条件
		if(!"".equals(moduleCaptionName)){
			buffer.append(" and (t.moduleCaption like '%"
					+ moduleCaptionName
					+ "%' or t.content like '%"
					+ moduleCaptionName+ "%')");			
		}
		
		buffer.append(" order by t.moduleCaption desc");
		List<SysResource> lst = sysResourceManager.find(buffer.toString());
		map.put("success", true);
		map.put("subData", lst);
		pw.print(JSONObject.fromObject(map).toString());
	}
	

	/**
	 * 时间：2014-07-22 ，作者：徐嘉谦
	 * 实现功能：查询出某个菜单根或节点下的所有的Action资源
	 * 逻辑描述：根据菜单根或节点的id查询出该菜单根或节点下所拥有的Action资源的id,
	 * 		     然后通过资源表获取该Action资源id所对应的名称
	 * @param pw
	 * @param id
	 */
	@RequestMapping("/getSubResource.action")
	public void getSubResource(PrintWriter pw,HttpServletRequest request) {
		logger.debug("SysResourceItemAction.getSubResource()");
		String reString;
		//获取页面上的值并判断该值是否为空
		String supResourceID = request.getParameter("supResourceID") == null ? "" : request.getParameter("supResourceID");
		
		Map<String ,Object> map = new HashMap<String, Object>();
		map.put("success", false);
		//根据资源子表查询出某资源下有那些Action资源
		List list = sysResourceItemManager.excuteSQL("select t.id as id , t.subordinateResource as subordinateResource from TB_SYSRESOURCE_ITEM t where t.superiorResource=? order by t.subordinateResource", supResourceID);
		
		if(!list.isEmpty()) 
			for (int i = 0; i < list.size(); i++) {
				Object[] arrs =(Object[])list.get(i);  
				Map<String, Object> m =new HashMap<String, Object>(); 
				m.put("id",arrs[1].toString());//Action子资源在SysResource中的id	
				m.put("moduleCaption", sysResourceManager.get(arrs[1].toString()).getModuleCaption());//Action资源的名字  		
				list.set(i, m);  
			} 		
		
		map.put("success", true);
		map.put("subdata", list);
		reString =  JSONObject.fromObject(map).toString();
		pw.write(reString);
			
	}
	
	
	
	/**
	 * 时间：2014-07-22 ，作者：徐嘉谦
	 * 实现功能：保存某资源下所添加或移除后的Action资源
	 * 逻辑描述：通过页面Ajax传递过来的菜单根或节点id删除该菜单根或节点的数据，
	 * 		     再重新插入页面Ajax传递过来的新数据
	 * @param pw	打印输出流
	 * @param supResourceID	菜单根或节点id
	 * @param subResourceIDS	添加或移除后的Action资源的id 
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/saveSysResourceItem.action")
	public String saveSysResourceItem(PrintWriter pw, String supResourceID,String subResourceIDS, HttpServletRequest request)
			throws ParseException {
		logger.debug("SysResourceAction.saveSupForsubResource()");
		String actionErrType = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = subResourceIDS.split(",");//根据“,”拆分id
			//删除资源子表中菜单根或节点下的所有Action资源(资源类型为“4”的资源)
			sysResourceItemManager.executeUpdate("delete from SysResourceItem t where t.superiorResource='"+supResourceID+"'");
			
			//判断有没有Action资源(资源类型为“4”的资源)
			if(!"".equals(subResourceIDS)){
				for(int i=0; i<idArr.length; i++){
					SysResourceItem sysItem = new SysResourceItem(); 
					//sysItem.setSuperiorResource(supResourceID);
					sysItem.setSubordinateResource(idArr[i]);
					sysResourceItemManager.save(sysItem);//保存数据
				}			
			}			
			map.put("success", true);		
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error(actionErrType, e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
}
