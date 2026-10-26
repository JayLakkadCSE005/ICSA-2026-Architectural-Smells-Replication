package org.apache.oozie.sla.purger;

import org.apache.oozie.ErrorCode;
import org.apache.oozie.command.CommandException;
import org.apache.oozie.sla.SLACalculatorMemory;
import org.apache.oozie.sla.SLAXCommandFactory;

import java.util.Iterator;

public class HistoryPurgeWorker extends Thread {

    private final SLACalculatorMemory slaCalculatorMemory;

    public HistoryPurgeWorker(SLACalculatorMemory slaCalculatorMemory) {
        this.slaCalculatorMemory = slaCalculatorMemory;
    }

    @Override
    public void run() {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        Iterator<String> jobItr = slaCalculatorMemory.getHistorySet().iterator();
        while (jobItr.hasNext()) {
            String jobId = jobItr.next();
            SLACalculatorMemory.LOG.debug(" Running HistoryPurgeWorker for " + jobId);
            try {
                boolean isDone = SLAXCommandFactory.getSLAJobHistoryXCommand(jobId).call();
                if (isDone) {
                    SLACalculatorMemory.LOG.debug("[{0}] job is finished and processed. Removing from history", jobId);
                    jobItr.remove();
                }
            }
            catch (CommandException e) {
                if (e.getErrorCode().equals(ErrorCode.E0604) || e.getErrorCode().equals(ErrorCode.E0605)) {
                    SLACalculatorMemory.LOG.warn("Job is not found in db: " + jobId, e);
                    jobItr.remove();
                }
                else {
                    SLACalculatorMemory.LOG.error("Failed to fetch the job: " + jobId, e);
                }
            }
        }
    }
}
