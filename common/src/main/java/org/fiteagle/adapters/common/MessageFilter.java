package org.fiteagle.adapters.common;

import org.fiteagle.api.core.IMessageBus;

public class MessageFilter {

	public static final String MESSAGE_FILTER = IMessageBus.METHOD_TYPE + " = '" + IMessageBus.TYPE_CREATE
			+ "' OR " + IMessageBus.METHOD_TYPE + " = '"
			+ IMessageBus.TYPE_CONFIGURE + "' OR " + IMessageBus.METHOD_TYPE
			+ " = '" + IMessageBus.TYPE_DELETE + "'";

}
