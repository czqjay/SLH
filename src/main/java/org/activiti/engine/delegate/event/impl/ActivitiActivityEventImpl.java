/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.delegate.event.impl;

import org.activiti.engine.delegate.event.ActivitiActivityEvent;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.runtime.InterpretableExecution;

/**
 * Implementation of an {@link ActivitiActivityEvent}.
 * 
 * @author Frederik Heremans
 */
public class ActivitiActivityEventImpl extends ActivitiEventImpl implements ActivitiActivityEvent {
 
	protected String activityId;
	protected InterpretableExecution execution ; 
	 
	 

	public InterpretableExecution getExecution() {
		return execution;
	}

	public void setExecution(InterpretableExecution execution) {
		this.execution = execution;
	}

	public ActivitiActivityEventImpl(ActivitiEventType type) {
	  super(type);
  }

  public String getActivityId() {
		return activityId;
  }
	
	public void setActivityId(String activityId) {
	  this.activityId = activityId;
  }
}
