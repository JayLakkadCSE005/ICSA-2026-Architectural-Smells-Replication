package org.apache.activemq.web.servlet.messaging;

import org.apache.activemq.web.AjaxWebClient;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

/*
 * an instance of this class runs every minute (started in init), to clean up old web clients & free resources.
 */
class ClientCleaner extends TimerTask {
    private final MessageListenerServlet messageListenerServlet;

    public ClientCleaner(MessageListenerServlet messageListenerServlet) {
        this.messageListenerServlet = messageListenerServlet;
    }

    @Override
    public void run() {
        if (MessageListenerServlet.LOG.isDebugEnabled()) {
            MessageListenerServlet.LOG.debug("Cleaning up expired web clients.");
        }

        synchronized (messageListenerServlet.ajaxWebClients) {
            Iterator<Map.Entry<String, AjaxWebClient>> it = messageListenerServlet.ajaxWebClients.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, AjaxWebClient> e = it.next();
                String key = e.getKey();
                AjaxWebClient val = e.getValue();
                if (MessageListenerServlet.LOG.isDebugEnabled()) {
                    MessageListenerServlet.LOG.debug("AjaxWebClient " + key + " last accessed " + val.getMillisSinceLastAccessed() / 1000 + " seconds ago.");
                }
                // close an expired client and remove it from the ajaxWebClients hash.
                if (val.closeIfExpired()) {
                    if (MessageListenerServlet.LOG.isDebugEnabled()) {
                        MessageListenerServlet.LOG.debug("Removing expired AjaxWebClient " + key);
                    }
                    it.remove();
                }
            }
        }
    }
}
