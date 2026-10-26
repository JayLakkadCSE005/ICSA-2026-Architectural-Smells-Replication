package org.apache.activemq.security.jaas;

import org.apache.activemq.security.context.SecurityContext;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;

class JaasSecurityContext extends SecurityContext {

    private final Subject subject;

    public JaasSecurityContext(String userName, Subject subject) {
        super(userName);
        this.subject = subject;
    }

    @Override
    public Set<Principal> getPrincipals() {
        return subject.getPrincipals();
    }
}
