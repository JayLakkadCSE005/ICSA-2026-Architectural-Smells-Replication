package org.apache.oozie.handler;

import org.apache.oozie.BundleEngineException;
import org.apache.oozie.DagEngineException;
import org.apache.oozie.coordinator.CoordinatorEngineException;
import org.json.simple.JSONObject;

public interface OozieOperationJob {
    JSONObject BundleJob() throws BundleEngineException;

    JSONObject CoordinatorJob() throws CoordinatorEngineException;

    JSONObject WorkflowsJob() throws DagEngineException;
}
