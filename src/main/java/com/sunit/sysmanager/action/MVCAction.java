package com.sunit.sysmanager.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.sunit.global.base.Global.UploadAfterEvent;
import com.sunit.global.base.binder.BaseAction;
import com.sunit.global.base.dao.BaseDAO;
import com.sunit.global.base.exception.SLHRuntimeException;
import com.sunit.global.util.Paging;
import com.sunit.global.util.SessionContext;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.jsonlibConvert.DateProcessor;
import com.sunit.sysmanager.po.SysResource;
import com.sunit.sysmanager.po.User;
import com.sunit.sysmanager.service.NaviMenuManager;
import com.sunit.sysmanager.service.SysResourceManager;
import com.sunit.sysmanager.service.UserManager;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

/**
 * a simple example for CRUD
 * 
 * @author joye
 * 
 */
@Controller
@RequestMapping("/base")
public class MVCAction extends BaseAction {

	static Logger logger = Logger.getLogger(MVCAction.class);

	@Autowired
	BaseDAO dao;

	@Autowired
	UserManager um;

	@Autowired
	NaviMenuManager nm;


	@Autowired
	SysResourceManager sm;

	public static Map<String, HttpSession> sessionMap = new HashMap();

	
	@RequestMapping("exportTempxls.action")
	public void exportTempxls(HttpServletRequest request,
			HttpServletResponse response,String[] title, String [] rows) {
		

		
		   // 创建Excel的工作书册 Workbook,对应到一个excel文档
		                 HSSFWorkbook wb = new HSSFWorkbook();
		 
		                 // 创建Excel的工作sheet,对应到一个excel文档的tab
		                 HSSFSheet sheet = wb.createSheet("sheet1");
		  
//		                 for (int i = 0; i < list.size(); i++) {
////								array_type array_element = <T>[i];
//			                	 HSSFRow row = sheet.createRow(i);
//			                	 action.doAction(row, list.get(i));
//							}
		                 HSSFRow row = sheet.createRow(0);
		                 for (int i = 0; i < title.length; i++) {
		                	 row.createCell(i).setCellValue(title[i]); 
						}
		                 
		                 for (int i = 0; i <rows.length; i++) {
		                	 row = sheet.createRow(0);
//							array_type array_element = <T>[i]; 
		                	 int col=0;
		                	 Map<String, String> m = JSONObject.fromObject(rows[i]);
		                	 for(Map.Entry<String, String> entry :  m.entrySet()) {
		                		 row.createCell(col++).setCellValue(entry.getValue());
		                	 } 
		                	 
//		                	 row.createCell(0).setCellValue(rowObject.getServiceName());
//		 					row.createCell(1).setCellValue(rowObject.getServiceImageName());
//		 					row.createCell(2).setCellValue(rowObject.getStTypeName());
						}
		                 
		                 if(sheet.getLastRowNum()>0){ 
		                	 try {
		                		String  fileName="exprot.xls"; 
		                			response.addHeader("Content-Disposition", "attachment;filename="
		                					+ new String(fileName.getBytes("gb2312"), "ISO8859-1"));
//		                			response.addHeader("Content-Length", "" + remoteFile.length());
		                			response.setContentType("application/octet-stream");
//		                			 OutputStream toClient = new BufferedOutputStream(  
//		                	                    response.getOutputStream());  
 		                		 wb.write(response.getOutputStream()); 
 		                		
							} catch (IOException e) {
								e.printStackTrace();
							}finally { 
								try {
									wb.close(); 
									response.getOutputStream().flush();   
									response.getOutputStream().close();  
								} catch (IOException e) {
									e.printStackTrace();
								} 
							}
//		                	  FileOutputStream os = new FileOutputStream(xlsFile);
//		                      wb.write(os);
//		                      os.close();
		                 }

	}

