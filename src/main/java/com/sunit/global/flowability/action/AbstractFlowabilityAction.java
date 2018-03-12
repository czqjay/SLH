package com.sunit.global.flowability.action;



import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.flowability.po.Flowability;
import com.sunit.global.flowability.service.FlowabilityManager;
import com.sunit.global.util.Paging;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.User;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;


public abstract class AbstractFlowabilityAction extends BaseAction { 

	static Logger logger = Logger.getLogger(AbstractFlowabilityAction.class);

	@Autowired
	protected FlowabilityManager flowabilityManager;
	
	@RequestMapping("/flowabilitySave.action")
	public String flowabilitySave(PrintWriter pw, Flowability flowability, HttpServletRequest request)
			throws ParseException {
		logger.debug("FlowabilityAction.flowabilitySave()");
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		User u =(User) request.getSession().getAttribute("user"); 
		map.put("success", false);
		try { 
			flowabilityManager.save(flowability); 
					map.put("success", true);
		} catch (Exception e) {
			map.put("msg", "新增tb_flowAbility失败");
			logger.error("新增tb_flowAbility", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	 
	@RequestMapping("/flowabilityInfoForUpdate.action")
	public String flowabilityUpdateInfo(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("FlowabilityAction.flowabilityUpdatePage()"); 
		
		if (!StringUtils.isBlank(id)) {
			Flowability  flowability= flowabilityManager.get(id); 
			request.setAttribute("flowability", flowability);  
		}
		return "xwt/flowability/flowabilityUpdate"; 
	} 
	
	 
	@RequestMapping("/flowabilityUpdate.action")
	public String flowabilityUpdate(PrintWriter pw, Flowability flowability,  
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("FlowabilityAction.flowabilityUpdate()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try { 
			flowabilityManager.save(flowability); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", "修改tb_flowAbility失败");
			logger.error("修改tb_flowAbility", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return  null;
	}
	
	 
	@RequestMapping("/flowabilityDelete.action")
	public String flowabilityDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			flowabilityManager.delete(idArr); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);  
			map.put("msg", "删除tb_flowAbility失败"); 
			logger.error("删除tb_flowAbility：", e); 
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	 
	  
	@RequestMapping("/loadFlowabilityListDataGrid.action")
	public String loadFlowabilityListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,
			final int rows, int page, FlowabilitySearchPara para) {
		logger.debug("FlowabilityAction.loadFlowabilityListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer(); 
											
																				
																				
																				
																				
																				
																				
															
		hqlBuf.append("  select new map( id as id ,activityId as activityId,poId as poId,taskId as taskId,assigneeType as assigneeType,proccessInstanceId as proccessInstanceId,assignee as assignee,candidate as candidate) from Flowability  where 1=1 "); 
		
							  
											  
											  
											  
											  
											  
											  
							 
		Paging paging = flowabilityManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据 
		JsonConfig jc = new JsonConfig(); 
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	} 
	
	
	
	 
	@RequestMapping("/isExists.action") 
	public String isExists(Flowability flowability, PrintWriter pw) {
		logger.debug("FlowabilityAction.isExists()");
		Map map = new HashMap();
		map.put("success", "false"); 
		List list;
		try { 
			list = flowabilityManager.isExtists(flowability, flowability.getId() != null); 
			if (list.size() == 0) { 
				map.put("success", "1");
			}
		} catch (Exception e) {
			map.put("msg", "isExists失败");
			logger.error("isExists：", e);
		} 
		pw.print(JSONObject.fromObject(map).toString()); 
		return null;
	} 
	
	 
	@RequestMapping("/getSelectBody.action")
	public String getSelectBody(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response,final String optionId,final String optionName) {
		List list = flowabilityManager.getAll();
		JsonConfig jc = new JsonConfig();
		jc.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object arg0, String arg1, Object arg2) {
				if (arg1.equals(optionId) || arg1.equals(optionName)  
						|| arg1.equals("list") || arg1.equals("success"))
					return false;
				else
					return true;
			}
		});
		
		Map map = new HashMap();
		map.put("success", true);
		map.put("list", list);
		String s = JSONObject.fromObject(map, jc).toString();
		pw.print(s);
		return null;

	}
	
	
	
	
	
	@InitBinder 
	public void flowabilityBinder(WebDataBinder binder) {
								
															
															
															
															
															
															
										
	}
	
	
	
	public static  class FlowabilitySearchPara { 
	
						 
											 
											 
											 
											 
											 
											 
								
	}
	
	
	
	
	
	
	
	
	
	
	 
	@RequestMapping("/frontFlowabilitySave.action")
	public String frontFlowabilitySave(PrintWriter pw, Flowability flowability, HttpServletRequest request)
			throws ParseException {
		logger.debug("FlowabilityAction.frontFlowabilitySave()");
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		User u =(User) request.getSession().getAttribute("user"); 
		map.put("success", false);
		try { 
			flowabilityManager.save(flowability); 
					map.put("success", true);
		} catch (Exception e) {
			map.put("msg", "新增tb_flowAbility失败");
			logger.error("新增tb_flowAbility", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	 
	@RequestMapping("/frontFlowabilityInfoForUpdate.action")
	public String frontFlowabilityUpdateInfo(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("FlowabilityAction.frontFlowabilityUpdatePage()"); 
		
		if (!StringUtils.isBlank(id)) {
			Flowability  flowability= flowabilityManager.get(id); 
			request.setAttribute("flowability", flowability);  
		}
		return "xwt/flowability/flowabilityUpdate"; 
	} 
	
	 
	@RequestMapping("/frontFlowabilityUpdate.action")
	public String frontFlowabilityUpdate(PrintWriter pw, Flowability flowability,  
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("FlowabilityAction.frontFlowabilityUpdate()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try { 
			flowabilityManager.save(flowability); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", "修改tb_flowAbility失败");
			logger.error("修改tb_flowAbility", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return  null;
	}
	
	 
	@RequestMapping("/frontFlowabilityDelete.action")
	public String frontFlowabilityDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			flowabilityManager.delete(idArr); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);  
			map.put("msg", "删除tb_flowAbility失败"); 
			logger.error("删除tb_flowAbility：", e); 
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	 
	   
	@RequestMapping("/frontLoadFlowabilityListDataGrid.action")
	public String frontLoadFlowabilityListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,
			final int rows, int page, FlowabilitySearchPara para) {
		logger.debug("FlowabilityAction.frontLoadFlowabilityListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer(); 
											
																				
																				
																				
																				
																				
																				
															
		hqlBuf.append("  select new map( id as id ,activityId as activityId,poId as poId,taskId as taskId,assigneeType as assigneeType,proccessInstanceId as proccessInstanceId,assignee as assignee,candidate as candidate) from Flowability  where 1=1 "); 
		
							  
											  
											  
											  
											  
											  
											  
							 
		Paging paging = flowabilityManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据 
		JsonConfig jc = new JsonConfig(); 
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	} 
	
	
	
	 
	@RequestMapping("/frontFlowabilityisExists.action") 
	public String frontFlowabilityisExists(Flowability flowability, PrintWriter pw) {
		logger.debug("FlowabilityAction.frontFlowabilityisExists()");
		Map map = new HashMap();
		map.put("success", "false"); 
		List list;
		try { 
			list = flowabilityManager.isExtists(flowability, flowability.getId() != null); 
			if (list.size() == 0) { 
				map.put("success", "1");
			}
		} catch (Exception e) {
			map.put("msg", "isExists失败");
			logger.error("isExists：", e);
		} 
		pw.print(JSONObject.fromObject(map).toString()); 
		return null;
	} 
	
	 
	@RequestMapping("/frontFlowabilitygetSelectBody.action")
	public String frontFlowabilitygetSelectBody(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response,final String optionId,final String optionName) {
		List list = flowabilityManager.getAll();
		JsonConfig jc = new JsonConfig();
		jc.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object arg0, String arg1, Object arg2) { 
				if (arg1.equals(optionId) || arg1.equals(optionName)  
						|| arg1.equals("list") || arg1.equals("success"))
					return false;
				else
					return true;
			}
		});
		
		Map map = new HashMap();
		map.put("success", true);
		map.put("list", list);
		String s = JSONObject.fromObject(map, jc).toString();
		pw.print(s);
		return null;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	 
	
	
	
	
	
	
	
	
	
}
