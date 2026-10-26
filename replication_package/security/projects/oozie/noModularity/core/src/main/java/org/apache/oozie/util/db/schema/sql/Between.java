package org.apache.oozie.util.db.schema.sql;

import org.apache.oozie.util.db.schema.Column;

/**
 * Generate the Range checking statements
 */
class Between extends SqlStatement.Condition {
    Between(boolean isInvert, Column column, Object lVal, Object rVal) {
        super(column);
        sb.append(isInvert ? " NOT" : "").append(" BETWEEN ");
        sb.append(" ? ");
        values.add(lVal);
        sb.append(" AND ");
        sb.append(" ? ").append(" )");
        values.add(rVal);
    }
}
