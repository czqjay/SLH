package com.sunit.sysmanager.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SessionContext;
import com.sunit.global.util.SunitBeanUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.Depart;
import com.sunit.sysmanager.po.Role;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.DepartManager;
import com.sunit.sysmanager.service.JobsManager;
import com.sunit.sysmanager.service.RoleManager;
import com.sunit.sysmanager.service.SysResourceManager;
import com.sunit.sysmanager.service.UserManager;

/**
 * 
 * 
 * 类名称：UserAction 类描述：管理用戶增刪改查等操作 创建人：liangrujian 创建时间：Mar 10, 2014 2:57:36 PM
 * 修改人： 修改时间：Mar 10, 2014 2:57:36 PM 修改备注：
 * 
 * @version
 * 
 */
@Controller
@RequestMapping("/user")
public class UserAction extends BaseAction {

	static Logger logger = Logger.getLogger(UserAction.class);
	@Autowired
	private UserManager userManager;

	@Autowired
	private JobsManager jobsManager;

	@Autowired
	private RoleManager roleManager;

	@Autowired
	private DepartManager departManager;

	@Autowired
	private SysResourceManager sysresourceManager;

	/**
	 * 用户list页面数据提供者
	 * 
	 * @Title: loadUserListDataGrid
	 * @Description: 用户list页面数据提供者
	 * @param
	 * @param pw
	 *            打印流参数pw
	 * @param
	 * @param request
	 * @param
	 * @param response
	 * @param
	 * @param rows
	 *            列表显示数据 行数 设置参数rows
	 * @param
	 * @param page
	 *            列表显示数据 总页数 设置参数page
	 * @param
	 * @param para
	 *            搜索类参数para
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:39:07 AM
	 */
	@RequestMapping("/loadUserListDataGrid.action")
	public String loadUserListDataGrid(PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response,
			final int rows, int page, UserSearchPara para) {
		logger.debug("UserAction.loadUserListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf
				.append("select new map(t.id as id ,t.station as station,t.accountName as accountName,t.userName as userName,t.phone as userPhone,t.address as userAddress,t.datatype as datatype,t.isOpen as isOpen,t.userSource as userSource,t.creator as creator,t.beginDate as beginDate) from User t where t.id<>'0' and t.flag='1' and datatype<>'2' ");
		if (!StringUtils.isBlank(para.getAccountName())) {
			para.setAccountName("%" + para.getAccountName() + "%");
			hqlBuf.append("  and t.accountName like:AccountName");
		}
		if (!StringUtils.isBlank(para.getUserName())) {
			para.setUserName("%" + para.getUserName() + "%");
			hqlBuf.append("  and t.userName like:UserName");
		}
		if (!StringUtils.isBlank(para.getPhone())) {
			para.setPhone("%" + para.getPhone() + "%");
			hqlBuf.append("  and t.phone like:Phone");
		}

		if (!("null".equals(para.getBeginDateStrat() + "") || StringUtils
				.isBlank(para.getBeginDateStrat() + ""))) {
			hqlBuf.append("  and t.beginDate >=:BeginDateStrat");
		}
		if (!("null".equals(para.getBeginDateEnd() + "") || StringUtils
				.isBlank(para.getBeginDateEnd() + ""))) {
			hqlBuf.append("  and t.beginDate<=:BeginDateEnd");
		}
		if (!StringUtils.isBlank(para.getDatatype())) {
			para.setDatatype("%" + para.getDatatype() + "%");
			hqlBuf.append("  and t.datatype like:Datatype");
		}
		hqlBuf.append(" order by t.userName desc ");
		Paging paging = userManager.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	/**
	 * 编辑用户记录经过action
	 * 
	 * @Title: userUpdate
	 * @Description: 编辑用户记录经过action
	 * @param
	 * @param pw
	 *            打印流参数pw
	 * @param
	 * @param id
	 *            当前用户id
	 * @param
	 * @param request
	 * @param
	 * @param response
	 * @param
	 * @return 返回请求界面的jsp路径
	 * @return String 返回字符串类型
	 * @author 梁汝健 Mar 10, 2014 11:41:06 AM
	 */
	@RequestMapping("/userUpdate.action")
	public String userUpdate(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("UserAction.userUpdate()");
		if (null != id) {
			User user = userManager.get(id);
			request.setAttribute("user", user);
		}
		return "user/userUpdate";
	}

	/**
	 * 删除用户记录
	 * 
	 * @Title: userDelete
	 * @Description:删除用户记录
	 * @param
	 * @param pw
	 *            打印流参数pw
	 * @param
	 * @param ids
	 *            需要删除的用户记录id集合
	 * @param
	 * @param request
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 2:43:11 PM
	 */
	@RequestMapping("/userDelete.action")
	public String userDelete(PrintWriter pw, String ids,
			HttpServletRequest request) {
		logger.debug("UserAction.userDelete()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			SunitStringUtil util = new SunitStringUtil();
			String str = util.getStringUtil(idArr); 
			Integer integer = userManager.executeUpdate(
					"update User t set t.flag='0' where t.id in(" + str + ")",
					idArr);
			if (integer.intValue() > 0) {
				map.put("success", true);
			} else { 
				map.put("success", false);
				map.put("msg", "记录不存在");
			}
		} catch (Exception e) {
			map.put("success", false);
			map.put("msg", e.getMessage());
			logger.error("删除用户记录异常：", e);
		}
		request.setAttribute("invokeResultForLog", map.get("success"));
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	/**
	 * 保存用户记录
	 * 
	 * @Title: userSave
	 * @Description: 保存用户记录
	 * @param
	 * @param pw
	 *            打印流参数pw
	 * @param
	 * @param user
	 *            页面表单组装实体
	 * @param
	 * @param departId
	 *            部门id
	 * @param
	 * @param deptSelectUserId
	 *            所属部门id集合
	 * @param
	 * @param jobsId
	 *            职务id
	 * @param
	 * @param password
	 *            用户登录密码
	 * @param
	 * @param request
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 2:46:44 PM
	 */
	@RequestMapping("/userSave.action")
	public String userSave(PrintWriter pw, User user, String departId,
			String deptSelectUserId, String jobsId, String password,
			HttpServletRequest request) {
		logger.debug("UserAction.userSave()");
		Timestamp t = new Timestamp(System.currentTimeMillis());// 获得系统当前日期
		String userId = SessionContext.getUserId(request); // 获得当前用户ID
		String actionErrType = "";
		String Epassword = "";// 接收加密后的密码
		if (!StringUtils.isBlank(user.getId())) {
			actionErrType = "修改用户记录";
		} else {
			actionErrType = "新增用户记录";
			user.setIsOpen("0");
			user.setUserSource("0"); // 设置数据来源
			user.setFlag("1"); // 0 无效 1有效
			user.setBeginDate(t); // 将系统当前日期设进去
			user.setCreator(userId); // 将当前登录的用户id设置进去
			Epassword = new Md5PasswordEncoder().encodePassword(password, ""); // 接表单的密码过来，MD5加密后，再保存。
			user.setPwd(Epassword);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			if (!StringUtils.isBlank(deptSelectUserId)) {
				String[] deptIds = deptSelectUserId.split(",");
				for (int index = 0; index < deptIds.length; index++) {
					Depart dept = departManager.get(deptIds[index]);
					user.getDeparts().add(dept);
				}
			}
			User u = null;
			String userDatatype = request.getParameter("datatype") == null ? ""
					: request.getParameter("datatype");
			if (!StringUtils.isBlank(user.getId())) { // 修改用户信息
				u = userManager.get(user.getId());
				if (!"".equals(userDatatype)) { // 如果是userDatatype不为空，则是管理员修改用户信息。有权限修改角色。
					if ("1".equals(u.getDatatype())) { // 业务管理员类型
						u.getRoles().remove((roleManager.get("2")));
					} else if ("3".equals(u.getDatatype())) {// 考评管理员类型
						u.getRoles().remove((roleManager.get("1")));
					} else if ("0".equals(u.getDatatype())) {
						u.getRoles().remove(roleManager.get("3"));// 普通用户
					}
					List<Role> roleList = new ArrayList<Role>();
					if ("1".equals(userDatatype)) { // 业务管理员类型
						roleList.add(roleManager.get("2"));
						u.setRoles(roleList);
					} else if ("3".equals(userDatatype)) {// 考评管理员类型
						roleList.add(roleManager.get("1"));
						u.setRoles(roleList);
					} else if ("0".equals(userDatatype)) {
						roleList.add(roleManager.get("3"));// 普通用户
						u.setRoles(roleList);
					}
				} else {// 如果是userDatatype为空，则是用户个人信息修改。则没有权限改变角色。
					user.setRoles(u.getRoles());
				}
				user.setJobs(u.getJobs());
				SunitBeanUtils.copyProperties(user, u); // form 页面表单对象 user ,
														// 覆盖数据库中数据对象 u
				userManager.save(u);

			} else {// 新增用户
				userManager.save(user);
				List<Role> roleList = new ArrayList<Role>();
				if ("1".equals(userDatatype)) { // 业务管理员类型
					roleList.add(roleManager.get("2"));
					user.setRoles(roleList);
				} else if ("3".equals(userDatatype)) {// 考评管理员类型
					roleList.add(roleManager.get("1"));
					user.setRoles(roleList);
				} else {
					roleList.add(roleManager.get("3"));// 普通用户
					user.setRoles(roleList);
				}
				userManager.save(user);
			}
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error(actionErrType + "出现错误！", e);
		}
		request.setAttribute("invokeResultForLog", map.get("success"));
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	/**
	 * 加载当前部门包含的所有用户
	 * 
	 * @Title: loadUserForDeptDataGrid
	 * @Description: 加载当前部门包含的所有用户
	 * @param
	 * @param pw
	 *            打印流pw
	 * @param
	 * @param request
	 * @param
	 * @param deptId
	 *            当前部门id
	 * @param
	 * @param response
	 * @param
	 * @param rows
	 *            列表显示数据 行数 设置参数rows
	 * @param
	 * @param page
	 *            列表显示数据 总页数 设置参数page
	 * @param
	 * @param para
	 *            搜索类参数para
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:09:43 PM
	 */
	@RequestMapping("/loadUserForDeptDataGrid.action")
	public String loadUserForDeptDataGrid(PrintWriter pw,
			HttpServletRequest request, String deptId,
			HttpServletResponse response, final int rows, int page,
			UserSearchPara para) {
		logger.debug("UserAction.loadUserForDeptDataGrid()");

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf
				.append("select new map(t.id as id ,t.accountName as accountName,t.userName as userName,t.phone as phone,t.address as userAddress,d.id as deptId) from User t left outer join t.departs as d where t.id<>'0' and t.flag='1' and t.datatype<>'2' and d.id='"
						+ deptId + "'");
		if (!StringUtils.isBlank(para.getUserName())) {
			para.setUserName("%" + para.getUserName() + "%");
			hqlBuf.append("  and t.userName like:UserName");
		}
		hqlBuf.append(" order by t.userName desc ");
		Paging paging = userManager.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	/**
	 * 加载除了当前部门的用户外的其他用户，供选择。
	 * 
	 * @Title: loadDeptOthersUserDataGrid
	 * @Description: 加载除了当前部门的用户外的其他用户，供选择。
	 * @param
	 * @param pw
	 *            打印流pw
	 * @param
	 * @param request
	 * @param
	 * @param deptId
	 *            当前部门id
	 * @param
	 * @param response
	 * @param
	 * @param rows
	 *            列表显示数据 行数 设置参数rows
	 * @param
	 * @param page
	 *            列表显示数据 总页数 设置参数page
	 * @param
	 * @param para
	 *            搜索类参数para
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:12:55 PM
	 */
	@RequestMapping("/loadDeptOthersUserDataGrid.action")
	public String loadDeptOthersUserDataGrid(PrintWriter pw,
			HttpServletRequest request, String deptId,
			HttpServletResponse response, final int rows, int page,
			UserSearchPara para) {
		logger.debug("UserAction.loadDeptOthersUserDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf
				.append("select new map(t.id as id ,t.accountName as accountName,t.userName as userName,t.phone as phone,t.address as userAddress) "
						+ "from User t where t.id<>'0' and t.flag='1' and t.isOpen='0' and t.datatype<>'2' and t.departs.size='0' and t.id not in(select b.id from User b join b.departs as d where d.id='"
						+ deptId + "')");
		if (!StringUtils.isBlank(para.getUserName())) {
			para.setUserName("%" + para.getUserName() + "%");
			hqlBuf.append("  and t.userName like:UserName");
		}
		hqlBuf.append(" order by t.userName desc ");
		Paging paging = userManager.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	/**
	 * 在职务列表点击设置用户时请求到加载页面
	 * 
	 * @Title: allocateUserForJobs
	 * @Description: 在职务列表点击设置用户时请求到加载页面
	 * @param
	 * @param request
	 * @param
	 * @param jobsId
	 *            当前职务id
	 * @param
	 * @param response
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:18:45 PM
	 */
	@RequestMapping("/allocateUserForJob.action")
	public String allocateUserForJob(HttpServletRequest request, String jobsId,
			HttpServletResponse response) {
		logger.debug("UserAction.allocateUserForJob()");
		request.setAttribute("JOBS_ID", jobsId);
		return "job/allocateUserForJob";

	}

	/**
	 * 加载当前职务包含的所有用户
	 * 
	 * @Title: loadJobsForUserDataGrid
	 * @Description: 加载当前职务包含的所有用户
	 * @param
	 * @param pw
	 *            打印流
	 * @param
	 * @param request
	 * @param
	 * @param jobsId
	 *            当前职务id
	 * @param
	 * @param response
	 * @param
	 * @param rows
	 *            列表显示数据 行数 设置参数rows
	 * @param
	 * @param page
	 *            列表显示数据 总页数 设置参数page
	 * @param
	 * @param para
	 *            搜索类参数para
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:22:29 PM
	 */
	@RequestMapping("/loadJobsForUserDataGrid.action")
	public String loadJobsForUserDataGrid(PrintWriter pw,
			HttpServletRequest request, String jobsId,
			HttpServletResponse response, final int rows, int page,
			UserSearchPara para) {
		logger.debug("UserAction.loadJobsForUserDataGrid()");

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf
				.append("select new map(t.id as id ,t.accountName as accountName,t.userName as userName,t.phone as phone,t.address as userAddress) from User t left outer join t.jobs as j where t.id<>'0' and t.flag='1' and t.datatype<>'2' and j.id='"
						+ jobsId + "'");
		if (!StringUtils.isBlank(para.getUserName())) {
			para.setUserName("%" + para.getUserName() + "%");
			hqlBuf.append("  and t.userName like:UserName");
		}
		hqlBuf.append(" order by t.userName desc ");
		Paging paging = userManager.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	/**
	 * 在角色列表点击设置用户时请求到加载页面
	 * 
	 * @Title: allocateUserForRole
	 * @Description: 在角色列表点击设置用户时请求到加载页面
	 * @param
	 * @param request
	 * @param
	 * @param roleId
	 *            当前角色id
	 * @param
	 * @param response
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:30:41 PM
	 */
	@RequestMapping("/allocateUserForRole.action")
	public String allocateUserForRole(HttpServletRequest request,
			String roleId, HttpServletResponse response) {
		logger.debug("UserAction.allocateUserForRole()");
		request.setAttribute("ROLE_ID", roleId);
		return "role/allocateUserForRole";

	}

	/**
	 * 加载当前角色包含的所有用户
	 * 
	 * @Title: loadRoleForUserDataGrid
	 * @Description: 加载当前角色包含的所有用户
	 * @param
	 * @param pw
	 *            打印流pw
	 * @param
	 * @param request
	 * @param
	 * @param roleId
	 *            当前角色id
	 * @param
	 * @param response
	 * @param
	 * @param rows
	 *            列表显示数据 行数 设置参数rows
	 * @param
	 * @param page
	 *            列表显示数据 总页数 设置参数page
	 * @param
	 * @param para
	 *            搜索类参数para
	 * @param
	 * @return
	 * @return String 返回类型
	 * @author 梁汝健 Mar 10, 2014 10:32:07 PM
	 */
	@RequestMapping("/loadRoleForUserDataGrid.action")
	public String loadRoleForUserDataGrid(PrintWriter pw,
			HttpServletRequest request, String roleId,
			HttpServletResponse response, final int rows, int page,
			UserSearchPara para) {
		logger.debug("UserAction.loadRoleForUserDataGrid()");

		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf
				.append("select new map(t.id as id ,t.accountName as accountName,t.userName as userName,t.phone as phone,t.address as address) from User t left outer join t.roles as r where t.id<>'0' and t.flag='1' and t.datatype<>'2' and r.id='"
						+ roleId + "'");
		if (!StringUtils.isBlank(para.getUserName())) {
			para.setUserName("%" + para.getUserName() + "%");
			hqlBuf.append("  and t.userName like:UserName");
		}
		hqlBuf.append(" order by t.userName desc ");
		Paging paging = userManager.paging(Paging.getCountForHQL(hqlBuf)
				.toString(), hqlBuf.toString(), rows, page, para);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("records", paging.getTotalRow());// 记录总数
		map.put("total", paging.getTotalPage());// 总页数
		map.put("page", paging.getPage());// 当前页数
		map.put("rows", paging.getList());// 记录数据
		JsonConfig jc = new JsonConfig();
		jc.registerJsonValueProcessor(Timestamp.class, new DateProcessor());
		String reString = JSONObject.fromObject(map, jc).toString();
		pw.print(reString);
		return null;
	}

	/**
	 * admin修改所有用户的密码
	 * 
	 * @Title: personPaswUpdate
	 * @Description: admin修改所有用户的密码
	 * @param
	 * @param user
	 *            当前修改实体
	 * @param
	 * @param pw
	 *            打印流pw
	 * @param
	 * @param request
	 * @param
	 * @param response
	 * @param
	 * @return
	 * @return String 返回类型
	 * @throws
	 * @author 梁汝健 Mar 13, 2014 9:34:14 AM
	 */
	@RequestMapping("/personPaswUpdate.action")
	public String personPaswUpdate(User user, PrintWriter pw,
			HttpServletRequest request, HttpServletResponse response) {
		logger.debug("UserAction.personPaswUpdate()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		try {
			String pastPasword = new Md5PasswordEncoder().encodePassword(user
					.getPwd(), ""); // 加密后的密码串
			Integer integer = userManager.executeUpdate(
					"update User t set t.pwd=? where t.accountName=?",
					pastPasword, SessionContext.getAttribute(request, "userAccount"));
			if (integer.intValue() > 0) {
				map.put("success", "1");
			} else {
				map.put("success", false);
				map.put("msg", "记录不存在");
				map.put("error", "修改密码失败！");
			}
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("personPaswUpdate：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	/**
	 * 验证用户信息某些字段是否唯一
	 * 
	 * @Title: isExists
	 * @Description: 验证用户信息某些字段是否唯一
	 * @param
	 * @param user
	 *            当前验证实体
	 * @param
	 * @param pw
	 *            打印流pw
	 * @param
	 * @return
	 * @return String 返回类型
	 * @throws
	 * @author 梁汝健 Mar 10, 2014 2:54:28 PM
	 */
	@RequestMapping("/isExists.action")
	public String isExists(User user, PrintWriter pw) {
		logger.debug("UserAction.isExists()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", false);
		user.setFlag("1");
		List<User> list;
		try {
			list = userManager.isExtists(user, user.getId() != null);
			if (list == null || list.size() == 0 || list.isEmpty()) {
				map.put("success", "1");
			} else {
				map.put("success", false);
			}
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("isExists：", e);
		}

		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		logger.debug("UserAction.initBinder()");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sf, true));
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public JobsManager getJobsManager() {
		return jobsManager;
	}

	public void setJobsManager(JobsManager jobsManager) {
		this.jobsManager = jobsManager;
	}

	public DepartManager getDepartManager() {
		return departManager;
	}

	public void setDepartManager(DepartManager departManager) {
		this.departManager = departManager;
	}

	public RoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(RoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public SysResourceManager getSysresourceManager() {
		return sysresourceManager;
	}

	public void setSysresourceManager(SysResourceManager sysresourceManager) {
		this.sysresourceManager = sysresourceManager;
	}
	
	
	@RequestMapping("/getSelectBody.action")
	public String getSelectBody(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletRequest response,final String optionId,final String optionName) {
		List list = userManager.getAll();
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
	 * 搜索内部类
	 * 
	 * @author 梁汝健
	 * @Description: 接收页面传过来的参数组装成内部类（页面传过来的参数要跟内部类的属性名保持一致）
	 */
	public static class UserSearchPara {

		private String accountName; // 登录账号

		private String userName; // 用户名称

		private String phone; // 用户联系电话

		private Date beginDateStrat; // 用户开始创建时间

		private Date beginDateEnd;// 用户结束时间

		private String datatype; // 用户类型

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public Date getBeginDateStrat() {
			return beginDateStrat;
		}

		public void setBeginDateStrat(Date beginDateStrat) {
			this.beginDateStrat = beginDateStrat;
		}

		public String getDatatype() {
			return datatype;
		}

		public void setDatatype(String datatype) {
			this.datatype = datatype;
		}

		public Date getBeginDateEnd() {
			return beginDateEnd;
		}

		public void setBeginDateEnd(Date beginDateEnd) {
			this.beginDateEnd = beginDateEnd;
		}

	}

}
