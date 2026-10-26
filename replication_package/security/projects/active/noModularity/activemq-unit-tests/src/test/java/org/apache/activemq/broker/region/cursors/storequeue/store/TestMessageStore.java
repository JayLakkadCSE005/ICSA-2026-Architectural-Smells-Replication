package org.apache.activemq.broker.region.cursors.storequeue.store;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.memory.messageStore.AbstractMessageStore;
import org.apache.activemq.store.recovery.MessageRecoveryListener;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

class TestMessageStore extends AbstractMessageStore {
    final Message[] messages;
    public AtomicLong batch = new AtomicLong();

    public TestMessageStore(Message[] messages, ActiveMQDestination dest) {
        super(dest);
        this.messages = messages;
    }

    @Override
    public void addMessage(ConnectionContext context, Message message) throws IOException {

    }

    @Override
    public Message getMessage(MessageId identity) throws IOException {
        return null;
    }

    @Override
    public void removeMessage(ConnectionContext context, MessageAck ack) throws IOException {

    }

    @Override
    public void removeAllMessages(ConnectionContext context) throws IOException {

    }

    @Override
    public void recover(MessageRecoveryListener container) throws Exception {

    }

    @Override
    public void resetBatching() {

    }

    @Override
    public void recoverNextMessages(int maxReturned, MessageRecoveryListener listener) throws Exception {
        for (int i = batch.intValue(); i < messages.length; i++) {
            StoreQueueCursorOrderTest.LOG.info("recovered index:" + i);
            listener.recoverMessage(messages[i]);
        }
    }

    @Override
    public void setBatch(MessageId message) {
        batch.set((Long) message.getFutureOrSequenceLong());
        batch.incrementAndGet();
    }

    @Override
    public void recoverMessageStoreStatistics() throws IOException {
        this.getMessageStoreStatistics().reset();
    }

}
