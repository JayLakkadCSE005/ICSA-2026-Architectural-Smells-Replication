package org.apache.activemq.broker.region.cursors.listMessage;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.util.IdGenerator;

class TestMessageReference implements MessageReference {

    private static final IdGenerator id = new IdGenerator();

    private MessageId messageId;
    private int referenceCount = 0;

    public TestMessageReference(int sequenceId) {
        messageId = new MessageId(id.generateId() + ":1", sequenceId);
    }

    @Override
    public MessageId getMessageId() {
        return messageId;
    }

    @Override
    public Message getMessageHardRef() {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public Destination getRegionDestination() {
        return null;
    }

    @Override
    public int getRedeliveryCounter() {
        return 0;
    }

    @Override
    public void incrementRedeliveryCounter() {
    }

    @Override
    public int getReferenceCount() {
        return this.referenceCount;
    }

    @Override
    public int incrementReferenceCount() {
        return this.referenceCount++;
    }

    @Override
    public int decrementReferenceCount() {
        return this.referenceCount--;
    }

    @Override
    public ConsumerId getTargetConsumerId() {
        return null;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public long getExpiration() {
        return 0;
    }

    @Override
    public String getGroupID() {
        return null;
    }

    @Override
    public int getGroupSequence() {
        return 0;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public boolean isDropped() {
        return false;
    }

    @Override
    public boolean isAdvisory() {
        return false;
    }

    @Override
    public boolean canProcessAsExpired() {
        return false;
    }
}
