package com.sunit.global.util;

import java.util.UUID;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

/**
 * 
 * 
 * @class name：UUIDRandom
 * @desc：匿名策略
 * @user：shanjizhou
 * @createTime：Aug 6, 2013 5:21:12 PM
 * @update user：shanjizhou
 * @updateTime：Aug 6, 2013 5:21:12 PM
 * @update desc：
 * @version 
 *
 */
public class AnonymousTactics {
	
	public String[] getAnonymousAccount(){
		String[] random = new String[3];
		String uuid = UUID.randomUUID()+"";
		String userName = uuid.substring(0,6);
		random[0] = userName;
		String password = uuid.substring(uuid.length()-6);
		random[1] = new Md5PasswordEncoder().encodePassword(password,"");
		random[2] = password;
		return random;
	}

	/*public static void main(String[] args){
		AnonymousTactics a = new AnonymousTactics();
		String[] aa = a.getAnonymousAccount();
		for(int i=0; i<aa.length; i++){
			System.out.println(aa[i]);
		}
	}*/
}
