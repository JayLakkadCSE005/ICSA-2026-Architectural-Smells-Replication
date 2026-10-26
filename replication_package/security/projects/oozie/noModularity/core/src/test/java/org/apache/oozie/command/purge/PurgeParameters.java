package org.apache.oozie.command.purge;

import org.apache.oozie.BundleJobBean;
import org.apache.oozie.coordinator.CoordinatorJobBean;
import org.apache.oozie.workflow.bean.WorkflowJobBean;
import org.apache.oozie.client.rest.JsonBean;
import org.apache.oozie.command.TestPurgeXCommand;

public class PurgeParameters {
    public int workflowDays;
    public int coordinatorDays;
    public int bundleDays;
    public int limit;
    public boolean purgeOldCoordAction;

    public static PurgeParameters createDefaultParameters() {
        PurgeParameters purgeParameters = new PurgeParameters();
        purgeParameters.workflowDays = 30;
        purgeParameters.coordinatorDays = 7;
        purgeParameters.bundleDays = 7;
        purgeParameters.limit = 100;
        purgeParameters.purgeOldCoordAction = false;
        return purgeParameters;
    }

    public PurgeParameters withPurgeOldCordAction() {
        purgeOldCoordAction = true;
        return this;
    }

    public PurgeParameters withKeepingJobs(JsonBean... jsonBeans) {
        for (JsonBean jsonBean : jsonBeans) {
            if (jsonBean instanceof WorkflowJobBean) {
                workflowDays = TestPurgeXCommand.getNumDaysToNotBePurged(((WorkflowJobBean) jsonBean).getEndTime());
            } else if (jsonBean instanceof CoordinatorJobBean) {
                coordinatorDays = TestPurgeXCommand.getNumDaysToNotBePurged(((CoordinatorJobBean) jsonBean).getLastModifiedTime());
            } else if (jsonBean instanceof BundleJobBean) {
                bundleDays = TestPurgeXCommand.getNumDaysToNotBePurged(((BundleJobBean) jsonBean).getLastModifiedTime());
            }
        }
        return this;
    }

    public PurgeParameters withLimit(int limit) {
        this.limit = limit;
        return this;
    }
}