	@RequestMapping("/getFile.action")
	public void getFile(HttpServletRequest request,
			HttpServletResponse response, String remoteFileName) {
		logger.debug("MVCAction.getFile()");
		String  p =SpringContextUtils.getGlobal().getUploadProtocol(); 
		if(p.toLowerCase().equals("nfs")){ 
			getFileByNFS(request, response, remoteFileName);
		} 
		else{
			getFileBySamba(request, response, remoteFileName);
		} 
		
	}
	
	
	public void getFileByNFS(HttpServletRequest request,
			HttpServletResponse response, String remoteFileName) {
		logger.debug("MVCAction.getFile()");
		InputStream in = null;
		OutputStream out = null;
		String remoteUrl = "";
		try {
			String fileName = remoteFileName; 
			remoteUrl = SpringContextUtils.getGlobal() 
					.getClusterFileServerDir() 
					+ "/" + fileName;
			 File remoteFile = new File(remoteUrl); 
			if (remoteFile == null || !remoteFile.exists()) { 
				logger.error("共享文件不存在:" + remoteUrl);
				return;
			}
			response.reset(); 
			in = new BufferedInputStream(new FileInputStream(remoteFile)); 
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes("gb2312"), "ISO8859-1"));
			response.addHeader("Content-Length", "" + remoteFile.length());
			response.setContentType("application/octet-stream");
			out = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[10240];
			while (in.read(buffer) != -1) {
				out.write(buffer);
				buffer = new byte[10240];
			}
		} catch (Exception e) {
			logger.error("文件下载:" + remoteUrl, e);
		} finally {
			try {
				if(out!=null)
					out.close();
				if(in!=null)
					in.close();
			} catch (IOException e) {
				logger.error("文件下载关闭流:", e);
			}
		}
	}
	
	
	public static  NtlmPasswordAuthentication getSMBAuth(){
		String  u = SpringContextUtils.getGlobal().getClusterFileServerUser();
		String p =SpringContextUtils.getGlobal().getClusterFileServerPwd();
		String d =SpringContextUtils.getGlobal().getClusterFileServerDomian(); 
		
		
//		u="smbUser";
//		p="vo_123"; 
//		d="xwtShare"; 
//		SpringContextUtils.getGlobal().setClusterFileServerDir("smb://xwtShare/upload");
//		System.out.println("d+u+p="+ d + u + p); 
		
		NtlmPasswordAuthentication auth =new NtlmPasswordAuthentication(d,u,p);
		return auth;
	} 
	
	public void getFileBySamba(HttpServletRequest request,
			HttpServletResponse response, String remoteFileName) {
		logger.debug("MVCAction.getFile()");
		InputStream in = null;
		OutputStream out = null;
		String remoteUrl = "";
		try {
			String fileName = remoteFileName; 
			remoteUrl = SpringContextUtils.getGlobal() 
					.getClusterFileServerDir() 
					+ "/" + fileName;
			SmbFile remoteFile = new SmbFile(remoteUrl,getSMBAuth());
			if (remoteFile == null) {
				logger.error("共享文件不存在:" + remoteUrl);
				return;
			}
			response.reset();
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(fileName.getBytes("gb2312"), "ISO8859-1"));
			response.addHeader("Content-Length", "" + remoteFile.length());
			response.setContentType("application/octet-stream");
			out = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[10240];
			while (in.read(buffer) != -1) {
				out.write(buffer);
				buffer = new byte[10240];
			}
		} catch (Exception e) {
			logger.error("文件下载:" + remoteUrl, e);
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
				logger.error("文件下载关闭流:", e);
			}
		}
	}
	
	
	
	@RequestMapping("/getRemoteImg.action")
	public void getRemoteImg(HttpServletRequest request, HttpServletResponse response,String remoteFileName,String upload) {
		logger.debug("MVCAction.getRemoteImg()");
		InputStream in = null;
		OutputStream out = null;
		String remoteUrl="";
		try { 
			if(StringUtils.isBlank(upload)){
				remoteUrl = "/waterMask";
			}else {
				remoteUrl = ""; 
			}
			String fileName = remoteFileName; 
			 remoteUrl = SpringContextUtils.getGlobal()
					.getClusterFileServerDir()
					+remoteUrl+"/" + fileName; 
			SmbFile remoteFile = new SmbFile(remoteUrl);
			logger.debug(remoteUrl);
			if (remoteFile == null) {
				logger.error("共享图片不存在:"+remoteUrl);  
				return ;
			} 
			response.reset();
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
//			response.addHeader("Content-Disposition", "attachment;filename=" 
//					+  new String( fileName.getBytes("gb2312"), "ISO8859-1" ));
//			response.addHeader("Content-Disposition", "attachment;filename=" 
//					+   fileName); 
//			response.addHeader("Content-Length", "" + remoteFile.length()); 
//			response.setContentType("application/octet-stream");
			out = new BufferedOutputStream(response.getOutputStream()); 
			byte[] buffer = new byte[10240];
			while (in.read(buffer) != -1) { 
				out.write(buffer);
				buffer = new byte[10240];
			} 
			out.flush();
		} catch (Exception e) { 
			logger.error("图片下载:"+remoteUrl, e);
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {  
				logger.error("图片下载关闭流:", e); 
			}
		} 
	}
	

	/**
	 * return to loginPage
	 * 
	 * @Title: login
	 * @Description:
	 * @param
	 * @param request
	 * @param
	 * @param userName
	 * @param
	 * @return
	 * @return String
	 * @throws
	 * @author joye Jun 16, 2013
	 */
	@RequestMapping("/login.action")
	public String login(HttpServletRequest request, String userName) {
		logger.debug("MVCAction.login()");
		return "/login.jsp";
	}

	/**
	 * 删除用户
	 * 
	 * @Title: delUsers
	 * @Description:
	 * @param
	 * @param pw
	 * @param
	 * @param ids
	 * @param
	 * @return
	 * @return String
	 * @throws
	 * @author joye Jun 16, 2013
	 */
	@RequestMapping("/delUsers.action")
	public String delUsers(PrintWriter pw, String ids) {
		logger.debug("MVCAction.delUsers()");
		Map map = new HashMap();
		map.put("success", false);
		try {
			String[] idArr = ids.split(",");
			um.delete(idArr);
			map.put("success", true);
		} catch (Exception e) {
			map.put("msg", e.getMessage());
			logger.error("删除用户：", e);
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	/**
	 * 对象是否存在
	 * 
	 * @Title: isExists
	 * @Description:
	 * @param
	 * @param u
	 * @param
	 * @param pw
	 * @param
	 * @return
	 * @return String
	 * @throws
	 * @author joye Jun 16, 2013
	 */
	@RequestMapping("/isExists.action")
	public String isExists(User u, PrintWriter pw) {
		logger.debug("MVCAction.isExists()");
		Map map = new HashMap();
		map.put("success", "false");
		List list = null;
		// List list = um.isExtists(u);
		if (list.size() == 0) {
			map.put("success", "1");
		}
		pw.print(JSONObject.fromObject(map).toString());
		return null;
	}

	/**
	 * return to gridPage
	 * 
	 * @Title: getList
	 * @Description:
	 * @param
	 * @param request
	 * @param
	 * @param response
	 * @param
	 * @return
	 * @return String
	 * @throws
	 * @author joye Jun 16, 2013
	 */
	@RequestMapping("/getList.action")
	public String getList(HttpServletRequest request,
			HttpServletRequest response) {
		logger.debug("MVCAction.getList()");
		return "plan/testPlan";
	}

	/**
	 * get Uusers by id, return to editPage
	 * 
	 * @Title: updateUsers
	 * @Description:
	 * @param
	 * @param pw
	 * @param
	 * @param id
	 * @param
	 * @param request
	 * @param
	 * @param response
	 * @param
	 * @return
	 * @return String
	 * @throws
	 * @author joye Jun 16, 2013
	 */
	@RequestMapping("/updateUsers.action")
	public String updateUsers(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletRequest response) {
		logger.debug("MVCAction.updateUsers()");
		User u = um.get(id);
		request.setAttribute("user", u);
		return "plan/testPlanFormUpdate";
	}

	/**
	 * jqGrid 数据提供者 返回JSON
	 * 
	 * @Title: loadDataGrid
	 * @Description:
	 * @param
	 * @param pw
	 *            resopnse stream
	 * @param
	 * @param request
	 * @param
	 * @param response
	 * @param
	 * @param rows
	 *            jqgrid 单页行数
	 * @param
	 * @param page
	 *            当前页数
	 * @param
	 * @param para
	 *            搜索VO
	 * @param
	 * @return
	 * @return String
	 * @throws
	 * @author joye Jun 16, 2013
	 */
	@RequestMapping("/loadDataGrid.action")
	public String loadDataGrid(PrintWriter pw, HttpServletRequest request,
			HttpServletRequest response, final int rows, int page,
			SearchPara para) {
		logger.debug("MVCAction.loadDataGrid()");
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf
				.append("select  new map(id as id , name as name , beginDate as beginDate, endDate as endDate,idCard as idCard ,isOpen as isOpen) from Users where 1=1");
		if (!StringUtils.isBlank(para.getSName())) {
			para.setSName("%" + para.getSName() + "%");
			hqlBuf.append("  and name like:SName");
		}
		if (para.getSDate() != null) {
			hqlBuf.append(" and beginDate >:SDate");
		}
		Paging paging = um.paging(Paging.getCountForHQL(hqlBuf).toString(),
				hqlBuf.toString(), rows, page, para);
		Map map = new HashMap();
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
	 * @Title: initBinder
	 * @Description: from对象注入action时,定义绑定参数行为
	 * @param
	 * @param binder
	 * @param
	 * @param depart
	 * @return void
	 * @throws
	 * @author joye May 23, 2013
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder, String depart) {
		logger.debug("MVCAction.initBinder()");
		// System.out.println("MVCAction.initBinder()");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sf, true));
	}

	@RequestMapping("/loadChildren.action")
	public void loadChildren(PrintWriter pw, String id,
			HttpServletRequest request) {

		String treeArgs = request.getParameter("postData");
		if (id.equals("root")) {
			if (treeArgs.equals("planManager")) {
				pw.write(JSONArray.fromObject(
						SessionContext.getAttribute(request,
								"planManagerRootMeunList")).toString());
			} else {
				pw.write(JSONArray.fromObject(
						SessionContext.getAttribute(request,
								"systemManagerRootMeunList")).toString());
			}
		} else {
			List authorityList = (List) SessionContext.getAttribute(request,
					"authorityList");

			List list = new ArrayList();
			SysResource sr = new SysResource();
			sr.setParent(id);
			for (SysResource s : sm.findByExample(sr)) {
				if (!authorityList.contains(s.getContent()))
					continue;
				Map map = new HashMap();
				map.put("title", s.getModuleCaption());
				map.put("url", s.getContent());
				map.put("id", s.getId());
				list.add(map);
			}
			pw.write(JSONArray.fromObject(list).toString());
		}
	}

	
	
	@RequestMapping("/loadMenuChildren.action")
	public void loadMenuChildren(PrintWriter pw, HttpServletRequest request,String id) {  

		String treeArgs = request.getParameter("postData");
	 
			List authorityList = (List) SessionContext.getAttribute(request,
					"authorityList");
			List list = new ArrayList();
//			SysResource sr = new SysResource();  
//			sr.setParent(id);
			
			List<SysResource> childSysResourceList =sm.find("from SysResource where parent=?  order by orderNum ", id) ;
			
//			for (SysResource s : sm.findByExample(sr)) {
			for (SysResource s : childSysResourceList) { 
				if (!authorityList.contains(s.getContent()))
					continue;
				Map map = new HashMap();
				map.put("title", s.getModuleCaption());
				map.put("url", s.getContent());
				map.put("id", s.getId()); 
				map.put("sourceType", s.getSourceType()); 
				list.add(map); 
//				data[v].title=data[v].moduleCaption  ; 
//    			data[v].key=data[v].id; 
//    			data[v].id=data[v].id;
//    			data[v].sourceType=data[v].sourceType;
//    			data[v].url=data[v].getContent;
				
				
			}
			pw.write(JSONArray.fromObject(list).toString());
	}
	
	
	
	@RequestMapping("/getSelectBody.action")
	public String getSelectBody(PrintWriter pw, String id,
			HttpServletRequest request, HttpServletRequest response) {
		// System.out.println("MVCAction.getSelectBody()");

		List list = um.getAll();
		JsonConfig jc = new JsonConfig();
		jc.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object arg0, String arg1, Object arg2) {
				if (arg1.equals("id") || arg1.equals("name")
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
	 * 處理request中的上傳文件,返回保存的文件名,多個文件時以 "," 分割
	 * 
	 * @Title: processFileUpload
	 * @Description:
	 * @param
	 * @param request
	 * @param
	 * @param FieldName
	 * @param
	 * @return
	 * @return String
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws
	 * @author joye Jun 4, 2015 9:08:51 PM
	 */ 
//	@RequestMapping("/processFileUpload.action")
//	public void processFileUpload(HttpServletRequest request,
//			 PrintWriter pw) {
//
//		Map<String, Object> map = new HashMap<String, Object>(); 
//		String fileName = "";
//		
//		String path = request.getSession().getServletContext().getRealPath(
//		"/WEB-INF/upload");
//		
//		
//		try { 
//			File dir = new File(path);
//			if (!dir.exists())
//				dir.mkdirs();
//			
//			MultipartHttpServletRequest mulitRequest = (MultipartHttpServletRequest) request;
//			
//			Enumeration  e =  request.getParameterNames(); 
//			while (e.hasMoreElements()){
//				Object o  = e.nextElement();
//				System.out.println("o.toString()=" + o.toString());
//			}
//			
//			Iterator it = mulitRequest.getFileNames();
//			while (it.hasNext()){
//				StringBuffer files = new StringBuffer(); 
//				String fieldName = it.next().toString();
//				List<MultipartFile> mFiles = mulitRequest.getFiles(fieldName);
//				for (MultipartFile mFile : mFiles) {
//					if (SunitStringUtil.isBlankOrNull(mFile.getOriginalFilename()))
//						continue;  
//					long fileSize = mFile.getSize()/1024;
//					Long allowsSize= Long.valueOf(request.getSession().getServletContext().getAttribute(fieldName+"_size").toString());
//					String  subDir= (String)request.getSession().getServletContext().getAttribute(fieldName+"_subDir");  
//					if(allowsSize!=null && allowsSize>0 && allowsSize<fileSize){
//						throw new Exception(mFile.getOriginalFilename()  +" 文件大小超出限制,不能超过"+allowsSize+"KB");
//					}
//					
//					
//					
//					fileName = System.currentTimeMillis()+"_"
//							+ mFile.getOriginalFilename();
//					
//					
//					// 创建子目录 
//					File f = null;
//					if(SunitStringUtil.isBlankOrNull(subDir)){
//						f  = new File(path + "/" + fileName); 
//					}else{
//						f  = new File(path + "/" + subDir+"/" + fileName);
//					}
//					
//					if (!f.exists())
//						f.mkdirs();
//					
//					mFile.transferTo(f);
//					if (files.length() > 0)
//						files.append(",");
//					files.append(fileName); 
//				}
//				map.put("success", true);
//				map.put(fieldName, files.toString() ); 
//			}
//		} catch (Exception e) {
//			map.put("success", false);
//			map.put("msg", e.getMessage()); 
//			logger.error("上传文件:", e);
//		} 
//		  pw.print(JSONObject.fromObject(map).toString()); 
//	}

	@RequestMapping("/reConfigratrionLog4j.action")
	public String reConfigratrionLog4j(HttpServletRequest request) {

		PropertyConfigurator.configure(request.getRealPath("/")
				+ "WEB-INF\\classes\\log4j.properties");
		return null;
	}

	
	
	/**
	 * 基于samba 协议的文件共享
	* @Title: processFileUploadBySamba 
	* @Description: 
	* @param @param request
	* @param @param response
	* @param @param pw     
	* @return void  
	* @throws 
	* @author joye 
	* Jun 4, 2016 10:33:30 PM
	 */
	public void  processFileUploadBySamba(HttpServletRequest request,
			HttpServletResponse response, PrintWriter pw){
		


		Map<String, Object> map = new HashMap<String, Object>();
		String fileName = "";

//		String path = request.getSession().getServletContext().getRealPath(
//				"/WEB-INF/upload");

		try {
//			File dir = new File(path);
//			if (!dir.exists())
//				dir.mkdirs();

			MultipartHttpServletRequest mulitRequest = (MultipartHttpServletRequest) request;

			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				Object o = e.nextElement();
				System.out.println("o.toString()=" + o.toString());
			}

			Iterator it = mulitRequest.getFileNames();
			while (it.hasNext()) {
				StringBuffer files = new StringBuffer();
				String fieldName = it.next().toString();
				List<MultipartFile> mFiles = mulitRequest.getFiles(fieldName);
				for (MultipartFile mFile : mFiles) {
					if (SunitStringUtil.isBlankOrNull(mFile
							.getOriginalFilename()))
						continue;
					long fileSize = mFile.getSize() / 1024;
					// Long allowsSize=
					// Long.valueOf(request.getSession().getServletContext().getAttribute(fieldName+"_size").toString());
					// String subDir=
					// (String)request.getSession().getServletContext().getAttribute(fieldName+"_subDir");
					Long allowsSize = Long.valueOf(SpringContextUtils
							.getGlobal().getUploadArgs(fieldName + "_size")
							.toString());
					String subDir = String.valueOf(SpringContextUtils
							.getGlobal().getUploadArgs(fieldName + "_subDir"));

					if (allowsSize != null && allowsSize > 0
							&& allowsSize < fileSize) {
						map.put("success", false);
						map.put("errFlag", "1");
						map.put("allowsSize", allowsSize);
						throw new Exception("文件大小超出限制,不能超过" + allowsSize + "KB");
						
					}
					fileName = System.currentTimeMillis() + "_"
							+ mFile.getOriginalFilename();
					// 创建子目录
					// File f = null;
					// if(SunitStringUtil.isBlankOrNull(subDir)){
					// f = new File(path + "/" + fileName);
					// }else{
					// f = new File(path + "/" + subDir+"/" + fileName);
					// }
					//					
					// if (!f.exists())
					// f.mkdirs();

					// mFile.transferTo(f);
					
					
					String remoteFileDir = SpringContextUtils.getGlobal()
							.getClusterFileServerDir();
					if (SunitStringUtil.isBlankOrNull(subDir)) {
						remoteFileDir = remoteFileDir;
					} else {
						remoteFileDir = remoteFileDir + "/" + subDir;
					}
					
					  
					SmbFile remoteFile = new SmbFile(remoteFileDir,getSMBAuth());    
					 
					if (!remoteFile.isDirectory()) {
						remoteFile.mkdir();
					}
					InputStream in = null;
					OutputStream out = null;
					in = mFile.getInputStream();

					out = new BufferedOutputStream(new SmbFileOutputStream(
							remoteFileDir + "/" + fileName));
					byte[] buffer = new byte[1024 * 10];
					while (in.read(buffer) != -1) {
						out.write(buffer);
						buffer = new byte[1024 * 10];
					}
					out.flush();
					out.close();

					if (files.length() > 0)
						files.append(",");

					if (SunitStringUtil.isBlankOrNull(subDir)) {
						files.append(fileName);
					} else {
						files.append(subDir + "/" + fileName);
					}

				}
				map.put(fieldName, files.toString());
				map.put("msg", files.toString());  //兼容 processFileUploadByWYSIWYG 的返回
				map.put("err", "");  //兼容 processFileUploadByWYSIWYG 的返回
				UploadAfterEvent event = new  UploadAfterEvent(this);
				event.setFiles(files.toString());
				event.setRequest(request);
				SpringContextUtils.getSpringContext().publishEvent(event);
				map.put("success", true);

			}
		}catch(SLHRuntimeException ex ){
			map.put("success", false);
			map.put("msg", ex.getMessage()); 
			map.put("err", ex.getMessage()); 
			logger.error("上传文件:", ex);
			if("1".equals(map.get("errFlag"))){//上传文件超出大小抛出的异常
				map.put("msg", "文件大小超出限制,不能超过" + map.get("allowsSize") + "KB"); 
			}
			
		} catch (Exception e) { 
			map.put("success", false);
			map.put("msg", "上传文件发生错误"); 
			map.put("err", "上传文件发生错误");
			logger.error("上传文件:", e);
			if("1".equals(map.get("errFlag"))){//上传文件超出大小抛出的异常
				map.put("msg", "文件大小超出限制,不能超过" + map.get("allowsSize") + "KB"); 
			}
		}
		response.setContentType("text/html;charset=UTF-8");
		String reString = JSONObject.fromObject(map).toString();
		pw.print(reString);
	} 
	
	
	
	/**
	 * 基于NFS mount  协议的文件共享
	* @Title: processFileUploadByNFS 
	* @Description: 
	* @param @param request
	* @param @param response
	* @param @param pw     
	* @return void  
	* @throws 
	* @author joye 
	* Jun 4, 2016 10:34:09 PM
	 */
	public void  processFileUploadByNFS(HttpServletRequest request,
			HttpServletResponse response, PrintWriter pw){
		


		Map<String, Object> map = new HashMap<String, Object>();
		String fileName = "";

//		String path = request.getSession().getServletContext().getRealPath(
//				"/WEB-INF/upload");

		try {
//			File dir = new File(path);
//			if (!dir.exists())
//				dir.mkdirs();

			MultipartHttpServletRequest mulitRequest = (MultipartHttpServletRequest) request;

			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				Object o = e.nextElement();
				System.out.println("o.toString()=" + o.toString());
			}

			Iterator it = mulitRequest.getFileNames();
			while (it.hasNext()) {
				StringBuffer files = new StringBuffer();
				String fieldName = it.next().toString();
				List<MultipartFile> mFiles = mulitRequest.getFiles(fieldName);
				for (MultipartFile mFile : mFiles) {
					if (SunitStringUtil.isBlankOrNull(mFile
							.getOriginalFilename()))
						continue;
					long fileSize = mFile.getSize() / 1024;
					// Long allowsSize=
					// Long.valueOf(request.getSession().getServletContext().getAttribute(fieldName+"_size").toString());
					// String subDir=
					// (String)request.getSession().getServletContext().getAttribute(fieldName+"_subDir");
					Long allowsSize=null;
					if(SpringContextUtils.getGlobal().getUploadArgs(fieldName + "_size")!=null) {
						 allowsSize = Long.valueOf(SpringContextUtils
									.getGlobal().getUploadArgs(fieldName + "_size")
									.toString());
					}
					String subDir=null;
					if(SpringContextUtils.getGlobal().getUploadArgs(fieldName + "_subDir")!=null) {
						 subDir = String.valueOf(SpringContextUtils
								.getGlobal().getUploadArgs(fieldName + "_subDir"));   
					}
			

					if (allowsSize != null && allowsSize > 0
							&& allowsSize < fileSize) {
						throw new Exception("文件大小超出限制,不能超过" + allowsSize + "KB");
					}
					fileName = System.currentTimeMillis() + "_"
							+ mFile.getOriginalFilename();
					// 创建子目录
					// File f = null;
					// if(SunitStringUtil.isBlankOrNull(subDir)){
					// f = new File(path + "/" + fileName);
					// }else{
					// f = new File(path + "/" + subDir+"/" + fileName);
					// }
					//					
					// if (!f.exists())
					// f.mkdirs();

					// mFile.transferTo(f);
					
					
					String nfsFiledir = SpringContextUtils.getGlobal()
							.getClusterFileServerDir();
					if (SunitStringUtil.isBlankOrNull(subDir)) {
						nfsFiledir = nfsFiledir;
					} else {
						nfsFiledir = nfsFiledir + "/" + subDir;
					}
					
					  
//					SmbFile remoteFile = new SmbFile(nfsFiledir,getSMBAuth());
					File remoteFile = new File(nfsFiledir); 
					 
					if (!remoteFile.isDirectory()) {
						remoteFile.mkdir();
					}
					InputStream in = null;
					OutputStream out = null;
					in = mFile.getInputStream();
 
					out = new BufferedOutputStream(new FileOutputStream(
							nfsFiledir + "/" + fileName));
					byte[] buffer = new byte[1024 * 10];
					while (in.read(buffer) != -1) {
						out.write(buffer);
						buffer = new byte[1024 * 10];
					}
					out.flush();
					out.close();

					if (files.length() > 0)
						files.append(",");

					if (SunitStringUtil.isBlankOrNull(subDir)) {
						files.append(fileName);
					} else {
						files.append(subDir + "/" + fileName);
					}

				}
				map.put(fieldName, files.toString());
				map.put("msg", files.toString());  //兼容 processFileUploadByWYSIWYG 的返回
				map.put("err", "");  //兼容 processFileUploadByWYSIWYG 的返回
				UploadAfterEvent event = new  UploadAfterEvent(this);
				event.setFiles(files.toString());
				event.setRequest(request);
				SpringContextUtils.getSpringContext().publishEvent(event);
				map.put("success", true);

			} 
		} catch (Exception e) { 
			map.put("success", false);
			map.put("msg", "上传文件发生错误"); 
			map.put("err", "上传文件发生错误");
			logger.error("上传文件:", e);
		}
		response.setContentType("text/html;charset=UTF-8");
		String reString = JSONObject.fromObject(map).toString();
		pw.print(reString);
	
		
		
	}
	
	
	

	/**
	 * 處理request中的上傳文件,返回保存的文件名,多個文件時以 "," 分割
	 * 
	 * @Title: processFileUpload
	 * @Description:
	 * @param
	 * @param request
	 * @param
	 * @param FieldName
	 * @param
	 * @return
	 * @return String
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws
	 * @author joye Jun 4, 2015 9:08:51 PM
	 */
	@RequestMapping("/processFileUpload.action")
	public void processFileUpload(HttpServletRequest request,
			HttpServletResponse response, PrintWriter pw) {
		
		String  p =SpringContextUtils.getGlobal().getUploadProtocol();
		if(p.toLowerCase().equals("nfs")){ 
			processFileUploadByNFS(request, response, pw);
		}
		else{
			processFileUploadBySamba(request, response, pw);
		}
	}
	
	/**
	 * VO by 搜索
	 * 
	 * @author joye
	 * 
	 */
	public static class SearchPara {

		private String sName;
		private Date sDate;

		public String getSName() {
			return sName;
		}

		public void setSName(String name) {
			sName = name;
		}

		public Date getSDate() {
			return sDate;
		}

		public void setSDate(Date date) {
			sDate = date;
		}
	}
	
	
public static void main(String[] args) {
	 Map m = JSONObject.fromObject("{a:1,b:2}");
	 System.out.println(m.get("a"));
}
}
