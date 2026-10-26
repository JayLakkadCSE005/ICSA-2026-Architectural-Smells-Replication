package org.apache.oozie.sla.predicate;

import com.google.common.collect.Sets;

public class SLARegistrationInsertUpdatePredicate extends DmlPredicate {
    public SLARegistrationInsertUpdatePredicate() {
        super("SLA_REGISTRATION", Sets.newHashSet("INSERT INTO ", "UPDATE "));
    }
}
