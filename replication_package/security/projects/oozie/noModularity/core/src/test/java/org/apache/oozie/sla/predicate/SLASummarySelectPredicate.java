package org.apache.oozie.sla.predicate;

import com.google.common.collect.Sets;

import javax.annotation.Nullable;

public class SLASummarySelectPredicate extends DmlPredicate {
    private int remainingSuccessfulAttempts;

    public SLASummarySelectPredicate(final int remainingSuccessfulAttempts) {
        super("SLA_SUMMARY", Sets.newHashSet("SELECT "));
        this.remainingSuccessfulAttempts = remainingSuccessfulAttempts;
    }

    @Override
    public boolean test(@Nullable String input) {
        if (super.test(input)) {
            if (remainingSuccessfulAttempts <= 0) {
                return true;
            } else {
                remainingSuccessfulAttempts--;
                return false;
            }
        } else {
            return false;
        }
    }
}
