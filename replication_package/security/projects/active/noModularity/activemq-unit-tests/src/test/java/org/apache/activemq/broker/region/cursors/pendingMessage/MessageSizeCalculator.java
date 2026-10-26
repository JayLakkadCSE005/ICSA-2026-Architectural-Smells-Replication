package org.apache.activemq.broker.region.cursors.pendingMessage;

interface MessageSizeCalculator {
    long getMessageSize() throws Exception;
}
