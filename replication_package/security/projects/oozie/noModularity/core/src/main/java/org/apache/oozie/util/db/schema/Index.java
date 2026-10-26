package org.apache.oozie.util.db.schema;

/**
 * Interface for Index
 */
public interface Index {
    /**
     * Column that is to be indexed
     *
     * @return Column that is to be indexed
     */
    Column column();
}
