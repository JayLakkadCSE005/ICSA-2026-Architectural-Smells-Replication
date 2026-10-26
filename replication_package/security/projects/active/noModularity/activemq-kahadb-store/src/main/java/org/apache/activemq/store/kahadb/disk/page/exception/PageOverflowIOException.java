package org.apache.activemq.store.kahadb.disk.page.exception;

import java.io.IOException;

/**
 * The PageOverflowIOException occurs when a page write is requested
 * and it's data is larger than what would fit into a single page.
 */
public class PageOverflowIOException extends IOException {
    private static final long serialVersionUID = 1L;

    public PageOverflowIOException(String message) {
        super(message);
    }
}
