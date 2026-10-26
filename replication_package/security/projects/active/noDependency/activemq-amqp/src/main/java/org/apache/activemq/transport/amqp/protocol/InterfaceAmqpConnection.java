package org.apache.activemq.transport.amqp.protocol;

import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.ConsumerInfo;

public interface InterfaceAmqpConnection {
	public void sendToActiveMQ(Command command, ResponseHandler handler);
	public ConnectionId getConnectionId();
	public String getConfiguredTransformer();
	public Subscription lookupPrefetchSubscription(ConsumerInfo consumerInfo);
	 public String getClientId();
}
