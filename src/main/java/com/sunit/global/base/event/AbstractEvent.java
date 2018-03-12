package com.sunit.global.base.event;

import org.springframework.context.ApplicationEvent; 


public class AbstractEvent extends ApplicationEvent{
	
	/**
	 * 事件类型, 为class的full path 例:
	 * AgencycustomInsertEvent.class.getName() 
	 * ServicesconfigEvent.class.getName() 
	 */
	public String eventType; 
	
	public AbstractEvent(Object source) {  
		super(source);
		// TODO Auto-generated constructor stub
	}

	public String getEventType() {
		
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

}
