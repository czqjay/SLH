package com.sunit.sysmanager.po;

import java.util.HashSet;
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

import org.hibernate.annotations.GenericGenerator;

;

@Entity
@Table(name = "tb_jobs")
//@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Jobs implements java.io.Serializable {

	
	private String id; //职务ID

	private String name; //职务名称

	private Set<User> user = new HashSet<User>(); //职务中用户
	
	

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(name = "User_Jobs",joinColumns = {@JoinColumn(name = "jobid")},inverseJoinColumns = {@JoinColumn(name = "userid")}) 
	public Set<User> getUser() {
		return user;
	}

	public void setUser(Set<User> user) {
		this.user = user;
	}


    
	
}
