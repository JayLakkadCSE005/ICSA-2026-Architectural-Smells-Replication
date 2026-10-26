package org.apache.oozie.workflow.lite.config;

import org.apache.oozie.workflow.lite.NodeDef;
import org.apache.oozie.workflow.lite.manager.istance.LiteWorkflowInstance;

public interface Context {

    NodeDef getNodeDef();

    String getExecutionPath();

    String getParentExecutionPath(String executionPath);

    String getSignalValue();

    void setVar(String name, String value);

    String getVar(String name);

    void setTransientVar(String name, Object value);

    Object getTransientVar(String name);

    String createExecutionPath(String name);

    //can be called only from exit(), creation of execPaths is automatic
    //when a handler returns more than one transition.
    void deleteExecutionPath();

    //must be used by multiExit
    String createFullTransition(String executionPath, String transition);

    void killJob();

    void completeJob();

    LiteWorkflowInstance getProcessInstance();
}
