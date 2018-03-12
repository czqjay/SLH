package com.sunit.sysmanager.po;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;



@Entity 
@Table(name="tb_role")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Role implements java.io.Serializable{

	@Id
	@GenericGenerator(name="system_uuid",strategy="uuid")
	@GeneratedValue(generator="system_uuid")
	public String id;  //角色ID
	
	@Column(name = "role_name")
	public String roleName; //角色名称
	 
	 
	@ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(name = "User_Role",joinColumns = {@JoinColumn(name = "roleid")},
    		inverseJoinColumns = {@JoinColumn(name = "userid")}) 
	private List<User> user ;  //角色中的用户
   
	
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OrderBy(value="orderNum")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "role_sysresource", joinColumns = { @JoinColumn(name = "roleid") }, inverseJoinColumns = { @JoinColumn(name = "SYSRESOURCEID") })
	private List<SysResource> sysResources; //角色中资源
  
	public List<SysResource> getSysResources() {
		return sysResources;
	}

	public void setSysResources(List<SysResource> sysResources) {
		this.sysResources = sysResources;
	}

	public String getId() { 
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}
 

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<User> getUser() {
		return user;
	}

	public void setUser(List<User> user) {
		this.user = user;
	}

}