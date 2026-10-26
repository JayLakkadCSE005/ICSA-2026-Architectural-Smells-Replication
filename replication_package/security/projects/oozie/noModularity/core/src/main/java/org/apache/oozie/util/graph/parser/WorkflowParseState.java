package org.apache.oozie.util.graph.parser;

public class WorkflowParseState {
    public String action;
    public String actionOK;
    public String actionErr;
    public String actionType;
    public String fork;
    public String decision;

    public void reset() {
        action = null;
        actionOK = null;
        actionErr = null;
        actionType = null;
    }
}
