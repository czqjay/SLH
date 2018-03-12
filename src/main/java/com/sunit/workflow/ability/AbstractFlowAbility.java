package com.sunit.workflow.ability;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.sunit.global.flowability.po.Flowability;
import com.sunit.global.util.SunitStringUtil;

/**
 * 
 * 普通 po 继承后,为其加上流程处理功能
 * 类名称：FlowAbilityInterface
 * 类描述：
 * 创建人：joyepc
 * 创建时间：Feb 27, 2016 11:04:19 AM
 * 修改人：joye
 * 修改时间：Feb 27, 2016 11:04:19 AM
 * 修改备注：
 * @version  
 *
 */  
@MappedSuperclass
public abstract class AbstractFlowAbility extends  com.sunit.global.base.AbstractID  {
	
	  
	private List<Flowability> flowabilitys=new ArrayList();
	 
	 
	private Flowability  validFlowability; 

	  
	@OneToMany( fetch=FetchType.LAZY,cascade=CascadeType.ALL )  
	@JoinColumn(  name="po_id", referencedColumnName="id") 
	public List<Flowability> getFlowabilitys() {
		return flowabilitys;
	} 

	public void setFlowabilitys(List<Flowability> flowabilitys) {
		this.flowabilitys = flowabilitys;
	} 
  
  
	
	
	
	
	/**
	 * 一个便捷性的方法,得到唯一的流程信息对象,用于一对一关系(一个po只有一个流程)时,如果是一多对的关系时不推荐使用
	* @Title: getValidFlowability 
	* @Description: 
	* @param @return     
	* @return Flowability  
	* @throws 
	* @author joye 
	* Mar 7, 2016 8:29:31 PM
	 */
	@Transient
	public Flowability getValidFlowability(){
		List<Flowability> list = this.getFlowabilitys();
		Flowability flowAbility=null;
		if(list!=null)
 		if(list.isEmpty()){   
			 flowAbility = new Flowability(); 
		}else{
			flowAbility = list.get(list.size()-1);
		}
		return flowAbility;
		
	}
	
	
	/**
	 * 一个便捷性的方法,得到唯一的基于ExcutionId的流程信息对象
	* @Title: getValidFlowabilityByExcutionId  
	* @Description: 
	* @param @return     
	* @return Flowability  
	* @throws 
	* @author joye 
	* Jul 16, 2016 5:08:24 PM
	 */  
	@Transient
	public Flowability getValidFlowabilityByExcutionId(String excutionId){
		List<Flowability> list = this.getFlowabilitys();
		Flowability flowAbility=null;
		if(list!=null)
		for (int i = 0; i < list.size(); i++) {
				if(SunitStringUtil.requireAndEquals(list.get(i).getExcutionId(), excutionId))
					return list.get(i);
		}
		return flowAbility;
	}
	
	
	@Transient
	public Flowability getValidFlowabilityByAccount(String excutionId){
		List<Flowability> list = this.getFlowabilitys();	 
		Flowability flowAbility=null;
		if(list!=null)
		for (int i = 0; i < list.size(); i++) {
				if(SunitStringUtil.requireAndEquals(list.get(i).getExcutionId(), excutionId))
					return list.get(i);
		}
		return flowAbility;
	}
	
	
	
	public void setValidFlowability(Flowability validFlowability) {
		this.validFlowability = validFlowability;
	}

	
	
	
}
