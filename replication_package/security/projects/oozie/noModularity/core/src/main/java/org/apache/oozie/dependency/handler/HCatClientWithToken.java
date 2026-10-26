package org.apache.oozie.dependency.handler;

import org.apache.hive.hcatalog.api.HCatClient;

public class HCatClientWithToken {
    private HCatClient hcatClient;
    private String token;

    public HCatClientWithToken(HCatClient client, String delegationToken) {
        this.hcatClient = client;
        this.token = delegationToken;
    }

    public HCatClient getHCatClient() {
        return this.hcatClient;
    }

    public String getDelegationToken() {
        return this.token;
    }
}
