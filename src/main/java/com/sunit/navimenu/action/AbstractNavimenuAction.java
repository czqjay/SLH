package com.sunit.navimenu.action;

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
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitBeanUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.navimenu.po.Navimenu;
import com.sunit.navimenu.service.NavimenuManager;
import com.sunit.sysmanager.po.User;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public abstract class AbstractNavimenuAction extends BaseAction {

	static Logger logger = Logger.getLogger(AbstractNavimenuAction.class);

	@Autowired
	protected NavimenuManager navimenuManager;

	@RequestMapping("/navimenuInfoForUpdate.action")
	public String navimenuUpdateInfo(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("NavimenuAction.navimenuUpdatePage()");

		if (!StringUtils.isBlank(id)) {
			Navimenu navimenu = navimenuManager.get(id);
			request.setAttribute("navimenu", navimenu);
		}
		return "/navimenu/navimenuUpdate";
	}

	@RequestMapping("/navimenuUpdate.action")
	public String navimenuUpdate(PrintWriter pw, Navimenu navimenu, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("NavimenuAction.navimenuUpdate()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			Navimenu o = navimenuManager.get(navimenu.getId());
			SunitBeanUtils.copyProperties(navimenu, o);
			navimenuManager.save(o);
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", "修改菜单表失败");
			logger.error("修改菜单表", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	@RequestMapping("/navimenuDelete.action")
	public String navimenuDelete(PrintWriter pw, String ids, HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			navimenuManager.delete(idArr);
			map.put("success", true);
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", "删除菜单表失败");
			logger.error("删除菜单表：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	} 

	@RequestMapping("/loadNavimenuListDataGrid.action")
	public String loadNavimenuListDataGrid(PrintWriter pw, HttpServletRequest request, HttpServletResponse response,
			final int rows, int page, NavimenuSearchPara para) {
		logger.debug("NavimenuAction.loadNavimenuListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(
				"  select new map( id as id ,title as title,sysresourceId as sysresourceId,remark as remark) from Navimenu  where 1=1 ");
		if (!SunitStringUtil.isBlankOrNull(para.getStitle())) {
			para.setStitle("%" + para.getStitle() + "%");
			hqlBuf.append("  and  title like :Stitle");
		}

		Paging paging = navimenuManager.paging(Paging.getCountForHQL(hqlBuf).toString(), hqlBuf.toString(), rows, page,
				para);
		Map map = new HashMap();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = getJsonConfig();
		jc.registerJsonValueProcessor(java.sql.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.util.Date.class, new DateProcessor());
		jc.registerJsonValueProcessor(java.sql.Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	@RequestMapping("/isExists.action")
	public String isExists(Navimenu navimenu, PrintWriter pw) {
		logger.debug("NavimenuAction.isExists()");
		Map map = new HashMap();
		map.put("success", "false");
		List list;
		try {
			list = navimenuManager.isExtists(navimenu, navimenu.getId() != null);
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
	public String getSelectBody(PrintWriter pw, String id, HttpServletRequest request, HttpServletResponse response,
			final String optionId, final String optionName) {
		List list = navimenuManager.getAll();
		JsonConfig jc = getJsonConfig();
		// jc.setJsonPropertyFilter(new PropertyFilter() {
		// public boolean apply(Object arg0, String arg1, Object arg2) {
		// if (arg1.equals(optionId) || arg1.equals(optionName)
		// || arg1.equals("list") || arg1.equals("success"))
		// return false;
		// else
		// return true;
		// }
		// });

		Map map = new HashMap();
		map.put("success", true);
		map.put("list", list);
		String s = JSONObject.fromObject(map, jc).toString();
		pw.print(s);
		return null;

	}

	@InitBinder
	public void navimenuBinder(WebDataBinder binder) {

	}

	public static class NavimenuSearchPara {

		private java.lang.String stitle;

		public void setStitle(java.lang.String stitle) {
			this.stitle = stitle;
		}

		public java.lang.String getStitle() {
			return stitle;
		}

	}

}
