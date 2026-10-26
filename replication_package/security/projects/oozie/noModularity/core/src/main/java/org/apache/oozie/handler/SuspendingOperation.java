package org.apache.oozie.handler;

import org.apache.oozie.*;
import org.apache.oozie.coordinator.CoordinatorEngine;
import org.apache.oozie.coordinator.CoordinatorEngineException;
import org.apache.oozie.coordinator.CoordinatorJobInfo;
import org.apache.oozie.workflow.bean.WorkflowsInfo;

class SuspendingOperation extends AbstractOozieOperationJob {

    private final OozieClientOperationHandler oozieClientOperationHandler;

    SuspendingOperation(OozieClientOperationHandler oozieClientOperationHandler, final String filter, final int start, final int len) {
        super(filter, start, len);
        this.oozieClientOperationHandler = oozieClientOperationHandler;
    }

    @Override
    BundleJobInfo createBundleJobInfo() throws BundleEngineException {
        return ((BundleEngine) oozieClientOperationHandler.engine).suspendJobs(filter, start, len);
    }

    @Override
    CoordinatorJobInfo createCoordinatorJobInfo() throws CoordinatorEngineException {
        return ((CoordinatorEngine) oozieClientOperationHandler.engine).suspendJobs(filter, start, len);
    }

    @Override
    WorkflowsInfo createWorkflowsInfo() throws DagEngineException {
        return ((DagEngine) oozieClientOperationHandler.engine).suspendJobs(filter, start, len);
    }
}
