package com.sunit.workflow.listener;

import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiActivityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.IdentityLinkType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunit.global.base.service.BaseService;
import com.sunit.global.flowability.po.Flowability;
import com.sunit.global.flowability.service.FlowabilityManager;
import com.sunit.global.flowvariables.po.Flowvariables;
import com.sunit.global.flowvariables.service.FlowvariablesManager;
import com.sunit.global.util.SpringContextUtils;
import com.sunit.global.util.SunitStringUtil;
import com.sunit.global.util.date.DateUtil;
import com.sunit.workflow.ability.AbstractFlowAbility;




/**
 * 
 * 
 * 类名称：TaskListener
 * 类描述：  全局事件监听器  监听类型见  ActivitiEventType
 * 创建人：joye
 * 创建时间：Jul 16, 2015 2:10:35 PM
 * 修改人：joye
 * 修改时间：Jul 16, 2015 2:10:35 PM
 * 修改备注：
 * @version 
 *
 */
@Component
public class TaskListener implements ActivitiEventListener{  
	
	static Logger logger = Logger.getLogger(TaskListener.class);
	

	
	@Autowired 
	FlowvariablesManager flowvariablesManager;
	
	@Autowired
	FlowabilityManager flowabilityManager;
	
	 private interface SyncBusinessPOCallback {
		 void  doSyncPO(AbstractFlowAbility po ,BaseService baseService);
	 } 
  
	 
	private void SyncBusinessPO(String procId,String busid, SyncBusinessPOCallback doSave){
		try { 
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
			Class clazz = Class.forName((String) runtimeService.getVariable(procId, "class")); 
			if(AbstractFlowAbility.class.isAssignableFrom(clazz)){
				String classManger =(String) runtimeService.getVariable(procId, "classManager");
				Class<BaseService> clazzManager = (Class<BaseService>)Class.forName(classManger); 
				BaseService baseService =SpringContextUtils.getSpringContext().getBean(
						clazzManager);
				AbstractFlowAbility po = (AbstractFlowAbility) baseService.get(busid); 
				doSave.doSyncPO(po,baseService);   
			} 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void onEvent(ActivitiEvent event) { 
		logger.debug("TaskListener.onEvent()"); 
	
		logger.error("event.getProcessDefinitionId()="
				+ event.getProcessDefinitionId());
		logger.error("event.getProcessInstanceId="
				+ event.getProcessInstanceId()); 
		
		logger.error("event.getExecutionId()=" + event.getExecutionId());
		logger.error("event.getType().name()=" + event.getType().name());
		
		 
		logger.error("event.getType()=" + event.getType());
		
		//同步流程节点id 
		if(event.getType() ==  ActivitiEventType.ACTIVITY_STARTED){
			System.out
					.println("((ActivitiActivityEventImpl)event).getActivityId()="
							+ ((ActivitiActivityEventImpl) event)
									.getActivityId());
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
			final String exid= event.getExecutionId();
			final String defVersion  = event.getProcessDefinitionId();
			 final String defKey = ((ActivitiActivityEventImpl)event).getExecution().getProcessDefinition().getKey(); 
			final String actid = ((ActivitiActivityEventImpl)event).getActivityId(); 
			String busid  =   ((ActivitiActivityEventImpl)event).getExecution().getProcessBusinessKey();
			final String procId= event.getProcessInstanceId();
			
//			String isOrderFlow = (String) ((ActivitiActivityEventImpl)event).getExecution().getVariable("isOrderFlow");
			String isOrderFlow =(String) runtimeService.getVariable(procId, "isOrderFlow") ; 
//			if(!SunitStringUtil.isBlankOrNull(isOrderFlow)){
//				Suborders sub =  subordersManager.get(busid);
//				sub.setOrdSubActivityId(actid); 
//				subordersManager.save(sub);  
//				return;  
//			} 
			 
			final String taskTitle = (String) runtimeService.getVariable(procId, "taskTitle") ; 
			final String xwyMI =(String) runtimeService.getVariable(procId, "xwyMI") ; 
			final String starter =(String) runtimeService.getVariable(procId, "starter") ; 
			logger.error(starter);
			
			SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
				public void doSyncPO(AbstractFlowAbility po, 
						BaseService baseService) {
					
					if(SunitStringUtil.isBlankOrNull(xwyMI)){
						Flowability flowAbility =po.getValidFlowability();   
						flowAbility.setProccessInstanceVersion(defVersion); 
						flowAbility.setProccessInstanceKey(defKey); 
						flowAbility.setActivityId(actid);
						flowAbility.setProccessInstanceId(procId);
						flowAbility.setPoId(po.getId());
						flowAbility.setUpdateTime(DateUtil.getCurrentTime()) ;
						po.getFlowabilitys().add(flowAbility); 
						baseService.save(po); 
					}else{
						Flowability flowAbility =po.getValidFlowabilityByExcutionId(exid); 
						if(flowAbility==null){
							 flowAbility = new  Flowability();   
							 po.getFlowabilitys().add(flowAbility); 
						} 
						flowAbility.setExcutionId(exid); 
						flowAbility.setProccessInstanceVersion(defVersion); 
						flowAbility.setProccessInstanceKey(defKey); 
						flowAbility.setActivityId(actid);
						flowAbility.setProccessInstanceId(procId);  
//						flowAbility.setPoId(po.getId());
//						 
						flowAbility.setCreateTime(DateUtil.getCurrentTime());
						flowAbility.setTaskTitle(taskTitle);
						flowAbility.setStarter(starter);						
////						po.getFlowabilitys().add(flowAbility); 
////						flowabilityManager.save(flowAbility); 
						baseService.save(po);  
						
					}
					
				
					 
//					po.setActivityId(actid);		  
//					po.setProccessInstanceId(procId);
//					baseService.save(po);   
				}
			}); 
		}   
		 
		if(event.getType() ==  ActivitiEventType.ENTITY_CREATED){
//			ActivitiEntityEventImpl
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
			final String procId= event.getProcessInstanceId();
			if(SunitStringUtil.isBlankOrNull(procId)) 
				return ; 
				
			String isStartFlowVariables =(String) runtimeService.getVariable(procId, "isStartFlowVariables") ; 
			if(!SunitStringUtil.isBlankOrNull(isStartFlowVariables)){
				Object o =runtimeService.getVariable(procId,"startFlowVariables"); 
				if(Map.class.isAssignableFrom(o.getClass())){
					Map<String ,String> map = (Map<String, String>)o;
					for ( Map.Entry<String, String> entry: map.entrySet()) {   
						Flowvariables  flowvariables = new Flowvariables();
						flowvariables.setFvType("0");  
						flowvariables.setFvTime(DateUtil.getCurrentTime()); 
						flowvariables.setFvUserId("");
						flowvariables.setFvTaskId("");
						flowvariables.setFvProcInstId(procId);
						flowvariables.setFvAction("start");
						flowvariables.setFvContent(String.valueOf(entry.getValue()));
						flowvariables.setFvName(entry.getKey());
						flowvariablesManager.save(flowvariables);
					}  
					runtimeService.setVariable(procId, "isStartFlowVariables","");
					
				}
			}
			Object o =  ((ActivitiEntityEventImpl)event).getEntity();  
			if(TaskEntity.class.isAssignableFrom(o.getClass())){
				TaskEntity te = (TaskEntity)o;
				final String taskId = te.getId(); 
				final String exId =  event.getExecutionId();
				
				   
//				runtimeService.setVariableLocal(exId, "excutionId", exId);
//				runtimeService.setVariable(exId, "procId", procId);
//				runtimeService.setVariable(exId, "taskId", procId);  
				
				final String taskTitle = (String) runtimeService.getVariable(procId, "taskTitle") ; 
				String busid = (String) runtimeService.getVariable(procId, "businessId") ; 
				String isOrderFlow =(String) runtimeService.getVariable(procId, "isOrderFlow") ; 
				if(!SunitStringUtil.isBlankOrNull(isOrderFlow)){
					return; 
				}
				final String xwyMI =(String) runtimeService.getVariable(procId, "xwyMI") ; 
				final String defVersion  = event.getProcessDefinitionId(); 
//				final String defKey = te.getExecution().getProcessDefinition().getKey();
				final String actid = te.getName();
				
				SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
					@SuppressWarnings("unchecked")
					public void doSyncPO(AbstractFlowAbility po,
							BaseService baseService) {
						
						if(SunitStringUtil.isBlankOrNull(xwyMI)){
							Flowability flowAbility =po.getValidFlowability();  
							flowAbility.setTaskId(taskId); 
							flowAbility.setPoId(po.getId()); 
							flowAbility.setUpdateTime(DateUtil.getCurrentTime()) ;
							po.getFlowabilitys().add(flowAbility);  
							baseService.save(po); 
						}else{  
//							Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);
//							if(flowAbility==null){
//								 flowAbility = new  Flowability();   
//								 po.getFlowabilitys().add(flowAbility); 
//							}
//							flowAbility.setTaskId(taskId);  
//							flowAbility.setPoId(po.getId());
//							flowAbility.setAssigneeType(""); 
//							flowAbility.setCandidate(""); 
////							
//							flowAbility.setExcutionId(exId);    
//							flowAbility.setProccessInstanceVersion(defVersion); 
//							flowAbility.setActivityId(actid);
//							flowAbility.setProccessInstanceId(procId); 
//							flowAbility.setCreateTime(DateUtil.getCurrentTime());
////							flowabilityManager.save(flowAbility);
////							po.getFlowabilitys().add(flowAbility);   
//							baseService.save(po); 
						}   
					} 
				}); 
				
				logger.error("o.toString()=" + o.toString());
			}
		}
		
		
		if(event.getType() ==  ActivitiEventType.TASK_ASSIGNED){
			logger.debug("TaskListener.onEvent()"); 
			
			Object o =  ((ActivitiEntityEventImpl)event).getEntity();  
			if(TaskEntity.class.isAssignableFrom(o.getClass())){
				RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
				TaskEntity te = (TaskEntity)o;
				final String exId =te.getExecutionId(); 
				logger.error("exId="+exId);
				final String taskId = te.getId();
				final String procId= event.getProcessInstanceId();
				String busid = (String) runtimeService.getVariable(procId, "businessId") ; 
				
				final String assignee = te.getAssignee();
				String isOrderFlow =(String) runtimeService.getVariable(procId, "isOrderFlow") ; 
				if(!SunitStringUtil.isBlankOrNull(isOrderFlow)){
					return;
				}
				final String xwyMI =(String) runtimeService.getVariable(procId, "xwyMI") ; 
				final String defVersion  = event.getProcessDefinitionId();
				final String defKey = te.getExecution().getProcessDefinition().getKey();
				final String actid = te.getName();
				final String taskTitle = (String) runtimeService.getVariable(procId, "taskTitle") ; 
				final String starter =(String) runtimeService.getVariable(procId, "starter") ;
				
				SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
					@SuppressWarnings("unchecked")
					public void doSyncPO(AbstractFlowAbility po,
							BaseService baseService) {
					
						if(SunitStringUtil.isBlankOrNull(xwyMI)){
							Flowability flowAbility =po.getValidFlowability();  
							flowAbility.setTaskId(taskId); 
							flowAbility.setPoId(po.getId()); 
							flowAbility.setAssignee(assignee);
							flowAbility.setAssigneeType("");
							flowAbility.setCandidate("");
							flowAbility.setUpdateTime(DateUtil.getCurrentTime()) ;
							po.getFlowabilitys().add(flowAbility); 
							baseService.save(po); 
	//						po.setTaskId(taskId);  
	//						baseService.save(po); 
						}else{ 
//							Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);  
//							flowAbility.setTaskId(taskId);  
//							flowAbility.setPoId(po.getId()); 
//							flowAbility.setAssignee(assignee);
//							flowAbility.setAssigneeType("");
//							flowAbility.setCandidate("");
////							po.getFlowabilitys().add(flowAbility);  
//							flowabilityManager.save(flowAbility); 
							Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);  
							if(flowAbility==null){
								 flowAbility = new  Flowability();   
								 po.getFlowabilitys().add(flowAbility);  
							}
							flowAbility.setTaskId(taskId); 
							flowAbility.setPoId(po.getId());
							flowAbility.setAssignee(assignee);
							flowAbility.setAssigneeType(""); 
							flowAbility.setCandidate("");
//							po.getFlowabilitys().add(flowAbility);    
							flowAbility.setExcutionId(exId);    
							flowAbility.setProccessInstanceVersion(defVersion); 
							flowAbility.setProccessInstanceKey(defKey);   
							flowAbility.setActivityId(actid);
							flowAbility.setProccessInstanceId(procId); 
							flowAbility.setTaskTitle(taskTitle);
							flowAbility.setStarter(starter); 
							flowAbility.setCreateTime(DateUtil.getCurrentTime());
							
//							po.getFlowabilitys().add(flowAbility);   
//							flowabilityManager.save(flowAbility);  
							baseService.save(po); 
							  
							
						} 
					} 
				}); 
				
				logger.error("o.toString()=" + o.toString());
			}
		
			
		} 
		
		if(event.getType() ==  ActivitiEventType.CUSTOM_IDENTITYLINK_BEFORE){
//			ActivitiEntityEventImpl
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
			final String procId= event.getProcessInstanceId();
			String busid = (String) runtimeService.getVariable(procId, "businessId") ; 
			if(SunitStringUtil.isBlankOrNull(busid)){ 
				return;
			}
			String isOrderFlow =(String) runtimeService.getVariable(procId, "isOrderFlow") ; 
			if(!SunitStringUtil.isBlankOrNull(isOrderFlow)){
				return; 
			}
			Object o =  ((ActivitiEntityEventImpl)event).getEntity(); 
			if(IdentityLinkEntity.class.isAssignableFrom(o.getClass())){
				final IdentityLinkEntity entity =((IdentityLinkEntity)o);
				if(entity.getType().equals(IdentityLinkType.PARTICIPANT)){ //参与者类型不写入
					return;
				}
//				if(entity.getType().equals(IdentityLinkType.STARTER)){
//					starter =
//				}
				final String exId =entity.getTask().getExecutionId();
				
				String candidateType="";
				String candidateUserOrGroup=""; 
				if(!SunitStringUtil.isBlankOrNull(entity.getGroupId())){
					candidateType ="candidateGroup";
					candidateUserOrGroup =entity.getGroupId();
				}else {
					candidateType= entity.getType();
					candidateUserOrGroup= entity.getUserId();
				}
					
				
				final String type =candidateType; 
				final String candidate =candidateUserOrGroup ; //entity.getUserId();
				final String xwyMI =(String) runtimeService.getVariable(procId, "xwyMI") ;
				
				
				SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
					public void doSyncPO(AbstractFlowAbility po,
							BaseService baseService) {
						
						if(SunitStringUtil.isBlankOrNull(xwyMI)){
							Flowability flowAbility =po.getValidFlowability();  
							flowAbility.setAssignee("");   
							flowAbility.setAssigneeType(type);  
							flowAbility.setUpdateTime(DateUtil.getCurrentTime()) ;
							String allCandidate =  flowAbility.getCandidate();
							if(!SunitStringUtil.isBlankOrNull(allCandidate)){
								allCandidate =allCandidate+","+candidate; 
							}else
								allCandidate = candidate;  
							flowAbility.setCandidate(allCandidate); 
							po.getFlowabilitys().add(flowAbility); 
							baseService.save(po); 
						}else{
//							Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);
//							if(flowAbility==null){
//								 flowAbility = new  Flowability();   
//								 po.getFlowabilitys().add(flowAbility); 
//							}
//							flowAbility.setAssignee("");   
//							flowAbility.setAssigneeType(type);  
//							String allCandidate =  flowAbility.getCandidate();
//							if(!SunitStringUtil.isBlankOrNull(allCandidate)){
//								allCandidate =allCandidate+","+candidate; 
//							}else
//								allCandidate = candidate;  
//							flowAbility.setCandidate(allCandidate); 
//							  
////							flowabilityManager.save(flowAbility); 
//							baseService.save(po);
						}
						
						
					} 
				}); 
				
				logger.error("o.toString()=" + o.toString());
			}
			
			  
		}
		
		
		 
		if(event.getType() ==  ActivitiEventType.CUSTOM_DELETEPROCESSINSTANCE_BEFORE){
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
			Object o =  ((ActivitiEntityEventImpl)event).getEntity(); 
			final String procId= (String) o;
			String busid = (String) runtimeService.getVariable(procId, "businessId") ;
			SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
				public void doSyncPO(AbstractFlowAbility po, 
						BaseService baseService) { 
					baseService.delete(po.getFlowabilitys());  
					po.setFlowabilitys(null);
				}  
			}); 
		}
		
		
		if(event.getType() ==  ActivitiEventType.ENTITY_DELETED){
			final String exId =event.getExecutionId();
			final String procId = event.getProcessInstanceId();
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
//			Object o =  ((ActivitiEntityEventImpl)event).getEntity(); 
//			final String procId= (String) o;
			String busid = (String) runtimeService.getVariable(procId, "businessId") ;
			final String xwyMI =(String) runtimeService.getVariable(procId, "xwyMI") ; 
			
			Object o =  ((ActivitiEntityEventImpl)event).getEntity();  
			if(TaskEntity.class.isAssignableFrom(o.getClass())){
				TaskEntity te = (TaskEntity)o;
				final String taskId = te.getId();
				if(!SunitStringUtil.isBlankOrNull(xwyMI)){
					SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
						public void doSyncPO(AbstractFlowAbility po, 
								BaseService baseService) { 
							Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);
							flowAbility.setTaskId(""); 
						}     
					}); 
				}
			}

			
