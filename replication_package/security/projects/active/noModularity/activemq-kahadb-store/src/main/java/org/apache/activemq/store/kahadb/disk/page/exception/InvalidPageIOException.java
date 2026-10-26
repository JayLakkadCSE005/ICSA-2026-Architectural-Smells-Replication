package org.apache.activemq.store.kahadb.disk.page.exception;

import java.io.IOException;

/**
 * The InvalidPageIOException is thrown if try to load/store a a page
 * with an invalid page id.
 */
public class InvalidPageIOException extends IOException {
    private static final long serialVersionUID = 1L;

    private final long page;

    public InvalidPageIOException(String message, long page) {
        super(message);
        this.page = page;
    }

    public long getPage() {
        return page;
    }
}
