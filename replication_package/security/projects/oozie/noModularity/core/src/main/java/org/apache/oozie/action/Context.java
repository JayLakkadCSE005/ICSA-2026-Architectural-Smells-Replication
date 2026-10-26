package org.apache.oozie.action;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.service.HadoopAccessorException;
import org.apache.oozie.util.ELEvaluator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Context information passed to the ActionExecutor methods.
 */
public interface Context {

    /**
     * Create the callback URL for the action.
     *
     * @param externalStatusVar variable for the caller to inject the external status.
     * @return the callback URL.
     */
    String getCallbackUrl(String externalStatusVar);

    /**
     * Return a proto configuration for actions with auth properties already set.
     *
     * @return a proto configuration for actions with auth properties already set.
     */
    Configuration getProtoActionConf();

    /**
     * Return the workflow job.
     *
     * @return the workflow job.
     */
    WorkflowJob getWorkflow();

    /**
     * Return an ELEvaluator with the context injected.
     *
     * @return configured ELEvaluator.
     */
    ELEvaluator getELEvaluator();

    /**
     * Set a workflow action variable. <p> Convenience method that prefixes the variable name with the action name
     * plus a '.'.
     *
     * @param name  variable name.
     * @param value variable value, <code>null</code> removes the variable.
     */
    void setVar(String name, String value);

    /**
     * Get a workflow action variable. <p> Convenience method that prefixes the variable name with the action name
     * plus a '.'.
     *
     * @param name variable name.
     * @return the variable value, <code>null</code> if not set.
     */
    String getVar(String name);

    /**
     * Set the action tracking information for an successfully started action.
     *
     * @param externalId the action external ID.
     * @param trackerUri the action tracker URI.
     * @param consoleUrl the action console URL.
     */
    void setStartData(String externalId, String trackerUri, String consoleUrl);

    /**
     * Set the action execution completion information for an action. The action status is set to {@link
     * org.apache.oozie.client.WorkflowAction.Status#DONE}
     *
     * @param externalStatus the action external end status.
     * @param actionData     the action data on completion, <code>null</code> if none.
     */
    void setExecutionData(String externalStatus, Properties actionData);

    /**
     * Set execution statistics information for a particular action. The action status is set to {@link
     * org.apache.oozie.client.WorkflowAction.Status#DONE}
     *
     * @param jsonStats the JSON string representation of the stats.
     */
    void setExecutionStats(String jsonStats);

    /**
     * Set external child IDs for a particular action (Eg: pig). The action status is set to {@link
     * org.apache.oozie.client.WorkflowAction.Status#DONE}
     *
     * @param externalChildIDs the external child IDs as a comma-delimited string.
     */
    void setExternalChildIDs(String externalChildIDs);

    /**
     * Set the action end completion information for a completed action.
     *
     * @param status      the action end status, it can be {@link org.apache.oozie.client.WorkflowAction.Status#OK} or
     *                    {@link org.apache.oozie.client.WorkflowAction.Status#ERROR}.
     * @param signalValue the action external end status.
     */
    void setEndData(WorkflowAction.Status status, String signalValue);

    /**
     * Return if the executor invocation is a retry or not.
     *
     * @return if the executor invocation is a retry or not.
     */
    boolean isRetry();

    /**
     * Sets the external status for the action in context.
     *
     * @param externalStatus the external status.
     */
    void setExternalStatus(String externalStatus);

    /**
     * Get the Action Recovery ID.
     *
     * @return recovery ID.
     */
    String getRecoveryId();

    /*
     * @return the path that will be used to store action specific data
     * @throws IOException @throws URISyntaxException @throws HadoopAccessorException
     */
    Path getActionDir() throws HadoopAccessorException, IOException, URISyntaxException;

    /**
     * @return filesystem handle for the application deployment fs.
     * @throws IOException             if IO error occurs
     * @throws URISyntaxException      if processed uri is not a proper URI
     * @throws HadoopAccessorException if accessing hadoop fails
     */
    FileSystem getAppFileSystem() throws HadoopAccessorException, IOException, URISyntaxException;

    void setErrorInfo(String str, String exMsg);
}
