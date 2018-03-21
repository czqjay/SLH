package com.sunit.global.base.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * 
 * 类名称：DBVoter
 * 类描述：
 * 创建人：joye
 * 创建时间：Jul 10, 2013 3:56:07 PM
 * 修改人：joye 
 * 修改时间：Jul 10, 2013 3:56:07 PM 
 * 修改备注：
 * @version  1.0 
 *
 */
public class DBVoter implements AccessDecisionVoter<FilterInvocation> {
	private SecurityExpressionHandler<FilterInvocation> expressionHandler = new DefaultWebSecurityExpressionHandler();
	private SessionRegistry sessionRegistry; 
	static Logger logger = Logger.getLogger(DBVoter.class); 
	public static List  abstainedList = new ArrayList();   //弃权列表
	static{
		
		abstainedList.add("/html/index.jsp");
		abstainedList.add("/base/loadChildren.action");
		abstainedList.add("/html/user/userTodoList.jsp");
		abstainedList.add("/deploy/businessView.action");  
		abstainedList.add("/sysuser/loadTodoListGrid.action");
		abstainedList.add("/generateTest/validateScore.action");
		abstainedList.add("/generateTest/generateScore.action");
		abstainedList.add("/generateTest/saveScore.action");
		abstainedList.add("/generateTest/showSingleImg.action");
		abstainedList.add("/generateTest/generateVote.action");
		abstainedList.add("/generateTest/seachScore.action");
		abstainedList.add("/base/reConfigratrionLog4j.action");
		//abstainedList.add("/excel/accountTotal.action"); //temp
		abstainedList.add("/base/getFile.action");   // temp
		abstainedList.add("/base/getRemoteImg.action");   // temp 
		//abstainedList.add("/plan/updateIsBreak.action"); 
		abstainedList.add("/html/client/personPaswUpdate.jsp");  
		abstainedList.add("/excel/showParTypeRemark.action");
		abstainedList.add("/html/adminScore/adminScoreList.jsp");
		   
		
//		//tempsForXWT     
//		suborders/loadAgencyRefundOrderList.action
//		abstainedList.add("/suborders/loadAgencyRefundOrderList.action");
//		abstainedList.add("/orders/refund.action"); 
//		abstainedList.add("/html/alipay/alipayrefund.jsp"); 
		  
		
//		abstainedList.add("/agencycustom/loadAgencycustomListDataByTypeArray.action");  
		 
//		abstainedList.add("/suborders/loadConfirmedOrderList.action");
//		abstainedList.add("/agencycustom/isExistsDataRange.action"); 
//		abstainedList.add("/cashflow/getCashflowAmount.action");
//		abstainedList.add("/deploy/getAllFlowHistory.action");
//		abstainedList.add("/deploy/process/trace.action");  
//		abstainedList.add("/deploy/resource/process-instance.action");  
//		abstainedList.add("/base/processFileUploadByWYSIWYG.action"); // html文本框的上传 
//		abstainedList.add("/html/xwt/dict/dictListForNews.jsp");   //   新政法规添加-税种的选择
		
		 
//		abstainedList.add("/usershare/frontLoadUsershareListDataGrid.action");   //我的分享
//		abstainedList.add("/usershare/frontLoadUsershareFirstLevelListDataGrid.action"); // 我的用户分享 
//		
//		//xwy
//		abstainedList.add("/xwydepart/getSelectBody.action");   //员工添加
//		abstainedList.add("/xwyjobs/getSelectBody.action");   //员工添加
//		 
//		abstainedList.add("/flowvariables/loadFlowvariablesListByACTKEY.action");     
//		abstainedList.add("/xwydepart/loadChildrenOnlyDepart.action");  
//		
		
		
	}  
	
    public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
        assert authentication != null;
        assert fi != null;
        assert attributes != null;
        Collection<GrantedAuthority> c = (Collection<GrantedAuthority>) authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : c) {
        	if(grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS")){
        		return 0;
        	}
		}  
        
        int  result = -1;  //1 grant -1 denied  0abstain 
        String attemptAccessUrl= fi.getRequestUrl();
        attemptAccessUrl=attemptAccessUrl.indexOf("?")==-1?attemptAccessUrl:attemptAccessUrl.substring(0,attemptAccessUrl.indexOf("?"));
        //验证权限白名单
        List<String> list =(List) fi.getRequest().getSession().getAttribute("authorityList");
        for (String string : list) {
        	if(attemptAccessUrl.startsWith(string)){
        		result=1;  
        		break;
        	}
		}
        //验证弃权列表
        if(abstainedList.contains(attemptAccessUrl))
        	result=0;
        
        if(result==-1){
        	result=-1; 
        }   
         
         
        result=0;    
        return result;
    }  
 
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(FilterInvocation.class);
    } 
    

    public void setExpressionHandler(SecurityExpressionHandler<FilterInvocation> expressionHandler) {
        this.expressionHandler = expressionHandler;
    }
 
	public boolean supports(ConfigAttribute attribute) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
}
