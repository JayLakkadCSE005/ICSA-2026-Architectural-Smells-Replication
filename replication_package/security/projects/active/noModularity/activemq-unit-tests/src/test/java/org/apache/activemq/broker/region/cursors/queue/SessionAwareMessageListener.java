package org.apache.activemq.broker.region.cursors.queue;

import jakarta.jms.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Message listener that is given the Session for transacted consumers
 */
class SessionAwareMessageListener implements MessageListener {
    private final List<Message> consumerList;
    private final CountDownLatch latch;
    private final Session consumerSession;
    private Session producerSession;
    private MessageProducer producer;

    public SessionAwareMessageListener(Session consumerSession, CountDownLatch latch, List<Message> consumerList) {
        this(null, consumerSession, null, latch, consumerList);
    }

    public SessionAwareMessageListener(Connection producerConnection, Session consumerSession, String outQueueName,
                                       CountDownLatch latch, List<Message> consumerList) {
        this.consumerList = consumerList;
        this.latch = latch;
        this.consumerSession = consumerSession;

        if (producerConnection != null) {
            try {
                producerSession = producerConnection.createSession(NegativeQueueTest.TRANSACTED, Session.AUTO_ACKNOWLEDGE);
                Destination queue = producerSession.createQueue(outQueueName);
                producer = producerSession.createProducer(queue);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMessage(Message msg) {
        try {
            if (producer == null) {
                // sleep to act as a slow consumer
                // which will force a mix of direct and polled dispatching
                // using the cursor on the broker
                Thread.sleep(50);
            } else {
                producer.send(msg);
                if (NegativeQueueTest.TRANSACTED) producerSession.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (consumerList) {
            consumerList.add(msg);
            if (NegativeQueueTest.DEBUG && consumerList.size() % 100 == 0) {
                int index = consumerList.size() / 100;
                System.out.print(index - ((index / 10) * 10));
            }
            if (consumerList.size() == NegativeQueueTest.MESSAGE_COUNT) {
                latch.countDown();
            }
        }
        if (NegativeQueueTest.TRANSACTED) {
            try {
                consumerSession.commit();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
