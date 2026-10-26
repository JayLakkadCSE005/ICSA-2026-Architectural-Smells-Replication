package org.apache.activemq.filter.function.exception;

/**
 * Exception indicating that an invalid function call expression was created, usually by the selector parser.
 * Conditions include invalid function names and invalid function arguments.
 */

public class invalidFunctionExpressionException extends Exception {
    public invalidFunctionExpressionException(String msg) {
        super(msg);
    }

    public invalidFunctionExpressionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
