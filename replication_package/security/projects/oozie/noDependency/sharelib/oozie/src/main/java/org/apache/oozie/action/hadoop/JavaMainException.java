package org.apache.oozie.action.hadoop;

/**
 * Used by JavaMain to wrap a Throwable when an Exception occurs
 */
@SuppressWarnings("serial")
class JavaMainException extends Exception {
    public JavaMainException(Throwable t) {
        super(t);
    }
}
