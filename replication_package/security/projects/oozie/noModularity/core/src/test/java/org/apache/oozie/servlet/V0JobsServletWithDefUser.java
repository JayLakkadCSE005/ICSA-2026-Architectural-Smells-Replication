package org.apache.oozie.servlet;

import org.apache.oozie.servlet.v0.V0JobsServlet;
import org.apache.oozie.test.XTestCase;

import javax.servlet.http.HttpServletRequest;

public class V0JobsServletWithDefUser extends V0JobsServlet {

    protected String getUser(HttpServletRequest request) {
        return XTestCase.getTestUser2();
    }
}
