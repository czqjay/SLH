package com.sunit.sysmanager.service;

import com.sunit.global.base.service.BaseService;
import com.sunit.sysmanager.po.User;


/**
 * 
 * 
 * 类名称：UserManager
 * 类描述：用户接口,数据提供层需要用到的方法在此声明
 * 创建人：liangrujian
 * 创建时间：Jul 24, 2013 10:29:18 AM
 * 修改人：liangrujian
 * 修改时间：Jul 24, 2013 10:29:18 AM
 * 修改备注：
 * @version 
 *
 */
public interface UserManager extends BaseService<User>{
	
	public static  String  normal ="0"; //普通用户
	public static String  vendor ="1"; //供应商	
	
	
	
}
