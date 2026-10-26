package org.apache.oozie.command.wf.action;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.action.Context;
import org.apache.oozie.client.Job;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.client.rest.JsonTags;
import org.apache.oozie.function.DagELFunctions;
import org.apache.oozie.service.*;
import org.apache.oozie.util.ELEvaluator;
import org.apache.oozie.util.JobUtils;
import org.apache.oozie.util.XConfiguration;
import org.apache.oozie.workflow.WorkflowInstance;
import org.apache.oozie.workflow.bean.WorkflowActionBean;
import org.apache.oozie.workflow.bean.WorkflowJobBean;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

/**
 * Workflow action executor context
 */
public class ActionExecutorContext implements Context {
    protected final WorkflowJobBean workflow;
    private Configuration protoConf;
    protected final WorkflowActionBean action;
    private final boolean isRetry;
    private final boolean isUserRetry;
    private boolean started;
    private boolean ended;
    private boolean executed;
    private boolean shouldEndWF;
    private Job.Status jobStatus;

    /**
     * Constructing the ActionExecutorContext, setting the private members
     * and constructing the proto configuration
     *
     * @param workflow    workflow
     * @param action      action
     * @param isRetry     set to true if retry mode
     * @param isUserRetry set to true if user retry mode
     */
    public ActionExecutorContext(WorkflowJobBean workflow, WorkflowActionBean action, boolean isRetry,
                                 boolean isUserRetry) {
        this.workflow = workflow;
        this.action = action;
        this.isRetry = isRetry;
        this.isUserRetry = isUserRetry;
        if (null != workflow.getProtoActionConf()) {
            try {
                protoConf = new XConfiguration(new StringReader(workflow.getProtoActionConf()));
            } catch (IOException ex) {
                throw new RuntimeException("Failed to construct the proto configuration", ex);
            }
        }
    }

    public ActionExecutorContext(WorkflowJobBean workflow, WorkflowActionBean action) {
        this(workflow, action, false, false);
    }

    public String getCallbackUrl(String externalStatusVar) {
        return Services.get().get(CallbackService.class).createCallBackUrl(action.getId(), externalStatusVar);
    }

    public Configuration getProtoActionConf() {
        return protoConf;
    }

    public WorkflowJob getWorkflow() {
        return workflow;
    }

    /**
     * Returns the workflow action of the given action context
     *
     * @return the workflow action of the given action context
     */
    public WorkflowAction getAction() {
        return action;
    }

    public ELEvaluator getELEvaluator() {
        ELEvaluator evaluator = Services.get().get(ELService.class).createEvaluator("workflow");
        DagELFunctions.configureEvaluator(evaluator, workflow, action);
        return evaluator;
    }

    public void setVar(String name, String value) {
        setVarToWorkflow(name, value);
    }

    /**
     * This is not thread safe, don't use if workflowjob is shared among multiple actions command
     *
     * @param name  variable name
     * @param value variable value
     */
    public void setVarToWorkflow(String name, String value) {
        name = action.getName() + WorkflowInstance.NODE_VAR_SEPARATOR + name;
        WorkflowInstance wfInstance = workflow.getWorkflowInstance();
        wfInstance.setVar(name, value);
        workflow.setWorkflowInstance(wfInstance);
    }

    public String getVar(String name) {
        name = action.getName() + WorkflowInstance.NODE_VAR_SEPARATOR + name;
        return workflow.getWorkflowInstance().getVar(name);
    }

    public void setStartData(String externalId, String trackerUri, String consoleUrl) {
        setVar(JobUtils.getRetryKey(action, JsonTags.WORKFLOW_ACTION_CONSOLE_URL), consoleUrl);
        action.setStartData(externalId, trackerUri, consoleUrl);
        started = true;
    }

    /**
     * Setting the start time of the action
     */
    public void setStartTime() {
        Date now = new Date();
        action.setStartTime(now);
    }

    public void setExecutionData(String externalStatus, Properties actionData) {
        action.setExecutionData(externalStatus, actionData);
        executed = true;
    }

    public void setExecutionStats(String jsonStats) {
        action.setExecutionStats(jsonStats);
        executed = true;
    }

    public void setExternalChildIDs(String externalChildIDs) {
        setVar(JobUtils.getRetryKey(action, JsonTags.WORKFLOW_ACTION_EXTERNAL_CHILD_IDS), externalChildIDs);
        action.setExternalChildIDs(externalChildIDs);
        executed = true;
    }

    public void setEndData(WorkflowAction.Status status, String signalValue) {
        action.setEndData(status, signalValue);
        ended = true;
    }

    public boolean isRetry() {
        return isRetry;
    }

    /**
     * Return if the executor invocation is a user retry or not.
     *
     * @return if the executor invocation is a user retry or not.
     */
    public boolean isUserRetry() {
        return isUserRetry;
    }

    /**
     * Returns whether setStartData has been called or not.
     *
     * @return true if start completion info has been set.
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Returns whether setExecutionData has been called or not.
     *
     * @return true if execution completion info has been set, otherwise false.
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * Returns whether setEndData has been called or not.
     *
     * @return true if end completion info has been set.
     */
    public boolean isEnded() {
        return ended;
    }

    public void setExternalStatus(String externalStatus) {
        action.setExternalStatus(externalStatus);
    }

    @Override
    public String getRecoveryId() {
        return action.getId() + ActionXCommand.RECOVERY_ID_SEPARATOR + workflow.getRun();
    }

    public Path getActionDir() throws HadoopAccessorException, IOException, URISyntaxException {
        String name = getWorkflow().getId() + "/" + action.getName() + "--" + action.getType();
        FileSystem fs = getAppFileSystem();
        String actionDirPath = Services.get().getSystemId() + "/" + name;
        Path fqActionDir = new Path(fs.getHomeDirectory(), actionDirPath);
        return fqActionDir;
    }

    public FileSystem getAppFileSystem() throws HadoopAccessorException, IOException, URISyntaxException {
        WorkflowJob workflow = getWorkflow();
        URI uri = new URI(getWorkflow().getAppPath());
        HadoopAccessorService has = Services.get().get(HadoopAccessorService.class);
        Configuration fsConf = has.createConfiguration(uri.getAuthority());
        return has.createFileSystem(workflow.getUser(), uri, fsConf);

    }

    @Override
    public void setErrorInfo(String str, String exMsg) {
        action.setErrorInfo(str, exMsg);
    }

    public boolean isShouldEndWF() {
        return shouldEndWF;
    }

    public void setShouldEndWF(boolean shouldEndWF) {
        this.shouldEndWF = shouldEndWF;
    }

    public Job.Status getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Job.Status jobStatus) {
        this.jobStatus = jobStatus;
    }
}
