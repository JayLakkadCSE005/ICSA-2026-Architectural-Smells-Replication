package org.apache.oozie.handler;

import org.apache.oozie.BundleEngineException;
import org.apache.oozie.BundleJobInfo;
import org.apache.oozie.DagEngineException;
import org.apache.oozie.OozieJsonFactory;
import org.apache.oozie.coordinator.CoordinatorEngineException;
import org.apache.oozie.coordinator.CoordinatorJobInfo;
import org.apache.oozie.workflow.bean.WorkflowsInfo;
import org.json.simple.JSONObject;

abstract class AbstractOozieOperationJob implements OozieOperationJob {
    protected final String filter;
    protected final int start;
    protected final int len;

    AbstractOozieOperationJob(final String filter, final int start, final int len) {
        this.filter = filter;
        this.start = start;
        this.len = len;
    }

    @Override
    public JSONObject BundleJob() throws BundleEngineException {
        return OozieJsonFactory.getBundleJSONObject(createBundleJobInfo(), OozieClientOperationHandler.GMT);
    }

    abstract BundleJobInfo createBundleJobInfo() throws BundleEngineException;

    @Override
    public JSONObject CoordinatorJob() throws CoordinatorEngineException {
        return OozieJsonFactory.getCoordJSONObject(createCoordinatorJobInfo(), OozieClientOperationHandler.GMT);
    }

    abstract CoordinatorJobInfo createCoordinatorJobInfo() throws CoordinatorEngineException;

    @Override
    public JSONObject WorkflowsJob() throws DagEngineException {
        return OozieJsonFactory.getWFJSONObject(createWorkflowsInfo(), OozieClientOperationHandler.GMT);
    }

    abstract WorkflowsInfo createWorkflowsInfo() throws DagEngineException;
}
