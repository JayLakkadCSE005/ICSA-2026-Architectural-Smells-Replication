package org.apache.activemq.store.kahadb.disk.journal;

import org.apache.activemq.store.kahadb.disk.journal.engine.Location;

public class WriteKey {
    private final int file;
    private final long offset;
    private final int hash;

    public WriteKey(Location item) {
        file = item.getDataFileId();
        offset = item.getOffset();
        // TODO: see if we can build a better hash
        hash = (int) (file ^ offset);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WriteKey) {
            WriteKey di = (WriteKey) obj;
            return di.file == file && di.offset == offset;
        }
        return false;
    }
}
