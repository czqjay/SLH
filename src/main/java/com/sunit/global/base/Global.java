package com.sunit.global.base;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

import com.sunit.global.base.Global.TaskCompleteBeforeEvent;
import com.sunit.global.base.event.AbstractEvent;
import com.sunit.global.util.SessionContext;
import com.sunit.global.util.date.DateStyle;
import com.sunit.global.util.date.DateUtil;
import com.sunit.sysmanager.po.User;

public class Global {

	public static final String IllegalAccessPage="/j_spring_security_logout";
	private   boolean scheduleActive=false; 
	private   boolean compressActive=false; 

	public  boolean funcSwitch=true; //版本开关变量 (阅卷功能.)
	private  String rootName="";
	
	
	private  String  uploadProtocol="";    // 上传协议   smb  或 nfs
	private  String clusterFileServerDir ="";      // 上传文件 存放目录
	private  String clusterFileServerURL="";     
	private  String clusterFileServerDomian="";   // smb 的域参数
	private  String clusterFileServerUser="";      // smb的用户名
	private  String clusterFileServerPwd="";     // smb的用户密码
	private  String AnonymousUserAccessURL=""; 
	
//	为每个上传控件配置环境变量( list: filename+"_size"  , filename+"_subdir"  )
	private   Map<String,Object> uploadArgs=new HashMap();  
	
	/**
	 * 为每个上传控件指定上传与读取环境变量,在上传与读取文件时都需要设置对应的环境变量
	* @Title: setUploadPath 
	* @Description: 
	* @param @param uploadFieldName  上传控件名称
	* @param @param subdir     子文件夹   
	* @return void   
	* @throws 
	* @author joye 
	* Dec 9, 2015 8:35:02 AM 
	 */ 
    public void setUploadArgs(String uploadFieldName, Object var){ 
    	uploadArgs.put(uploadFieldName, var);
    }   
	 
	public Object getUploadArgs(String uploadFieldName){ 
		return uploadArgs.get(uploadFieldName);
	}
	
	public String getRootName() { 
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	

	public static  void processIllegalAccess(HttpServletRequest request, 
			HttpServletResponse response) throws IOException{  
			response.sendRedirect(request.getContextPath()+Global.IllegalAccessPage);
	} 

	public  boolean isScheduleActive() {  
		return scheduleActive;
	} 
  
	public  void setScheduleActive(boolean scheduleActive) {
		this.scheduleActive = scheduleActive;
	}

	public  boolean isCompressActive() { 
		return compressActive;
	}

	public  void setCompressActive(boolean compressActive) {
		this.compressActive = compressActive;
	}

	public String getAnonymousUserAccessURL() {
		return AnonymousUserAccessURL;
	}

	public void setAnonymousUserAccessURL(String anonymousUserAccessURL) {
		this.AnonymousUserAccessURL = anonymousUserAccessURL;
	}
 
	public static String getIllegalAccessPage() {
		return IllegalAccessPage;
	}

	public boolean isFuncSwitch() {
		return funcSwitch;
	}

	public void setFuncSwitch(boolean funcSwitch) {
		this.funcSwitch = funcSwitch;
	}

	public String getUploadProtocol() {
		return uploadProtocol;
	}

	public void setUploadProtocol(String uploadProtocol) {
		this.uploadProtocol = uploadProtocol;
	}


	
	
    
   public static class UploadAfterEvent  extends ApplicationEvent {

   

		public UploadAfterEvent(Object source) { 
			super(source);
		}

		String files;
   	HttpServletRequest request; 
   	

		public String getFiles() {
			return files; 
		}

		public void setFiles(String files) {
			this.files = files;
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public void setRequest(HttpServletRequest request) {
			this.request = request;
		} 
   	
   }





public String getClusterFileServerDir() {
	return clusterFileServerDir;
}

public void setClusterFileServerDir(String clusterFileServerDir) {
	this.clusterFileServerDir = clusterFileServerDir;
}

public String getClusterFileServerURL() {
	return clusterFileServerURL;
}

public void setClusterFileServerURL(String clusterFileServerURL) {
	this.clusterFileServerURL = clusterFileServerURL;
}

public String getClusterFileServerDomian() {
	return clusterFileServerDomian;
}

public void setClusterFileServerDomian(String clusterFileServerDomian) {
	this.clusterFileServerDomian = clusterFileServerDomian;
}

public String getClusterFileServerUser() {
	return clusterFileServerUser;
}

public void setClusterFileServerUser(String clusterFileServerUser) {
	this.clusterFileServerUser = clusterFileServerUser;
}

public String getClusterFileServerPwd() {
	return clusterFileServerPwd;
}

public void setClusterFileServerPwd(String clusterFileServerPwd) {
	this.clusterFileServerPwd = clusterFileServerPwd;
}
	
/*public  static void fillDataRange(StringBuffer hqlBuf,HttpServletRequest reques) {
	
	fillDataRange(hqlBuf,reques,"");
	
}  */
  
public  static void fillDataRange(StringBuffer hqlBuf,HttpServletRequest request,String prefix) {
	
	User u  =SessionContext.getUser(request); 
	if(u.getDatatype().equals("1") ) {  
		hqlBuf.append("  and "+prefix+" =  '"+ u.getAccountName()+"'");
	}
	
} 

public static  String getCustomNo(String pre) {
	// TODO  改良ID算法  
	pre = pre + DateUtil.getDateFormat(DateStyle.YYYYMMDDHHMMSS.getValue()).format(Calendar.getInstance().getTime()); 
	
	return pre;
}

public static  String getCustomNoYYMMDDHHMMSS(String pre) { 
	// TODO  改良ID算法  
	pre = pre + DateUtil.getDateFormat(DateStyle.YYMMDDHHMMSS.getValue()).format(Calendar.getInstance().getTime()); 
	
	return pre;
} 



public static class TaskCompleteBeforeEvent  extends AbstractEvent {
	
	Class POClass;
	String poId;
	String msg="";;
	boolean flag=false;

	public Class getPOClass() {
		return POClass;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void setPOClass(Class class1) {
		POClass = class1; 
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	/**
	 * 流程任务完成事件
	 * @param source
	 */
	public TaskCompleteBeforeEvent(Object source) {
		super(source);
		this.setEventType(TaskCompleteBeforeEvent.class.getName()); 
	}
	
	public boolean  isFail() {
		return flag; 
	}

} 



}
 