//			SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
//				public void doSyncPO(AbstractFlowAbility po, 
//						BaseService baseService) { 
//					Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);
//					if(flowAbility!=null){ 
////						baseService.delete(flowAbility);
//						List list = po.getFlowabilitys();
//						list.remove(flowAbility);
//						flowabilityManager.get(flowAbility.getId());
//						flowabilityManager.delete(flowAbility);
////						baseService.save(po);
//					}
//				}   
//			});  
		}
		
		if(event.getType()==ActivitiEventType.TASK_COMPLETED){
			//保存流程业务信息到 流程变量表
			TaskEntity  task= (TaskEntity) ((ActivitiEntityEventImpl)event).getEntity();  
			Map<String ,String> map = (Map<String, String>) task.getExecution().getVariable("businessVars");
			for ( Map.Entry<String, String> entry: map.entrySet()) {
				Flowvariables  flowvariables = new Flowvariables();
				flowvariables.setFvType("0"); 
				flowvariables.setFvTime(DateUtil.getCurrentTime());
				flowvariables.setFvUserId(task.getAssignee());
				flowvariables.setFvTaskId(task.getId());
				flowvariables.setFvProcInstId(task.getProcessInstanceId());
				flowvariables.setFvAction(task.getName());
				flowvariables.setFvContent(String.valueOf(entry.getValue()));
				flowvariables.setFvName(entry.getKey());
				flowvariables.setRemark(task.getTaskDefinitionKey()); 
				flowvariablesManager.save(flowvariables);
			}
			
			RuntimeService runtimeService= SpringContextUtils.getSpringContext().getBean(RuntimeService.class);
			final String procId= event.getProcessInstanceId();
			
			String isOrderFlow =(String) runtimeService.getVariable(procId, "isOrderFlow") ; 
//			if(!SunitStringUtil.isBlankOrNull(isOrderFlow)){
//				String busid= task.getProcessInstance().getBusinessKey();
//				Suborders sub =  subordersManager.get(busid);
//				sub.setOrdSubPreviousActivityId(task.getTaskDefinitionKey());
//				subordersManager.save(sub);  
//				return;
//			}
			
			
			String busid = (String) runtimeService.getVariable(procId, "businessId") ; 
			if(SunitStringUtil.isBlankOrNull(busid)){ 
				return;
			}
			final String xwyMI =(String) runtimeService.getVariable(procId, "xwyMI") ; 
			final String exId = task.getExecutionId();
			final String processor = task.getAssignee();
			
			SyncBusinessPO(procId,busid, new SyncBusinessPOCallback(){
				public void doSyncPO(AbstractFlowAbility po,
						BaseService baseService) {
					
					if(SunitStringUtil.isBlankOrNull(xwyMI)){
						Flowability flowAbility =po.getValidFlowability();  
						flowAbility.setAssignee("");   
						flowAbility.setAssigneeType("");  
						flowAbility.setCandidate("");
						flowAbility.setTaskId("");
						flowAbility.setUpdateTime(DateUtil.getCurrentTime()) ;
						po.getFlowabilitys().add(flowAbility);  
						baseService.save(po);
					}else{ 
						Flowability flowAbility =po.getValidFlowabilityByExcutionId(exId);  
//						if(flowAbility==null){ 
//							 flowAbility = new  Flowability();   
//							 po.getFlowabilitys().add(flowAbility); 
//						} 
//						flowAbility.setAssignee("");     
						flowAbility.setUpdateTime(DateUtil.getCurrentTime()) ;
						flowAbility.setLastProcessor(processor);
//						flowAbility.setAssigneeType("");   
//						flowAbility.setCandidate("");
//						flowAbility.setTaskId(""); 
//						 
////						po.getFlowabilitys().add(flowAbility);    
////						flowabilityManager.save(flowAbility); 
						baseService.save(po);
					}
				 
				} 
			}); 
			
			
			
			
			
			
//			if(task.getExecution().getVariable("auditRemark")!=null){
//				String message = task.getExecution().getVariable("auditRemark").toString();
//				event.getEngineServices().getTaskService().addComment(task.getId(),task.getProcessInstanceId(), "String", message);
//			}
		}
		 
		
		
		
