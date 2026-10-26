package org.apache.oozie.store.config;

import org.apache.oozie.util.db.schema.Column;
import org.apache.oozie.util.db.schema.Table;

import java.sql.Blob;
import java.sql.Timestamp;

public enum OozieColumn implements Column {
    // Process Instance Table
    PI_wfId(OozieTable.WF_PROCESS_INSTANCE, String.class, true, 100),
    PI_state(OozieTable.WF_PROCESS_INSTANCE, Blob.class, false),

    // WorkflowJob Table
    WF_id(OozieTable.WORKFLOWS, String.class, true, 100),
    WF_externalId(OozieTable.WORKFLOWS, String.class, false, 100),
    WF_appName(OozieTable.WORKFLOWS, String.class, false, 100),
    WF_appPath(OozieTable.WORKFLOWS, String.class, false, 255),
    WF_conf(OozieTable.WORKFLOWS, String.class, false),
    WF_protoActionConf(OozieTable.WORKFLOWS, String.class, false),
    WF_logToken(OozieTable.WORKFLOWS, String.class, false, 100),
    WF_status(OozieTable.WORKFLOWS, String.class, false, 100),
    WF_run(OozieTable.WORKFLOWS, Long.class, false),
    WF_lastModTime(OozieTable.WORKFLOWS, Timestamp.class, false),
    WF_createdTime(OozieTable.WORKFLOWS, Timestamp.class, false),
    WF_startTime(OozieTable.WORKFLOWS, Timestamp.class, false),
    WF_endTime(OozieTable.WORKFLOWS, Timestamp.class, false),
    WF_userName(OozieTable.WORKFLOWS, String.class, false, 100),
    WF_groupName(OozieTable.WORKFLOWS, String.class, false, 100),
    WF_authToken(OozieTable.WORKFLOWS, String.class, false),

    // Actions Table
    ACTIONS_id(OozieTable.ACTIONS, String.class, true, 100),
    ACTIONS_name(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_type(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_wfId(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_conf(OozieTable.ACTIONS, String.class, false),
    ACTIONS_status(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_externalStatus(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_errorCode(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_errorMessage(OozieTable.ACTIONS, String.class, false),
    ACTIONS_transition(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_retries(OozieTable.ACTIONS, Long.class, false),
    ACTIONS_startTime(OozieTable.ACTIONS, Timestamp.class, false),
    ACTIONS_endTime(OozieTable.ACTIONS, Timestamp.class, false),
    ACTIONS_lastCheckTime(OozieTable.ACTIONS, Timestamp.class, false),
    ACTIONS_data(OozieTable.ACTIONS, String.class, false),
    ACTIONS_externalId(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_trackerUri(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_consoleUrl(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_executionPath(OozieTable.ACTIONS, String.class, false, 255),
    ACTIONS_pending(OozieTable.ACTIONS, Boolean.class, false),
    ACTIONS_pendingAge(OozieTable.ACTIONS, Timestamp.class, false),
    ACTIONS_signalValue(OozieTable.ACTIONS, String.class, false, 100),
    ACTIONS_logToken(OozieTable.ACTIONS, String.class, false, 100),

    // Version Table
    VER_versionNumber(OozieTable.VERSION, String.class, false, 255);

    final Table table;
    final Class<?> type;
    int length = -1;
    final boolean isPrimaryKey;

    OozieColumn(Table table, Class<?> type, boolean isPrimaryKey) {
        this(table, type, isPrimaryKey, -1);
    }

    OozieColumn(Table table, Class<?> type, boolean isPrimaryKey, int length) {
        this.table = table;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.length = length;
    }

    private String getName() {
        String tName = table.name();
        return tName + "." + columnName();
    }

    public String columnName() {
        return name().split("_")[1].toLowerCase();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Table table() {
        return table;
    }

    public Class<?> getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public String asLabel() {
        return name().toUpperCase();
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
}
