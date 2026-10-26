package org.apache.activemq.filter.expression.unary;

import org.apache.activemq.command.Message;

interface SubExpression {
    Object evaluate(Message message);
}
