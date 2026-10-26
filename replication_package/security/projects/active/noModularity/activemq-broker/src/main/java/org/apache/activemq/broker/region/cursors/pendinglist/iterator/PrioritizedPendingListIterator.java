package org.apache.activemq.broker.region.cursors.pendinglist.iterator;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.cursors.pendinglist.OrderedPendingList;
import org.apache.activemq.broker.region.cursors.pendinglist.PrioritizedPendingList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public final class PrioritizedPendingListIterator implements Iterator<MessageReference> {

    private final PrioritizedPendingList messageReferences;
    private final Deque<Iterator<MessageReference>> iterators = new ArrayDeque<Iterator<MessageReference>>();

    private Iterator<MessageReference> current;
    private MessageReference currentMessage;

    public PrioritizedPendingListIterator(PrioritizedPendingList messageReferences) {
        this.messageReferences = messageReferences;
        for (OrderedPendingList list : messageReferences.lists) {
            if (!list.isEmpty()) {
                iterators.push(list.iterator());
            }
        }

        current = iterators.poll();
    }

    @Override
    public boolean hasNext() {
        while (current != null) {
            if (current.hasNext()) {
                return true;
            } else {
                current = iterators.poll();
            }
        }

        return false;
    }

    @Override
    public MessageReference next() {
        MessageReference result = null;

        while (current != null) {
            if (current.hasNext()) {
                result = currentMessage = current.next();
                break;
            } else {
                current = iterators.poll();
            }
        }

        return result;
    }

    @Override
    public void remove() {
        if (currentMessage != null) {
            messageReferences.pendingMessageHelper.removeFromMap(currentMessage);
            current.remove();
            currentMessage = null;
        }
    }
}
