package org.apache.activemq.store.kahadb.plist.impl;

import org.apache.activemq.store.kahadb.disk.journal.engine.Location;
import org.apache.activemq.store.kahadb.disk.page.closure.Closure;
import org.apache.activemq.store.kahadb.disk.page.engine.Transaction;
import org.apache.activemq.store.kahadb.plist.util.Locator;
import org.apache.activemq.store.pList.PList;
import org.apache.activemq.store.pList.PListEntry;
import org.apache.activemq.util.ByteSequence;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class PListIteratorImpl implements PList.PListIterator {
    private final PListImpl pList;
    final Iterator<Map.Entry<String, Location>> iterator;
    final Transaction tx;

    public PListIteratorImpl(PListImpl pList) throws IOException {
        this.pList = pList;
        tx = pList.store.pageFile.tx();
        synchronized (pList.indexLock) {
            this.iterator = pList.iterator(tx);
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public PListEntry next() {
        Map.Entry<String, Location> entry = iterator.next();
        ByteSequence bs = null;
        try {
            bs = pList.store.getPayload(entry.getValue());
        } catch (IOException unexpected) {
            NoSuchElementException e = new NoSuchElementException(unexpected.getLocalizedMessage());
            e.initCause(unexpected);
            throw e;
        }
        return new PListEntry(entry.getKey(), bs, new Locator(pList, entry.getKey()));
    }

    @Override
    public void remove() {
        try {
            synchronized (pList.indexLock) {
                tx.execute(new Closure<IOException>() {
                    @Override
                    public void execute(Transaction tx) throws IOException {
                        iterator.remove();
                    }
                });
            }
        } catch (IOException unexpected) {
            IllegalStateException e = new IllegalStateException(unexpected);
            e.initCause(unexpected);
            throw e;
        }
    }

    @Override
    public void release() {
        try {
            tx.rollback();
        } catch (IOException unexpected) {
            IllegalStateException e = new IllegalStateException(unexpected);
            e.initCause(unexpected);
            throw e;
        }
    }
}
