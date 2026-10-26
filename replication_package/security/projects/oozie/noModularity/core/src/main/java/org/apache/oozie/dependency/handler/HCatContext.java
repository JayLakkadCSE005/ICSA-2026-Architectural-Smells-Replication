package org.apache.oozie.dependency.handler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.oozie.util.XLog;

public class HCatContext extends Context {

    private HCatClient hcatClient;
    private String delegationToken;

    /**
     * Create a HCatContext that can be used to access a hcat URI
     *
     * @param conf       Configuration to access the URI
     * @param user       name of the user the URI should be accessed as
     * @param hcatClient HCatClient to talk to hcatalog server
     */
    public HCatContext(Configuration conf, String user, HCatClient hcatClient) {
        super(conf, user);
        this.hcatClient = hcatClient;
    }

    public HCatContext(Configuration conf, String user, HCatClientWithToken hcatClient) {
        super(conf, user);
        this.hcatClient = hcatClient.getHCatClient();
        this.delegationToken = hcatClient.getDelegationToken();
    }

    /**
     * Get the HCatClient to talk to hcatalog server
     *
     * @return HCatClient to talk to hcatalog server
     */
    public HCatClient getHCatClient() {
        return hcatClient;
    }

    /**
     * Get the Delegation token to access HCat
     *
     * @return delegationToken
     */
    public String getDelegationToken() {
        return delegationToken;
    }

    @Override
    public void destroy() {
        try {
            if (delegationToken != null && !delegationToken.isEmpty()) {
                hcatClient.cancelDelegationToken(delegationToken);
            }
            delegationToken = null;
        } catch (Exception ignore) {
            XLog.getLog(HCatContext.class).warn("Error cancelling delegation token", ignore);
        }
        try {
            hcatClient.close();
        } catch (Exception ignore) {
            XLog.getLog(HCatContext.class).warn("Error closing hcat client", ignore);
        }
    }

}
