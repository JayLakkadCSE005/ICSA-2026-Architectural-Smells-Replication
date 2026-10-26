package org.apache.activemq.transport.stomp;

import java.io.IOException;

import org.apache.activemq.command.ActiveMQMessage;

import jakarta.jms.JMSException;

public interface SubscriptionProtocolManager {
	public boolean isStomp12();
	public StompTransport getStompTransport();
	public StompFrame convertMessage(ActiveMQMessage message, boolean ignoreTransformation) throws IOException, JMSException;
}
