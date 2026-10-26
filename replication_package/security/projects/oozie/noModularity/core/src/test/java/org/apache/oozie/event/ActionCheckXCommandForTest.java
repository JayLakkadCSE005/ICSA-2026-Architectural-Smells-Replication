package org.apache.oozie.event;

import org.apache.oozie.command.wf.action.ActionExecutorContext;
import org.apache.oozie.workflow.bean.WorkflowActionBean;
import org.apache.oozie.action.Context;
import org.apache.oozie.action.control.ControlNodeActionExecutor;
import org.apache.oozie.action.executor.ActionExecutor;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.command.CommandException;
import org.apache.oozie.command.wf.action.ActionCheckXCommand;
import org.apache.oozie.executor.jpa.JPAExecutorException;
import org.apache.oozie.executor.jpa.WorkflowActionGetJPAExecutor;
import org.apache.oozie.service.EventHandlerService;
import org.apache.oozie.service.JPAService;
import org.apache.oozie.service.Services;
import org.apache.oozie.test.XTestCase;

class ActionCheckXCommandForTest extends ActionCheckXCommand {

    Context context;
    ActionExecutor executor;
    WorkflowActionBean action;
    JPAService jpa;

    public ActionCheckXCommandForTest(Context context, ActionExecutor executor, String actionId)
            throws JPAExecutorException {
        super(actionId);
        this.context = context;
        this.executor = executor;
        jpa = Services.get().get(JPAService.class);
        this.action = jpa.execute(new WorkflowActionGetJPAExecutor(actionId));
    }

    @Override
    public Void execute() throws CommandException {
        handleNonTransient(context, executor, WorkflowAction.Status.START_MANUAL);
        action = (WorkflowActionBean) ((ActionExecutorContext) context).getAction();
        if (!(executor instanceof ControlNodeActionExecutor) && EventHandlerService.isEnabled()) {
            generateEvent(action, XTestCase.getTestUser());
        }
        return null;
    }

    public WorkflowActionBean getAction() {
        return action;
    }

}
