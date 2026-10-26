package org.apache.oozie.workflow.lite.manager.istance;

import org.apache.oozie.workflow.WorkflowInstance;
import org.apache.oozie.workflow.lite.NodeDef;

class Context implements org.apache.oozie.workflow.lite.config.Context {
    private final LiteWorkflowInstance liteWorkflowInstance;
    private NodeDef nodeDef;
    private String executionPath;
    private String exitState;
    WorkflowInstance.Status status = WorkflowInstance.Status.RUNNING;

    Context(LiteWorkflowInstance liteWorkflowInstance, NodeDef nodeDef, String executionPath, String exitState) {
        this.liteWorkflowInstance = liteWorkflowInstance;
        this.nodeDef = nodeDef;
        this.executionPath = executionPath;
        this.exitState = exitState;
    }

    public NodeDef getNodeDef() {
        return nodeDef;
    }

    public String getExecutionPath() {
        return executionPath;
    }

    public String getParentExecutionPath(String executionPath) {
        return LiteWorkflowInstance.getParentPath(executionPath);
    }

    public String getSignalValue() {
        return exitState;
    }

    public String createExecutionPath(String name) {
        return LiteWorkflowInstance.createChildPath(executionPath, name);
    }

    public String createFullTransition(String executionPath, String transition) {
        return LiteWorkflowInstance.createFullTransition(executionPath, transition);
    }

    public void deleteExecutionPath() {
        if (!liteWorkflowInstance.executionPaths.containsKey(executionPath)) {
            throw new IllegalStateException();
        }
        liteWorkflowInstance.executionPaths.remove(executionPath);
        executionPath = LiteWorkflowInstance.getParentPath(executionPath);
    }

    public void failJob() {
        status = WorkflowInstance.Status.FAILED;
    }

    public void killJob() {
        status = WorkflowInstance.Status.KILLED;
    }

    public void completeJob() {
        status = WorkflowInstance.Status.SUCCEEDED;
    }

    @Override
    public Object getTransientVar(String name) {
        return liteWorkflowInstance.getTransientVar(name);
    }

    @Override
    public String getVar(String name) {
        return liteWorkflowInstance.getVar(name);
    }

    @Override
    public void setTransientVar(String name, Object value) {
        liteWorkflowInstance.setTransientVar(name, value);
    }

    @Override
    public void setVar(String name, String value) {
        liteWorkflowInstance.setVar(name, value);
    }

    @Override
    public LiteWorkflowInstance getProcessInstance() {
        return liteWorkflowInstance;
    }

}
