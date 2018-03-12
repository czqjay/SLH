package com.sunit.sysmanager.po;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "tb_user")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements java.io.Serializable {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID")
	private String id; //用户id

	@Column(name = "account_name") 
	private String accountName;  // 用户登录账号
	
	@Column(name = "user_name")
	private String userName;    // 用户名

	private String phone; 	    //电话
	
	private String address;     // 联系地址
	
	private String pwd; 	    // 登录密码
	
	private String station; 	    // 岗位
	
	
	@Column(name = "plaintext_pwd") 
	private String plainTextPwd; // 匿名明文密码
	
	private String datatype;    // 用户类型  0：普通用户    1：业务管理员, 2: 匿名账号 ,3 考评管理员
	
	
	@Transient
	private Date curDate;

	private String idCard;     //身份证号码
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date beginDate;    //开始创建时间
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate; 	   //结束时间
	
	private String isOpen; 	   // 是否开启 0：开启  1：关闭;
	
	private String userSource; //用户来源  0：同步  1：本地;
	
	private String creator;    //创建者
	
	private String flag; //标志该用户是否在用 0 无效  ，1 有效
	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "User_Depart",joinColumns = {@JoinColumn(name = "userid")},inverseJoinColumns = {@JoinColumn(name = "departid")}) 
	private Set<Depart> departs =  new HashSet<Depart>(); 
	
	@ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(name = "User_Jobs",joinColumns = {@JoinColumn(name = "userid")},inverseJoinColumns = {@JoinColumn(name = "jobid")}) 
	private Set<Jobs> jobs = new HashSet<Jobs>();
	// @OneToMany(mappedBy="users",cascade={CascadeType.ALL})
	// @OneToMany(fetch=FetchType.LAZY)
	// @JoinColumn(referencedColumnName="user_id", table="jobs")
	// public Set<Jobs> jobs;
	// @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy =
	// "users")
	// private Set<Jobs> jobses = new HashSet<Jobs>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "User_Role", joinColumns = { @JoinColumn(name = "userid") }, inverseJoinColumns = { @JoinColumn(name = "roleid") })
	private List<Role> roles;
	
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCurDate() {
		return curDate;
	}

	public void setCurDate(Date curDate) {
		this.curDate = curDate;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public Set<Depart> getDeparts() {
		return departs;
	}

	public void setDeparts(Set<Depart> departs) {
		this.departs = departs;
	}

	public Set<Jobs> getJobs() {
		return jobs;
	}

	public void setJobs(Set<Jobs> jobs) {
		this.jobs = jobs;
	}

	public String getUserSource() {
		return userSource;
	}

	public void setUserSource(String userSource) {
		this.userSource = userSource;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPlainTextPwd() {
		return plainTextPwd;
	}

	public void setPlainTextPwd(String plainTextPwd) {
		this.plainTextPwd = plainTextPwd;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	
	
}
