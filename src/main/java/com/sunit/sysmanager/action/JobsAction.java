package com.sunit.sysmanager.action;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SunitBeanUtils;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.Jobs;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.JobsManager;
import com.sunit.sysmanager.service.UserManager;
/**
 * 
 * 类名称：JobsAction
 * 类描述：职务管理的增删改查
 * 创建人：liangrujian
 * 创建时间：Jul 22, 2013 10:32:52 AM
 * 修改人：liangrujian
 * 修改时间：Jul 22, 2013 10:32:52 AM
 * 修改备注：
 * @version 
 *
 */
@Controller
@RequestMapping("/job")
public class JobsAction extends BaseAction {

	static Logger logger = Logger.getLogger(JobsAction.class);
	
	@Autowired
	private JobsManager jobsManager;
	@Autowired
	private UserManager userManager;
	
	
	/**
	 * 职务列表数据提供者
	* @Title: loadJobsListDataGrid 
	* @Description:  职务列表数据提供者
	* @param @param pw
	* @param @param request
	* @param @param response
	* @param @param rows 列表显示数据 行数 设置参数rows
	* @param @param page 列表显示数据 总页数 设置参数page
	* @param @param para 搜索类参数para 	* @param @return     
	* @return String  返回类型
	* @author 梁汝健
	* Mar 12, 2014 9:32:46 PM
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/loadJobListDataGrid.action")
	public String loadJobsListDataGrid(PrintWriter pw, HttpServletRequest request,
			HttpServletResponse response, final int rows, int page,
			JobSearchPara para) {
		logger.debug("JobsAction.loadJobsListDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("select new map(id as id , name as name) from Jobs  where 1=1");
		if (!StringUtils.isBlank(para.getJobName())) {
			para.setJobName("%" + para.getJobName() + "%");
			hqlBuf.append("  and name like:JobName");
			
		}
		hqlBuf.append("  order by id desc  ");
		Paging paging = jobsManager.paging(Paging.getCountForHQL(hqlBuf).toString(),
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
	 * 修改职务记录经过action
	* @Title: updateJobs 
	* @Description: 修改职务记录经过action
	* @param @param pw 打印流pw
	* @param @param id 当前职务记录id
	* @param @param request
	* @param @param response
	* @param @return     
	* @return String  返回类型
	* @author 梁汝健
	* Mar 12, 2014 9:37:03 PM
	 */
	@RequestMapping("/jobUpdate.action")
	public String jobUpdate(PrintWriter pw, String id, HttpServletRequest request,
			HttpServletResponse response) {	
		//request.setAttribute("TABLE_ID", id);
		logger.debug("JobsAction.jobUpdate()");
		if(null!=id){
			Jobs jobs = jobsManager.get(id);
			request.setAttribute("jobs", jobs);
		}
		return "job/jobUpdate";
	}
	

	/**
	 * 删除职务记录信息
	* @Title: JobDelete 
	* @Description: 删除职务记录信息
	* @param @param pw 打印流pw
	* @param @param ids 删除职务记录id集合
	* @param @return     
	* @return String  返回类型
	* @author 梁汝健
	* Mar 12, 2014 9:39:21 PM
	 */
	@RequestMapping("/JobDelete.action")
	public String JobDelete(PrintWriter pw, String ids) {
		logger.debug("JobsAction.JobDelete()");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			jobsManager.delete(idArr);
			map.put("success", true);
		}catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除职务记录：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	
	
