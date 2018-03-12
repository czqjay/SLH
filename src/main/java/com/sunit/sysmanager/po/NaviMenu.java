package com.sunit.sysmanager.po;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="navimenu")
public class NaviMenu implements java.io.Serializable{

	@Id()
	@GenericGenerator(name="system_uuid",strategy="uuid")
	@GeneratedValue(generator="system_uuid")
	public String id; 
	
	
	String title;
	boolean isFolder;
	String parentId;
	String url;
	@Transient
	String key = "null";
	@Transient
	boolean expand;
	@Transient
	String tooltip = "null";
	@Transient
	boolean noLink;
	@Transient
	String isLazy = "false";
	@Transient
	List children;
	 
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isExpand() {
		return expand;
	}
	public void setExpand(boolean expand) {
		this.expand = expand;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	public boolean isNoLink() {
		return noLink;
	}
	public void setNoLink(boolean noLink) {
		this.noLink = noLink;
	}
	public String getIsLazy() {
		return isLazy;
	}
	public void setIsLazy(String isLazy) {
		this.isLazy = isLazy;
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean getIsFolder() {
		return isFolder;
	}
	public void setIsFolder(String isFolder) {
		if(!StringUtils.isBlank(isFolder))
			if(isFolder.equals("0"))
				this.isFolder=false;
			else 
				this.isFolder=true;
		
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
} 
