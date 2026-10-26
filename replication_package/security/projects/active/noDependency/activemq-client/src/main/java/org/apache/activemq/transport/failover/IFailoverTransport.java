package org.apache.activemq.transport.failover;

public interface IFailoverTransport {
	public void reconnect(boolean rebalance);
}
