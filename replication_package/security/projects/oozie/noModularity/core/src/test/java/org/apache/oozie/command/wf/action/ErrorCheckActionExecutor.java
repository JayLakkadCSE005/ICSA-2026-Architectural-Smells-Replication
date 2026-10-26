package org.apache.oozie.command.wf.action;

import org.apache.oozie.action.Context;
import org.apache.oozie.action.ErrorType;
import org.apache.oozie.action.executor.ActionExecutor;
import org.apache.oozie.action.executor.ActionExecutorException;
import org.apache.oozie.client.WorkflowAction;

class ErrorCheckActionExecutor extends ActionExecutor {
    public static final String ERROR_CODE = "some_error";

    protected ErrorCheckActionExecutor() {
        super("map-reduce");
    }

    @Override
    public void start(Context context, WorkflowAction action) throws ActionExecutorException {
    }

    @Override
    public void end(Context context, WorkflowAction action) throws ActionExecutorException {
    }

    @Override
    public void check(Context context, WorkflowAction action) throws ActionExecutorException {
        throw new ActionExecutorException(ErrorType.ERROR, ERROR_CODE, "check");
    }

    @Override
    public void kill(Context context, WorkflowAction action) throws ActionExecutorException {
    }

    @Override
    public boolean isCompleted(String externalStatus) {
        return false;
    }
}
