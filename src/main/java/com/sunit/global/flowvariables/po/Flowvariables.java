package com.sunit.global.flowvariables.po; 
 
 
 
 
 
 
 
 
import gen.SLH; 
// Generated 2015-11-30 17:53:30 by Hibernate Tools 3.4.0.CR1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Flowvariables generated by hbm2java
 */
@Entity
@Table(name="tb_flowvariables")

@gen.SLH("{'formInputType':'DIV','width':'1164','code':'tb_flowVariables','slhName':'流程变量表'}")    
public class Flowvariables extends com.sunit.global.base.AbstractID implements java.io.Serializable {


			    @gen.SLH("{'formInputType':'text','formValidate':'','width':'300','dbLength':'2','code':'fv_type','slhName':'变量类型'}")     
	 private String fvType;
	
			    @gen.SLH("{'formInputType':'text','formValidate':'','width':'300','dbLength':'30','code':'fv_time','slhName':'创建时间','dbType':'date'}")     
	 private String fvTime;
	
			    @gen.SLH("{'psuiargs':'#{defaultOption:[{id:'',value:'请选择'}],dataUrl:'/user/getSelectBody.action',jsonReader:{'id':'id','value':'userName'}}#','formInputType':'suiselect','width':'300','dbLength':'32','code':'fv_userId','slhName':'用户Id'}")     
	 private String fvUserId;
	
			    @gen.SLH("{'formInputType':'text','formValidate':'','width':'300','dbLength':'32','code':'fv_taskId','slhName':'taskId'}")     
	 private String fvTaskId;
	
			    @gen.SLH("{'formInputType':'text','width':'300','dbLength':'32','code':'fv_proc_inst_id','slhName':'实例Id'}")     
	 private String fvProcInstId;
	
			    @gen.SLH("{'formInputType':'text','width':'300','dbLength':'100','code':'fv_action','slhName':'动作'}")     
	 private String fvAction;
	
			    @gen.SLH("{'formInputType':'text','width':'300','dbLength':'50000','code':'fv_content','slhName':'内容'}")     
	 private String fvContent;
	
			    @gen.SLH("{'formInputType':'text','width':'300','dbLength':'500','code':'remark','slhName':'remark'}")     
	 private String remark;
			    
	private String fvName;   
			     
			    
	 @Column(name="fv_name", length=100) 
    public String getFvName() {
		return fvName;
	}


	public void setFvName(String fvName) {
		this.fvName = fvName;
	}


	public Flowvariables() {
    }

	
    public Flowvariables(String id) {
        this.id = id;
    }
    public Flowvariables(String id, String fvType, String fvTime, String fvUserId, String fvTaskId, String fvProcInstId, String fvAction, String fvContent, String remark) {
       this.id = id;
       this.fvType = fvType;
       this.fvTime = fvTime;
       this.fvUserId = fvUserId;
       this.fvTaskId = fvTaskId;
       this.fvProcInstId = fvProcInstId;
       this.fvAction = fvAction;
       this.fvContent = fvContent;
       this.remark = remark;
    }
   

    
    @Column(name="fv_type", length=2)
	    public String getFvType() {
	        return this.fvType;
	    }
	    
	    public void setFvType(String fvType) {
	        this.fvType = fvType;
	    }

    
    @Column(name="fv_time", length=30)
	    public String getFvTime() {
	        return this.fvTime;
	    }
	    
	    public void setFvTime(String fvTime) {
	        this.fvTime = fvTime;
	    }

    
    @Column(name="fv_userId", length=32)
	    public String getFvUserId() {
	        return this.fvUserId;
	    }
	    
	    public void setFvUserId(String fvUserId) {
	        this.fvUserId = fvUserId;
	    }

    
    @Column(name="fv_taskId", length=32)
	    public String getFvTaskId() {
	        return this.fvTaskId;
	    }
	    
	    public void setFvTaskId(String fvTaskId) {
	        this.fvTaskId = fvTaskId;
	    }

    
    @Column(name="fv_proc_inst_id", length=32)
	    public String getFvProcInstId() {
	        return this.fvProcInstId;
	    }
	    
	    public void setFvProcInstId(String fvProcInstId) {
	        this.fvProcInstId = fvProcInstId;
	    }

    
    @Column(name="fv_action", length=100)
	    public String getFvAction() {
	        return this.fvAction;
	    }
	    
	    public void setFvAction(String fvAction) {
	        this.fvAction = fvAction;
	    }

    
    @Column(name="fv_content")
	    public String getFvContent() {
	        return this.fvContent;
	    }
	    
	    public void setFvContent(String fvContent) {
	        this.fvContent = fvContent;
	    }

    
    @Column(name="remark")
	    public String getRemark() {
	        return this.remark;
	    }
	    
	    public void setRemark(String remark) {
	        this.remark = remark;
	    }




}


  