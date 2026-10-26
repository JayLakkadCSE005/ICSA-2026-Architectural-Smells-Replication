package org.apache.oozie.executor.jpa.sla;

import org.apache.oozie.client.event.EventStatusSLA;
import org.apache.oozie.client.event.SLAStatus;
import org.apache.oozie.util.DateUtils;
import org.apache.oozie.util.Pair;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public enum FilterField {
    APP_NAME(String.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_APP_NAME, FilterComparator.LIKE),
    APP_TYPE(String.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_APP_TYPE, FilterComparator.LIKE),
    USER_NAME(String.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_USER, FilterComparator.LIKE),
    JOB_STATUS(String.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_JOB_STATUS, FilterComparator.LIKE),
    NOMINAL_START(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_NOMINAL_TIME_TS, FilterComparator.GREATER_OR_EQUALS),
    NOMINAL_END(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_NOMINAL_TIME_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    NOMINAL_AFTER(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_NOMINAL_TIME_TS, FilterComparator.GREATER_OR_EQUALS),
    NOMINAL_BEFORE(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_NOMINAL_TIME_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    CREATED_AFTER(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_CREATED_TIME_TS, FilterComparator.GREATER_OR_EQUALS),
    CREATED_BEFORE(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_CREATED_TIME_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    EXPECTEDSTART_AFTER(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS, FilterComparator.GREATER_OR_EQUALS),
    EXPECTEDSTART_BEFORE(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    EXPECTEDEND_AFTER(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS, FilterComparator.GREATER_OR_EQUALS),
    EXPECTEDEND_BEFORE(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    ACTUALSTART_AFTER(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS, FilterComparator.GREATER_OR_EQUALS),
    ACTUALSTART_BEFORE(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    ACTUALEND_AFTER(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS, FilterComparator.GREATER_OR_EQUALS),
    ACTUALEND_BEFORE(Timestamp.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS, FilterComparator.LESSTHAN_OR_EQUALS),
    ACTUAL_DURATION_MIN(Integer.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_DURATION, FilterComparator.GREATER_OR_EQUALS),
    ACTUAL_DURATION_MAX(Integer.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_DURATION, FilterComparator.LESSTHAN_OR_EQUALS),
    EXPECTED_DURATION_MIN(Integer.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_DURATION, FilterComparator.GREATER_OR_EQUALS),
    EXPECTED_DURATION_MAX(Integer.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_DURATION, FilterComparator.LESSTHAN_OR_EQUALS),
    SLA_STATUS(SLAStatus.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_SLA_STATUS, FilterComparator.IN),
    EVENT_STATUS(EventStatusSLA.class, null, null),
    ID(String.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_JOB_ID, null),
    PARENT_ID(String.class, SLASummaryGetForFilterJPAExecutor.DBFIELD_PARENT_ID, null),
    BUNDLE(String.class, null, null);

    private final Class type;
    final String dbFieldName;
    final FilterComparator filterComparator;
    public static final List<Pair<FilterField, FilterField>> intervalFieldPairs =
            new ArrayList<Pair<FilterField, FilterField>>() {{
        add(new Pair<>(FilterField.NOMINAL_AFTER, FilterField.NOMINAL_BEFORE));
        add(new Pair<>(FilterField.CREATED_AFTER, FilterField.CREATED_BEFORE));
        add(new Pair<>(FilterField.EXPECTEDSTART_AFTER, FilterField.EXPECTEDSTART_BEFORE));
        add(new Pair<>(FilterField.EXPECTEDEND_AFTER, FilterField.EXPECTEDEND_BEFORE));
        add(new Pair<>(FilterField.ACTUALSTART_AFTER, FilterField.ACTUALSTART_BEFORE));
        add(new Pair<>(FilterField.ACTUALEND_AFTER, FilterField.ACTUALEND_BEFORE));
        add(new Pair<>(FilterField.ACTUAL_DURATION_MIN, FilterField.ACTUAL_DURATION_MAX));
        add(new Pair<>(FilterField.EXPECTED_DURATION_MIN, FilterField.EXPECTED_DURATION_MAX));
    }};

    public static final List<Pair<FilterField, FilterField>> deprecatedFieldPairs =
            new ArrayList<Pair<FilterField, FilterField>>() {{
                add(new Pair<>(FilterField.NOMINAL_START, FilterField.NOMINAL_AFTER));
                add(new Pair<>(FilterField.NOMINAL_END, FilterField.NOMINAL_BEFORE));
            }};

    FilterField(final Class type, final String dbFieldName, final FilterComparator filterComparator) {
        this.type = type;
        this.dbFieldName = dbFieldName;
        this.filterComparator = filterComparator;
    }

    static Object convertFromString(final FilterField field, final String valueStr) throws ParseException {
        if (String.class.equals(field.type)) {
            return valueStr;
        }
        else if (Timestamp.class.equals(field.type)) {
            Date date = DateUtils.parseDateUTC(valueStr);
            return new Timestamp(date.getTime());
        }
        else if (Integer.class.equals(field.type)) {
            return Integer.parseInt(valueStr);
        }
        else if (field.type.isEnum()) {
            List<String> list = new ArrayList<>();
            String[] statusArr = valueStr.split(SLASummaryGetForFilterJPAExecutor.multiValueSeparator);
            for (String s : statusArr) {
                list.add(Enum.valueOf(field.type, s).toString());
            }
            return list;
        }
        return null;
    }

    static FilterField findByName(String name) {
        if (name==null || !name.equals(name.toLowerCase(Locale.US))) {
            return null;
        }
        try {
            return FilterField.valueOf(name.toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    String getColumnName() {
        return name().toLowerCase(Locale.US);
    }
}
