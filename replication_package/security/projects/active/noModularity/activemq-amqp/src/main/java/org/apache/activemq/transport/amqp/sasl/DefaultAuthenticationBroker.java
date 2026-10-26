package org.apache.activemq.transport.amqp.sasl;

import org.apache.activemq.security.authentication.AuthenticationBroker;
import org.apache.activemq.security.context.SecurityContext;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Set;

public class DefaultAuthenticationBroker implements AuthenticationBroker {

    @Override
    public SecurityContext authenticate(String username, String password, X509Certificate[] peerCertificates) throws SecurityException {
        return new SecurityContext(username) {

            @Override
            public Set<Principal> getPrincipals() {
                return null;
            }
        };
    }
}
