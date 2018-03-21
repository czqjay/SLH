package com.sunit.global.base.authorization;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.sunit.global.singleLogin.ClusterSingleLoginManager;
import com.sunit.global.singleLogin.po.ClusterSingleLogin;
import com.sunit.global.util.date.DateUtil;

/**
 * 
 * 
 * 类名称：BadCredentialListener
 * 类描述：登录失败事件监听 
 * 创建人：joye
 * 创建时间：Dec 6, 2013 10:04:44 AM
 * 修改人：joye
 * 修改时间：Dec 6, 2013 10:04:44 AM
 * 修改备注：
 * @version 
 *
 */
@Component
public class BadCredentialListener  implements ApplicationListener<AuthenticationFailureBadCredentialsEvent>{

	@Autowired
	private ClusterSingleLoginManager cslmpl;
	
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent arg0) {
		String userName =((UsernamePasswordAuthenticationToken) arg0
				.getSource()).getPrincipal().toString();
		
		 // 登录次数
        ClusterSingleLogin csl = new ClusterSingleLogin();  
        csl.setAccount(userName);
        List<ClusterSingleLogin> list =cslmpl.findByExample(csl);  
        if(!list.isEmpty()){
        	csl =list.get(0); 
        }    
        csl.setTryCount(csl.getTryCount()==null?0:csl.getTryCount()+1);  
        csl.setLastDate(DateUtil.getCurrentTime()); 
        cslmpl.save(csl);   
	}
} 
