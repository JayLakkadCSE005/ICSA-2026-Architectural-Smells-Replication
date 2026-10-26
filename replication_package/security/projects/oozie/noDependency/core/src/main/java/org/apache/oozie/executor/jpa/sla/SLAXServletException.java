package org.apache.oozie.executor.jpa.sla;

import javax.servlet.ServletException;

import org.apache.oozie.ErrorCode;
import org.apache.oozie.util.XCause;

public class SLAXServletException extends ServletException {
	private static final long serialVersionUID = 1L;
    private final ErrorCode errorCode;
    private final int httpStatusCode;
    
    public SLAXServletException(int httpStatusCode, ErrorCode errorCode, Object... params) {
        super(errorCode.format(params), XCause.getCause(params));
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
    /**
     * Return the Oozie error code for the exception.
     *
     * @return error code for the exception.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Return the HTTP error code to return to the client.
     *
     * @return HTTP error code.
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
