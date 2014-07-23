package org.fiteagle.api.core;


public interface IMessageBus {
	public static final String TYPE_RESPONSE = "response";
	public static final String TYPE_REQUEST = "request";
	public static final String TYPE_RESULT = "result";
	public static final String TOPIC_CORE = "topic/core";
	public static final String TOPIC_CORE_NAME = "java:/" + TOPIC_CORE;
	public static final String TOPIC_ADAPTERS = "java:/topic/adapters";
}
