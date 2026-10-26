package org.apache.oozie.store.config;

import org.apache.oozie.util.db.schema.Column;
import org.apache.oozie.util.db.schema.Index;

public enum OozieIndex implements Index {
    IDX_WF_APPNAME(OozieColumn.WF_appName),
    IDX_WF_USER(OozieColumn.WF_userName),
    IDX_WF_GROUP(OozieColumn.WF_groupName),
    IDX_WF_STATUS(OozieColumn.WF_status),
    IDX_WF_EXTERNAL_ID(OozieColumn.WF_externalId),

    IDX_ACTIONS_BEGINTIME(OozieColumn.ACTIONS_pendingAge),
    IDX_ACTIONS_WFID(OozieColumn.ACTIONS_wfId);

    final Column column;

    OozieIndex(Column column) {
        this.column = column;
    }

    public Column column() {
        return column;
    }
}
