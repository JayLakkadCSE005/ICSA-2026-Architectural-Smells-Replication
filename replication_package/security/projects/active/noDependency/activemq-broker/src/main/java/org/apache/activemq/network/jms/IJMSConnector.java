package org.apache.activemq.network.jms;

import jakarta.jms.Connection;
import jakarta.jms.Destination;

public interface IJMSConnector {
	abstract Destination createReplyToBridge(Destination destination, Connection consumerConnection, Connection producerConnection);
	public ReconnectionPolicy getReconnectionPolicy();
	void handleConnectionFailure(Connection connection);
}
