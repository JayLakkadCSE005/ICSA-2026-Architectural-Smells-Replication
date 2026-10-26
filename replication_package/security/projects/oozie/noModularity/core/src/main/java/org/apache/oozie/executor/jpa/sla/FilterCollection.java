package org.apache.oozie.executor.jpa.sla;

import com.google.common.annotations.VisibleForTesting;
import org.apache.oozie.ErrorCode;
import org.apache.oozie.servlet.base.XServletException;
import org.apache.oozie.util.Pair;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@VisibleForTesting
class FilterCollection {
    final Map<FilterField, Object> filterValues = new LinkedHashMap<>();

    @VisibleForTesting
    void checkAndSetFilterField(String name, String value) throws ServletException, ParseException {
        FilterField field = FilterField.findByName(name);
        if (field == null) {
            throwNewXServletException(ErrorCode.E0401, String.format("Invalid/unsupported names in filter: %s", name));
        } else {
            validateAndSetFilterField(findNonDeprecatedFilterField(field), FilterField.convertFromString(field, value));
        }
    }

    @VisibleForTesting
    FilterField findNonDeprecatedFilterField(FilterField maybeDeprecatedFilterField) {
        for (Pair<FilterField, FilterField> pair : FilterField.deprecatedFieldPairs) {
            if (pair.getFirst().equals(maybeDeprecatedFilterField)) {
                return pair.getSecond();
            }
        }
        return maybeDeprecatedFilterField;
    }

    private void validateAndSetFilterField(FilterField field, Object value) throws ServletException {
        boolean fieldConstraintViolated;
        for (Pair<FilterField, FilterField> pair : FilterField.intervalFieldPairs) {
            String firstColumnName = pair.getFirst().getColumnName();
            String secondColumnName = pair.getSecond().getColumnName();
            if (pair.getFirst().equals(field)) {
                fieldConstraintViolated = checkFieldConstrantViolation((Comparable) value,
                        (Comparable) getFilterField(secondColumnName));
            } else if (pair.getSecond().equals(field)) {
                fieldConstraintViolated = checkFieldConstrantViolation((Comparable) getFilterField(firstColumnName),
                        (Comparable) value);
            } else {
                fieldConstraintViolated = false;
            }
            if (fieldConstraintViolated) {
                String errorMessage = String.format("should be: field %s <= field %s", firstColumnName, secondColumnName);
                throwNewXServletException(ErrorCode.E0302, errorMessage);
            }
        }
        filterValues.put(field, value);
    }

    private void throwNewXServletException(ErrorCode errorCode, String message) throws ServletException {
        SLASummaryGetForFilterJPAExecutor.LOG.error(message);
        throw new XServletException(HttpServletResponse.SC_BAD_REQUEST, errorCode, message);
    }

    private boolean checkFieldConstrantViolation(Comparable minValue, Comparable maxValue) {
        return minValue != null && maxValue != null && minValue.compareTo(maxValue) > 0;
    }

    @VisibleForTesting
    Object getFilterField(String name) {
        FilterField field = FilterField.findByName(name);
        if (field != null) {
            return filterValues.get(field);
        } else {
            return null;
        }
    }
}
