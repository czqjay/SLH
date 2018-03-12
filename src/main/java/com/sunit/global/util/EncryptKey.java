package com.sunit.global.util;

/**
 * 
 * 
 * 类名称：EncryptKey
 * 类描述：加密key值
 * 创建人：shanjizhou
 * 创建时间：2013-12-2 下午3:13:33
 * 修改人：shanjizhou
 * 修改时间：2013-12-2 下午3:13:33
 * 修改备注：
 * @version 
 *
 */
public class EncryptKey {
	
	public static final String key = "sun";
	
	public String getEncryptKey( String encryptValue ){
		if(null!=encryptValue && !"".equals(encryptValue)){
			return key+encryptValue;
		}else{
			return key;
		}
	}

}
