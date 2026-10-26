package org.apache.oozie.util.db.schema.sql;

import org.apache.oozie.util.db.schema.Column;

/**
 * Comparison condition statement generator
 */
class Compare extends SqlStatement.Condition {
    Compare(String oper, Column column, Object value) {
        super(column);
        if (value instanceof Column) {
            sb.append(oper).append(value).append(" )");
        } else {
            sb.append(oper).append("?").append(" )");
            values.add(value);
        }
    }
}
