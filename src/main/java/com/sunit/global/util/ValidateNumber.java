package com.sunit.global.util;

/**
 * 
 * 
 * @class name：ValidateNumber
 * @desc： 验证是不是整型
 * @user：shanjizhou
 * @createTime：Aug 6, 2013 8:38:08 PM
 * @update user：shanjizhou
 * @updateTime：Aug 6, 2013 8:38:08 PM
 * @update desc：
 * @version 
 *
 */
public class ValidateNumber {
	
	
	public boolean isValidateNumber( String desc ){
		boolean flag = false;
		try{
			Integer.parseInt(desc);
			flag = true;
		}catch(NumberFormatException e){
			flag = false;
		}
		return flag;
	}
	
	
	public boolean isValidateDouble( String desc ){
		boolean flag = false;
		try{
		   Double.parseDouble( desc );
		   flag = true;
		}catch(NumberFormatException e){
			flag = false;
		}
		return flag;
	}
	
	/*public static void main(String[] args){
		ValidateNumber v = new ValidateNumber();
		boolean f = v.isValidateDouble("23.01");
		System.out.println(f);
	}*/

}
