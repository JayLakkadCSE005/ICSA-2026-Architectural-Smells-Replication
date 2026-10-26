package org.apache.oozie.action.executor;

import org.apache.oozie.action.ErrorType;

class ErrorInfo {
    ErrorType errorType;
    String errorCode;
    Class<?> errorClass;

    ErrorInfo(ErrorType errorType, String errorCode, Class<?> errorClass) {
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorClass = errorClass;
    }
}
