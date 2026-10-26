package org.apache.oozie.util.db.schema;

/**
 * Interface or table columns
 */
public interface Column {
    /**
     * Table to which the column belongs
     *
     * @return table name
     */
    Table table();

    /**
     * Alias to be used by the select statement for this column
     *
     * @return alias for column
     */
    String asLabel();

    /**
     * Name of the column
     *
     * @return column name
     */
    String columnName();

    /**
     * Returns the datatype of the column
     *
     * @return column type
     */
    Class<?> getType();

    /**
     * Returns the length of the column
     *
     * @return Returns the length of the column
     */
    int getLength();

    /**
     * Returns if the field is a primary key or not
     *
     * @return true if field is a primary key
     */
    boolean isPrimaryKey();
}
