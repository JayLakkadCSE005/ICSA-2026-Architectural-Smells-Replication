package org.apache.oozie.command.schema;

import org.apache.oozie.store.blob.BinaryBlob;
import org.apache.oozie.sla.event.SLAEventBean;
import org.apache.oozie.StringBlob;
import org.apache.oozie.client.rest.JsonBean;
import org.apache.oozie.client.rest.JsonSLAEvent;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TableInfo {
    String primaryKeyColumn;
    Map<String, Integer> columnTypes;
    Set<String> indexedColumns;

    public TableInfo(Class<? extends JsonBean> clazz, String dbType) {
        columnTypes = new HashMap<String, Integer>();
        indexedColumns = new HashSet<String>();
        populate(clazz, dbType);
        // The "SLA_EVENTS" table is made up of two classes (JsonSLAEvent and SLAEventBean), and the reflection doesn't pick up
        // from both automatically, so we have to manually do this
        if (clazz.equals(JsonSLAEvent.class)) {
            populate(SLAEventBean.class, dbType);
        }
    }

    private void populate(Class<? extends JsonBean> clazz, String dbType) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column colAnn = field.getAnnotation(Column.class);
            if (colAnn != null) {
                String name = caseColumnName(colAnn.name(), dbType);
                boolean isLob = (field.getAnnotation(Lob.class) != null);
                Integer type = getSQLType(field.getType(), isLob, dbType);
                columnTypes.put(name, type);
                boolean isIndex = (field.getAnnotation(Index.class) != null);
                if (isIndex) {
                    indexedColumns.add(name);
                }
                boolean isPrimaryKey = (field.getAnnotation(Id.class) != null);
                if (isPrimaryKey) {
                    indexedColumns.add(name);
                    primaryKeyColumn = name;
                }
            } else {
                // Some Id fields don't have an @Column annotation
                Id idAnn = field.getAnnotation(Id.class);
                if (idAnn != null) {
                    String name = caseColumnName(field.getName(), dbType);
                    boolean isLob = (field.getAnnotation(Lob.class) != null);
                    Integer type = getSQLType(field.getType(), isLob, dbType);
                    columnTypes.put(name, type);
                    indexedColumns.add(name);
                    primaryKeyColumn = name;
                }
            }
        }
        DiscriminatorColumn discAnn = clazz.getAnnotation(DiscriminatorColumn.class);
        if (discAnn != null) {
            String name = caseColumnName(discAnn.name(), dbType);
            Integer type = getSQLType(discAnn.discriminatorType());
            columnTypes.put(name, type);
            indexedColumns.add(name);
        }
    }

    private static Integer getSQLType(Class<?> clazz, boolean isLob, String dbType) {
        if (clazz.equals(String.class)) {
            if (dbType.equals("mysql") && isLob) {
                return Types.LONGVARCHAR;
            }
            if (dbType.equals("oracle") && isLob) {
                return Types.CLOB;
            }
            return Types.VARCHAR;
        }
        if (clazz.equals(StringBlob.class) || clazz.equals(BinaryBlob.class)) {
            if (dbType.equals("mysql")) {
                return Types.LONGVARBINARY;
            }
            if (dbType.equals("oracle")) {
                return Types.BLOB;
            }
            return Types.BINARY;
        }
        if (clazz.equals(Timestamp.class)) {
            return Types.TIMESTAMP;
        }
        if (clazz.equals(int.class)) {
            if (dbType.equals("oracle")) {
                return Types.DECIMAL;
            }
            return Types.INTEGER;
        }
        if (clazz.equals(long.class)) {
            if (dbType.equals("oracle")) {
                return Types.DECIMAL;
            }
            return Types.BIGINT;
        }
        if (clazz.equals(byte.class)) {
            if (dbType.equals("mysql")) {
                return Types.TINYINT;
            }
            if (dbType.equals("oracle")) {
                return Types.DECIMAL;
            }
            return Types.SMALLINT;
        }
        return null;
    }

    private static Integer getSQLType(DiscriminatorType discType) {
        switch (discType) {
            case STRING:
                return Types.VARCHAR;
            case CHAR:
                return Types.CHAR;
            case INTEGER:
                return Types.INTEGER;
        }
        return null;
    }

    private static String caseColumnName(String name, String dbType) {
        // Oracle wants column names in all caps
        if (dbType.equals("oracle")) {
            return name.toUpperCase();
        }
        // Postgres and MySQL want column names in all lowers
        if (dbType.equals("postgresql") || dbType.equals("mysql")) {
            return name.toLowerCase();
        }
        return name;
    }
}
