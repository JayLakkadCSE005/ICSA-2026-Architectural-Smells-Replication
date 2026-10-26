package org.apache.activemq.store.kahadb.disk.journal;

import org.apache.activemq.store.kahadb.disk.journal.file.DataFile;

public interface DataFileRemovedListener {
    void fileRemoved(DataFile datafile);
}
