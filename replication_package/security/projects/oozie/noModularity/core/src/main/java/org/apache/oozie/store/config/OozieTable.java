package org.apache.oozie.store.config;

import org.apache.oozie.util.db.schema.Table;

public enum OozieTable implements Table {
    WORKFLOWS,
    ACTIONS,
    WF_PROCESS_INSTANCE,
    VERSION;

    @Override
    public String toString() {
        return OozieSchema.oozieDbName + "." + name().toUpperCase();
    }
}
