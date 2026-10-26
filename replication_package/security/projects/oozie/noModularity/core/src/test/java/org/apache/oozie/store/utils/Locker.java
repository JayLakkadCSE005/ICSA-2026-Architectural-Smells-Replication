package org.apache.oozie.store.utils;

import org.apache.oozie.service.Services;
import org.apache.oozie.service.WorkflowStoreService;
import org.apache.oozie.store.WorkflowStore;
import org.apache.oozie.util.XLog;

public class Locker implements Runnable {
    protected String id;
    private String nameIndex;
    private StringBuffer sb;
    protected long timeout;

    public Locker(String id, String nameIndex, StringBuffer buffer) {
        this.id = id;
        this.nameIndex = id + ":" + nameIndex;
        this.sb = buffer;
    }

    public void run() {
        XLog log = XLog.getLog(getClass());
        try {
            WorkflowStore store = Services.get().get(WorkflowStoreService.class).create();
            log.info("Get [{0}]", nameIndex);
            store.beginTrx();
            store.getWorkflow(id, false);
            log.info("Got [{0}]", nameIndex);
            sb.append(nameIndex + "-L ");
            synchronized (this) {
                wait();
            }
            sb.append(nameIndex + "-U ");
            store.commitTrx();
            store.closeTrx();
            log.info("Release [{0}]", nameIndex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void finish() {
        synchronized (this) {
            notify();
        }
    }
}
