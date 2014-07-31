package org.fiteagle.api.core;


public interface IMessageBus {
	public static final String TYPE_RESPONSE = "response";
	public static final String TYPE_REQUEST = "request";
	public static final String TYPE_RESULT = "result";
	public static final String TYPE_NOTIFICATION = "notification";
	
	public static final String EVENT_NOTIFICATION = "eventNotification";
	
	public static final String TOPIC_CORE = "topic/core";
	public static final String TOPIC_CORE_NAME = "java:/" + TOPIC_CORE;
	public static final String TOPIC_ADAPTERS = "java:/topic/adapters";
	
	public static final String REQUEST_DESCRIBE = "description";
	public static final String REQUEST_LIST_RESOURCES = "listResources";
	public static final String REQUEST_PROVISION = "provision";
	public static final String REQUEST_MONITOR = "monitor";
	public static final String REQUEST_CONTROL = "control";
	public static final String REQUEST_TERMINATE = "terminate";

}
