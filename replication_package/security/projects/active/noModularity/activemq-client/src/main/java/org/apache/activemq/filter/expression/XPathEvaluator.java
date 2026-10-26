package org.apache.activemq.filter.expression;

import jakarta.jms.JMSException;
import org.apache.activemq.command.Message;

public interface XPathEvaluator {
    boolean evaluate(Message message) throws JMSException;
}
