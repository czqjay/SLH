package com.sunit.navimenu.action;

import java.io.PrintWriter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.util.SunitStringUtil;
import com.sunit.navimenu.po.Navimenu;
import com.sunit.relations.service.RelationsManager;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.service.NaviMenuManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping("/navimenu")
public class NavimenuAction extends AbstractNavimenuAction {

	static Logger logger = Logger.getLogger(NavimenuAction.class);

	@Autowired
	RelationsManager relationsManager;

	@RequestMapping("/navimenuSave.action")
	public String navimenuSave(PrintWriter pw, Navimenu navimenu, HttpServletRequest request, String parentMenu)
			throws ParseException {
		logger.debug("NavimenuAction.navimenuSave()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (SunitStringUtil.isBlankOrNull(parentMenu)) {
				navimenu.setParent(null);
			} else {
				navimenu.setParent(navimenuManager.get(parentMenu));
			}
			if (SunitStringUtil.isBlankOrNull(navimenu.getId())) {
				navimenu.setId(null);
			
			}
			navimenuManager.save(navimenu);
			map.put("id", navimenu.getId()); 
			map.put("title", navimenu.getTitle()); 
			map.put("success", true); 
		} catch (Exception e) {
			map.put("msg", "新增菜单表失败");
			logger.error("新增菜单表", e);
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

	@RequestMapping("/getNodesById.action")
	public void getNodesById(PrintWriter pw, HttpServletRequest request, String id) {
		List<Navimenu> list = null;
		if (SunitStringUtil.isBlankOrNull(id)) {
			list = new Navimenu().getRoots();
			// list=relationsManager.getRoot(Navimenu.class);
		} else {
			Navimenu n = navimenuManager.get(id);
			list = n.getChild();
			// list =relationsManager.getChilds(Navimenu.class, id);  
		}
		JsonConfig c = this.getJsonConfig();
		c.setExcludes(new String[] { "roots", "childs", "parent" });
		pw.write(JSONArray.fromObject(list, c).toString());
	}

	@RequestMapping("/getNaviMenu.action")
	public void getNaviMenu(PrintWriter pw, HttpServletRequest request, String id) {
		logger.debug("NavimenuAction.getNaviMenu()");
		Map map = new HashMap();
		map.put("success", false);
		try {
			if (!StringUtils.isBlank(id)) {
				Navimenu m = navimenuManager.get(id);
				m.getParent();
				map.put("data", m);
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("获取菜单时发生错误", e);
		}
		JsonConfig c = this.getJsonConfig();
		c.setExcludes(new String[] { "roots", "childs" });
		pw.print(JSONObject.fromObject(map, c).toString());

	}
}
