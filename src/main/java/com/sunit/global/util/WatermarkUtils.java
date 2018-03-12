package com.sunit.global.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sunit.global.SysSchedule;
import com.sunit.sysmanager.po.User;

public class WatermarkUtils {

	static Logger logger = Logger.getLogger(WatermarkUtils.class);
	
	/**
	 * 生成水印图片
	 * 
	 * @Title: GeneratWatermarkForUserName
	 * @Description:
	 * @param
	 * @param user
	 *            用户 
	 * @param
	 * @param request
	 *            水印文件名
	 * @return String
	 * @throws
	 * @author liangrujian Nov 20, 2013 3:04:22 PM
	 */
	public static String GeneratWatermarkForUserID(User user,
			HttpServletRequest request) {

		
		
		File f = new File(request.getRealPath("/upload/waterMask"));
		f.mkdirs();
		f = new File(request.getRealPath("/upload/waterMask/" + user.getId()
				+ ".jpg"));
		if (!f.exists()) {// 判断文件是否存在,不存在则创建文件
			GeneratWatermark.GeneratWatermarkForUserName(user.getUserName(), f);
		}

		if (!StringUtils.isBlank(SpringContextUtils.getGlobal()
				.getClusterFileServerDir())) {
			String remoteFileDir = SpringContextUtils.getGlobal()
					.getClusterFileServerDir(); 
			SmbFile remoteFile;
			try {
				remoteFile = new SmbFile(remoteFileDir + "/waterMask");

				if (!remoteFile.isDirectory())
					remoteFile.mkdir();
				InputStream in = new FileInputStream(f);
				OutputStream out = null;
				out = new BufferedOutputStream(new SmbFileOutputStream(
						remoteFile + "/" + f.getName()));
				byte[] buffer = new byte[1024];
				while (in.read(buffer) != -1) {
					out.write(buffer);
					buffer = new byte[1024];
				}
				out.flush();
				out.close();
			} catch (Exception e) { 
				logger.error("生成水印失败",e); 
			}
		}
		return user.getId();
	}
}
