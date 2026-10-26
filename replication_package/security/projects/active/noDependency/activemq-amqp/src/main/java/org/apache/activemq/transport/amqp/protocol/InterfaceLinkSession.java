package org.apache.activemq.transport.amqp.protocol;

import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.TransactionId;
import org.apache.qpid.proton.engine.Sender;

public interface InterfaceLinkSession {
	public InterfaceAmqpConnection getConnection();
    public void unregisterSender(ConsumerId consumerId);
    public void pumpProtonToSocket();
    public void enlist(TransactionId txId);
    public long getMaxFrameSize();
    public SessionId getSessionId();
	public void registerSender(ConsumerId consumerId, InterfaceLinkSession session, Sender endpoint,
			ConsumerInfo consumerInfo);
}
