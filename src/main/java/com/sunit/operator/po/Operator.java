package com.sunit.operator.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@SuppressWarnings("serial")
@Entity
@Table(name = "TB_OPERATOR")
public class Operator implements java.io.Serializable {


	private String id; //日志id
 	private String userName;//用户名称
	private String userId; //用户id
	private String userOperator; //用户操作
	private String operatortime; //操作时间
	private String hostIp; //终端ip地址
	private String accountName;//用户账号
	private String operatorState;//操作状态
	public Operator() {
	}

	public Operator(String userName, String userId, String userOperator,
			String operatortime) {
		this.userName = userName;
		this.userId = userId;
		this.userOperator = userOperator;
		this.operatortime = operatortime;
	}

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID")
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "USER_NAME", length = 500)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "USER_ID", length = 32)
	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Column(name = "USER_OPERATOR", length = 200)
	public String getUserOperator() {
		return this.userOperator;
	}

	public void setUserOperator(String userOperator) {
		this.userOperator = userOperator;
	}

	@Column(name = "OPERATORTIME", length = 30)
	public String getOperatortime() {
		return this.operatortime;
	}

	public void setOperatortime(String operatortime) {
		this.operatortime = operatortime;
	}


	@Column(name = "IP")
	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	@Column(name = "USER_ACCOUNT")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getOperatorState() {
		return operatorState;
	}

	public void setOperatorState(String operatorState) {
		this.operatorState = operatorState;
	}

}