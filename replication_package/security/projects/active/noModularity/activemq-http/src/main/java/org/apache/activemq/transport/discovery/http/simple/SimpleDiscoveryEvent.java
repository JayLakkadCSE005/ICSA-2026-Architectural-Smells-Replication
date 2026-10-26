package org.apache.activemq.transport.discovery.http.simple;

import org.apache.activemq.command.DiscoveryEvent;
import org.apache.activemq.transport.discovery.http.HTTPDiscoveryAgent;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleDiscoveryEvent extends DiscoveryEvent {

    private final HTTPDiscoveryAgent httpDiscoveryAgent;
    public int connectFailures;
    public long reconnectDelay = HTTPDiscoveryAgent.initialReconnectDelay;
    public long connectTime = System.currentTimeMillis();
    public AtomicBoolean failed = new AtomicBoolean(false);
    public AtomicBoolean removed = new AtomicBoolean(false);

    public SimpleDiscoveryEvent(HTTPDiscoveryAgent httpDiscoveryAgent, String service) {
        super(service);
        this.httpDiscoveryAgent = httpDiscoveryAgent;
    }
}
