package org.apache.oozie.action;

import org.apache.oozie.action.executor.ActionExecutor;

/**
 * Enum that defines the type of error an {@link ActionExecutor} has produced.
 */
public enum ErrorType {

    /**
     * The action will be automatically retried by Oozie.
     */
    TRANSIENT,

    /**
     * The job in set in SUSPEND mode and it will wait for an admin to resume the job.
     */

    NON_TRANSIENT,

    /**
     * The action completes with an error transition.
     */
    ERROR,

    /**
     * The action fails. No transition is taken.
     */
    FAILED
}
