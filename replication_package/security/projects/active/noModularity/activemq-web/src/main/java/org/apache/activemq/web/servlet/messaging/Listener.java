package org.apache.activemq.web.servlet.messaging;

import jakarta.jms.MessageConsumer;
import org.apache.activemq.MessageAvailableConsumer;
import org.apache.activemq.MessageAvailableListener;
import org.apache.activemq.web.async.AsyncServletRequest;

/*
 * Listen for available messages and wakeup any asyncRequests.
 */
class Listener implements MessageAvailableListener {
    MessageConsumer consumer;
    AsyncServletRequest asyncRequest;

    Listener(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    public void setAsyncRequest(AsyncServletRequest asyncRequest) {
        synchronized (consumer) {
            this.asyncRequest = asyncRequest;
        }
    }

    @Override
    public void onMessageAvailable(MessageConsumer consumer) {
        assert this.consumer == consumer;

        ((MessageAvailableConsumer) consumer).setAvailableListener(null);

        synchronized (this.consumer) {
            if (asyncRequest != null) {
                asyncRequest.dispatch();
            }
        }
    }
}
