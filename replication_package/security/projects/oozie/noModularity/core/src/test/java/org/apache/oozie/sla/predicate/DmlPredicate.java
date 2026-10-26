package org.apache.oozie.sla.predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.annotation.Nullable;
import java.util.Set;

class DmlPredicate implements java.util.function.Predicate<String> {
    private final String tableName;
    private final Set<String> operationPrefixes;

    DmlPredicate(final String tableName, final Set<String> operationPrefixes) {
        this.tableName = tableName;
        this.operationPrefixes = operationPrefixes;
    }

    @Override
    public boolean test(@Nullable String input) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(input));
        boolean operationMatch = false;
        for (String s : operationPrefixes) {
            if (input.startsWith(s)) {
                operationMatch = true;
                break;
            }
        }
        return operationMatch && input.toUpperCase().contains(tableName);
    }
}