	/**
	 * 职务信息保存
	* @Title: jobSave 
	* @Description:职务信息保存 
	* @param @param pw 打印流pw
	* @param @param jobs 当前对应表单实体
	* @param @param request
	* @param @return
	* @return String  返回类型
	* @author 梁汝健
	* Mar 12, 2014 9:43:06 PM
	 */
	@RequestMapping("/jobSave.action")
	public String jobSave(PrintWriter pw, Jobs jobs,HttpServletRequest request ) {
			logger.debug("JobsAction.jobSave()");
			String actionErrType = "";
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("success", false);
			map.put("error", "");
			try {
				List<Jobs> list; 
					if(!StringUtils.isBlank(jobs.getId())){
						list = jobsManager.find("from Jobs t where t.name=? and t.id<>?", jobs.getName(),jobs.getId());
					}else {
						list = jobsManager.find("from Jobs t where t.name=? ", jobs.getName());
					}
					if ( list==null || list.size() == 0 || list.isEmpty()) {
						map.put("success", false);
					}else{
						map.put("success", false);
						map.put("error", "false");
						pw.print(JSONObject.fromObject(map).toString());
						return null;
					}
				
				if (!StringUtils.isBlank(jobs.getId())) {
					actionErrType = "修改职务记录";
				    Jobs job =jobsManager.get( jobs.getId() );
				    jobs.setUser( job.getUser());
					SunitBeanUtils.copyProperties( jobs , job ); //form 表单对象 jobs 覆盖数据库中数据对象 job
				    jobsManager.save(job);
				} else {
					actionErrType = "新增职务记录";
					jobsManager.save(jobs);
				}
				map.put("success", true);
			} catch (Exception e) {
				map.put("msg",actionErrType+"失败");
				logger.error(actionErrType, e);
			}
			pw.print(JSONObject.fromObject(map).toString());
			return null;
		}
			
		/**
		 * 保存当前选中的用户记录添加到当前职务中
		* @Title: userForJobSave 
		* @Description: 保存当前选中的用户记录添加到当前职务中
		* @param @param pw 打印流pw
		* @param @param jobsId 当前职务id
		* @param @param ids 选中用户记录id集合
		* @param @param request
		* @return void  返回类型
		* @author 梁汝健
		* Mar 12, 2014 10:27:31 PM
		 */
		@RequestMapping("/userForJobSave.action")
		public void userForJobSave(PrintWriter pw, String jobsId,String ids,HttpServletRequest request) {
			logger.debug("JobsAction.userForJobSave()");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("success", false); 
			try {
			 if(!StringUtils.isBlank(jobsId)){
				 Jobs job = jobsManager.get( jobsId );
				  Set<User> userSet =  job.getUser();
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
									   job.getUser().add(user);	
		            		 }
						  }	  
						  jobsManager.save( job );
						  map.put("success", true); 
				     }
			 }else{
					map.put("success", false);
					map.put("msg", "职务不存在!");
				}
			} catch (Exception e) {
				map.put("success", false);
				map.put("msg", e.getMessage());
				logger.error(e);
			}
			pw.print(JSONObject.fromObject(map).toString());

		}
		
		
		/**
		 * 删除当前职务中选中的用户记录
		* @Title: userForJobDel 
		* @Description: 删除当前职务中选中的用户记录 
		* @param @param pw 打印流pw
		* @param @param ids 需要删除的用户记录id集合
		* @param @param jobsID 当前职务id
		* @param @return     
		* @return String  返回类型
		* @author 梁汝健
		* Mar 12, 2014 10:24:27 PM
		 */
		@RequestMapping("/userForJobDel.action")
		public String userForJobDel(PrintWriter pw, String ids,String jobsID) {
			logger.debug("JobsAction.userForJobDel()");
			Map<String,Object> map = new HashMap<String,Object>();
			Jobs job =null;
	    	try {
				if(!StringUtils.isBlank( jobsID )){ 
					 job = jobsManager.get( jobsID );
				}
				 map.put("success", false);	
				Set<User> userSet = job.getUser();
				for(Iterator iterator = userSet.iterator(); iterator.hasNext();) {
						User user = (User) iterator.next();
						if(ids.indexOf(user.getId())>-1){ 
							iterator.remove();
						}	
				 }
					jobsManager.save( job );
					map.put("success", true);	
			} catch (Exception e) {
				map.put("msg", e.getMessage());
				logger.error("删除职务中的选中的用户记录：", e);
			}
			pw.print(JSONObject.fromObject(map).toString());
			return null;
		}
		
		
		
		public JobsManager getJobsManager() {
			return jobsManager;
		}


		public void setJobsManager(JobsManager jobsManager) {
			this.jobsManager = jobsManager;
		}
	

	/**
	 * VO by 搜索
	 * 
	 * @author 梁汝健
	 * 
	 */
	public static class JobSearchPara {

		private String jobName;

		public String getJobName() {
			return jobName;
		}

		public void setJobName(String jobName) {
			this.jobName = jobName; 
		}

	}



}