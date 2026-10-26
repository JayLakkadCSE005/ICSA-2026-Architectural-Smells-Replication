package org.apache.oozie.util;

public class XCause {
    /**
     * Utility method that extracts the <code>Throwable</code>, if present, from the parameters.
     *
     * @param params parameters.
     * @return a <code>Throwable</code> instance if it is the last parameter, <code>null</code> otherwise.
     */
    public static Throwable getCause(Object... params) {
        Throwable throwable = null;
        if (params != null && params.length > 0 && params[params.length - 1] instanceof Throwable) {
            throwable = (Throwable) params[params.length - 1];
        }
        return throwable;
    }
}
