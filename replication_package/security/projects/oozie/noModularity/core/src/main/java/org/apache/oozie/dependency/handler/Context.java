package org.apache.oozie.dependency.handler;

import org.apache.hadoop.conf.Configuration;

public abstract class Context {

    private Configuration conf;
    private String user;

    /**
     * Create a Context that can be used to access a URI
     *
     * @param conf Configuration to access the URI
     * @param user name of the user the URI should be accessed as
     */
    public Context(Configuration conf, String user) {
        this.conf = conf;
        this.user = user;
    }

    /**
     * Get the Configuration to access the URI
     *
     * @return Configuration to access the URI
     */
    public Configuration getConfiguration() {
        return conf;
    }

    /**
     * Get the name of the user the URI will be accessed as
     *
     * @return the user name the URI will be accessed as
     */
    public String getUser() {
        return user;
    }

    /**
     * Destroy the Context
     */
    public void destroy() {
    }

}
