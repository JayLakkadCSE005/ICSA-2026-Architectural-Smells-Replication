package org.apache.activemq.broker.region.cursors.memory.iterator;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.cursors.memory.FilePendingMessageCursor;
import org.apache.activemq.command.Message;
import org.apache.activemq.store.pList.PList;
import org.apache.activemq.store.pList.PListEntry;

import java.io.IOException;
import java.util.Iterator;

public final class DiskIterator implements Iterator<MessageReference> {
    private final FilePendingMessageCursor filePendingMessageCursor;
    private final PList.PListIterator iterator;

    public DiskIterator(FilePendingMessageCursor filePendingMessageCursor) {
        this.filePendingMessageCursor = filePendingMessageCursor;
        try {
            iterator = filePendingMessageCursor.getDiskList().iterator();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public MessageReference next() {
        try {
            PListEntry entry = iterator.next();
            Message message = filePendingMessageCursor.getMessage(entry.getByteSequence());
            message.getMessageId().setPlistLocator(entry.getLocator());
            return message;
        } catch (IOException e) {
            FilePendingMessageCursor.LOG.error("I/O error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        iterator.remove();
    }

    public void release() {
        iterator.release();
    }
}
