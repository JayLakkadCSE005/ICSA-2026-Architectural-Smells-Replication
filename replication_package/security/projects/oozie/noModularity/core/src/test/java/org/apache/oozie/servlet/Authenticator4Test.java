package org.apache.oozie.servlet;

import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.client.PseudoAuthenticator;

import java.io.IOException;
import java.net.URL;

public class Authenticator4Test extends PseudoAuthenticator {

    static boolean USED = false;

    @Override
    public void authenticate(URL url, AuthenticatedURL.Token token) throws IOException, AuthenticationException {
        USED = true;
        super.authenticate(url, token);
    }
}
