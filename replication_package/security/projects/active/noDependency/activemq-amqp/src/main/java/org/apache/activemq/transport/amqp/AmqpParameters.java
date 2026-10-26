package org.apache.activemq.transport.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AmqpParameters {
    String PACKAGE_NAME = "org.apache.activemq.transport.amqp;";
    public static final Logger TRACE_FRAMES = LoggerFactory.getLogger(PACKAGE_NAME + ".FRAMES");
}
