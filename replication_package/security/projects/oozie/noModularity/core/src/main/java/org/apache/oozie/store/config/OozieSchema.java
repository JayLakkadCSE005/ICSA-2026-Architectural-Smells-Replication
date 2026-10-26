/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.store.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.oozie.util.db.schema.Schema;
import org.apache.oozie.util.db.schema.Column;
import org.apache.oozie.util.db.schema.DBType;
import org.apache.oozie.util.db.schema.Table;

public class OozieSchema {

    static String oozieDbName;

    private static final String OOZIE_VERSION = "0.1";

    public static final Map<Table, List<Column>> TABLE_COLUMNS;

    static {
        Map<Table, List<Column>> tmpColumns = new HashMap<>();

        for (Column column : OozieColumn.values()) {
            List<Column> tColumns = tmpColumns.get(column.table());
            if (tColumns == null) {
                tColumns = new ArrayList<Column>();
                tmpColumns.put(column.table(), tColumns);
            }
            tColumns.add(column);
        }

        TABLE_COLUMNS = Collections.unmodifiableMap(tmpColumns);
    }

    public static void setOozieDbName(String dbName) {
        oozieDbName = dbName;
    }

    /**
     * Generates the create table SQL Statement
     *
     * @param table table
     * @param dbType database type
     * @return SQL Statement to create the table
     */
    public static String generateCreateTableScript(Table table, DBType dbType) {
        return Schema.generateCreateTableScript(table, dbType, TABLE_COLUMNS.get(table));
    }

    /**
     * Gets the query that will be used to validate the connection
     *
     * @param dbName database name
     * @return String returns the query that will be used to validate the connection
     */
    public static String getValidationQuery(String dbName) {
        return "select count(" + OozieColumn.VER_versionNumber.columnName() + ") from " + dbName + "."
                + OozieTable.VERSION.name().toUpperCase();
    }

    /**
     * Generates the Insert statement to insert the OOZIE_VERSION to table
     *
     * @param dbName database name
     * @return String returns the Insert statement for the OOZIE_VERSION to a table
     */
    public static String generateInsertVersionScript(String dbName) {
        return "INSERT INTO " + dbName + "." + OozieTable.VERSION.name().toUpperCase() + "("
                + OozieColumn.VER_versionNumber.columnName() + ") VALUES(" + OOZIE_VERSION + ")";
    }

    /**
     * Gets the Oozie Schema Version
     *
     * @return String returns the Oozie Schema Version
     */
    public static String getOozieVersion() {
        return OOZIE_VERSION;
    }
}
