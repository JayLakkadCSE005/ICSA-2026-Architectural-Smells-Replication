package org.apache.oozie.util.db.schema.sql;

import org.apache.oozie.util.db.schema.Column;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * <code>ResultSetReader</code> is used to parse the result set and gives methods for getting appropriate type of
 * data given the column name
 */
public class ResultSetReader {
    final ResultSet rSet;

    ResultSetReader(ResultSet rSet) {
        this.rSet = rSet;
    }

    /**
     * Move the Result Set to next record
     *
     * @return true if there is a next record
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    public boolean next() throws SQLException {
        return rSet.next();
    }

    /**
     * Close the Result Set
     *
     * @throws SQLException if a database access error occurs
     */
    public void close() throws SQLException {
        rSet.close();
    }

    /**
     * Get the Column data given its type and name
     *
     * @param <T>   Type of the column
     * @param clazz Class of the Type
     * @param col   Column name
     * @return Column data
     * @throws SQLException if the columnLabel is not valid;
     *                      if a database access error occurs or this method is called on a closed result set
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz, Column col) throws SQLException {
        if (clazz.isAssignableFrom(col.getType())) {
            return (T) rSet.getObject(col.asLabel());
        } else {
            if (String.class.equals(clazz)) {
                return (T) ("" + rSet.getObject(col.asLabel()));
            } else {
                throw new RuntimeException("Column Error : Actual Type [" + col.getType() + "]," + " Requested Type ["
                        + clazz + "] !!");
            }
        }
    }

    /**
     * Get the data for columns with blob type
     *
     * @param col Column name
     * @return Column data
     * @throws SQLException when accessing binary stream fails
     */
    public byte[] getByteArray(Column col) throws SQLException {
        byte[] bArray = null;
        if (Blob.class.equals(col.getType())) {
            BufferedInputStream bStream = new BufferedInputStream(rSet.getBinaryStream(col.asLabel()));
            byte[] temp = new byte[1024];
            int num = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream bOut = new BufferedOutputStream(baos);
            try {
                while ((num = bStream.read(temp)) != -1) {
                    bOut.write(temp, 0, num);
                }
                bOut.flush();
                bOut.close();
            } catch (IOException e) {
                throw new SQLException(e);
            }
            bArray = baos.toByteArray();
        } else {
            throw new RuntimeException("Column Error : Actual Type [" + col.getType() + "]," + " Requested Type ["
                    + Blob.class + "] !!");
        }
        return bArray;
    }

    /**
     * Get a String Column
     *
     * @param col Column Name
     * @return Column data
     * @throws SQLException if the columnLabel is not valid;
     *                      if a database access error occurs or this method is called on a closed result set
     */
    public String getString(Column col) throws SQLException {
        return get(String.class, col);
    }

    /**
     * Get the Timestamp Column
     *
     * @param col Column name
     * @return Column data
     * @throws SQLException if the columnLabel is not valid;
     *                      if a database access error occurs or this method is called on a closed result set
     */
    public Timestamp getTimestamp(Column col) throws SQLException {
        return get(Timestamp.class, col);
    }

    /**
     * Get the Boolean Column
     *
     * @param col Column name
     * @return Column data
     * @throws SQLException if the columnLabel is not valid;
     *                      if a database access error occurs or this method is called on a closed result set
     */
    public Boolean getBoolean(Column col) throws SQLException {
        return get(Boolean.class, col);
    }

    /**
     * Get the Numeric data
     *
     * @param col Column name
     * @return Column data
     * @throws SQLException if the columnLabel is not valid;
     *                      if a database access error occurs or this method is called on a closed result set
     */
    public Long getLong(Column col) throws SQLException {
        return get(Long.class, col);
    }

}