//		if(event.getType()==ActivitiEventType.CUSTOM_IDENTITYLINK_BEFORE){ 
//			System.out.println("TaskListener.onEvent(CUSTOM_IDENTITYLINK_BEFORE)"); 
//			
//			IdentityLinkEntity  link= (IdentityLinkEntity) ((ActivitiEntityEventImpl)event).getEntity();
//			if(link.getTask()==null) return;
//			if (link.getTask().getExecution().getVariable("customRole")!=null){
//				link.setUserId(null);
//				link.setGroupId(link.getTask().getExecution().getVariable("customRole").toString());
//				
//				link.setType(IdentityLinkType.CANDIDATE);
//				
//			}
//			if (link.getTask().getExecution().getVariable("customUser")!=null){
//				link.setGroupId(null);
//				link.setUserId(link.getTask().getExecution().getVariable("customUser").toString());
//				
//				link.setType(IdentityLinkType.CANDIDATE);
//				 
//			} 
//			
//			if (link.getTask().getExecution().getVariable("customAssignee")!=null){
//				link.setGroupId(null);
//				link.setUserId(link.getTask().getExecution().getVariable("customAssignee").toString());  
//				link.setType(IdentityLinkType.ASSIGNEE);
//				
//			}
//			
//		} 
		
		
	}



	public boolean isFailOnException() {
		// TODO Auto-generated method stub
		return true;
	}
}
