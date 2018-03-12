/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.web.authentication.logout;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.date.DateUtil;
import com.sunit.operator.po.Operator;
import com.sunit.operator.service.OperatorManager;

/**
 * Performs a logout by modifying the {@link org.springframework.security.core.context.SecurityContextHolder}.
 * <p>
 * Will also invalidate the {@link HttpSession} if {@link #isInvalidateHttpSession()} is {@code true} and the
 * session is not {@code null}.
 *
 * @author Ben Alex
 */
public class SecurityContextLogoutHandler implements LogoutHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());

    private boolean invalidateHttpSession = true; 
    

    //~ Methods ========================================================================================================

    /**
     * Requires the request to be passed in.
     *
     * @param request        from which to obtain a HTTP session (cannot be null)
     * @param response       not used (can be <code>null</code>)
     * @param authentication not used (can be <code>null</code>)
     */
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Assert.notNull(request, "HttpServletRequest required");
        Operator operator = new Operator();
        OperatorManager opm =	SpringContextUtils.getSpringContext().getBean(OperatorManager.class);//注入日志操作接口
        HttpSession session = request.getSession(false);
        if (invalidateHttpSession) {
        	 
        	 if(request.getSession().getAttribute("user")!=null){ 
        		 try {
        			String userID = request.getSession().getAttribute("userId").toString();//用户id
             		String userName = request.getSession().getAttribute("userName").toString();//用户名称
             		String userAccount = request.getSession().getAttribute("userAccount").toString();//用户登录账号
             		String ip = request.getRemoteAddr(); //获取访问本机ip
             		operator.setUserId(userID); //用户id
             		operator.setOperatortime(DateUtil.getCurrentTime());//当前登录时间
             		operator.setUserName(userName);//用户名称
             		operator.setAccountName(userAccount);//用户账号
             		operator.setHostIp(ip);//IP地址
             		operator.setUserOperator("退出系统"); 
             		operator.setOperatorState("0");
             		opm.save(operator); 
				} catch (Exception e) {
					logger.error(e.getMessage()); 
				}
             }
            if (session != null) { 
                logger.debug("Invalidating session: " + session.getId());
                session.invalidate();
            }
        }
        SecurityContextHolder.clearContext();
        	
    }

    public boolean isInvalidateHttpSession() {
        return invalidateHttpSession;
    }

    /**
     * Causes the {@link HttpSession} to be invalidated when this {@link LogoutHandler} is invoked. Defaults to true.
     *
     * @param invalidateHttpSession true if you wish the session to be invalidated (default) or false if it should
     * not be.
     */
    public void setInvalidateHttpSession(boolean invalidateHttpSession) {
        this.invalidateHttpSession = invalidateHttpSession;
    }

}
