package com.sunit.global.flowvariables.action;



import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.flowvariables.po.Flowvariables;
import com.sunit.global.flowvariables.service.FlowvariablesManager;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.User;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

@Controller
@RequestMapping("/flowvariables")
public class FlowvariablesAction extends BaseAction { 

	static Logger logger = Logger.getLogger(FlowvariablesAction.class);

	@Autowired
	private FlowvariablesManager flowvariablesManager;
	
;
	 
	
	 
	@RequestMapping("/flowvariablesSave.action")
	public String flowvariablesSave(PrintWriter pw, Flowvariables flowvariables, HttpServletRequest request)
			throws ParseException {
		logger.debug("FlowvariablesAction.flowvariablesSave()");
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String, Object> variables = new HashMap<String, Object>();
		User u =(User) request.getSession().getAttribute("user"); 
		map.put("success", false);
		try { 
			flowvariablesManager.save(flowvariables); 
					map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("新增流程变量表", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	 
	@RequestMapping("/flowvariablesInfoForUpdate.action")
	public String flowvariablesUpdateInfo(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("FlowvariablesAction.flowvariablesUpdatePage()"); 
		
		if (!StringUtils.isBlank(id)) {
			Flowvariables  flowvariables= flowvariablesManager.get(id); 
			request.setAttribute("flowvariables", flowvariables);  
		}
		return "xwt/flowvariables/flowvariablesUpdate"; 
	} 
	
	 
	@RequestMapping("/flowvariablesUpdate.action")
	public String flowvariablesUpdate(PrintWriter pw, Flowvariables flowvariables,  
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("FlowvariablesAction.flowvariablesUpdate()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try { 
			flowvariablesManager.save(flowvariables); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("修改流程变量表", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return  null;
	}
	
	 
	@RequestMapping("/flowvariablesDelete.action")
	public String flowvariablesDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			flowvariablesManager.delete(idArr); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);  
			map.put("msg", e.getMessage()); 
			logger.error("删除流程变量表：", e); 
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	 
	  
	@RequestMapping("/loadFlowvariablesListDataGrid.action")
	public String loadFlowvariablesListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,
			final int rows, int page, FlowvariablesSearchPara para) {
		logger.debug("FlowvariablesAction.loadFlowvariablesListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer(); 
		hqlBuf.append("  select new map( id as id ,fvType as fvType,fvTime as fvTime,fvUserId as fvUserId,fvTaskId as fvTaskId,fvProcInstId as fvProcInstId,fvAction as fvAction,fvContent as fvContent,remark as remark) from Flowvariables  where 1=1 "); 
		if (!SunitStringUtil.isBlankOrNull(para.getSfvProcInstId())) {  
	 	 	hqlBuf.append("  and   fvProcInstId = :SfvProcInstId" ); 
		}   																	 	
							 
		Paging paging = flowvariablesManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
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
	
	@RequestMapping("/loadFlowvariablesListByPara.action")
	public String loadFlowvariablesListByPara(PrintWriter pw, 
			HttpServletRequest request, HttpServletResponse response,
			FlowvariablesSearchPara para) {
		logger.debug("FlowvariablesAction.loadFlowvariablesListByPara()"); 
		Map map = new HashMap();
		map.put("success", false);
		List<Flowvariables> list = Collections.EMPTY_LIST;
		try { 
			
			StringBuffer hqlBuf = new StringBuffer(); 
			hqlBuf.append("  from Flowvariables  where 1=1 "); 
//			if (!SunitStringUtil.isBlankOrNull(para.getSfvProcInstId())) {  
		 	 	hqlBuf.append("  and   fvProcInstId = :SfvProcInstId" ); 
//			}
			if (!SunitStringUtil.isBlankOrNull(para.getSfvTaskId())) {   
		 	 	hqlBuf.append("  and   fvTaskId = :SfvTaskId" ); 
			} 
			
			hqlBuf.append(" order by  fvTime "); 
			
			list =flowvariablesManager.findByVO(hqlBuf.toString(), para);
			 
			Map<String,List> tasks = new LinkedMap();
			
			for (int i = 0; i < list.size(); i++) {  // 包装成map方式,便于前端展示
				Flowvariables fv = list.get(i);
				if(tasks.get(fv.getFvTaskId())==null){
					tasks.put(fv.getFvTaskId(),new LinkedList<Flowvariables>());
				}
				tasks.get(fv.getFvTaskId()).add(fv);
			}
			map.put("list", tasks);
			map.put("success", true);    
		} catch (Exception e) {
			map.put("success", false);   
			map.put("msg", e.getMessage()); 
			logger.error("获取流程历史变量：", e);  
		}
		JsonConfig jc = new JsonConfig(); 
		
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	} 
	 
	
	/**
	 *获取流程参数从 流程节点定义key 与 流程ProcInstId 
	* @Title: loadFlowvariablesListByACTKEY  
	* @Description: 
	* @param @param pw
	* @param @param request
	* @param @param response
	* @param @param para
	* @param @return     
	* @return String  
	* @throws 
	* @author joye 
	* Jul 5, 2016 7:32:13 PM
	 */
	@RequestMapping("/loadFlowvariablesListByACTKEY.action")
	public String loadFlowvariablesListByACTKEY(PrintWriter pw, 
			HttpServletRequest request, HttpServletResponse response,
			FlowvariablesSearchPara para) {
		logger.debug("FlowvariablesAction.loadFlowvariablesListByPara()"); 
		Map map = new HashMap();
		map.put("success", false);
		List<Flowvariables> list = Collections.EMPTY_LIST;
		try { 
			StringBuffer hqlBuf = new StringBuffer(); 
			hqlBuf.append("  from Flowvariables  where 1=1 "); 
		 	hqlBuf.append("  and   fvProcInstId = :SfvProcInstId" ); 
		 	hqlBuf.append("  and   remark = :SfvDefKey" );
		 	hqlBuf.append("  and   fvName like 'tax_ex%'" ); 
			hqlBuf.append(" order by  fvTime ");  
			list =flowvariablesManager.findByVO(hqlBuf.toString(), para); 
			Map<String,List> tasks = new LinkedMap();
			for (int i = 0; i < list.size(); i++) { 
				Flowvariables fv = list.get(i);
				if(tasks.get(fv.getFvTaskId())==null){
					tasks.put(fv.getFvTaskId(),new LinkedList<Flowvariables>());
				}
				tasks.get(fv.getFvTaskId()).add(fv);
			}
			map.put("list", tasks);
			map.put("success", true);   
		} catch (Exception e) {
			map.put("success", false);   
			map.put("msg", e.getMessage()); 
			logger.error("获取流程历史变量：", e); 
		}
		JsonConfig jc = new JsonConfig(); 
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	} 
	 
	@RequestMapping("/isExists.action") 
	public String isExists(Flowvariables flowvariables, PrintWriter pw) {
		logger.debug("FlowvariablesAction.isExists()");
		Map map = new HashMap();
		map.put("success", "false"); 
		List list;
		try { 
			list = flowvariablesManager.isExtists(flowvariables, flowvariables.getId() != null); 
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
		List list = flowvariablesManager.getAll();
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
	
	
	
	/**
	 * 获取当前有效的提交的记账与申报环节业务与异常数据 
	* @Title: getLastTaxNORMData 
	* @Description: 
	* @param @param pw
	* @param @param pid        ACT 流程ProcInst id
	* @param @param flowNoteName          ACT 流程key
	* @return void   
	* @throws 
	* @author joye 
	* Jul 15, 2016 5:07:53 PM
	 */
	@RequestMapping("/getLastTaxNORMData.action")
	public void getLastTaxNORMData( PrintWriter pw, String pid , String flowNoteName ) {
		logger.debug("FlowvariablesAction.getLastTaxNORMData()");
		Map map = new HashMap();
		map.put("success", false);
		StringBuffer hqlBuf = new StringBuffer(); 
		List list =new ArrayList();
		try {
			if(flowNoteName.equals("utTaxAccount")){ //记账节点时回填上一次数据
			
				hqlBuf.append("select new map(f.fvName as fvName , f.fvContent as fvContent) from Flowvariables f where f.fvProcInstId=? and (  f.fvName like 'tax_ex%' or f.fvName like 'tax_NORM%' ) ");
				hqlBuf.append(" and f.fvTaskId =  " );
				hqlBuf.append("(");
				hqlBuf.append(" select  max(CAST(fl.fvTaskId as integer))  from Flowvariables fl where fl.fvProcInstId =?  "); 
				hqlBuf.append(" and fl.remark in  ('utTaxDeclare' ) "); 
				hqlBuf.append(")");
				List<Flowvariables> utTaxDeclareList = flowvariablesManager.find(hqlBuf.toString(),pid ,pid );
				hqlBuf.delete(0, hqlBuf.length());
				if(utTaxDeclareList.isEmpty()){//不存在申报数据
					hqlBuf.append("select new map(f.fvName as fvName , f.fvContent as fvContent) from Flowvariables f where f.fvProcInstId=? and ( f.fvName like 'tax_NORM%' )");
					hqlBuf.append(" and f.fvTaskId =  " );
					hqlBuf.append("(");
					hqlBuf.append(" select  max(CAST(fl.fvTaskId as integer))  from Flowvariables fl where fl.fvProcInstId =?  "); 
					hqlBuf.append(" and fl.remark in  ('utTaxAccount' ) ");
					hqlBuf.append(")");
					List<Flowvariables>  utTaxAccountList = flowvariablesManager.find(hqlBuf.toString(),pid ,pid );
					list.addAll(utTaxAccountList);
				
				}else
					list.addAll(utTaxDeclareList);	 
			}
			
			if(flowNoteName.equals("utTaxDeclare")){ 
				hqlBuf.append("select new map(f.fvName as fvName , f.fvContent as fvContent) from Flowvariables f where f.fvProcInstId=? and ( f.fvName like 'tax_NORM%' )");
				hqlBuf.append(" and f.fvTaskId =  " );
				hqlBuf.append("(");
				hqlBuf.append(" select  max(CAST(fl.fvTaskId as integer))  from Flowvariables fl where fl.fvProcInstId =?  "); 
				hqlBuf.append(" and fl.remark in  ('utTaxAccount' ) ");
				hqlBuf.append(")");
				List<Flowvariables>  utTaxAccountList = flowvariablesManager.find(hqlBuf.toString(),pid ,pid );
				
				hqlBuf.delete(0, hqlBuf.length());
				hqlBuf.append("select new map(f.fvName as fvName , f.fvContent as fvContent) from Flowvariables f where f.fvProcInstId=? and (  f.fvName like 'tax_ex%' )");
				hqlBuf.append(" and f.fvTaskId =  " );
				hqlBuf.append("(");
				hqlBuf.append(" select  max(CAST(fl.fvTaskId as integer))  from Flowvariables fl where fl.fvProcInstId =?  "); 
				hqlBuf.append(" and fl.remark in  ('utTaxDeclare' ) "); 
				hqlBuf.append(")");
				List<Flowvariables> utTaxDeclareList = flowvariablesManager.find(hqlBuf.toString(),pid ,pid );
				list.addAll(utTaxAccountList);
				list.addAll(utTaxDeclareList);
			}
			
			 
			map.put("list", list);  
			map.put("success", true);   
		} catch (Exception e) {
			map.put("success", false);   
			map.put("msg", "获取记账数据发生错误"); 
			logger.error("获取最后一次提交的记账与申报环节正常数据 ：", e); 
		}
		JsonConfig jc = new JsonConfig(); 
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor()); 
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
	}
	
	
	
	@InitBinder 
	public void flowvariablesBinder(WebDataBinder binder) {
								
															
															
															
															
															
						 									
															
										
	}
	
	
	
	public static  class FlowvariablesSearchPara { 
	
		String sfvProcInstId;
		String sfvDefKey;

		public String getSfvDefKey() {
			return sfvDefKey;
		}
 
		public void setSfvDefKey(String sfvDefKey) {
			this.sfvDefKey = sfvDefKey;
		}

		public String getSfvProcInstId() { 
			return sfvProcInstId; 
		}

		public void setSfvProcInstId(String sfvProcInstId) {
			this.sfvProcInstId = sfvProcInstId;
		}

		String sfvTaskId;

		public String getSfvTaskId() {
			return sfvTaskId;
		}

		public void setSfvTaskId(String sfvTaskId) {
			this.sfvTaskId = sfvTaskId;
		}					 
		
		
		 
			
		
		
											 
											 
											 
											 
											 
								
	}
	
}
