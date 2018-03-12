package com.sunit.sysmanager.po;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;


@Entity  
@Table(name="tb_sysResource")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE) 
public class SysResource implements java.io.Serializable{ 
  
	@Id()
	@GenericGenerator(name="system_uuid",strategy="uuid")
	@GeneratedValue(generator="system_uuid")
	public String id; 
	 
	private String content;  //资源入口路径
	private String parent;  //上级资源
	private String moduleCaption; //资源模块说明
	private String caption; //资源说明
	private String code; //资源的唯一标识code
	@Column(name = "source_Type")
	private String sourceType; 
	 
	
	private String orderNum;  //排序
	
	
	
	
	
	
	
//	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.REMOVE ,mappedBy="items")
//	private List<SysResourceItem> items;   
	
//	public List<SysResourceItem> getItems() {
//		return items; 
//	}
//	public void setItems(List<SysResourceItem> items) {
//		this.items = items;
//	} 
	
	 
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL ,mappedBy="superiorResource")
	private List<SysResourceItem> sysResourceItem;  
	 
	
	public List<SysResourceItem> getSysResourceItem() {
		return sysResourceItem;
	} 
	public void setSysResourceItem(List<SysResourceItem> sysResourceItem) {
		this.sysResourceItem = sysResourceItem;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getModuleCaption() {
		return moduleCaption;
	}
	public void setModuleCaption(String moduleCaption) {
		this.moduleCaption = moduleCaption;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType; 
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	
}
