package org.apache.activemq.transport.mqtt;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.apache.activemq.command.ActiveMQMessage;
import org.fusesource.mqtt.codec.PUBLISH;

import jakarta.jms.JMSException;

public interface InterfaceMQTTProtocolConverter {
	public static final int DEFAULT_CACHE_SIZE = 5000;
	public MQTTPacketIdGenerator getPacketIdGenerator();
	public String getClientId();
	public PUBLISH convertMessage(ActiveMQMessage message) throws IOException, JMSException, DataFormatException;
}
