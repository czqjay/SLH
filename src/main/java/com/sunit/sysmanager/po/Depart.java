package com.sunit.sysmanager.po;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
/**
 * 
 * 
 * 类名称：Depart
 * 类描述：部门实体类
 * 创建人：liangrujian
 * 创建时间：Jul 22, 2013 2:26:38 PM
 * 修改人：liangrujian
 * 修改时间：Jul 22, 2013 2:26:38 PM
 * 修改备注：
 * @version 
 *
 */
@Entity
@Table(name = "tb_depart")
public class Depart implements java.io.Serializable {


	private String id;
    
	private String deptno; //部门编号
	
	private String deptname; //部门名称
	
	private String location; //部门地址
	
	private String isenabled; //是否开启 0:开启 1:关闭
	
	
	private List<Depart> childDepart; //子部门
	
	private Depart parentDepart; //父部门
	
	private String sources;  //数据来源 0:本地 1:同步
	
	private String note; // 备注
	
	private String flag; // //标志该部门是否在用 0:无效 1:有效
	
	private String deptRefCode; // 部门关系串 以"_"分割,展示所有的父ID+自身ID 
	 
	private Set<User> users = new HashSet<User>(); //部门中的用户
	
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

	public String getDeptno() {
		return deptno;
	}

	public void setDeptno(String deptno) {
		this.deptno = deptno;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIsenabled() {
		return isenabled;
	}

	public void setIsenabled(String isenabled) {
		this.isenabled = isenabled;
	}



	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parentid")
	public Depart getParentDepart() {
		return parentDepart;
	}

	public void setParentDepart(Depart parentDepart) {
		this.parentDepart = parentDepart;
	}




	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(name = "User_Depart",joinColumns = {@JoinColumn(name = "departid")},inverseJoinColumns = {@JoinColumn(name = "userid")}) 
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@OneToMany(fetch = FetchType.LAZY,cascade = { CascadeType.PERSIST,CascadeType.MERGE},mappedBy="parentDepart")
	public List<Depart> getChildDepart() {
		return childDepart;
	}

	public void setChildDepart(List<Depart> childDepart) {
		this.childDepart = childDepart;
	}

	@Column(name = "DEPT_REF_CODE") 
	public String getDeptRefCode() {
		return deptRefCode;
	}

	public void setDeptRefCode(String deptRefCode) {
		this.deptRefCode = deptRefCode;
	}
 	
}
