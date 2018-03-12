package com.sunit.navimenu.service;

import com.sunit.global.base.service.BaseService;
import com.sunit.navimenu.po.Navimenu;
 
public interface NavimenuManager extends BaseService<Navimenu>{
	public static String relationName =Navimenu.class.getName();
}  