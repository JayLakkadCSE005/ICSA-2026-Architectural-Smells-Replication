package org.apache.oozie.handler;

import org.apache.oozie.BundleEngineException;
import org.apache.oozie.DagEngineException;
import org.apache.oozie.coordinator.CoordinatorEngineException;
import org.json.simple.JSONObject;

public class OozieJobOperationCaller {
    public JSONObject call(final String jobType, final OozieOperationJob job)
            throws DagEngineException, BundleEngineException, CoordinatorEngineException {
        switch (jobType) {
            case OozieClientOperationHandler.WF:
                return job.WorkflowsJob();
            case OozieClientOperationHandler.COORD:
                return job.CoordinatorJob();
            case OozieClientOperationHandler.BUNDLE:
                return job.BundleJob();
            default:
                throw new IllegalArgumentException("Invalid jobType passed. jobType: " + jobType);
        }
    }
}
