package org.apache.oozie.util.db.schema.sql;

import org.apache.oozie.util.db.schema.Column;

/**
 * IN/NOT IN condition statement generator for checking multiple values and for sub queries
 */
class In extends SqlStatement.Condition {
    In(boolean isInvert, Column column, Object... values) {
        super(column);
        sb.append(isInvert ? " NOT" : "").append(" IN (");
        for (Object value : values) {
            sb.append(" ? ").append(",");
            this.values.add(value);
        }
        // remove last comma
        sb.setLength(sb.length() - 1);
        sb.append(") )");
    }

    In(boolean isInvert, Column column, SqlStatement.Select select) {
        super(column);
        sb.append(isInvert ? " NOT" : "").append(" IN (");
        for (Object value : select.values) {
            this.values.add(value);
        }
        sb.append(select.toString());
        sb.append(") )");
    }
}
