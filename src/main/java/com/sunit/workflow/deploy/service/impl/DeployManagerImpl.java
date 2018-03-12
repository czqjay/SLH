package com.sunit.workflow.deploy.service.impl;

import java.util.Map;

import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunit.workflow.deploy.service.DeployManager;


@Service
public class DeployManagerImpl implements DeployManager{

	@Autowired 
	TaskService taskService; 
	
	public void saveComplete(String taskId,Map flowVariables) {
		taskService.complete(taskId, flowVariables); 
//		System.out.println(1 / 0); 
	} 
 
}
