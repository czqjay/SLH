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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity 
@Table(name="TB_SYSRESOURCE_ITEM")
public class SysResourceItem implements java.io.Serializable{
  
	@Id()
	@GenericGenerator(name="system_uuid",strategy="uuid")
	@GeneratedValue(generator="system_uuid")
	public String id; //记录id
	
	private String superiorResource;  //上级资源
	
	private String subordinateResource;   //下级资源

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "SUPERIORRESOURCE", length = 32) 
	public String getSuperiorResource() {
		return superiorResource;
	}

	public void setSuperiorResource(String superiorResource) {
		this.superiorResource = superiorResource;
	}

	@Column(name = "SUBORDINATERESOURCE", length = 32) 
	public String getSubordinateResource() {
		return subordinateResource;
	}

	public void setSubordinateResource(String subordinateResource) {
		this.subordinateResource = subordinateResource;
	}
	
	
	
	
}
