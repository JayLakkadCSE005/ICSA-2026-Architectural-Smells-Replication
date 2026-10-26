package org.apache.oozie.util.db.schema.sql;

import org.apache.oozie.util.db.schema.Column;

/**
 * LIKE/NOT LIKE Condition Generator
 */
class Like extends SqlStatement.Condition {
    Like(boolean isInvert, Column column, String value) {
        super(column);
        sb.append(isInvert ? " NOT" : "").append(" LIKE ").append("?").append(" )");
        values.add(value);
    }
}
