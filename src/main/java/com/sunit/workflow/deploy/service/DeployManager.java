package com.sunit.workflow.deploy.service;

import java.util.Map;

public interface DeployManager {
 
	public void saveComplete(String taskId,Map flowVariables); 
}
