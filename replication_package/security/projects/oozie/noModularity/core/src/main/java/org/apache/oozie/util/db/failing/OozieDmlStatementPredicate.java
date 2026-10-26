package org.apache.oozie.util.db.failing;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class OozieDmlStatementPredicate implements Predicate<String> {
    private static final Set<String> DML_PREFIXES = Sets.newHashSet(
            "SELECT ", "INSERT INTO ", "UPDATE ", "DELETE FROM ");
    private static final Set<String> OOZIE_TABLE_NAMES = Sets.newHashSet(
            "BUNDLE_ACTIONS", "BUNDLE_JOBS", "COORD_ACTIONS", "COORD_JOBS", "SLA_REGISTRATION", "SLA_SUMMARY",
            "WF_ACTIONS", "WF_JOBS");

    @Override
    public boolean test(@Nullable String input) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(input));

        boolean isDmlStatement = false;
        for (final String dmlPrefix : DML_PREFIXES) {
            if (input.toUpperCase().startsWith(dmlPrefix)) {
                isDmlStatement = true;
            }
        }

        boolean isOozieTable = false;
        for (final String oozieTableName : OOZIE_TABLE_NAMES) {
            if (input.toUpperCase().contains(oozieTableName)) {
                isOozieTable = true;
            }
        }

        return isDmlStatement && isOozieTable;
    }
}
