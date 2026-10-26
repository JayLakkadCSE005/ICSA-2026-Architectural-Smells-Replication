package org.apache.activemq.store.kahadb.disk.journal.enumeration;

public enum PreallocationStrategy {
    SPARSE_FILE,
    OS_KERNEL_COPY,
    ZEROS,
    CHUNKED_ZEROS;
}
