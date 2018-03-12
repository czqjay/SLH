package com.sunit.operator.action;


import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.operator.po.Operator;
import com.sunit.operator.service.OperatorManager;


@Controller
@RequestMapping("/operator")
public class OperatorAction extends BaseAction { 

	static Logger logger = Logger.getLogger(OperatorAction.class);

	@Autowired
	private OperatorManager operatorManager;
	
	
	
	
	@RequestMapping("/operatorSave.action")
	public String operatorSave(PrintWriter pw, Operator operator, HttpServletRequest request)
			throws ParseException {
		logger.debug("OperatorAction.operatorSave()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try { 
			operatorManager.save(operator); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("新增操作日志", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	
	@RequestMapping("/operatorInfoForUpdate.action")
	public String operatorUpdateInfo(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("OperatorAction.operatorUpdatePage()");
		
		if (!StringUtils.isBlank(id)) {
			Operator  operator= operatorManager.get(id); 
			request.setAttribute("operator", operator);  
		}
		return "operator/operatorUpdate"; 
	}
	
	@RequestMapping("/operatorUpdate.action")
	public String operatorUpdate(PrintWriter pw, Operator operator,  
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("OperatorAction.operatorUpdate()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try { 
			operatorManager.save(operator); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("修改操作日志", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return  null;
	}
	
	
	@RequestMapping("/operatorDelete.action")
	public String operatorDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			operatorManager.delete(idArr); 
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);  
			map.put("msg", e.getMessage()); 
			logger.error("删除操作日志：", e); 
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}
	
	
	/**
	* @Title: loadOperatorListDataGrid 
	* @Description: 加载日志列表
	* @param @param pw
	* @param @param request
	* @param @param response
	* @param @param rows
	* @param @param page
	* @param @param para
	* @param @return     
	* @return String  
	* @throws 
	* @author 梁汝健
	* 2013-10-13 下午14:40:34
	 */
	@RequestMapping("/loadOperatorListDataGrid.action")
	public String loadOperatorListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletRequest response,
			final int rows, int page, OperatorSearchPara para) {
		logger.debug("OperatorAction.loadOperatorListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer(); 
		hqlBuf.append(" from Operator where 1=1 "); 
		
		if(!SunitStringUtil.isBlankOrNull(para.getName())){
			para.setName("%" + para.getName() + "%");
			hqlBuf.append("  and   	 userName  like :name"); 
		}
		
		if(!SunitStringUtil.isBlankOrNull(para.getName())){
			para.setName("%" + para.getName() + "%");
			hqlBuf.append("  and   	 userName  like :name"); 
		}
		
		if (!SunitStringUtil.isBlankOrNull(para.getDateFrom())) {
			hqlBuf.append(" and operatortime >= :dateFrom ");
		} 
		if (!SunitStringUtil.isBlankOrNull(para.getDateTo())) {
			hqlBuf.append(" and operatortime <= :dateTo");
		}
		
		
		Paging paging = operatorManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}
	
	
	public static  class OperatorSearchPara { 

		private String name;
		private String dateFrom;
		private String dateTo;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDateFrom() {
			return dateFrom;
		}
		public void setDateFrom(String dateFrom) {
			this.dateFrom = dateFrom;
		}
		public String getDateTo() {
			return dateTo;
		}
		public void setDateTo(String dateTo) {
			this.dateTo = dateTo;
		}
		
	}
	
}