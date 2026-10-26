package org.apache.activemq.broker.region.cursors.listMessage;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.cursors.pendinglist.PendingList;
import org.apache.activemq.broker.region.cursors.pendinglist.PendingNode;
import org.apache.activemq.command.MessageId;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

class TestPendingList implements PendingList {

    private final LinkedList<MessageReference> theList = new LinkedList<MessageReference>();

    @Override
    public boolean isEmpty() {
        return theList.isEmpty();
    }

    @Override
    public void clear() {
        theList.clear();
    }

    @Override
    public PendingNode addMessageFirst(MessageReference message) {
        theList.addFirst(message);
        return new PendingNode(null, message);
    }

    @Override
    public PendingNode addMessageLast(MessageReference message) {
        theList.addLast(message);
        return new PendingNode(null, message);
    }

    @Override
    public PendingNode remove(MessageReference message) {
        if (theList.remove(message)) {
            return new PendingNode(null, message);
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return theList.size();
    }

    @Override
    public long messageSize() {
        long size = 0;
        Iterator<MessageReference> i = theList.iterator();
        while (i.hasNext()) {
            size += i.next().getMessage().getSize();
        }
        return size;
    }

    @Override
    public Iterator<MessageReference> iterator() {
        return theList.iterator();
    }

    @Override
    public boolean contains(MessageReference message) {
        return theList.contains(message);
    }

    @Override
    public Collection<MessageReference> values() {
        return theList;
    }

    @Override
    public void addAll(PendingList pendingList) {
        for (MessageReference messageReference : pendingList) {
            theList.add(messageReference);
        }
    }

    @Override
    public MessageReference get(MessageId messageId) {
        for (MessageReference messageReference : theList) {
            if (messageReference.getMessageId().equals(messageId)) {
                return messageReference;
            }
        }
        return null;
    }
}
