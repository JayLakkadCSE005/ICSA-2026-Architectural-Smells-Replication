package org.apache.activemq.store.jdbc;

import java.io.IOException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.util.LongSequenceGenerator;

public interface MessageManagerInterface {
	public BrokerService getBrokerService();
	public TransactionContext getTransactionContext() throws IOException;
	public TransactionContext getTransactionContext(ConnectionContext context) throws IOException;
	public long getNextSequenceId();
	public LongSequenceGenerator getSequenceGenrator();
	public long[] getStoreSequenceIdForMessageId(ConnectionContext context, MessageId messageId, ActiveMQDestination destination) throws IOException;
}
