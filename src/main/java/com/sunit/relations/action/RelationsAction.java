package com.sunit.relations.action;



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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.relations.po.RelationTree;
import com.sunit.relations.service.RelationsManager;
import com.sunit.sysmanager.po.User;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

@Controller
@RequestMapping("/relations")
public class RelationsAction extends BaseAction { 

	static Logger logger = Logger.getLogger(RelationsAction.class);

	@Autowired
	private RelationsManager relationsManager;
	
		 
	 
	
	 
	@RequestMapping("/relationsSave.action")
	public String relationsSave(PrintWriter pw, RelationTree relations, HttpServletRequest request)
			throws ParseException {
		logger.debug("RelationsAction.relationsSave()");
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		User u =(User) request.getSession().getAttribute("user"); 
		map.put("success", false);
		try { 
			relationsManager.save(relations); 
					map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("新增用户关系树表", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	 
	@RequestMapping("/relationsInfoForUpdate.action")
	public String relationsUpdateInfo(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("RelationsAction.relationsUpdatePage()"); 
		
		if (!StringUtils.isBlank(id)) {
			RelationTree  relations= relationsManager.get(id); 
			request.setAttribute("relations", relations);  
		}
		return "xwt/relations/relationsUpdate"; 
	} 
	
	 
	@RequestMapping("/relationsUpdate.action")
	public String relationsUpdate(PrintWriter pw, RelationTree relations,  
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("RelationsAction.relationsUpdate()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try { 
			relationsManager.save(relations); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("修改用户关系树表", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return  null;
	}
	
	 
	@RequestMapping("/relationsDelete.action")
	public String relationsDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			relationsManager.delete(idArr); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);  
			map.put("msg", e.getMessage()); 
			logger.error("删除用户关系树表：", e); 
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	 
	  
	@RequestMapping("/loadRelationsListDataGrid.action")
	public String loadRelationsListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,
			final int rows, int page, RelationsSearchPara para) {
		logger.debug("RelationsAction.loadRelationsListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer(); 
											
																				
																				
																				
																				
																				
																				
															
		hqlBuf.append("  select new map( id as id ,relationsLeft as relationsLeft,relationsUserId as relationsUserId,relationsAncestorId as relationsAncestorId,relationsRight as relationsRight,relationsLevel as relationsLevel,relationsParentId as relationsParentId,remark as remark) from Relations  where 1=1 "); 
		
							  
											  
					if (!SunitStringUtil.isBlankOrNull(para.getSrelationsUserId())) {  
		 		
		 	 		 	 
		 	 
		 	 		 	 	hqlBuf.append("  and relationsUserId in (select id from User d where d.userName = :SrelationsUserId)  ");
		 	 		 	 
		}   
		    								  
					if (!SunitStringUtil.isBlankOrNull(para.getSrelationsAncestorId())) {  
		 		
		 	 		 	 
		 	 
		 	 		 	 	hqlBuf.append("  and relationsAncestorId in (select id from User d where d.userName = :SrelationsAncestorId)  ");
		 	 		 	 
		}   
		    								  
											  
											  
					if (!SunitStringUtil.isBlankOrNull(para.getSrelationsParentId())) {  
		 		
		 	 		 	 
		 	 
		 	 		 	 	hqlBuf.append("  and relationsParentId in (select id from User d where d.userName = :SrelationsParentId)  ");
		 	 		 	 
		}   
		    								  
							 
		Paging paging = relationsManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
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
	public String isExists(RelationTree relations, PrintWriter pw) {
		logger.debug("RelationsAction.isExists()");
		Map map = new HashMap();
		map.put("success", "false"); 
		List list;
		try { 
			list = relationsManager.isExtists(relations, relations.getId() != null); 
			if (list.size() == 0) { 
				map.put("success", "1");
			}
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("isExists：", e);
		} 
		pw.print(JSONObject.fromObject(map).toString()); 
		return null;
	} 
	
	 
	@RequestMapping("/getSelectBody.action")
	public String getSelectBody(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response,final String optionId,final String optionName) {
		List list = relationsManager.getAll();
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
	public void relationsBinder(WebDataBinder binder) {
								
															
															
															
															
															
															
										
	}
	
	
	
	public static  class RelationsSearchPara { 
	
						 
											 
					
				 
		private java.lang.String srelationsUserId;
		
		public void setSrelationsUserId(java.lang.String srelationsUserId  ) {
			this.srelationsUserId = srelationsUserId;
		}
		public  java.lang.String  getSrelationsUserId() {  
			return srelationsUserId;
		}   
		    								 
					
				 
		private java.lang.String srelationsAncestorId;
		
		public void setSrelationsAncestorId(java.lang.String srelationsAncestorId  ) {
			this.srelationsAncestorId = srelationsAncestorId;
		}
		public  java.lang.String  getSrelationsAncestorId() {  
			return srelationsAncestorId;
		}   
		    								 
											 
											 
					
				 
		private java.lang.String srelationsParentId;
		
		public void setSrelationsParentId(java.lang.String srelationsParentId  ) {
			this.srelationsParentId = srelationsParentId;
		}
		public  java.lang.String  getSrelationsParentId() {  
			return srelationsParentId;
		}   
		    								 
								
	}
	
}
