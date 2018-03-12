package com.sunit.global.singleLogin.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * 
 * 类名称：ClusterSingleLogin
 * 类描述： 用于记录当前登录用户的sessionId,保证每个用户只能有一个sessionId 处于可用状态
 * 创建人：Administrator
 * 创建时间：Nov 14, 2013 9:14:57 AM
 * 修改人：joye
 * 修改时间：Nov 14, 2013 9:14:57 AM
 * 修改备注：
 * @version 
 *
 */
@Entity
@Table(name = "TB_CLUSTERSINGLELOGIN")   
public class ClusterSingleLogin {
	private String id;
	private String account;
	private String sessionId;
	private String lastDate;
	private Integer tryCount; 
	
	
	
	@GenericGenerator(name = "generator", strategy = "uuid") 
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "ID", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "ACCOUNT", length = 500)
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	@Column(name = "SESSIONID", length = 500)
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) { 
		this.sessionId = sessionId;
	}
	@Column(name = "LASTDATE", length = 30)
	public String getLastDate() { 
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	public Integer getTryCount() { 
		return tryCount; 
	} 
	@Column(name = "TRYCOUNT")
	public void setTryCount(Integer tryCount) {
		this.tryCount = tryCount;
	}
	
}
