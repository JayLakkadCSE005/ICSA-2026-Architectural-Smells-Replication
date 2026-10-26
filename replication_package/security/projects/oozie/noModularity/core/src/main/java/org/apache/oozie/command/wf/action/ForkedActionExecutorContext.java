package org.apache.oozie.command.wf.action;

import org.apache.oozie.workflow.bean.WorkflowActionBean;
import org.apache.oozie.workflow.bean.WorkflowJobBean;

import java.util.HashMap;
import java.util.Map;

public class ForkedActionExecutorContext extends ActionExecutorContext {
    private Map<String, String> contextVariableMap = new HashMap<String, String>();

    public ForkedActionExecutorContext(WorkflowJobBean workflow, WorkflowActionBean action, boolean isRetry,
                                       boolean isUserRetry) {
        super(workflow, action, isRetry, isUserRetry);
    }

    public void setVar(String name, String value) {
        if (value == null) {
            contextVariableMap.remove(name);
        } else {
            contextVariableMap.put(name, value);
        }
    }

    public String getVar(String name) {
        return contextVariableMap.get(name);
    }

    public Map<String, String> getContextMap() {
        return contextVariableMap;
    }
}
