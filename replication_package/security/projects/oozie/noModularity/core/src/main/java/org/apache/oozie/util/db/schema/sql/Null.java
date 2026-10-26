package org.apache.oozie.util.db.schema.sql;

import org.apache.oozie.util.db.schema.Column;

/**
 * NULL/NOT NULL Condition Generator
 */
class Null extends SqlStatement.Condition {
    Null(boolean isInvert, Column column) {
        super(column);
        sb.append(" IS");
        sb.append(isInvert ? " NOT" : "");
        sb.append(" NULL ");
        sb.append(" )");
    }
}
