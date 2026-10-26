package org.apache.activemq.filter.expression.unary;

import jakarta.jms.JMSException;
import org.apache.activemq.filter.eval.MessageEvaluationContext;
import org.apache.activemq.filter.expression.core.BooleanExpression;
import org.apache.activemq.filter.expression.core.Expression;

abstract class BooleanUnaryExpression extends UnaryExpression implements BooleanExpression {
    public BooleanUnaryExpression(Expression left) {
        super(left);
    }

    public boolean matches(MessageEvaluationContext message) throws JMSException {
        Object object = evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
}
